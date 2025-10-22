package com.inetkr.install_referrer

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class InstallReferrer private constructor(private val context: Context) {

    private val prefs = context.getSharedPreferences("ref_prefs", Context.MODE_PRIVATE)
    private val calledKey = "called_install_app"

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: InstallReferrer? = null

        fun getInstance(context: Context): InstallReferrer {
            return instance ?: synchronized(this) {
                instance ?: InstallReferrer(context.applicationContext).also { instance = it }
            }
        }
    }

    fun initialize(debug: Boolean) {
        try {
            ReferrerHelper(context).getReferrer { referrer ->
                Log.d("InstallReferrer", "Referrer = $referrer")
                DeviceInfoHelper(context).getDeviceInfo { deviceInfo ->
                    val rawNonce =
                        "${referrer ?: "temp_id="}&package_name=${deviceInfo.packageName}"
                    val nonce = Base64.encodeToString(rawNonce.toByteArray(), Base64.NO_WRAP)
                    IntegrityHelper(context).requestIntegrityToken(nonce) { token, error ->
                        if (token != null) {
                            val installInfo = mapOf(
                                "referrer" to referrer,
                                "gaid" to deviceInfo.gaId,
                                "device_id" to deviceInfo.deviceId,
                                "device_type" to deviceInfo.deviceType,
                                "package_name" to deviceInfo.packageName,
                                "version_code" to deviceInfo.versionCode,
                                "version_name" to deviceInfo.versionName,
                                "first_install_time" to deviceInfo.firstInstallTime,
                                "last_update_time" to deviceInfo.lastUpdateTime,
                                "device_model" to deviceInfo.deviceModel,
                                "os_version" to deviceInfo.osVersion,
                                "language" to deviceInfo.language,
                                "region" to deviceInfo.region,
                                "user_agent" to deviceInfo.userAgent,
                                "is_reinstall" to deviceInfo.isReinstall
                            )
                            val data = mapOf(
                                "verify_token" to token,
                                "data" to installInfo
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                sendData(data)
                            }
                        } else {
                            Log.e("InstallReferrer", "Error: $error")
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("InstallReferrer", "Error: ${e.message}", e)
        }
    }

    fun sendData(data: Map<String, Any>) {
        val client = OkHttpClient()

        try {
            val json = JSONObject(data).toString()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://appxon.net/api/ref/install_app")
                .post(body)
                .build()

            Log.d("InstallReferrer", "➡️ Sending: $json")

            val response = client.newCall(request).execute()

            val code = response.code
            val responseBody = response.body?.string()
            Log.d("InstallReferrer", "⬅️ Response ($code): $responseBody")

            if (code == 200) {
                prefs.edit { putBoolean(calledKey, true) }
            } else {
                Log.w("InstallReferrer", "install_app failed: $code $responseBody")
            }

        } catch (e: Exception) {
            Log.e("InstallReferrer", "sendData error", e)
        }
    }
}