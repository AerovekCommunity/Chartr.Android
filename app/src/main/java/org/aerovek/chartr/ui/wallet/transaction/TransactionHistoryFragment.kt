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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.aerovek.chartr.R
import org.aerovek.chartr.data.cache.TransactionCache
import org.aerovek.chartr.databinding.TransactionHistoryFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.adapterItems.TransactionItem
import org.aerovek.chartr.util.*
import org.koin.android.ext.android.inject

class TransactionHistoryFragment : BaseFragment() {
    private val viewModel: TransactionHistoryViewModel by inject()
    private lateinit var binding: TransactionHistoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<TransactionHistoryFragmentBinding>(inflater, R.layout.transaction_history_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            setupRecyclerView(binding.recyclerView)
            binding.swipeRefreshLayout.apply {
                setOnRefreshListener {
                    isRefreshing = true
                    viewModel.buildTransactionItems(true)
                }
            }

            viewModel.transactionItemModels.observe(viewLifecycleOwner) { models ->
                val items = models.map {
                    TransactionItem(requireContext(), it)
                }

                binding.recyclerView.setDataItems(items)
                viewModel.showLoading.postValue(false)
                binding.swipeRefreshLayout.isRefreshing = false
            }

            setupBackPressListener { findNavController().navigateUp() }

            viewModel.buildTransactionItems(TransactionCache.transactionHistory.isEmpty())

        }.root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = GroupAdapter<GroupieViewHolder>()
        }
    }

    companion object {
        fun newInstance() = TransactionHistoryFragment()
    }
}