package org.aerovek.chartr.ui.wallet.verifywords

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.VerifyWordsFragmentBinding
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.util.DialogModel
import org.aerovek.chartr.util.NavigationObserver
import org.aerovek.chartr.util.showGenericDialog
import org.koin.android.ext.android.inject

class VerifyWordsFragment : BaseFragment() {
    private val viewModel: VerifyWordsViewModel by inject()
    private lateinit var binding: VerifyWordsFragmentBinding
    private val sharedPreferences: SharedPreferences by inject()
    private var continueMenuItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<VerifyWordsFragmentBinding>(inflater, R.layout.verify_words_fragment, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel

            setupBackPressListener { findNavController().navigateUp() }

            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.toolbar_continue, menu)
                    continueMenuItem = menu.findItem(R.id.action_continue)
                    continueMenuItem?.isEnabled = true
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_continue -> {
                            val entry1 = binding.wordEntry1.text.toString().trim()
                            val entry2 = binding.wordEntry2.text.toString().trim()
                            val entry3 = binding.wordEntry3.text.toString().trim()
                            val entry4 = binding.wordEntry4.text.toString().trim()

                            val word1 = viewModel.wordsToValidate.first[viewModel.wordsToValidate.second[0]]
                            val word2 = viewModel.wordsToValidate.first[viewModel.wordsToValidate.second[1]]
                            val word3 = viewModel.wordsToValidate.first[viewModel.wordsToValidate.second[2]]
                            val word4 = viewModel.wordsToValidate.first[viewModel.wordsToValidate.second[3]]

                            if (entry1 != word1 || entry2 != word2 || entry3 != word3 || entry4 != word4) {
                                showGenericDialog(
                                    requireContext(),
                                    DialogModel(
                                        null,
                                        message = R.string.words_do_not_match,
                                        positive = R.string.ok,
                                        negative = R.string.dismiss,
                                        exitVisibility = View.VISIBLE
                                    ), dismissListener = null
                                )
                            } else {
                                Snackbar.make(requireView(), getString(R.string.wallet_created_successfully), Snackbar.LENGTH_LONG).show()

                                // Finally copy the temp pin to the real pin. The temp pin was there in case
                                // the user force quits the app before completely finishing account setup
                                sharedPreferences.edit {
                                    putString(AppConstants.UserPrefsKeys.USER_PIN,
                                        sharedPreferences.getString(AppConstants.UserPrefsKeys.USER_TEMP_PIN, null))
                                    // We don't need the temp PIN anymore
                                    remove(AppConstants.UserPrefsKeys.USER_TEMP_PIN)
                                    apply()
                                }

                                findNavController().navigate(VerifyWordsFragmentDirections.actionVerifyWordsToHomeFragment())
                            }
                        }
                    }
                    return true
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(NavHostFragment.findNavController(this@VerifyWordsFragment))
        )
    }
}