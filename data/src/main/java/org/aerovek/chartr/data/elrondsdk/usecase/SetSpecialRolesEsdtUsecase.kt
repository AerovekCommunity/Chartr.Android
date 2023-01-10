package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.esdt.EsdtConstants.ESDT_MANAGEMENT_GAS_LIMIT
import org.aerovek.chartr.data.model.elrond.esdt.EsdtConstants.ESDT_TRANSACTION_VALUE
import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.esdt.EsdtConstants
import org.aerovek.chartr.data.model.elrond.esdt.EsdtSpecialRole
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.util.toHex

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class SetSpecialRolesEsdtUsecase internal constructor(
    private val sendTransactionUsecase: SendTransactionUsecase
) {

    fun execute(
        account: Account,
        wallet: Wallet,
        networkConfig: NetworkConfig,
        gasPrice: Long,
        tokenIdentifier: String,
        addressToUpdate: Address,
        specialRoles: List<EsdtSpecialRole>,
        action: Action
    ): Transaction {
        if (specialRoles.isEmpty()) {
            throw IllegalArgumentException("specialRoles should not be empty")
        }
        val args = mutableListOf(
            tokenIdentifier.toHex(),
            addressToUpdate.hex
        ).apply {
            addAll(specialRoles.map { it.name.toHex() })
        }
        return sendTransactionUsecase.execute(
            Transaction(
                sender = account.address,
                receiver = EsdtConstants.ESDT_SC_ADDR,
                value = ESDT_TRANSACTION_VALUE,
                gasLimit = ESDT_MANAGEMENT_GAS_LIMIT,
                gasPrice = gasPrice,
                data = args.fold(action.serializedValue) { it1, it2 -> "$it1@$it2" },
                chainID = networkConfig.chainID,
                nonce = account.nonce
            ),
            wallet
        )
    }

    enum class Action(val serializedValue: String) {
        Set("setSpecialRole"),
        Unset("unSetSpecialRole")
    }

}
