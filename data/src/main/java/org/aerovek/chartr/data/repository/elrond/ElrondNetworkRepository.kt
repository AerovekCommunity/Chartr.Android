package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.network.NetworkEconomics
import java.io.IOException

interface ElrondNetworkRepository {

    @Throws(IOException::class, ElrondException.ProxyRequestException::class)
    fun getNetworkConfig(): NetworkConfig

    @Throws(IOException::class, ElrondException.ProxyRequestException::class)
    fun getNetworkEconomics(): NetworkEconomics

}