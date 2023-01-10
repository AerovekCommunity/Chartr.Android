package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.network.ElrondGatewayService
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.esdt.*
import org.aerovek.chartr.data.model.elrond.esdt.EsdtConstants
import org.aerovek.chartr.data.model.elrond.toEsdtProperties
import org.aerovek.chartr.data.model.elrond.toSpecialRoles
import org.aerovek.chartr.data.network.ElrondApiService
import org.aerovek.chartr.data.util.toHex

internal class EsdtRepositoryImpl(
    private val elrondApiService: ElrondApiService,
    private val elrondGatewayService: ElrondGatewayService,
    private val vmRepository: VmRepository
) : EsdtRepository {

    override fun getTokenDetails(tokenId: String): EsdtToken {
        return requireNotNull(elrondApiService.getTokenDetails(tokenId))
    }

    override fun getAllEsdtIssued(): List<String> {
        return requireNotNull(elrondGatewayService.getAllIssuedEsdt().data).tokens
    }

    override fun getEsdtProperties(tokenIdentifier: String): EsdtProperties {
        val response = vmRepository.queryContract(
            QueryContractInput(
                scAddress = EsdtConstants.ESDT_SC_ADDR.bech32,
                funcName = EsdtConstants.GET_TOKEN_PROPERTIES,
                args = listOf(tokenIdentifier.toHex())
            )
        )
        return response.toEsdtProperties()
    }

    override fun getEsdtSpecialRoles(tokenIdentifier: String): EsdtSpecialRoles? {
        val response = vmRepository.queryContract(
            QueryContractInput(
                scAddress = EsdtConstants.ESDT_SC_ADDR.bech32,
                funcName = EsdtConstants.GET_SPECIAL_ROLES,
                args = listOf(tokenIdentifier.toHex())
            )
        )
        return response.toSpecialRoles()
    }

}
