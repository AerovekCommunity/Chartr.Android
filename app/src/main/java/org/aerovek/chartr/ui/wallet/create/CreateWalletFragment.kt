package org.aerovek.chartr.ui.wallet.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.CreateWalletFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.util.NavigationObserver
import org.koin.android.ext.android.inject

class CreateWalletFragment : BaseFragment() {
    private val viewModel: CreateWalletViewModel by inject()
    private lateinit var binding: CreateWalletFragmentBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<CreateWalletFragmentBinding>(inflater, R.layout.create_wallet_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            setupBackPressListener {
                findNavController(this@CreateWalletFragment).navigateUp()
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(findNavController(this@CreateWalletFragment))
        )
    }

    companion object {
        fun newInstance() = CreateWalletFragment()
    }
}