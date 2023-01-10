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