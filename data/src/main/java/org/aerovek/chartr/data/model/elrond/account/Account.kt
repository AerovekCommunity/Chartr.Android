package org.aerovek.chartr.data.model.elrond.account

import org.aerovek.chartr.data.model.elrond.address.Address
import java.math.BigInteger

data class Account(
    val address: Address,
    val nonce: Long = 0,
    val balance: BigInteger = BigInteger.ZERO,
    val code: String? = null,
    val username: String? = null
) {
    // keep it to allow companion extension
    companion object
}