package org.aerovek.chartr.ui.wallet.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.WalletOverviewFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.koin.android.ext.android.inject

class WalletOverviewFragment : BaseFragment() {
    private val viewModel: WalletOverviewViewModel by inject()
    private lateinit var binding: WalletOverviewFragmentBinding

    companion object {
        fun newInstance() = WalletOverviewFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<WalletOverviewFragmentBinding>(inflater, R.layout.wallet_overview_fragment, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel

            viewModel.refreshingComplete.observe(viewLifecycleOwner) {
                binding.swipeRefreshLayout.isRefreshing = false
            }

            binding.swipeRefreshLayout.apply {
                setOnRefreshListener {
                    isRefreshing = true
                    viewModel.updateBalance(true)
                }
            }
        }.root
    }
}