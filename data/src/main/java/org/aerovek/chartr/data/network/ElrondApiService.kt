package org.aerovek.chartr.data.network

import com.google.gson.Gson
import org.aerovek.chartr.data.model.elrond.account.AccountToken
import org.aerovek.chartr.data.model.elrond.esdt.EsdtToken
import org.aerovek.chartr.data.model.elrond.network.NetworkEconomics

interface ElrondApiService {
    fun getAccountTokenDetails(address: String, tokenId: String): AccountToken
    fun getNetworkEconomics(): NetworkEconomics
    fun getTokenDetails(tokenId: String): EsdtToken
    fun getTokensForAccount(address: String): List<AccountToken>
}

internal class ElrondApiServiceImpl(
    private val restClient: RestClient,
    private val gson: Gson
) : ElrondApiService {

    override fun getTokenDetails(tokenId: String): EsdtToken {
        return restClient.apiGet("tokens/$tokenId")
    }

    override fun getTokensForAccount(address: String): List<AccountToken> {
        return restClient.apiGet("accounts/$address/tokens")
    }

    override fun getAccountTokenDetails(address: String, tokenId: String): AccountToken {
        return restClient.apiGet("accounts/$address/tokens/$tokenId")
    }

    override fun getNetworkEconomics(): NetworkEconomics {
        return restClient.apiGet("economics")
    }
}