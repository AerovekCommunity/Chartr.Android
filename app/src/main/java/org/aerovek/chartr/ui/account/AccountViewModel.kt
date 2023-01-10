package org.aerovek.chartr.ui.account

import android.app.Application
import android.content.SharedPreferences
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.model.ChartrAccount
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.repository.elrond.VmRepository
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel

class AccountViewModel(
    app: Application,
    sharedPreferences: SharedPreferences,
    environmentRepository: EnvironmentRepository,
    private val vmRepository: VmRepository
) : BaseViewModel(app) {
    private val senderAddress = sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, null) ?: ""
    private val scAddress = environmentRepository.selectedElrondEnvironment.scAddress
    private val wallet: Wallet = Wallet.createFromPrivateKey(
        sharedPreferences.getString(
            AppConstants.UserPrefsKeys.WALLET_PRIVATE_KEY,
            null
        ) ?: ""
    )

    suspend fun retrieveAccount(): ChartrAccount? {
        val queryContractInput = QueryContractInput(
            scAddress = scAddress,
            funcName = "getAccount",
            args = listOf(wallet.publicKeyHex),
            caller = senderAddress,
            value = "0"
        )
        return vmRepository.getChartrAccount(queryContractInput)
    }
}