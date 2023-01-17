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