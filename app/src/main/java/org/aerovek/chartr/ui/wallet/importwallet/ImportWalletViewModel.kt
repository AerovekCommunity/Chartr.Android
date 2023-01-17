/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.aerovek.chartr.ui.wallet.importwallet

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.data.repository.elrond.VmRepository
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.ui.MainActivityViewModel
import org.aerovek.chartr.ui.adapterItems.StringItemTapCallback
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.NavigationEvent
import java.lang.Exception

class ImportWalletViewModel(
    app: Application,
    private val accountRepository: AccountRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val sharedPreferences: SharedPreferences,
    private val environmentRepository: EnvironmentRepository,
    private val vmRepository: VmRepository,
    private val mainActivityViewModel: MainActivityViewModel
    ) : BaseViewModel(app), StringItemTapCallback {

    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveEvent<NavigationEvent> = _navigationEvent

    val typeaheadItemTapped = LiveEvent<String>()
    val importSuccessful = LiveEvent<Unit>()
    val showConfirmation = LiveEvent<Unit>()
    val backPressed = LiveEvent<Unit>()
    val showProgressBar = LiveEvent<Boolean>()
    lateinit var walletWords: List<String>

    fun setWords(words: List<String>) {
        walletWords = words
    }

    fun createWalletAndNavigate() {
        showProgressBar.postValue(true)
        viewModelScope.launch(dispatcherProvider.IO) {
            try {
                val wallet = Wallet.createFromMnemonic(walletWords.joinToString(" "), 0)

                val account = (async {
                    accountRepository.getAccount(Address.fromHex(wallet.publicKeyHex))
                }).await()

                val address = account.address.bech32
                println("ACCOUNT ADDRESS: $address")
                println("ACCOUNT BALANCE: ${account.balance}")
                println("-----> WALLET PRIVATE KEY: ${wallet.privateKeyHex}")
                println("-----> WALLET PUBLIC KEY: ${wallet.publicKeyHex}")
                println("-----> WALLET WORDS: ${walletWords.mapIndexed { idx, word -> "${idx + 1}-$word"}}")

                // Try retrieving existing account from smart contract
                val scAddress = environmentRepository.selectedElrondEnvironment.scAddress
                val queryContractInput = QueryContractInput(
                    scAddress = scAddress,
                    funcName = "getAccount",
                    args = listOf(wallet.publicKeyHex),
                    caller = address,
                    value = "0"
                )

                val chartrAccount = vmRepository.getChartrAccount(queryContractInput)

                // Finally copy the temp pin to the real pin. The temp pin was there in case
                // the user force quits the app before completely finishing account setup
                sharedPreferences.edit {
                    putString(AppConstants.UserPrefsKeys.USER_PIN, sharedPreferences.getString(AppConstants.UserPrefsKeys.USER_TEMP_PIN, null))
                    putString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, address)
                    putString(AppConstants.UserPrefsKeys.WALLET_PRIVATE_KEY, wallet.privateKeyHex)
                    putString(AppConstants.UserPrefsKeys.WALLET_PUBLIC_KEY, wallet.publicKeyHex)
                    putString(AppConstants.UserPrefsKeys.WALLET_WORDS, walletWords.joinToString(" "))
                    // Now clear the temp PIN
                    remove(AppConstants.UserPrefsKeys.USER_TEMP_PIN)

                    if (chartrAccount != null) {
                        putString(AppConstants.UserPrefsKeys.ACCOUNT_TYPE, chartrAccount.accountType)
                    }

                    apply()
                }

                // If account was found, let observer know so we can show the account bottom nav menu item
                if (chartrAccount != null) {
                    mainActivityViewModel.postAccountCreated()
                }

                importSuccessful.postValue(Unit)

            } catch (e: Exception) {
                println("ERROR RECOVERING WALLET")
                showProgressBar.postValue(false)
            }
        }
    }

    fun backspaceImageClicked() {
        backPressed.postValue(Unit)
    }

    override fun onItemTapped(item: String) {
        typeaheadItemTapped.postValue(item)
    }
}