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