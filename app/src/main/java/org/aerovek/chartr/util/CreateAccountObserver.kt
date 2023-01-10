package org.aerovek.chartr.util

import android.view.MenuItem
import androidx.lifecycle.Observer

class CreateAccountObserver(private val accountMenuItem: MenuItem?) : Observer<Boolean> {
    override fun onChanged(accountCreated: Boolean) {
        accountMenuItem?.isVisible = true
    }
}