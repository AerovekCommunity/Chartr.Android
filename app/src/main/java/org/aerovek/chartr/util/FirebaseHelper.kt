/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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