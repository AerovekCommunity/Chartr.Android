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
package org.aerovek.chartr.ui.wallet.protect

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.ProtectWalletFragmentBinding
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.passcode.PassCodeDismissListener
import org.aerovek.chartr.ui.passcode.PassCodeFragment
import org.aerovek.chartr.util.NavigationObserver
import org.koin.android.ext.android.inject

class ProtectWalletFragment : BaseFragment() {
    private val protectWalletViewModel: ProtectWalletViewModel by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val args: ProtectWalletFragmentArgs by navArgs()
    private lateinit var binding: ProtectWalletFragmentBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return DataBindingUtil.inflate<ProtectWalletFragmentBinding>(inflater, R.layout.protect_wallet_fragment, container, false).apply {
            binding = this
            viewModel = protectWalletViewModel
            lifecycleOwner = viewLifecycleOwner

            protectWalletViewModel.isCreatingNewWallet = args.isCreatingNewWallet

            setupBackPressListener { findNavController().navigateUp() }

            // To keep things simple, if user lands here for first time or navigating
            //  back let's clear the temp PIN so they have to start fresh.
            sharedPreferences.edit().remove(AppConstants.UserPrefsKeys.USER_TEMP_PIN).apply()
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        protectWalletViewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(NavHostFragment.findNavController(this@ProtectWalletFragment))
        )

        protectWalletViewModel.showPassCodeView.observe(viewLifecycleOwner) {
            val passcodeView = PassCodeFragment()
            passcodeView.setup(object : PassCodeDismissListener {
                override fun onDismiss(isValidPin: Boolean) {
                    if (isValidPin) {
                        setEnabledControls()
                    }
                }
            }, true)

            passcodeView.show(this@ProtectWalletFragment.parentFragmentManager, AppConstants.PASSCODE_BOTTOMSHEET_TAG_NEW)
        }
    }

    private fun setEnabledControls() {
        binding.passcodeStatus.text = resources.getString(R.string.completed)
        binding.passcodeStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        binding.continueBtn.isEnabled = true
        binding.continueBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_square_black)
    }
}