package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.toDomain
import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.account.AccountToken
import org.aerovek.chartr.data.network.ElrondApiService
import org.aerovek.chartr.data.network.ElrondGatewayService
import java.io.IOException
import java.math.BigInteger

internal class AccountRepositoryImpl(
    private val elrondGatewayService: ElrondGatewayService,
    private val elrondApiService: ElrondApiService
) : AccountRepository {

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.AddressException::class
    )
    override fun getAccount(address: Address): Account {
        val response = elrondGatewayService.getAccount(address)
        val payload = requireNotNull(response.data).account
        return payload.toDomain(address)
    }

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.AddressException::class
    )
    override fun getAddressNonce(address: Address): Long {
        val response = elrondGatewayService.getAddressNonce(address)
        return requireNotNull(response.data).nonce
    }

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.AddressException::class
    )
    override fun getAddressBalance(address: Address): BigInteger {
        val response = elrondGatewayService.getAddressBalance(address)
        return requireNotNull(response.data).balance
    }

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class
    )
    override fun getAccountTokenDetails(bech32Address: String, tokenId: String): AccountToken {
        return elrondApiService.getAccountTokenDetails(bech32Address, tokenId)
    }

    @Throws(
        IOException::class,
        ElrondException.ProxyRequestException::class
    )
    override fun getTokensForAccount(bech32Address: String): List<AccountToken> {
        return elrondApiService.getTokensForAccount(bech32Address)
    }
}
