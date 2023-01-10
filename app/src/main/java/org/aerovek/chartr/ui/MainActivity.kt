package org.aerovek.chartr.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.MainActivityBinding
import org.aerovek.chartr.util.CreateAccountObserver
import org.aerovek.chartr.util.DispatcherProvider
import org.koin.android.ext.android.inject

private lateinit var biometricPrompt: BiometricPrompt

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    val activityViewModel: MainActivityViewModel by inject()
    val sharedPreferences: SharedPreferences by inject()
    val dispatcherProvider: DispatcherProvider by inject()
    private lateinit var activityBinding: MainActivityBinding
    private lateinit var toolbarTextView: TextView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        activityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        activityBinding.viewModel = activityViewModel
        navController = findNavController(R.id.nav_host_fragment)
        activityBinding.lifecycleOwner = this

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        toolbarTextView = toolbar.findViewById(R.id.toolbarTextView)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bottomNav = findViewById(R.id.navigation)
        setupBottomNav()

        val accountMenuItem = bottomNav.menu.findItem(R.id.myAccountFragment)
        accountMenuItem.isVisible = sharedPreferences.contains(AppConstants.UserPrefsKeys.ACCOUNT_TYPE)
                && sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN)

        // Listen for the account created event so we can show the Account menu item
        activityViewModel.createAccountEvent.observe(
            this,
            CreateAccountObserver(accountMenuItem)
        )

        //biometricPrompt = createBiometricPrompt()

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _: Bundle? ->
            // Only show bottom nav when on home, wallet, or account fragments, so start with it hidden
            this.bottomNav.visibility = View.GONE

            when (destination.id) {
                R.id.splashFragment -> this.supportActionBar?.hide()
                R.id.createWalletFragment -> {
                    this.supportActionBar?.show()
                    this.toolbarTextView.text = getString(R.string.create_wallet)
                }
                R.id.walletFragment -> {
                    this.supportActionBar?.show()
                    this.toolbarTextView.text = getString(R.string.wallet_title)
                    this.bottomNav.visibility = View.VISIBLE
                }
                R.id.protectWalletFragment -> {
                    this.toolbarTextView.text = getString(R.string.protect_wallet_title)
                }
                R.id.protectWalletTipsFragment -> {
                    this.toolbarTextView.text = getString(R.string.protect_wallet_tips_title)
                }
                R.id.secretPhraseFragment -> {
                    this.toolbarTextView.text = getString(R.string.recovery_phrase_title)
                }
                R.id.importWalletFragment -> {
                    this.toolbarTextView.text = getString(R.string.import_wallet_title)
                }
                R.id.verifyWordsFragment -> {
                    this.toolbarTextView.text = getString(R.string.verify_words_title)
                }
                R.id.homeFragment -> {
                    this.supportActionBar?.hide()
                    this.bottomNav.visibility = View.VISIBLE
                }
                R.id.searchFragment -> {
                    this.supportActionBar?.show()
                    this.bottomNav.visibility = View.GONE
                    this.toolbarTextView.text = getString(R.string.search_flights_title)
                }
                R.id.createAccountFragment -> {
                    this.supportActionBar?.show()
                    this.bottomNav.visibility = View.GONE
                    this.toolbarTextView.text = getString(R.string.create_account_title)
                }
                R.id.myAccountFragment -> {
                    this.supportActionBar?.show()
                    this.toolbarTextView.text = getString(R.string.my_account_title)
                    this.bottomNav.visibility = View.VISIBLE
                }
                R.id.sendAeroFragment -> {
                    this.supportActionBar?.show()
                    this.toolbarTextView.text = getString(R.string.withdraw_title)
                }
                R.id.receiveAeroFragment -> {
                    this.supportActionBar?.show()
                    this.toolbarTextView.text = getString(R.string.receive_title)
                }
                R.id.transactionHistoryFragment -> {
                    this.supportActionBar?.show()
                    this.toolbarTextView.text = getString(R.string.transaction_history_title)
                }
                R.id.moreFragment -> {
                    this.supportActionBar?.show()
                    this.bottomNav.visibility = View.VISIBLE
                    this.toolbarTextView.text = getString(R.string.more_title)
                }
            }
        }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupBottomNav() {
        bottomNav.setupWithNavController(navController)

        bottomNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    println("---- Home was reselected")
                }
                R.id.walletFragment -> {
                    println("---- Wallet was reselected")
                }
                R.id.myAccountFragment -> {
                    println("---- Account was reselected")
                }
            }
        }
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            @SuppressLint("RestrictedApi")
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                   // loginWithPassword() // Because in this app, the negative button allows the user to enter an account password. This is completely optional and your app doesn’t have to do it.
                }
            }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d(TAG, "Authentication failed for an unknown reason")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Authentication was successful")

                }
        }

        val biometricPrompt = BiometricPrompt(this,executor,callback)

        return biometricPrompt

    }

}
