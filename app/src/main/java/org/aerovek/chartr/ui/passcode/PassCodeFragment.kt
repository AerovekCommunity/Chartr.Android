package org.aerovek.chartr.ui.passcode

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.PasscodeFragmentBinding
import org.aerovek.chartr.ui.adapterItems.PassCodeEntryItem
import org.aerovek.chartr.ui.adapterItems.PassCodeKeyboardItem
import org.aerovek.chartr.util.DialogModel
import org.aerovek.chartr.util.makeBottomsheetFullScreen
import org.aerovek.chartr.util.setDataItems
import org.aerovek.chartr.util.showGenericDialog
import org.koin.android.ext.android.inject

class PassCodeFragment : BottomSheetDialogFragment() {

    private val GRID_COLUMN_COUNT_DOTS = 6
    private val GRID_COLUMN_COUNT_KEYPAD = 3
    private val passCodeViewModel: PassCodeViewModel by inject()
    private lateinit var binding: PasscodeFragmentBinding
    private lateinit var pinItems : MutableList<PassCodeEntryItem>
    private lateinit var pinNumbers : List<PassCodeKeyboardItem>
    private var dismissListener: PassCodeDismissListener? = null
    private var isDismissable: Boolean = false
    private var isValidPin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<PasscodeFragmentBinding>(inflater, R.layout.passcode_fragment, container, false).apply {

            binding = this
            viewModel = passCodeViewModel
            lifecycleOwner = this@PassCodeFragment.viewLifecycleOwner
            setupRecyclerView(binding.recycleButtonView , GRID_COLUMN_COUNT_DOTS)
            setupRecyclerView(binding.recycleKeyPadView , GRID_COLUMN_COUNT_KEYPAD)

            pinItems = mutableListOf(
                PassCodeEntryItem(0 , false),
                PassCodeEntryItem(1 , false),
                PassCodeEntryItem(2 , false),
                PassCodeEntryItem(3 , false),
                PassCodeEntryItem(4 , false),
                PassCodeEntryItem(5 , false)
            )

            binding.recycleButtonView.apply {
                setDataItems(pinItems)
            }

            passCodeViewModel.insecurePinDetected.observe(viewLifecycleOwner) {
                showGenericDialog(
                    requireContext(),
                    DialogModel(
                        R.string.insecure_pin_title,
                        R.string.insecure_pin_desc
                    ), dismissListener = null
                )
            }

            passCodeViewModel.clearDots.observe(viewLifecycleOwner) {
                pinItems.forEach {
                    it.isActive = false
                    binding.recycleButtonView.adapter?.notifyItemChanged(pinItems.indexOf(it))
                }
            }

            passCodeViewModel.invalidPin.observe(viewLifecycleOwner) {
                showGenericDialog(
                    requireContext(),
                    DialogModel(
                        null,
                        R.string.pin_no_match
                    ), dismissListener = null
                )
            }

            passCodeViewModel.pinCodePair.observe(viewLifecycleOwner) { pair ->
                if (pair.second == PinPadType.Value) {
                    pinItems[pair.first].isActive = true
                }
                else if (pair.second == PinPadType.BackSpace)
                {
                    pinItems[pair.first].isActive = false
                }
                binding.recycleButtonView.adapter?.notifyItemChanged(pair.first)
            }

            passCodeViewModel.entryComplete.observe(viewLifecycleOwner) {
                isValidPin = true
                dismiss()
            }

            val pinPadList = listOf(
                PinPadModel("1",PinPadType.Value),
                PinPadModel("2",PinPadType.Value),
                PinPadModel("3",PinPadType.Value),
                PinPadModel("4",PinPadType.Value),
                PinPadModel("5",PinPadType.Value),
                PinPadModel("6",PinPadType.Value),
                PinPadModel("7",PinPadType.Value),
                PinPadModel("8",PinPadType.Value),
                PinPadModel("9",PinPadType.Value),
                PinPadModel("",PinPadType.Blank),
                PinPadModel("0",PinPadType.Value),
                PinPadModel("",PinPadType.BackSpace, ContextCompat.getDrawable(this@PassCodeFragment.requireActivity(), R.drawable.ic_back_space)!!),
            )

            ContextCompat.getDrawable(this@PassCodeFragment.requireActivity(), R.drawable.button_square_white)?.let { drawable ->
                pinNumbers = pinPadList.map { pinPadModel->
                    PassCodeKeyboardItem(drawable, pinPadModel, passCodeViewModel)
                }
                binding.recycleKeyPadView.apply {
                    setDataItems(pinNumbers)
                }
            }
        }.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.makeBottomsheetFullScreen(this, isDismissable)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(isValidPin)
    }

    fun setup(dismissListener: PassCodeDismissListener, isDismissable: Boolean) {
        this.dismissListener = dismissListener
        this.isDismissable = isDismissable
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, columnCount: Int) {
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@PassCodeFragment.requireActivity(), columnCount)
            adapter = GroupAdapter<GroupieViewHolder>()
        }
    }

}

data class PinPadModel(
    val value: String? = null,
    val type: PinPadType,
    val background: Drawable? = null
)

enum class PinPadType {
    Value,
    BackSpace,
    Blank
}

interface PassCodeDismissListener {
    fun onDismiss(isValidPin: Boolean)
}



