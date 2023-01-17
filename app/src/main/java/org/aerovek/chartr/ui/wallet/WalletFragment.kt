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
package org.aerovek.chartr.ui.wallet

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.WalletFragmentBinding
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.wallet.overview.WalletOverviewFragment
import org.aerovek.chartr.ui.wallet.transaction.ReceiveAeroFragment
import org.aerovek.chartr.ui.wallet.transaction.SendAeroFragment
import org.aerovek.chartr.ui.wallet.transaction.TransactionHistoryFragment
import org.aerovek.chartr.util.NavigationObserver
import org.koin.android.ext.android.inject

class WalletFragment : BaseFragment() {
    private val sharedPreferences: SharedPreferences by inject()

    private val viewModel: WalletViewModel by inject()
    lateinit var binding: WalletFragmentBinding
    private lateinit var pagerAdapter: WalletPageAdapter
    private var fragmentList: List<Fragment> = listOf()
    private var hasWallet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasWallet = sharedPreferences.contains(AppConstants.UserPrefsKeys.WALLET_ADDRESS)

        // If user hasn't created a wallet yet don't initialize these fragments yet
        if (hasWallet) {
            fragmentList = listOf(
                WalletOverviewFragment.newInstance(),
                SendAeroFragment.newInstance(),
                ReceiveAeroFragment.newInstance(),
                TransactionHistoryFragment.newInstance()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<WalletFragmentBinding>(inflater, R.layout.wallet_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            setupBackPressListener {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerAdapter = WalletPageAdapter(this@WalletFragment)
        binding.viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
            println("TabLayoutMediator called with - tab: ${tab.id}, position: $position")
            when (position) {
                0 -> tab.text = getString(R.string.overview_title)
                1 -> tab.text = getString(R.string.withdraw_title)
                2 -> tab.text = getString(R.string.deposit_title)
                3 -> tab.text = getString(R.string.activity_title)
            }
        }.attach()

        viewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(NavHostFragment.findNavController(this@WalletFragment))
        )
    }

    private inner class WalletPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}