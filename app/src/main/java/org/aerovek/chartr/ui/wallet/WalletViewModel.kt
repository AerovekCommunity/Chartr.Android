package org.aerovek.chartr.ui.wallet

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.NavigationEvent

class WalletViewModel(app: Application, sharedPreferences: SharedPreferences) : BaseViewModel(app) {
    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    val showLoading = MutableLiveData(true)
    val showCreateWalletButton = MutableLiveData(!sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN))

    fun createWalletClicked() {
        _navigationEvent.postValue(NavigationEvent.Directions(
            WalletFragmentDirections.actionWalletFragmentToCreateWalletFragment()
        ))
    }
}