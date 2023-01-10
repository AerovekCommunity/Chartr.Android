package org.aerovek.chartr.data.network

import com.google.gson.Gson
import org.aerovek.chartr.data.model.ResponseBase
import org.aerovek.chartr.data.model.primetrust.PTCreateUserData
import org.aerovek.chartr.data.model.primetrust.PTCreateUserResponseData

interface PrimeTrustService {
    suspend fun createUser(data: PTCreateUserData): ResponseBase<PTCreateUserResponseData>
}

internal class PrimeTrustServiceImpl(
    private val restClient: RestClient,
    private val gson: Gson
): PrimeTrustService {
    override suspend fun createUser(data: PTCreateUserData): ResponseBase<PTCreateUserResponseData> {
        return restClient.gatewayPost("v2/users", gson.toJson(data))
    }
}
