package org.aerovek.chartr.util

import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import org.aerovek.chartr.R

// Need both to be handled in same function as it can happen that the initialSelection is called prior to setting the adapter
@BindingAdapter(value = ["entries", "initialSelection"], requireAll = false)
fun <T : Any> AutoCompleteTextView.setAdapterWithInitialSelection(entries: List<T>?, position: Int?) {
    if (entries != null && position != null) {
        setAdapterWithInitialSelectionWorker(entries, position)
    }
}

// Need both to be handled in same function as it can happen that the initialSelection is called prior to setting the adapter
@BindingAdapter(value = ["entries", "initialSelection"], requireAll = false)
fun AutoCompleteTextView.setAdapterWithInitialSelectionArray(entries: Array<String>?, position: Int?) {
    if (entries != null && position != null) {
        setAdapterWithInitialSelectionWorker(entries.asList(), position)
    }
}

private fun <T : Any> AutoCompleteTextView.setAdapterWithInitialSelectionWorker(entries: List<T>, position: Int) {
    if (entries.isEmpty()) {
        (parent as ViewGroup).visibility = View.GONE
        return
    }
    val adjustedPosition = if (position !in entries.indices) {
        0
    } else {
        position
    }
    (parent as ViewGroup).visibility = View.VISIBLE
    val adapter = NoFilterArrayAdapter(context, R.layout.dropdown_menu_popup_item, entries)
    setAdapter(adapter)
    setText(adapter.getItem(adjustedPosition).toString())
}