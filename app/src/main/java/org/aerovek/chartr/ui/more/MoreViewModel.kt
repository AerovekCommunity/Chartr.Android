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
package org.aerovek.chartr.ui.more

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.ui.adapterItems.MoreItemTapCallbacks
import org.aerovek.chartr.ui.adapterItems.viewmodels.MoreItemViewModel
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.NavigationEvent
import java.math.BigInteger

class MoreViewModel(
    app: Application,
    private val sharedPreferences: SharedPreferences,
    accountRepository: AccountRepository,
    dispatcherProvider: DispatcherProvider
    ) : BaseViewModel(app), MoreItemTapCallbacks {

    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent
    val showNoBalanceMessage = LiveEvent<Unit>()
    val showLoading = MutableLiveData(true)
    val privacyPolicyTap = LiveEvent<Unit>()

    private var egldBalance: BigInteger? = null

    init {
        viewModelScope.launch(dispatcherProvider.IO) {
            // Only check balance if they already created a wallet
            if (sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN)) {
                val address = Address.fromBech32(
                    sharedPreferences.getString(
                        AppConstants.UserPrefsKeys.WALLET_ADDRESS,
                        null
                    )!!
                )
                egldBalance = try {
                    val account = accountRepository.getAccount(address)
                    account.balance
                } catch (e: Exception) {
                    BigInteger.ZERO
                }
                showLoading.postValue(false)
            }
        }
    }

    override fun onMoreItemTapped(vm: MoreItemViewModel) {
        when (vm.type) {
            MoreItemType.CreateAccount -> {
                if (egldBalance != null && egldBalance == BigInteger.ZERO) {
                    showNoBalanceMessage.postValue(Unit)
                    return
                }

                if (sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN)) {
                    _navigationEvent.postValue(NavigationEvent.Directions(
                        MoreFragmentDirections.actionMoreFragmentToCreateAccountFragment()))
                } else {
                    _navigationEvent.postValue(NavigationEvent.Directions(
                        MoreFragmentDirections.actionMoreFragmentToCreateWalletFragment()))
                }

                println("!!! CREATE ACCOUNT ITEM TAPPED")
            }
            MoreItemType.Settings -> {
                println("!!! SETTINGS ITEM TAPPED")
            }
            MoreItemType.PrivacyPolicy -> {
                privacyPolicyTap.postValue(Unit)
            }
        }
    }
}