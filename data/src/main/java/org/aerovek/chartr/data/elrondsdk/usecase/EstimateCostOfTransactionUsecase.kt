package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.repository.elrond.TransactionRepository

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class EstimateCostOfTransactionUsecase internal constructor(private val transactionRepository: TransactionRepository) {

    fun execute(transaction: Transaction) =
        transactionRepository.estimateCostOfTransaction(transaction)
}
