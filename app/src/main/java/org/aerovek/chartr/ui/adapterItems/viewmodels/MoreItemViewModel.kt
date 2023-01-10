package org.aerovek.chartr.ui.adapterItems.viewmodels

import android.graphics.drawable.Drawable
import org.aerovek.chartr.ui.more.MoreItemType

data class MoreItemViewModel(
    val type: MoreItemType,
    val title: String,
    val imageDrawable: Drawable
)
