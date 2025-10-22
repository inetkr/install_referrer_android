package com.inetkr.install_referrer

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails

internal class ReferrerHelper(private val context: Context) {

    fun getReferrer(callback: (String?) -> Unit) {
        val referrerClient = InstallReferrerClient.newBuilder(context).build()

        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response: ReferrerDetails = referrerClient.installReferrer
                            val referrerUrl = response.installReferrer
                            val clickTime = response.referrerClickTimestampSeconds
                            val installTime = response.installBeginTimestampSeconds
                            val googlePlayInstant = response.googlePlayInstantParam

                            Log.d("ReferrerHelper", "✅ Referrer: $referrerUrl")
                            Log.d("ReferrerHelper", "Click time: $clickTime")
                            Log.d("ReferrerHelper", "Install time: $installTime")
                            Log.d("ReferrerHelper", "Instant app: $googlePlayInstant")

                            callback(referrerUrl)
                        } catch (e: Exception) {
                            Log.e("ReferrerHelper", "Error: ${e.message}")
                            callback(null)
                        } finally {
                            referrerClient.endConnection()
                        }
                    }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED ->
                        Log.w("ReferrerHelper", "❌ FEATURE_NOT_SUPPORTED")

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE ->
                        Log.w("ReferrerHelper", "❌ SERVICE_UNAVAILABLE")

                    else ->
                        Log.w("ReferrerHelper", "❌ Unknown response: $responseCode")
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.w("ReferrerHelper", "Service disconnected, retry later")
            }
        })
    }
}
