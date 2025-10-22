package com.inetkr.install_referrer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings
import android.webkit.WebSettings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DeviceInfo(
    val deviceId: String,
    val gaId: String,
    val deviceType: String,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val deviceModel: String,
    val osVersion: String,
    val language: String,
    val region: String,
    val isReinstall: Boolean,
    val userAgent: String
)

class DeviceInfoHelper(private val context: Context) {
    fun getDeviceInfo(callback: (DeviceInfo) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val info = getDeviceInfo()
            withContext(Dispatchers.Main) {
                callback(info)
            }
        }
    }

    @SuppressLint("NewApi", "HardwareIds")
    suspend fun getDeviceInfo(): DeviceInfo = withContext(Dispatchers.IO) {
        val gaId = try {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: ""
        } catch (e: Exception) {
            ""
        }

        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val deviceType = "AOS"

        val pm = context.packageManager
        val packageInfo = pm.getPackageInfo(context.packageName, 0)
        val packageName = packageInfo.packageName
        val versionCode = getLongVersionCode(packageInfo)
        val versionName = packageInfo.versionName ?: ""
        val firstInstallTime = packageInfo.firstInstallTime
        val lastUpdateTime = packageInfo.lastUpdateTime
        val deviceModel = Build.MODEL
        val osVersion = Build.VERSION.RELEASE

        val locale = context.resources.configuration.locales.get(0)
        val language = locale.language
        val region = locale.country

        val isReinstall = firstInstallTime != lastUpdateTime
        val userAgent = WebSettings.getDefaultUserAgent(context)

        return@withContext DeviceInfo(
            deviceId = androidId,
            gaId = gaId,
            deviceType = deviceType,
            packageName = packageName,
            versionCode = versionCode,
            versionName = versionName,
            firstInstallTime = firstInstallTime,
            lastUpdateTime = lastUpdateTime,
            deviceModel = deviceModel,
            osVersion = osVersion,
            language = language,
            region = region,
            isReinstall = isReinstall,
            userAgent = userAgent
        )
    }

    @Suppress("deprecation")
    private fun getLongVersionCode(info: PackageInfo): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            info.versionCode.toLong()
        }
    }
}
