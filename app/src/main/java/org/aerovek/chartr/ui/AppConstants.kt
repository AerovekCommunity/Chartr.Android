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
package org.aerovek.chartr.ui

object AppConstants {
    const val PLAID_CLIENT_NAME = "Chartr"
    const val ONBOARDING_COMPLETE_REQUEST_CODE = 100
    const val PASSCODE_BOTTOMSHEET_TAG_NEW = "new_passcode"
    const val ACCOUNT_TYPE_BUSINESS_VALUE = "business"
    const val ACCOUNT_TYPE_PERSONAL_VALUE = "personal"
    val FLIGHT_CATEGORY_LIST = listOf("", "Angel Flight Services", "Agriculture", "Cargo", "General Aviation", "Medical", "Private Charter")
    const val PRIVACY_POLICY_URL = "https://www.aerovek.io/utility-pages/privacy-policy"
    const val ACCOUNT_RETRIEVAL_ADDRESS = "erd1nkxen2p0cumk65rdmggtxng3j7j3qt0x8wjmyrr5yfpvr6v2t5rq9hqnjw"

    object UserPrefsKeys {
        const val USER_PIN = "user_pin"
        const val USER_TEMP_PIN = "user_temp_pin"
        const val WALLET_WORDS = "wallet_words"
        const val WALLET_ADDRESS = "wallet_address"
        const val WALLET_PRIVATE_KEY = "wallet_private_key"
        const val WALLET_PUBLIC_KEY = "wallet_public_key"
        const val ACCOUNT_TYPE = "account_type"
        const val SHOW_WELCOME_SCREEN = "show_welcome_screen"
    }
}