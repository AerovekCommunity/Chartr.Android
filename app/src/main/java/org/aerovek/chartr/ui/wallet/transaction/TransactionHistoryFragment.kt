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