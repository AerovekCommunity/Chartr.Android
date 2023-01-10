package org.aerovek.chartr.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.SearchFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.adapterItems.SearchResultItem
import org.aerovek.chartr.ui.adapterItems.viewmodels.SearchItemViewModel
import org.aerovek.chartr.util.setDataItems
import org.koin.android.ext.android.inject

class SearchFragment : BaseFragment() {
    private val viewModel: SearchViewModel by inject()
    private lateinit var binding: SearchFragmentBinding

    private lateinit var accountsList: List<SearchResultItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<SearchFragmentBinding>(inflater, R.layout.search_fragment, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean { return false }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (!text.isNullOrEmpty()) {
                        val results = accountsList.filter {
                            it.searchItemViewModel.tags.contains(text)
                                    || (!it.searchItemViewModel.businessName.isNullOrEmpty() && it.searchItemViewModel.businessName.startsWith(text))
                                    || it.searchItemViewModel.username.startsWith(text)
                                    || it.searchItemViewModel.category.startsWith(text)
                        }

                        binding.recyclerView.setDataItems(results)
                    } else {
                        // Search bar cleared out so load original dataset
                        binding.recyclerView.setDataItems(accountsList)
                    }

                    return true
                }
            })

            setupRecyclerView(recyclerView)

            binding.swipeRefreshLayout.apply {
                setOnRefreshListener {
                    isRefreshing = true
                    viewModel.retrieveAccounts(true)
                }
            }

            viewModel.viewReady.observe(viewLifecycleOwner) { businessAccounts ->
                accountsList = businessAccounts.map {
                    SearchResultItem(SearchItemViewModel(
                        username = it.username,
                        profileImageUrl = it.profileImageUrl,
                        businessName = it.businessProfile?.businessName?.trim(),
                        email = it.email?.trim() ?: "",
                        category = it.businessProfile?.businessCategory?.trim() ?: "",
                        tags = it.businessProfile?.searchTags?.joinToString(",") ?: ""
                    ))
                }

                recyclerView.apply {
                    this.setDataItems(accountsList)
                }

                viewModel.showLoading.postValue(false)
                swipeRefreshLayout.isRefreshing = false
            }

            setupBackPressListener {
                findNavController().navigateUp()
            }

        }.root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = GroupAdapter<GroupieViewHolder>()
        }
    }
}