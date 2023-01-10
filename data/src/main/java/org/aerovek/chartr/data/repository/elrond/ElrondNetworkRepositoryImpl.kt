package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.model.elrond.toDomain
import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.network.NetworkEconomics
import org.aerovek.chartr.data.network.ElrondApiService
import org.aerovek.chartr.data.network.ElrondGatewayService
import java.io.IOException


internal class ElrondNetworkRepositoryImpl(
    private val elrondGatewayService: ElrondGatewayService,
    private val elrondApiService: ElrondApiService
) : ElrondNetworkRepository {

    @Throws(IOException::class, ElrondException.ProxyRequestException::class)
    override fun getNetworkConfig(): NetworkConfig {
        val response = elrondGatewayService.getNetworkConfig()
        return requireNotNull(response.data).config.toDomain()
    }

    @Throws(IOException::class, ElrondException.ProxyRequestException::class)
    override fun getNetworkEconomics(): NetworkEconomics {
        return elrondApiService.getNetworkEconomics()
    }
}