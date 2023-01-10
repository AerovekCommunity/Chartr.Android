package org.aerovek.chartr.data.network

import org.aerovek.chartr.data.model.elrond.esdt.EsdtConstants

/**
 * The chosen build variant will ultimately dictate which URLs are currently active.
 * If 'productionRelease' is the selected build variant, MainNet will be used,
 * all other build variants will use the DevNet object.
 * The scAddress should be the smart contract address that stores account information.
 * Refer to the createAccount logic to see how that works.
 */
sealed class ElrondNetwork(
    open val apiUrl: String,
    open val gatewayUrl: String,
    open val aeroTokenId: String,
    open val scAddress: String) {

    object MainNet : ElrondNetwork(
        apiUrl = "https://api.elrond.com",
        gatewayUrl = "https://gateway.elrond.com",
        aeroTokenId = EsdtConstants.AERO_TOKEN_ID_MAIN,
        scAddress = "")

    object DevNet : ElrondNetwork(
        apiUrl = "https://devnet-api.elrond.com",
        gatewayUrl = "https://devnet-gateway.elrond.com",
        aeroTokenId = EsdtConstants.MARS_TOKEN_ID_DEV,
        scAddress = "erd1qqqqqqqqqqqqqpgq0vra4223q6kwmlzrdtrcjwwrmawm0j79v8yqpygz0l")

    //object TestNet : ElrondNetwork("https://testnet-api.elrond.com", "https://testnet-gateway.elrond.com")

    data class Custom(
        override val apiUrl: String,
        override val gatewayUrl: String,
        override val aeroTokenId: String,
        override val scAddress: String) : ElrondNetwork(apiUrl, gatewayUrl, aeroTokenId, scAddress)
}