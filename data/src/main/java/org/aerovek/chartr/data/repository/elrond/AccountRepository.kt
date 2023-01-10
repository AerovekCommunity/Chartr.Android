package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.account.AccountToken
import org.aerovek.chartr.data.model.elrond.address.Address
import java.io.IOException
import java.math.BigInteger

interface AccountRepository {

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.AddressException::class
    )
    fun getAccount(address: Address): Account

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.AddressException::class
    )
    fun getAddressNonce(address: Address): Long

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.AddressException::class
    )
    fun getAddressBalance(address: Address): BigInteger

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class
    )
    fun getAccountTokenDetails(bech32Address: String, tokenId: String): AccountToken

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class
    )
    fun getTokensForAccount(bech32Address: String): List<AccountToken>
}
