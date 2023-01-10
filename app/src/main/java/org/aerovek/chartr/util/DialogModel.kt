package org.aerovek.chartr.util

import android.view.View

class DialogModel(
    val title: Int? = 0,
    internal val message: Int? = null,
    val negative: Int? = android.R.string.cancel,
    val positive: Int? = android.R.string.ok,
    val negativeFun: (() -> Unit)? = null,
    val positiveFun: (() -> Unit)? = null,
    val isDontShowAgainVisible: Boolean = false,
    val exitVisibility: Int? = View.GONE
)