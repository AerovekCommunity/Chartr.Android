package org.aerovek.chartr.ui.media

import android.app.Application
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.BaseViewModel

class MediaChooserViewModel(app: Application) : BaseViewModel(app) {

    val takePhotoTap = LiveEvent<Unit>()
    val chooseFromLibraryTap = LiveEvent<Unit>()
    val cancelTap = LiveEvent<Unit>()

    fun takePhotoClicked() {
        takePhotoTap.postValue(Unit)
    }

    fun chooseFromLibraryClicked() {
        chooseFromLibraryTap.postValue(Unit)
    }

    fun cancelClicked() {
        cancelTap.postValue(Unit)
    }
}