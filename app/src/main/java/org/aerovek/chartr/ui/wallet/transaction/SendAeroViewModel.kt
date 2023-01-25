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
package org.aerovek.chartr.ui.wallet.transaction

import android.app.Application
import android.content.SharedPreferences
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.cache.WalletCache
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.network.ElrondNetwork
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.data.repository.elrond.ElrondNetworkRepository
import org.aerovek.chartr.data.repository.elrond.EsdtRepository
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.*
import java.lang.Exception
import java.net.SocketTimeoutException

class SendAeroViewModel(
    app: Application,
    networkRepository: ElrondNetworkRepository,
    private val dispatcherProvider: DispatcherProvider,
    esdtRepository: EsdtRepository,
    private val accountRepository: AccountRepository,
    sharedPreferences: SharedPreferences,
    private val environmentRepository: EnvironmentRepository
    ) : BaseViewModel(app) {

    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    val recipientAddressText = MutableLiveData("")
    val amountText = MutableLiveData("")
    val usdAmountText = MutableLiveData("")
    val showLoadingView = MutableLiveData(true)
    val showInvalidAddress = LiveEvent<Unit>()
    val scannerTapped = LiveEvent<Unit>()
    val sendAeroChecked = MutableLiveData(true)
    val sendEgldChecked = MutableLiveData(false)

    private var aeroBalanceText: String? = null
    var aeroPrice = 0.0

    private var egldBalanceText: String? = null
    var egldPrice = 0.0

    init {
        viewModelScope.launch(dispatcherProvider.IO) {
            try {

                val economics = if (WalletCache.networkEconomics == null) {
                    networkRepository.getNetworkEconomics()
                } else {
                    WalletCache.networkEconomics!!
                }
                WalletCache.networkEconomics = economics

                println("TOTAL SUPPLY = ${economics.totalSupply}")
                println("CIRCULATING SUPPLY =  ${economics.circulatingSupply}")
                println("STAKED =  ${economics.staked}")
                println("PRICE =  ${economics.price}")
                println("MARKET CAP = ${economics.marketCap}")
                println("APR = ${economics.apr}")
                println("TOP UP APR = ${economics.topUpApr}")
                println("BASE APR = ${economics.baseApr}")


                val address = Address.fromBech32(sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, null)!!)

                val accountDetails = if (WalletCache.walletAccount == null) {
                    accountRepository.getAccount(address)
                } else {
                    WalletCache.walletAccount!!
                }
                WalletCache.walletAccount = accountDetails

                val accountTokenDetails = if (WalletCache.accountTokenDetails == null) {
                    accountRepository.getAccountTokenDetails(address.bech32, environmentRepository.selectedElrondEnvironment.aeroTokenId)
                } else {
                    WalletCache.accountTokenDetails!!
                }
                // Update cache in case we retrieved from backend
                WalletCache.accountTokenDetails = accountTokenDetails

                val aeroTokenDetails = if (WalletCache.aeroDetails == null) {
                    esdtRepository.getTokenDetails(environmentRepository.selectedElrondEnvironment.aeroTokenId)
                } else {
                    WalletCache.aeroDetails!!
                }
                WalletCache.aeroDetails = aeroTokenDetails

                aeroPrice = if (environmentRepository.selectedElrondEnvironment == ElrondNetwork.DevNet) {
                    0.08
                } else {
                    aeroTokenDetails.price
                }

                aeroBalanceText = accountTokenDetails.balance
                egldBalanceText = accountDetails.balance.toString()
                egldPrice = economics.price

                showLoadingView.postValue(false)
            } catch (ste: SocketTimeoutException) {
                ste.message?.let {
                    FirebaseHelper.HandledException.logEvent("Socket timeout exception")
                }
            } catch (e: Exception) {
                e.message?.let {
                    if (it.length > 100) {
                        FirebaseHelper.HandledException.logEvent(it.substring(IntRange(0, 99)))
                    } else {
                        FirebaseHelper.HandledException.logEvent(it)
                    }
                }
            }
        }
    }

    fun sendAeroCheckboxChanged(checkbox: CompoundButton, isChecked: Boolean) {
        sendAeroChecked.postValue(isChecked)
    }

    fun sendEgldCheckboxChanged(checkbox: CompoundButton, isChecked: Boolean) {
        sendEgldChecked.postValue(isChecked)
    }

    fun scannerImageTapped() {
        scannerTapped.postValue(Unit)
    }

    fun maxButtonTapped() {
        if (sendEgldChecked.value == true) {
            egldBalanceText?.let {
                amountText.postValue(it.formatTokenBalance())
            }
        } else {
            aeroBalanceText?.let {
                amountText.postValue(it.formatTokenBalance())
            }
        }
    }
}