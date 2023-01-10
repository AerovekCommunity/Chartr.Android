package org.aerovek.chartr.ui.splash

import android.app.Application
import android.content.SharedPreferences
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.cache.ChartrAccountsCache
import org.aerovek.chartr.data.cache.CountryCache
import org.aerovek.chartr.data.cache.MnemonicWordCache
import org.aerovek.chartr.data.cache.WalletCache
import org.aerovek.chartr.data.model.Country
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.data.repository.elrond.ElrondNetworkRepository
import org.aerovek.chartr.data.repository.elrond.EsdtRepository
import org.aerovek.chartr.data.repository.elrond.VmRepository
import org.aerovek.chartr.data.util.deserialize
import org.aerovek.chartr.data.util.toHex
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.ui.passcode.PassCodeDismissListener
import org.aerovek.chartr.ui.passcode.PassCodeFragment
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.NavigationEvent
import java.io.IOException

class SplashFragmentViewModel(
    private val app: Application,
    private val dispatcherProvider: DispatcherProvider,
    private val sharedPreferences: SharedPreferences,
    private val accountRepository: AccountRepository,
    private val networkRepository: ElrondNetworkRepository,
    private val environmentRepository: EnvironmentRepository,
    private val esdtRepository: EsdtRepository,
    private val vmRepository: VmRepository
) : BaseViewModel(app) {
    val startOnboarding = LiveEvent<Unit>()

    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    fun initView(fragmentManager: FragmentManager) {
        viewModelScope.launch(dispatcherProvider.IO) {
            warmCaches()

            val showWelcome = !sharedPreferences.contains(AppConstants.UserPrefsKeys.SHOW_WELCOME_SCREEN)

            // If first time launching app, show the welcome screen activity
            if (showWelcome) {
                startOnboarding.postValue(Unit)
                sharedPreferences.edit().putBoolean(AppConstants.UserPrefsKeys.SHOW_WELCOME_SCREEN, false).apply()
            } else if (sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN)) {
                val passcodeView = PassCodeFragment()
                passcodeView.setup(object : PassCodeDismissListener {
                    override fun onDismiss(isValidPin: Boolean) {
                        if (isValidPin) {
                            _navigationEvent.postValue(
                                NavigationEvent.Directions(
                                    SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                                )
                            )
                        }
                    }
                }, false)
                passcodeView.show(fragmentManager, AppConstants.PASSCODE_BOTTOMSHEET_TAG_NEW)
            } else {
                _navigationEvent.postValue(
                    NavigationEvent.Directions(
                        SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                    )
                )
            }
        }
    }

    private fun warmCaches() {
        // TODO if we add the country dropdown back in uncomment this
        //populateCountries()

        populateMnemonicWordList()
        populateAccounts()

        if (sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN)) {
            val address = Address.fromBech32(
                sharedPreferences.getString(
                    AppConstants.UserPrefsKeys.WALLET_ADDRESS,
                    null
                )!!
            )
            WalletCache.networkEconomics = networkRepository.getNetworkEconomics()
            WalletCache.walletAccount = accountRepository.getAccount(address)

            WalletCache.accountTokenDetails = accountRepository.getAccountTokenDetails(
                address.bech32, environmentRepository.selectedElrondEnvironment.aeroTokenId
            )

            WalletCache.aeroDetails = esdtRepository.getTokenDetails(
                environmentRepository.selectedElrondEnvironment.aeroTokenId
            )
        }
    }

    /** Cache off all the available words Elrond uses to create wallets, this will be useful for typeahead selections */
    private fun populateMnemonicWordList() {
        try {
            val jsonString = app.applicationContext.assets?.open("mnemonic_dictionary_english.json")?.bufferedReader().use {
                it?.readText()
            }

            val gson = GsonBuilder().create()
            MnemonicWordCache.wordsList = gson.deserialize(jsonString!!)
            println("Words List: ${MnemonicWordCache.wordsList}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun populateCountries() {
        try {
            val jsonString = app.applicationContext.assets?.open("countries.json")?.bufferedReader().use {
                it?.readText()
            }

            val gson = GsonBuilder().create()
            CountryCache.countryList = gson.deserialize<List<Country>?>(jsonString!!).sortedBy {
                it.name
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun populateAccounts() {
        var contractInput = QueryContractInput(
            scAddress = environmentRepository.selectedElrondEnvironment.scAddress,
            funcName = "accountList",
            args = listOf(AppConstants.ACCOUNT_TYPE_BUSINESS_VALUE.toHex()),
            caller = AppConstants.ACCOUNT_RETRIEVAL_ADDRESS,
            value = "0"
        )
        val accounts = vmRepository.getChartrAccountList(contractInput)
        accounts?.let { items ->
            ChartrAccountsCache.businessAccounts = items
                .groupBy { item ->
                    item.username
                }.map {
                    it.value.sortedByDescending { account ->
                        account.recordVersion
                    }.take(1)[0]
                }
        }


        contractInput = QueryContractInput(
            scAddress = environmentRepository.selectedElrondEnvironment.scAddress,
            funcName = "accountList",
            args = listOf(AppConstants.ACCOUNT_TYPE_PERSONAL_VALUE.toHex()),
            caller = AppConstants.ACCOUNT_RETRIEVAL_ADDRESS,
            value = "0"
        )
        ChartrAccountsCache.userAccounts = vmRepository.getChartrAccountList(contractInput) ?: listOf()
        println("---> BUSINESS ACCOUNTS: ${ChartrAccountsCache.businessAccounts.size}")
        println("---> PERSONAL ACCOUNTS: ${ChartrAccountsCache.userAccounts.size}")
    }
}