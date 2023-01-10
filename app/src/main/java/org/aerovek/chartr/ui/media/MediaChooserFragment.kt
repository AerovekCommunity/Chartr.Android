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
