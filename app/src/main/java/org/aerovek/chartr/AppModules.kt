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
package org.aerovek.chartr

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.aerovek.chartr.data.buildconfig.BuildConfigProvider
import org.aerovek.chartr.ui.MainActivityViewModel
import org.aerovek.chartr.ui.home.HomeViewModel
import org.aerovek.chartr.ui.media.MediaChooserViewModel
import org.aerovek.chartr.ui.more.MoreViewModel
import org.aerovek.chartr.ui.account.*
import org.aerovek.chartr.ui.passcode.PassCodeViewModel
import org.aerovek.chartr.ui.qrscanner.QRScannerViewModel
import org.aerovek.chartr.ui.search.SearchViewModel
import org.aerovek.chartr.ui.splash.SplashFragmentViewModel
import org.aerovek.chartr.ui.wallet.*
import org.aerovek.chartr.ui.wallet.create.CreateWalletViewModel
import org.aerovek.chartr.ui.wallet.importwallet.ImportWalletViewModel
import org.aerovek.chartr.ui.wallet.overview.WalletOverviewViewModel
import org.aerovek.chartr.ui.wallet.protect.ProtectWalletTipsViewModel
import org.aerovek.chartr.ui.wallet.protect.ProtectWalletViewModel
import org.aerovek.chartr.ui.wallet.secretphrase.SecretPhraseViewModel
import org.aerovek.chartr.ui.wallet.transaction.ConfirmTransferViewModel
import org.aerovek.chartr.ui.wallet.transaction.ReceiveAeroViewModel
import org.aerovek.chartr.ui.wallet.transaction.SendAeroViewModel
import org.aerovek.chartr.ui.wallet.transaction.TransactionHistoryViewModel
import org.aerovek.chartr.ui.wallet.verifywords.VerifyWordsViewModel
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.DispatcherProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModules {
    val appModule = module {
        single { Gson() }
        single<BuildConfigProvider> { BuildConfigProviderImpl() }
        single<SharedPreferences> { androidContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
        single<DispatcherProvider> { DispatcherProviderImpl() }
        single { MainActivityViewModel(app = get())}
        viewModel { ProtectWalletViewModel(app = get()) }
        viewModel { ProtectWalletTipsViewModel(app = get()) }
        viewModel { PassCodeViewModel(app = get(), sharedPreferences = get()) }
        viewModel { CreateWalletViewModel(app = get()) }
        viewModel { SecretPhraseViewModel(app = get(), sharedPreferences = get()) }
        viewModel { VerifyWordsViewModel(app = get(), sharedPreferences = get()) }
        viewModel { QRScannerViewModel(app = get()) }
        viewModel { ReceiveAeroViewModel(app = get()) }
        viewModel { MediaChooserViewModel(app = get()) }
        viewModel { MoreViewModel(app = get(), sharedPreferences = get(), accountRepository = get(), dispatcherProvider = get()) }
        viewModel { SearchViewModel(app = get(), dispatcherProvider = get(), vmRepository = get(), environmentRepository = get()) }

        viewModel { ImportWalletViewModel(
            app = get(),
            accountRepository = get(),
            dispatcherProvider = get(),
            sharedPreferences = get(),
            environmentRepository = get(),
            vmRepository = get(),
            mainActivityViewModel = get()
        )}

        viewModel { WalletOverviewViewModel(
            app = get(),
            esdtRepository = get(),
            environmentRepository = get(),
            accountRepository = get(),
            networkRepository = get(),
            sharedPreferences = get(),
            dispatcherProvider = get()
        )}

        viewModel { SplashFragmentViewModel(
            app = get(),
            dispatcherProvider = get(),
            sharedPreferences = get(),
            networkRepository = get(),
            accountRepository = get(),
            environmentRepository = get(),
            esdtRepository = get(),
            vmRepository = get()
        )}

        viewModel { CreateAccountViewModel(
            app = get(),
            environmentRepository = get(),
            dispatcherProvider = get(),
            accountRepository = get(),
            networkRepository = get(),
            sharedPreferences = get(),
            transactionRepository = get(),
            vmRepository = get(),
            mainActivityViewModel = get()
        )}

        viewModel { ProfileViewModel(
            app = get(),
            sharedPreferences = get(),
            environmentRepository = get(),
            dispatcherProvider = get(),
            accountRepository = get(),
            transactionRepository = get(),
            networkRepository = get(),
            vmRepository = get()
        )}

        viewModel { AccountViewModel(
            app = get(),
            sharedPreferences = get(),
            environmentRepository = get(),
            vmRepository = get()
        )}

        viewModel { WalletViewModel(
            app = get(),
            sharedPreferences = get()
        )}

        viewModel { HomeViewModel(
            app = get()
        )}

        viewModel { TransactionHistoryViewModel(
            app = get(),
            transactionRepository = get(),
            dispatcherProvider = get(),
            sharedPreferences = get()
        )}

        viewModel { ConfirmTransferViewModel(
            app = get(),
            networkRepository = get(),
            dispatcherProvider = get(),
            accountRepository = get(),
            transactionRepository = get(),
            sharedPreferences = get(),
            environmentRepository = get()
        )}

        viewModel { SendAeroViewModel(
            app = get(),
            networkRepository = get(),
            dispatcherProvider = get(),
            esdtRepository = get(),
            accountRepository = get(),
            sharedPreferences = get(),
            environmentRepository = get()
        )}
    }
}