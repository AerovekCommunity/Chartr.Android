package org.aerovek.chartr.data.repository

import org.aerovek.chartr.data.model.plaid.LinkTokenCreateRequest
import org.aerovek.chartr.data.model.plaid.LinkTokenCreateResponse
import org.aerovek.chartr.data.network.AeroPlaidService
import java.lang.Exception

interface AeroPlaidRepository {
    suspend fun createLinkToken(clientName: String, userId: String): LinkTokenCreateResponse?
}

internal class AeroPlaidRepositoryImpl(
    private val aeroPlaidService: AeroPlaidService
): AeroPlaidRepository {
    override suspend fun createLinkToken(clientName: String, userId: String): LinkTokenCreateResponse? {
        val request = LinkTokenCreateRequest(clientName, userId)
        return try {
            aeroPlaidService.createLinkToken(request).data
        } catch (e: Exception) {
            println("ERROR trying to create link token - ${e.localizedMessage}")
            null
        }
    }

}