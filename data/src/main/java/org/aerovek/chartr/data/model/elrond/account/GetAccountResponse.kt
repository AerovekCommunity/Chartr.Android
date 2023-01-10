package org.aerovek.chartr.data.model.elrond.account

import java.math.BigInteger

internal data class GetAccountResponse(
    val account: AccountData
) {
    internal data class AccountData(
        val nonce: Long,
        val balance: BigInteger,
        val code: String?,
        val username: String?
    )
}
