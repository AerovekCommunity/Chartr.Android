package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.network.ElrondGatewayService
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.toDomain
import org.aerovek.chartr.data.model.elrond.transaction.SimulateTransactionInfo
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.transaction.TransactionInfo
import org.aerovek.chartr.data.model.elrond.transaction.TransactionOnNetwork
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import java.io.IOException

internal class TransactionRepositoryImpl(
    private val elrondService: ElrondGatewayService
) : TransactionRepository {
    @Throws(
        IOException::class,
        ElrondException.CannotSerializeTransactionException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.CannotSignTransactionException::class
    )
    override fun sendTransaction(transaction: Transaction, wallet: Wallet): Transaction {
        val signedTransaction = if (transaction.isSigned) {
            transaction
        } else {
            try {
                transaction.copy(signature = wallet.sign(transaction.serialize()))
            } catch (error: ElrondException.CannotSerializeTransactionException) {
                throw ElrondException.CannotSignTransactionException()
            }
        }

        val sentTransaction = elrondService.sendTransaction(signedTransaction)
        return signedTransaction.copy(txHash = sentTransaction.data?.txHash!!)
    }

    @Throws(
        IOException::class,
        ElrondException.CannotSerializeTransactionException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.CannotSignTransactionException::class
    )
    override fun simulateTransaction(transaction: Transaction, wallet: Wallet ): SimulateTransactionInfo {
        val signedTransaction = if (transaction.isSigned) {
            transaction
        } else {
            try {
                transaction.copy(signature = wallet.sign(transaction.serialize()))
            } catch (error: ElrondException.CannotSerializeTransactionException) {
                throw ElrondException.CannotSignTransactionException()
            }
        }

        val result = elrondService.simulateTransaction(signedTransaction)
        return requireNotNull(result.data).toDomain()
    }

    override fun getTransactions(address: Address): List<TransactionOnNetwork> {
        val response = elrondService.getAddressTransactions(address)
        return response.data?.transactions?.map { it.toDomain() } ?: emptyList()
    }

    override fun estimateCostOfTransaction(transaction: Transaction): String {
        val response = elrondService.estimateCostOfTransaction(transaction)
        return requireNotNull(response.data).txGasUnits
    }

    override fun getTransactionInfo(txHash: String, sender: Address?, withResults: Boolean): TransactionInfo {
        val response = elrondService.getTransactionInfo(txHash, sender, withResults)
        return requireNotNull(response.data).transaction.toDomain(txHash)
    }

    override fun getTransactionStatus(txHash: String, sender: Address?): String {
        val response = elrondService.getTransactionStatus(txHash, sender)
        return requireNotNull(response.data).status
    }
}
