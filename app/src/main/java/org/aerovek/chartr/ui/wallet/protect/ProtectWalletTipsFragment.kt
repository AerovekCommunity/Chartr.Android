package org.aerovek.chartr.ui.wallet.protect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.ProtectWalletTipsFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.util.NavigationObserver
import org.koin.android.ext.android.inject

class ProtectWalletTipsFragment : BaseFragment() {
    private val vm: ProtectWalletTipsViewModel by inject()
    private lateinit var binding: ProtectWalletTipsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<ProtectWalletTipsFragmentBinding>(inflater, R.layout.protect_wallet_tips_fragment, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm

            setupBackPressListener { findNavController().navigateUp() }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(NavHostFragment.findNavController(this@ProtectWalletTipsFragment))
        )
    }
}