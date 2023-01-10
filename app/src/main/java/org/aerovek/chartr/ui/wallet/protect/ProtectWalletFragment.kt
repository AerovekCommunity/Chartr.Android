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