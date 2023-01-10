package org.aerovek.chartr.ui.wallet.create

import android.app.Application
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.NavigationEvent

class CreateWalletViewModel(app: Application) : BaseViewModel(app) {
    private val _navigationEvent = LiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> = _navigationEvent

    fun createWalletClicked() {
        _navigationEvent.postValue(NavigationEvent.Directions(
            CreateWalletFragmentDirections.actionCreateWalletToProtectWallet(true)
        ))
    }

    fun importWalletClicked() {
        _navigationEvent.postValue(NavigationEvent.Directions(
            CreateWalletFragmentDirections.actionCreateWalletToProtectWallet(false)
        ))
    }
}