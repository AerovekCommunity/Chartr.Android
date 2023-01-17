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
package org.aerovek.chartr.ui.wallet.transaction

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.ConfirmTransferFragmentBinding
import org.aerovek.chartr.ui.passcode.PassCodeDismissListener
import org.aerovek.chartr.ui.passcode.PassCodeFragment
import org.aerovek.chartr.util.makeBottomsheetFullScreen
import org.koin.android.ext.android.inject


class ConfirmTransferFragment : BottomSheetDialogFragment() {
    private val viewModel: ConfirmTransferViewModel by inject()
    lateinit var binding: ConfirmTransferFragmentBinding
    private var dismissListener: ConfirmTransferDismissListener? = null
    private var didCompleteTransaction: Boolean = false
    var assetBeingSent: String? = null

    companion object {
        fun newInstance() = ConfirmTransferFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<ConfirmTransferFragmentBinding>(inflater, R.layout.confirm_transfer_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            viewModel.closeButtonClicked.observe(viewLifecycleOwner) {
                dismiss()
            }

            viewModel.showPinPad.observe(viewLifecycleOwner) {
                val passcodeView = PassCodeFragment()
                passcodeView.setup(object : PassCodeDismissListener {
                    override fun onDismiss(isValidPin: Boolean) {
                        if (isValidPin) {
                            viewModel.sendTransaction()
                        }
                    }
                }, true)

                passcodeView.show(this@ConfirmTransferFragment.parentFragmentManager, "send_transaction_pin")
            }

            viewModel.transactionComplete.observe(viewLifecycleOwner) {
                didCompleteTransaction = true
                dismiss()
            }

            viewModel.transactionFailed.observe(viewLifecycleOwner) {
                didCompleteTransaction = false
                dismiss()
            }
        }.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.makeBottomsheetFullScreen(this, true)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(didCompleteTransaction)
    }

    fun setup(dismissListener: ConfirmTransferDismissListener, aeroAmount: String, usdAmount: String, recipientAddress: String, asset: String) {
        this.dismissListener = dismissListener
        val truncatedAddress =
            "${recipientAddress.substring(0, 9)}...${recipientAddress.substring(recipientAddress.length - 10)}"
        val usd = usdAmount.substring(usdAmount.indexOf("~") + 1).trim()
        viewModel.truncatedAddress.postValue(truncatedAddress)
        viewModel.recipientAddress.postValue(recipientAddress)
        viewModel.transferAmount.postValue("-$aeroAmount $asset")
        viewModel.usdAmount.postValue(usd)
        viewModel.initialize(aeroAmount, usdAmount, asset)
    }
}

interface ConfirmTransferDismissListener {
    fun onDismiss(didCompleteTransaction: Boolean)
}