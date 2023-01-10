package org.aerovek.chartr.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import org.aerovek.chartr.ui.BaseViewModel

class HomeViewModel(
    app: Application
) : BaseViewModel(app) {

    val showLoading = MutableLiveData(true)

    init {
        showLoading.postValue(false)
    }
}