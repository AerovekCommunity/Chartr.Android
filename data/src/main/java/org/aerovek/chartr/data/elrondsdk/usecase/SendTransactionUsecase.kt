package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.repository.elrond.TransactionRepository
import java.io.IOException

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class SendTransactionUsecase internal constructor(
    private val signTransactionUsecase: SignTransactionUsecase,
    private val transactionRepository: TransactionRepository
) {
    @Throws(
        ElrondException.CannotSignTransactionException::class, IOException::class,
        ElrondException.ProxyRequestException::class,
        ElrondException.CannotSerializeTransactionException::class
    )
    fun execute(transaction: Transaction, wallet: Wallet): Transaction {
        val signedTransaction = when {
            transaction.isSigned -> transaction
            else -> signTransactionUsecase.execute(transaction, wallet)
        }
        return transactionRepository.sendTransaction(signedTransaction, wallet).let { sentTransaction ->
            signedTransaction.copy(txHash = "")
        }
    }

}
