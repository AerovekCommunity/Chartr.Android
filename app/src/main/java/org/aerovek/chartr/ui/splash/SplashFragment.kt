package org.aerovek.chartr.ui.splash

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.SplashFragmentBinding
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.onboarding.OnboardingActivity
import org.aerovek.chartr.util.NavigationObserver
import org.koin.android.ext.android.inject

class SplashFragment : BaseFragment() {
    private val splashViewModel: SplashFragmentViewModel by inject()
    private lateinit var onboardingLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: SplashFragmentBinding
    private val app: Application by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register a listener for when the onboarding activity finishes
        onboardingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppConstants.ONBOARDING_COMPLETE_REQUEST_CODE) {
                findNavController(this@SplashFragment).navigate(R.id.action_splashFragment_to_homeFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<SplashFragmentBinding>(inflater, R.layout.splash_fragment, container, false).apply {
            println("[SplashFragment onCreateView]")
            lifecycleOwner = viewLifecycleOwner
            binding = this

            versionName.text = app.packageManager!!.getPackageInfo(app.packageName, 0).versionName
            splashViewModel.initView(this@SplashFragment.parentFragmentManager)

            splashViewModel.startOnboarding.observe(viewLifecycleOwner) {
                val intent = Intent(requireActivity(), OnboardingActivity::class.java)
                onboardingLauncher.launch(intent)
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splashViewModel.navigationEvent.observe(
            viewLifecycleOwner,
            NavigationObserver(findNavController(this@SplashFragment))
        )
    }
}