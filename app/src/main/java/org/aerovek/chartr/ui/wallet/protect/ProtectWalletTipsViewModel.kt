package org.aerovek.chartr.ui.wallet.protect

import android.app.Application
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.NavigationEvent

class ProtectWalletTipsViewModel(app: Application) : BaseViewModel(app) {

    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    fun continueButtonClicked() {
        _navigationEvent.postValue(NavigationEvent.Directions(
            ProtectWalletTipsFragmentDirections.actionProtectWalletTipsToSecretPhraseFragment()
        ))
    }
}