package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.util.ScUtils
import java.math.BigInteger

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class CallContractUsecase internal constructor(
    private val sendTransactionUsecase: SendTransactionUsecase,
) {

    // source:
    // https://github.com/ElrondNetwork/elrond-sdk/blob/576fdc4bc0fa713738d8556600f04e6377c7623f/erdpy/contracts.py#L62
    fun execute(
        account: Account,
        wallet: Wallet,
        networkConfig: NetworkConfig,
        gasPrice: Long,
        gasLimit: Long,
        contractAddress: Address,
        funcName: String,
        args: List<String> = emptyList(),
        value: BigInteger = BigInteger.ZERO,
    ): Transaction {
        val transaction = Transaction(
            nonce = account.nonce,
            receiver = contractAddress,
            sender = account.address,
            chainID = networkConfig.chainID,
            gasPrice = gasPrice,
            gasLimit = gasLimit,
            value = value,
            data = args.fold(funcName) { it1, it2 -> it1 + "@${ScUtils.prepareArgument(it2)}" }
        )
        return sendTransactionUsecase.execute(transaction, wallet)
    }

}
