/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.aerovek.chartr.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.aerovek.chartr.R

fun Dialog.makeBottomsheetFullScreen(fragment: BottomSheetDialogFragment, isDismissable: Boolean) {
    setOnShowListener(object : DialogInterface.OnShowListener {
        override fun onShow(dialogInterface: DialogInterface?) {
            dialogInterface?.let {
                val dlg = it as BottomSheetDialog
                val parent = dlg.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                val behavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(parent as FrameLayout)
                val layoutParams = parent.layoutParams

                // Less than API 30 need to use different methods for getting screen dimensions
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    val displayMetrics = DisplayMetrics()
                    fragment.requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                    layoutParams.height = displayMetrics.heightPixels
                } else {
                    layoutParams.height = fragment.requireActivity().windowManager.currentWindowMetrics.bounds.height()
                }

                parent.layoutParams = layoutParams
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = isDismissable
                fragment.isCancelable = isDismissable
            }
        }
    })
}

fun showGenericDialog(
    context: Context,
    dialogModel: DialogModel,
    dismissListener: IDismissDialog?
) {
    lateinit var dialog: AlertDialog
    dialogModel.apply {
        val view = LayoutInflater.from(context).inflate(R.layout.generic_dialog, null, false)
        dialog = AlertDialog.Builder(context, R.style.dialog).setView(view).create()

        view.findViewById<TextView>(R.id.message).apply {
            if (message != null) {
                setText(message)
            } else {
                isVisible = false
            }
        }

        view.findViewById<Button>(R.id.btnOk).apply {
            if (positive != null) {
                setText(positive)
                setOnClickListener {
                    dialog.dismiss()
                    positiveFun?.invoke()
                }
            } else {
                isVisible = false
            }
        }

        view.findViewById<ImageView>(R.id.exit).apply {
            if (exitVisibility != null) {
                this.visibility = exitVisibility
            }
            setOnClickListener {
                dialog.dismiss()
            }
        }

        view.findViewById<Button>(R.id.cancelCta).apply {
            if (negative != null) {
                setText(negative)
                setOnClickListener {
                    dialog.dismiss()
                    negativeFun?.invoke()
                }
            } else {
                isVisible = false
            }
        }

        view.findViewById<CheckBox>(R.id.dontShowAgainCheckbox).apply {
            isVisible = isDontShowAgainVisible
        }

        dialog.setCancelable(false)
        dialog.show()

        dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(dialog: DialogInterface?) {
                dismissListener?.onDismiss()
            }
        })
    }
}