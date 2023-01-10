package org.aerovek.chartr.ui.wallet.verifywords

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.NavigationEvent

class VerifyWordsViewModel(app: Application, private val sharedPreferences: SharedPreferences) : BaseViewModel(app) {
    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    private val _enterWordLabel1 = MutableLiveData("")
    val enterWordLabel1: LiveData<String> = _enterWordLabel1
    private val _enterWordLabel2 = MutableLiveData("")
    val enterWordLabel2 = _enterWordLabel2
    private val _enterWordLabel3 = MutableLiveData("")
    val enterWordLabel3 = _enterWordLabel3
    private val _enterWordLabel4 = MutableLiveData("")
    val enterWordLabel4 = _enterWordLabel4

    private val randomMap: MutableMap<Int, String> = mutableMapOf()
    private var randomKeys: List<Int> = mutableListOf()

    var wordsToValidate: Pair<MutableMap<Int, String>, List<Int>> = Pair(mutableMapOf(), listOf())

    init {
        sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_WORDS, null)?.let {
            // First create a dictionary in the correct order
            val wordsDictionary = it.split(" ").mapIndexed { idx, word ->
                idx to word
            }.toMap()

            // Now randomize the words and store in a new map for display and for validating
            wordsDictionary.keys.shuffled().forEach { key ->
                randomMap[key] = wordsDictionary.getValue(key)
            }

            randomKeys = randomMap.keys.toList()

            wordsToValidate = Pair(randomMap, randomKeys)

            // Pull the first four from the dictionary to use as our random selection to validate
            _enterWordLabel1.postValue("Enter word #${randomKeys[0] + 1}")
            _enterWordLabel2.postValue("Enter word #${randomKeys[1] + 1}")
            _enterWordLabel3.postValue("Enter word #${randomKeys[2] + 1}")
            _enterWordLabel4.postValue("Enter word #${randomKeys[3] + 1}")
        }
    }
}