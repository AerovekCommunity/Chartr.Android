package org.aerovek.chartr.data.network

import com.google.gson.Gson
import org.aerovek.chartr.data.model.ResponseBase
import org.aerovek.chartr.data.model.plaid.LinkTokenCreateRequest
import org.aerovek.chartr.data.model.plaid.LinkTokenCreateResponse

internal interface AeroPlaidService {
    suspend fun createLinkToken(
        linkTokenCreateRequest: LinkTokenCreateRequest
    ): ResponseBase<LinkTokenCreateResponse>
}

internal class AeroPlaidServiceImpl(
    private val restClient: RestClient,
    private val gson: Gson
): AeroPlaidService {
    override suspend fun createLinkToken(
        linkTokenCreateRequest: LinkTokenCreateRequest
    ): ResponseBase<LinkTokenCreateResponse> {
        return restClient.gatewayPost("api/create_link_token", gson.toJson(linkTokenCreateRequest))
    }
}



