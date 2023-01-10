package org.aerovek.chartr.ui

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun setupBackPressListener(backPressHandler: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backPressHandler()
                }
            }
        )
    }

//    @CallSuper
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//
//        // TODO maybe some authentication processing here
//    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        // TODO maybe save off authenticated state here
//        super.onSaveInstanceState(outState)
//    }
}

//private const val BUNDLE_AUTHENTICATED = "authenticated"