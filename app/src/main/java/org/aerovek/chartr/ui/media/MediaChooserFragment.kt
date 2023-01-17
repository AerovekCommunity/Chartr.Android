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
package org.aerovek.chartr.ui.media

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.MediaChooserFragmentBinding
import org.koin.android.ext.android.inject

class MediaChooserFragment : BottomSheetDialogFragment() {
    private val viewModel: MediaChooserViewModel by inject()
    private lateinit var binding: MediaChooserFragmentBinding
    private lateinit var dismissListener: MediaChooserDismissListener
    private var chosenType: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<MediaChooserFragmentBinding>(inflater, R.layout.media_chooser_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            viewModel.takePhotoTap.observe(viewLifecycleOwner) {
                chosenType = TYPE_TAKE_PHOTO
                dismiss()
            }

            viewModel.chooseFromLibraryTap.observe(viewLifecycleOwner) {
                chosenType = TYPE_CHOOSE_FROM_LIBRARY
                dismiss()
            }

            viewModel.cancelTap.observe(viewLifecycleOwner) {
                dismiss()
            }
        }.root
    }

    fun setDismissListener(listener: MediaChooserDismissListener) {
        this.dismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        this.dismissListener.onDismiss(chosenType)
    }

    companion object {
        val TYPE_TAKE_PHOTO = 0
        val TYPE_CHOOSE_FROM_LIBRARY = 1
    }
}

interface MediaChooserDismissListener {
    fun onDismiss(chosenType: Int?)
}
