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
package org.aerovek.chartr.ui.wallet.importwallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.aerovek.chartr.R
import org.aerovek.chartr.data.cache.MnemonicWordCache
import org.aerovek.chartr.databinding.ImportWalletFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.adapterItems.WalletTypeaheadWordsItem
import org.aerovek.chartr.ui.adapterItems.WalletWordsItem
import org.aerovek.chartr.util.*
import org.koin.android.ext.android.inject

class ImportWalletFragment : BaseFragment() {
    private val GRID_COLUMN_COUNT = 3
    private val viewModel: ImportWalletViewModel by inject()
    private lateinit var binding: ImportWalletFragmentBinding
    private val wordItems: MutableList<WalletWordsItem> = mutableListOf()
    private var continueMenuItem: MenuItem? = null
    private val cachedWords = MnemonicWordCache.wordsList

    override fun onResume() {
        super.onResume()
        requireActivity().showKeyboard(binding.editText)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<ImportWalletFragmentBinding>(inflater, R.layout.import_wallet_fragment, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel

            setupRecyclerView(binding.walletWordsList)
            setupTypeaheadResultsRecycler(binding.typeaheadResultsRecyclerView)

            setupBackPressListener {
                showGenericDialog(
                    requireContext(),
                    DialogModel(
                        title = R.string.exit_setup_title,
                        message = R.string.exit_screen_message,
                        positive = R.string.exit_button_title,
                        negative = R.string.stay_button_title,
                        positiveFun = { findNavController().navigateUp() },
                        negativeFun = { },
                        exitVisibility = View.VISIBLE
                    ), dismissListener = null
                )
            }

            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.toolbar_continue, menu)
                    continueMenuItem = menu.findItem(R.id.action_continue)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_continue -> {
                            showGenericDialog(
                                requireContext(),
                                DialogModel(
                                    null,
                                    message = R.string.import_wallet_confirmation,
                                    positive = R.string.continue_title,
                                    negative = R.string.stay_button_title,
                                    positiveFun = { viewModel.createWalletAndNavigate() },
                                    exitVisibility = View.VISIBLE
                                ), dismissListener = null
                            )
                        }
                    }
                    return true
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

            viewModel.importSuccessful.observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), getString(R.string.wallet_imported_successfully), Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(ImportWalletFragmentDirections.actionImportWalletToHomeFragment())
            }

            viewModel.backPressed.observe(viewLifecycleOwner) {
                if (wordItems.size > 0) {
                    val removalIndex = wordItems.size - 1
                    val removedItem = wordItems.removeAt(removalIndex)
                    binding.walletWordsList.setDataItems(wordItems)

                    // If removing a word we should disable the continue button again
                    continueMenuItem?.isEnabled = false
                }
            }

            viewModel.showProgressBar.observe(viewLifecycleOwner) { show ->
                binding.progressBar.visibility = if (show) { View.VISIBLE } else { View.GONE }
            }

            viewModel.showConfirmation.observe(viewLifecycleOwner) {
                showGenericDialog(
                    requireContext(),
                    DialogModel(
                        null,
                        message = R.string.import_wallet_confirmation,
                        positive = R.string.continue_title,
                        negative = R.string.stay_button_title,
                        positiveFun = { viewModel.createWalletAndNavigate() },
                        exitVisibility = View.VISIBLE
                    ), dismissListener = null
                )
            }

            viewModel.typeaheadItemTapped.observe(viewLifecycleOwner) { item ->
                wordItems.add(WalletWordsItem( "${wordItems.size + 1}-$item"))

                binding.walletWordsList.setDataItems(wordItems)
                binding.editText.text.clear()

                if (wordItems.size == 24) {
                    continueMenuItem?.isEnabled = true
                    viewModel.setWords(wordItems.map { item ->
                        // Remove the number prefix or it will generate some random wallet
                        item.title.substring(startIndex = item.title.indexOf("-") + 1)
                    })
                }
            }

            binding.editText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun afterTextChanged(p0: Editable?) { }

                override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                    // Nothing to match against so just bail, this should never be the case but being defensive here
                    if (cachedWords.isEmpty()) {
                        return
                    }

                    sequence?.toString()?.let { enteredText ->
                        if (enteredText.isEmpty()) {
                            binding.typeaheadResultsRecyclerView.setDataItems(listOf())
                        } else {
                            var matches = cachedWords.filter { word ->
                                word.startsWith(enteredText)
                            }

                            // User should type enough to narrow down the results,
                            // so no reason to show more 20
                            if (matches.size > 20) {
                                matches = matches.take(20)
                            }

                            binding.typeaheadResultsRecyclerView.setDataItems(
                                matches.map { word ->
                                    WalletTypeaheadWordsItem(word, viewModel)
                                }
                            )
                        }
                    }
                }
            })

            binding.editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    // If the words failed to get cached go through this mechanism,
                    // otherwise let onTextChanged above match against the cached words so user can choose
                    if (cachedWords.isNotEmpty()) {
                        return false
                    }

                    if (wordItems.size < 24 && actionId == EditorInfo.IME_ACTION_DONE) {
                        binding.editText.text?.let {
                            val text = it.toString().trim()

                            if (text.isNotEmpty()) {
                                println("CURRENT TEXT = $text")

                                // If more than one word was pasted let's split it up
                                val splitWords = text.split( " ")
                                if (splitWords.size > 1) {
                                    wordItems.addAll(splitWords.mapIndexed { idx, word ->
                                        WalletWordsItem("${wordItems.size + idx + 1}-$word")
                                    })
                                } else {
                                    wordItems.add(WalletWordsItem( "${wordItems.size + 1}-$text"))
                                }

                                binding.walletWordsList.setDataItems(wordItems)
                                it.clear()

                                if (wordItems.size == 24) {
                                    viewModel.setWords(wordItems.map { item ->
                                        // Remove the number prefix or it will generate some random wallet
                                        item.title.substring(startIndex = item.title.indexOf("-") + 1)
                                    })
                                }
                            }
                        }
                        return true
                    }
                    return false
                }
            })
        }.root
    }

    override fun onDestroy() {
        requireActivity().hideKeyboard()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editText.requestFocus()
        requireActivity().showKeyboard(binding.editText)

        viewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(NavHostFragment.findNavController(this@ImportWalletFragment))
        )
    }

    private fun setupTypeaheadResultsRecycler(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = GroupAdapter<GroupieViewHolder>()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@ImportWalletFragment.requireActivity(), GRID_COLUMN_COUNT)
            adapter = GroupAdapter<GroupieViewHolder>()
        }
    }
}