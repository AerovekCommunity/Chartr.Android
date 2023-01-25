package org.aerovek.chartr.data.model.elrond.network

import java.math.BigInteger

data class NetworkEconomics(
    val totalSupply: BigInteger,
    val circulatingSupply: BigInteger,
    val staked: BigInteger,
    val price: Double,
    val marketCap: BigInteger,
    val apr: Double,
    val topUpApr: Double,
    val baseApr: Double
) {
    // keep it to allow companion extension
    companion object
}
