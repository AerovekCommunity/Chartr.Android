package org.aerovek.chartr.data.network

import com.google.gson.Gson
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.model.elrond.account.GetAccountResponse
import org.aerovek.chartr.data.model.elrond.address.GetAddressBalanceResponse
import org.aerovek.chartr.data.model.elrond.address.GetAddressNonceResponse
import org.aerovek.chartr.data.model.elrond.network.GetNetworkConfigResponse

import org.aerovek.chartr.data.model.ResponseBase
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.address.GetAddressTransactionsResponse
import org.aerovek.chartr.data.model.elrond.contract.QueryContractDigitResponse
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractResponse
import org.aerovek.chartr.data.model.elrond.contract.QueryContractStringResponse
import org.aerovek.chartr.data.model.elrond.esdt.GetAllEsdtResponse
import org.aerovek.chartr.data.model.elrond.esdt.GetAllIssuedEsdtResponse
import org.aerovek.chartr.data.model.elrond.esdt.GetEsdtBalanceResponse
import org.aerovek.chartr.data.model.elrond.transaction.*
import org.aerovek.chartr.data.model.elrond.transaction.EstimateCostOfTransactionResponse
import org.aerovek.chartr.data.model.elrond.transaction.GetTransactionInfoResponse
import org.aerovek.chartr.data.model.elrond.transaction.GetTransactionStatusResponse
import org.aerovek.chartr.data.model.elrond.transaction.SendTransactionResponse

internal interface ElrondGatewayService {
    fun getNetworkConfig(): ResponseBase<GetNetworkConfigResponse>
    fun getAccount(address: Address): ResponseBase<GetAccountResponse>
    fun getAddressNonce(address: Address): ResponseBase<GetAddressNonceResponse>
    fun getAddressBalance(address: Address): ResponseBase<GetAddressBalanceResponse>
    fun getAddressTransactions(address: Address): ResponseBase<GetAddressTransactionsResponse>
    fun sendTransaction(transaction: Transaction): ResponseBase<SendTransactionResponse>
    fun simulateTransaction(transaction: Transaction): ResponseBase<SimulateTransactionResponse>
    fun estimateCostOfTransaction(transaction: Transaction): ResponseBase<EstimateCostOfTransactionResponse>
    fun getTransactionInfo(txHash: String, sender: Address?, withResults: Boolean): ResponseBase<GetTransactionInfoResponse>
    fun getTransactionStatus(txHash: String, sender: Address?): ResponseBase<GetTransactionStatusResponse>
    fun queryContract(queryContractInput: QueryContractInput): ResponseBase<QueryContractResponse>
    fun queryContractHex(queryContractInput: QueryContractInput): ResponseBase<QueryContractStringResponse>
    fun queryContractString(queryContractInput: QueryContractInput): ResponseBase<QueryContractStringResponse>
    fun queryContractInt(queryContractInput: QueryContractInput): ResponseBase<QueryContractDigitResponse>
    fun getEsdtTokens(address: Address): ResponseBase<GetAllEsdtResponse>
    fun getEsdtBalance(address: Address, tokenIdentifier: String): ResponseBase<GetEsdtBalanceResponse>
    fun getAllIssuedEsdt(): ResponseBase<GetAllIssuedEsdtResponse>
}

internal class ElrondGatewayServiceImpl(
    private val environmentRepository: EnvironmentRepository,
    private val restClient: RestClient,
    private val gson: Gson
): ElrondGatewayService {

    override fun getNetworkConfig(): ResponseBase<GetNetworkConfigResponse> {
        return restClient.gatewayGet("network/config")
    }

    /** Addresses **/

    override fun getAccount(address: Address): ResponseBase<GetAccountResponse> {
        return restClient.gatewayGet("address/${address.bech32}")
    }

    override fun getAddressNonce(address: Address): ResponseBase<GetAddressNonceResponse> {
        return restClient.gatewayGet("address/${address.bech32}/nonce")
    }

    override fun getAddressBalance(address: Address): ResponseBase<GetAddressBalanceResponse> {
        return restClient.gatewayGet("address/${address.bech32}/balance")
    }

    override fun getAddressTransactions(address: Address): ResponseBase<GetAddressTransactionsResponse> {
        return restClient.gatewayGet("address/${address.bech32}/transactions")
    }

    /** Transactions **/

    override fun sendTransaction(transaction: Transaction): ResponseBase<SendTransactionResponse> {
        val requestJson = transaction.serialize()
        return restClient.gatewayPost("transaction/send", requestJson)
    }

    override fun simulateTransaction(transaction: Transaction): ResponseBase<SimulateTransactionResponse> {
        val requestJson = transaction.serialize()
        return restClient.gatewayPost("transaction/simulate", requestJson)
    }

    override fun estimateCostOfTransaction(transaction: Transaction): ResponseBase<EstimateCostOfTransactionResponse> {
        return restClient.gatewayPost("transaction/cost", transaction.serialize())
    }

    override fun getTransactionInfo(txHash: String, sender: Address?, withResults: Boolean): ResponseBase<GetTransactionInfoResponse> {
        val params = ArgFormatter().apply {
            addArg(sender) { "sender=${it.bech32}" }
            addArg(withResults) { "withResults=true" }
        }
        return restClient.gatewayGet("transaction/$txHash$params")
    }

    override fun getTransactionStatus(txHash: String, sender: Address?): ResponseBase<GetTransactionStatusResponse> {
        val senderAddress = when (sender){
            null -> ""
            else -> "?sender=${sender.bech32}"
        }
        return restClient.gatewayGet("transaction/$txHash/status$senderAddress")
    }

    /** VM **/

    // Compute Output of Pure Function
    override fun queryContract(queryContractInput: QueryContractInput): ResponseBase<QueryContractResponse> {
        return restClient.gatewayPost("vm-values/query", gson.toJson(queryContractInput))
    }

    override fun queryContractHex(queryContractInput: QueryContractInput): ResponseBase<QueryContractStringResponse> {
        return restClient.gatewayPost("vm-values/hex", gson.toJson(queryContractInput))
    }

    override fun queryContractString(queryContractInput: QueryContractInput): ResponseBase<QueryContractStringResponse> {
        return restClient.gatewayPost("vm-values/string", gson.toJson(queryContractInput))
    }

    override fun queryContractInt(queryContractInput: QueryContractInput): ResponseBase<QueryContractDigitResponse> {
        return restClient.gatewayPost("vm-values/int", gson.toJson(queryContractInput))
    }

    /** ESDT **/

    // Get all ESDT tokens for an address
    override fun getEsdtTokens(address: Address): ResponseBase<GetAllEsdtResponse> {
        return restClient.gatewayGet("address/${address.bech32}/esdt")
    }

    // Get balance for an address and an ESDT token
    override fun getEsdtBalance(address: Address, tokenIdentifier: String): ResponseBase<GetEsdtBalanceResponse> {
        return restClient.gatewayGet("address/${address.bech32}/esdt/$tokenIdentifier")
    }


    // Get all issued ESDT tokens
    override fun getAllIssuedEsdt(): ResponseBase<GetAllIssuedEsdtResponse> {
        return restClient.gatewayGet("network/esdts")
    }

    /** Private **/

    private class ArgFormatter {
        private var args = ""

        fun <T> addArg(arg: T?, formatArg: (T) -> String) {
            val prefix = when {
                args.isEmpty() -> "?"
                else -> "&"
            }
            args += when (arg){
                null -> ""
                else -> prefix + formatArg(arg)
            }
        }

        override fun toString(): String {
            return args
        }
    }
}
