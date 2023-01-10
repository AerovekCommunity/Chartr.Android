package org.aerovek.chartr.ui.wallet.secretphrase

import android.app.Application
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.ui.adapterItems.WalletWordsItem
import org.aerovek.chartr.util.NavigationEvent

class SecretPhraseViewModel(
    app: Application,
    private val sharedPreferences: SharedPreferences) : BaseViewModel(app) {

    private lateinit var wallet: Wallet
    private var wordsList: List<String> = mutableListOf()

    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    val checkboxChecked = MutableLiveData(false)
    val showLoading = MutableLiveData(true)
    val walletCreated = MutableLiveData<List<WalletWordsItem>>()

    init {
        generateWallet()
    }

    fun checkboxButtonClicked(view: View, isChecked: Boolean) {
        checkboxChecked.postValue(isChecked)
    }

    fun continueButtonClicked() {
        val privateKey = wallet.privateKeyHex
        val publicKey = wallet.publicKeyHex
        val bech32 = Address.fromHex(publicKey).bech32
        println("-----> Private Key: $privateKey")
        println("-----> Public Key: $publicKey")
        println("-----> WALLET ADDRESS : $bech32")
        println("-----> WALLET WORDS: ${wordsList.mapIndexed { idx, word -> "${idx + 1}-$word"}}")
        println("-----> WALLET WORDS: ${wordsList.joinToString(" ")}")

        // Store wallet private keys and the words in user preferences
        sharedPreferences.edit {
            putString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, bech32)
            putString(AppConstants.UserPrefsKeys.WALLET_PRIVATE_KEY, privateKey)
            putString(AppConstants.UserPrefsKeys.WALLET_PUBLIC_KEY, publicKey)
            putString(AppConstants.UserPrefsKeys.WALLET_WORDS, wordsList.joinToString(" "))
            apply()
        }

        _navigationEvent.postValue(NavigationEvent.Directions(
            SecretPhraseFragmentDirections.actionSecretPhraseToVerifyWordsFragment()
        ))
    }

    private fun generateWallet() {
        wordsList = Wallet.generateMnemonic()

        val wordsListDisplay = wordsList.mapIndexed { idx, word ->
            WalletWordsItem("${idx + 1} - $word")
        }

        wallet = Wallet.createFromMnemonic(mnemonic = wordsList.joinToString(" "), 0)
        walletCreated.postValue(wordsListDisplay)
    }
}