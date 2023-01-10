package org.aerovek.chartr.ui

import android.app.Application
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent

class MainActivityViewModel(app: Application) : BaseViewModel(app) {
    private val _createAcountEvent = LiveEvent<Boolean>()
    val createAccountEvent: LiveData<Boolean> = _createAcountEvent

    fun postAccountCreated() {
        _createAcountEvent.postValue(true)
    }

    init {
        println("[MainActivityViewModel init]")
    }
}