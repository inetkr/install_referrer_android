package com.inetkr.install_referrer

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest

internal class IntegrityHelper(context: Context) {

    private val integrityManager = IntegrityManagerFactory.create(context)

    fun requestIntegrityToken(nonce: String, callback: (String?, String?) -> Unit) {
        val request = IntegrityTokenRequest.builder()
            .setNonce(nonce)
            .setCloudProjectNumber(508304446719)
            .build()

        integrityManager.requestIntegrityToken(request)
            .addOnSuccessListener { response ->
                callback(response.token(), null)
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }
}
