package org.aerovek.chartr.ui.wallet.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.BaseViewModel

class ReceiveAeroViewModel(app: Application) : BaseViewModel(app) {
    val addressText = MutableLiveData("")
    val addressCopied = LiveEvent<String>()
    val close = LiveEvent<Unit>()

    fun copyClicked() {
        addressText.value?.let {
            addressCopied.postValue(it)
        }
    }

    fun closeClicked() {
        close.postValue(Unit)
    }
}