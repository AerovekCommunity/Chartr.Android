package org.aerovek.chartr.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Activity.showKeyboard(view: View) {
    val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

fun Activity.hideKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}