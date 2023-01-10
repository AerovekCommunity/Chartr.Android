package org.aerovek.chartr.data.model.elrond.esdt

import org.aerovek.chartr.data.model.elrond.address.Address
import java.math.BigInteger

object EsdtConstants {

    const val ESDT_MANAGEMENT_GAS_LIMIT = 60000000L
    val ESDT_TRANSACTION_VALUE = BigInteger.ZERO
    val ESDT_SC_ADDR =
        Address.fromBech32("erd1qqqqqqqqqqqqqqqpqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqzllls8a5w6u")

    val AERO_IMAGE_URL = "https://media.elrond.com/tokens/asset/AERO-458bbf/logo.png"
    val EGLD_IMAGE_URL = "https://elrond.com/assets/images/favicon/android-icon-192x192.png"

    const val GET_TOKEN_PROPERTIES = "getTokenProperties"
    const val GET_SPECIAL_ROLES = "getSpecialRoles"

    const val MARS_TOKEN_ID_DEV = "MARS-adff2c"
    const val AERO_TOKEN_ID_MAIN = "AERO-458bbf"
    const val AERO_TOKEN_ID_DEV = "AERO-7e99d6"

}
