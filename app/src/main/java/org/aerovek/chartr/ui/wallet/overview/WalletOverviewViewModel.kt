package org.aerovek.chartr.ui.wallet.overview

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.cache.WalletCache
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.esdt.EsdtConstants
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.data.repository.elrond.ElrondNetworkRepository
import org.aerovek.chartr.data.repository.elrond.EsdtRepository
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.formatTokenBalance
import java.math.BigInteger

class WalletOverviewViewModel(
    app: Application,
    private val dispatcherProvider: DispatcherProvider,
    private val esdtRepository: EsdtRepository,
    private val accountRepository: AccountRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkRepository: ElrondNetworkRepository,
    private val environmentRepository: EnvironmentRepository
) : BaseViewModel(app) {
    val egldBalanceDisplay = MutableLiveData("0")
    val egldUsdBalance = MutableLiveData("$0.00")
    val aeroBalanceDisplay = MutableLiveData("0")
    val aeroUsdBalance = MutableLiveData("$0.00")
    val showLoading = MutableLiveData(true)

    val aeroImageUrl = MutableLiveData(EsdtConstants.AERO_IMAGE_URL)
    val egldImageUrl = MutableLiveData(EsdtConstants.EGLD_IMAGE_URL)

    val refreshingComplete = LiveEvent<Unit>()

    private var egldBalance = BigInteger.ZERO
    private var aeroBalance = BigInteger.ZERO

    init {
        updateBalance(false)
    }

    fun updateBalance(retrieveFromBackend: Boolean) {
        viewModelScope.launch(dispatcherProvider.IO) {
            val address = Address.fromBech32(sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, null)!!)
            try {
                val account = if (WalletCache.walletAccount == null || retrieveFromBackend) {
                    accountRepository.getAccount(address)
                } else {
                    WalletCache.walletAccount!!
                }

                // Update cache in case we got new data
                WalletCache.walletAccount = account

                val economics = if (WalletCache.networkEconomics == null || retrieveFromBackend) {
                    networkRepository.getNetworkEconomics()
                } else {
                    WalletCache.networkEconomics!!
                }
                WalletCache.networkEconomics = economics

                val accountTokenDetails = if (WalletCache.accountTokenDetails == null || retrieveFromBackend) {
                    accountRepository.getAccountTokenDetails(address.bech32, environmentRepository.selectedElrondEnvironment.aeroTokenId)
                } else {
                    WalletCache.accountTokenDetails!!
                }
                WalletCache.accountTokenDetails = accountTokenDetails

                val tokenDetails = if (WalletCache.aeroDetails == null || retrieveFromBackend) {
                    esdtRepository.getTokenDetails(environmentRepository.selectedElrondEnvironment.aeroTokenId)
                } else {
                    WalletCache.aeroDetails!!
                }
                WalletCache.aeroDetails = tokenDetails

                val tokenPrice = if (tokenDetails.price > 0) {
                    tokenDetails.price
                } else {
                    0.08
                }

                egldBalance = account.balance
                val egldBalanceFormatted = account.balance.toString().formatTokenBalance(4)
                egldBalanceDisplay.postValue(egldBalanceFormatted)

                aeroBalance = accountTokenDetails.balance?.toBigInteger() ?: BigInteger.ZERO
                val aeroBalanceFormatted = accountTokenDetails.balance?.formatTokenBalance(4) ?: "0"
                aeroBalanceDisplay.postValue(aeroBalanceFormatted)

                val egldUsd = getUsdBalance(egldBalanceFormatted, economics.price)
                val aeroUsd = getUsdBalance(aeroBalanceFormatted, tokenPrice)

                egldUsdBalance.postValue(egldUsd)
                aeroUsdBalance.postValue(aeroUsd)
                refreshingComplete.postValue(Unit)
                showLoading.postValue(false)
            } catch (e: Exception) {
                aeroBalanceDisplay.postValue("0")
                egldBalanceDisplay.postValue("0")
                showLoading.postValue(false)
                refreshingComplete.postValue(Unit)
            }
        }
    }

    private fun getUsdBalance(amount: String, price: Double): String {
        return try {
            "$${String.format("%.2f", amount.toBigDecimal() * price.toBigDecimal())}"
        } catch (e: java.lang.Exception) {
            "$0.00"
        }
    }
}