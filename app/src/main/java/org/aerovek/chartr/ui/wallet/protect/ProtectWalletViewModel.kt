package org.aerovek.chartr.ui.wallet.protect

import android.app.Application
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.NavigationEvent

class ProtectWalletViewModel(app: Application) : BaseViewModel(app) {
    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent
    var isCreatingNewWallet = true

    val showPassCodeView = LiveEvent<Unit>()

    fun continueButtonClicked() {
        if (isCreatingNewWallet) {
            _navigationEvent.postValue(NavigationEvent.Directions(ProtectWalletFragmentDirections.actionProtectWalletToWalletTips()))
        } else {
            _navigationEvent.postValue(NavigationEvent.Directions(ProtectWalletFragmentDirections.actionProtectWalletToImportWallet()))
        }
    }

    fun createPasscodeClicked() {
        showPassCodeView.postValue(Unit)
    }
}