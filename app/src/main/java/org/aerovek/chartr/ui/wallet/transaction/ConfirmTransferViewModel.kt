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
package org.aerovek.chartr.ui.wallet.transaction

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.data.repository.elrond.ElrondNetworkRepository
import org.aerovek.chartr.data.repository.elrond.TransactionRepository
import org.aerovek.chartr.data.util.toHex
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.FirebaseHelper
import java.lang.Exception
import java.math.BigInteger
import java.util.*

class ConfirmTransferViewModel(
    app: Application,
    private val networkRepository: ElrondNetworkRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val sharedPreferences: SharedPreferences,
    private val environmentRepository: EnvironmentRepository
    ) : BaseViewModel(app) {
    val transferAmount = MutableLiveData("")
    val usdAmount = MutableLiveData("")
    val recipientAddress = MutableLiveData("")
    val truncatedAddress = MutableLiveData("")
    val networkFee = MutableLiveData("")
    val closeButtonClicked = LiveEvent<Unit>()
    val showLoadingView = MutableLiveData(true)
    val showPinPad = LiveEvent<Unit>()
    val transactionComplete = LiveEvent<Unit>()
    val transactionFailed = LiveEvent<Unit>()

    private lateinit var transaction: Transaction

    private val senderAddress =
        Address.fromBech32(sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, null) ?: "")

    fun initialize(amount: String, usdAmountDisplay: String, asset: String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            val networkConfig = networkRepository.getNetworkConfig()
            val preparedAmount = prepareAmount(amount)

            // This is a ESDT transfer so we need to call a special function
            //  and requires the value field to be set to 0 on the transaction
            // The token and value need to be hex encoded
            val amountHex = preparedAmount.toBigInteger().toHex()
            println("AMOUNT HEX: $amountHex")
            val tokenIdHex = environmentRepository.selectedElrondEnvironment.aeroTokenId.toHex()
            println("TOKENID HEX: $tokenIdHex")
            val dataField = "ESDTTransfer@$tokenIdHex@$amountHex"

            transaction = prepareTransaction(
                recipientAddress.value!!,
                networkConfig,
                dataField,
                asset,
                preparedAmount
            )

            println("DATA FIELD: ${transaction.data}")
            println("TX COST: ${transaction.gasLimit}")

            showLoadingView.postValue(false)
        }
    }

    fun continueClicked() {
        showPinPad.postValue(Unit)
    }

    fun sendTransaction() {
        viewModelScope.launch(dispatcherProvider.IO) {
            showLoadingView.postValue(true)

            try {
                val wallet = Wallet.createFromPrivateKey(
                    sharedPreferences.getString(
                        AppConstants.UserPrefsKeys.WALLET_PRIVATE_KEY,
                        null
                    ) ?: ""
                )

                val sentTransaction = transactionRepository.sendTransaction(transaction, wallet)
                println("TX HASH: ${sentTransaction.txHash}")
                transactionComplete.postValue(Unit)
            } catch (e: ElrondException.CannotSignTransactionException) {
                FirebaseHelper.HandledException.logEvent("Transaction failed - could not sign transaction")
                transactionFailed.postValue(Unit)
            } catch (e: Exception) {
                FirebaseHelper.HandledException.logEvent("Transaction failed - ${e.message?.substring(0, 75)}")
                transactionFailed.postValue(Unit)
            }

            showLoadingView.postValue(false)
        }
    }

    fun closeClicked() {
        closeButtonClicked.postValue(Unit)
    }

    /**
     * Takes the user entered amount and converts it to the long erd string version
     * i.e., if user entered 100.125, we split 100 and 125 into a 2 element array,
     * then start padding zeros to the end of 125 until we effectively move the decimal 18 places to the right
     * to end up with 100125000000000000000
     *  */
    private fun prepareAmount(amount: String): String {
        val amountParts = amount.split(".")
        val wholeNumValue = amountParts[0]
        var decimalValue = "0"

        if (amountParts.size == 2) {
             decimalValue = amountParts[1]
        }

        // Initialize first with the whole number
        var amountString = wholeNumValue

        // If the fractional piece is not 18 digits pad zeros to it
        if (decimalValue.length in 1..17) {
            // First append the initial fractional value
            amountString += decimalValue

            // Start padding zeros
            for (i in decimalValue.length..17) {
                amountString += "0"
            }
        } else {
            // else just append the 18 digit fractional piece to the whole number piece
            amountString += decimalValue
        }

        return amountString
    }

    private fun prepareTransaction(
        toAddress: String,
        networkConfig: NetworkConfig,
        data: String,
        asset: String,
        preparedAmount: String
    ): Transaction {
        val receiverAddress = Address.fromBech32(toAddress)
        val senderAccount = accountRepository.getAccount(senderAddress)

        val transactionCost = 500000L

        var tx = Transaction(
            sender = senderAddress,
            receiver = receiverAddress,
            chainID = networkConfig.chainID,
            gasPrice = networkConfig.minGasPrice,
            version = networkConfig.minTransactionVersion,
            nonce = senderAccount.nonce
        )

        tx = if (asset.lowercase(Locale.getDefault()) == "egld") {
            tx.copy(value = preparedAmount.toBigInteger())
        } else {
            tx.copy(data = data, value = BigInteger.ZERO)
        }

        val gasLimit = try {
            val cost =
                transactionRepository.estimateCostOfTransaction(tx).toLong()
            if (cost > 0L) {
                cost
            } else {
                transactionCost
            }
        } catch (e: Exception) {
            transactionCost
        }

        return tx.copy(gasLimit = gasLimit)
    }
}