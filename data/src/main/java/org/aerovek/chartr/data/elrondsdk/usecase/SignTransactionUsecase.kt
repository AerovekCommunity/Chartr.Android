package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
internal class SignTransactionUsecase {

    @Throws(ElrondException.CannotSignTransactionException::class)
    fun execute(transaction: Transaction, wallet: Wallet) = try {
        transaction.copy(signature = wallet.sign(transaction.serialize()))
    } catch (error: ElrondException.CannotSerializeTransactionException) {
        throw ElrondException.CannotSignTransactionException()
    }
}
