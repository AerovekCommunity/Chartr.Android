package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.util.toHexBytes

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class RegisterDnsUsecase internal constructor(
    private val sendTransactionUsecase: SendTransactionUsecase,
    private val computeDnsAddressUsecase: ComputeDnsAddressUsecase,
    private val getRegistrationCostUsecase: GetDnsRegistrationCostUsecase
) {

    fun execute(
        username: String,
        account: Account,
        wallet: Wallet,
        networkConfig: NetworkConfig,
        gasPrice: Long,
        gasLimit: Long
    ): Transaction {
        val dnsAddress = computeDnsAddressUsecase.execute(username)
        val encodedName = username.toHexBytes()
        val transaction = Transaction(
            nonce = account.nonce,
            receiver = dnsAddress,
            sender = account.address,
            chainID = networkConfig.chainID,
            gasPrice = gasPrice,
            gasLimit = gasLimit,
            value = getRegistrationCostUsecase.execute(dnsAddress),
            data = "register@${String(encodedName)}"
        )
        return sendTransactionUsecase.execute(transaction, wallet)
    }

}
