package org.aerovek.chartr.ui.wallet.secretphrase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.SecretPhraseFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.adapterItems.WalletWordsItem
import org.aerovek.chartr.util.DialogModel
import org.aerovek.chartr.util.NavigationObserver
import org.aerovek.chartr.util.setDataItems
import org.aerovek.chartr.util.showGenericDialog
import org.koin.android.ext.android.inject

class SecretPhraseFragment : BaseFragment() {
    private val GRID_COLUMN_COUNT = 3
    private val viewModel: SecretPhraseViewModel by inject()
    private lateinit var binding: SecretPhraseFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<SecretPhraseFragmentBinding>(inflater, R.layout.secret_phrase_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            setupBackPressListener {
                showGenericDialog(
                    requireContext(),
                    DialogModel(
                        title = R.string.exit_setup_title,
                        message = R.string.exit_recover_phrase_warning,
                        positive = R.string.exit_button_title,
                        negative = R.string.stay_button_title,
                        positiveFun = { findNavController().navigateUp() },
                        negativeFun = { },
                        exitVisibility = View.VISIBLE
                    ), dismissListener = null
                )
            }

            viewModel.checkboxChecked.observe(viewLifecycleOwner) { isChecked ->
                if (isChecked) {
                    binding.continueBtn.isEnabled = true
                    binding.continueBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_square_black)
                } else {
                    binding.continueBtn.isEnabled = false
                    binding.continueBtn.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_square_disabled_grey)
                }
            }

            viewModel.walletCreated.observe(viewLifecycleOwner) { wordsList ->
                setupRecyclerView(binding.walletWordsList, wordsList)
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(NavHostFragment.findNavController(this@SecretPhraseFragment))
        )
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, items: List<WalletWordsItem>) {
        recyclerView.apply {
            layoutManager = GridLayoutManager(this@SecretPhraseFragment.requireActivity(), GRID_COLUMN_COUNT)
            adapter = GroupAdapter<GroupieViewHolder>()
            setDataItems(items)
            viewModel.showLoading.postValue(false)
        }
    }
}