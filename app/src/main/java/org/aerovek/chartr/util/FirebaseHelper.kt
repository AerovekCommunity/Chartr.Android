package org.aerovek.chartr.util

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

enum class FirebaseHelper(private val eventName: String) {
    HandledException("handled_exception");

    fun logEvent(payload: String) {
        val bundle = Bundle()

        // Firebase only allows 100 characters per event
        if (payload.length > 100) {
            println("[FirebaseHelper] PAYLOAD LENGTH WAS GREATER THAN 100, BAILING OUT!")
            return
        }

        bundle.putString(this.eventName, payload)
        Firebase.analytics.logEvent(this.eventName, bundle)
    }
}