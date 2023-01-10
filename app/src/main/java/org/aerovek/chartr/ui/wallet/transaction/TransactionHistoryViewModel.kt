package org.aerovek.chartr.ui.wallet.transaction

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.cache.TransactionCache
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.transaction.TransactionOnNetwork
import org.aerovek.chartr.data.repository.elrond.TransactionRepository
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.ui.adapterItems.viewmodels.TransactionItemViewModel
import org.aerovek.chartr.util.*
import java.lang.Exception
import java.math.BigInteger
import java.time.*

class TransactionHistoryViewModel(
    app: Application,
    private val transactionRepository: TransactionRepository,
    private val dispatcherProvider: DispatcherProvider,
    sharedPreferences: SharedPreferences) : BaseViewModel(app) {

    val showLoading = MutableLiveData(true)
    val transactionItemModels = MutableLiveData<List<TransactionItemViewModel>>()
    val userAddress = sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, null)
    val displayNoTransactions = MutableLiveData(false)

    fun buildTransactionItems(fetchNew: Boolean) {
        viewModelScope.launch(dispatcherProvider.IO) {
            try {
                userAddress?.let { bech32 ->
                    val transactions =
                        if (fetchNew || TransactionCache.transactionHistory.isEmpty()) {
                            transactionRepository.getTransactions(address = Address.fromBech32(bech32))
                        } else {
                            TransactionCache.transactionHistory
                        }

                    // Update the cache
                    TransactionCache.transactionHistory = transactions

                    val itemViewModels = transactions.map { tx ->

                        val dateInstance = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(tx.timestamp * 1000),
                            ZoneId.systemDefault()
                        )
                        val localDate = dateInstance.toLocalDate()
                        val localTime = dateInstance.toLocalTime()

                        val timeAmPm = localTime.formattedAmPm()
                        val hourMin = localTime.formattedWith(HOUR_MINUTE_DATE_FORMATTER)

                        val dateTimeFormatted = localDate.formattedWith(DAY_OF_MONTH_DATE_FORMATTER)
                            .plus(" ")
                            .plus(localDate.formattedWith(MONTH_NAME_DATE_FORMATTER))
                            .plus(", $hourMin $timeAmPm")

                        var isReceive = false
                        val title = if (tx.sender.bech32 == userAddress) {
                            "Sent to ${tx.receiver.bech32.substring(0, 8)}...${tx.receiver.bech32.substring(tx.receiver.bech32.length - 8)}"
                        } else {
                            isReceive = true
                            "Received from ${tx.sender.bech32.substring(0, 8)}...${tx.sender.bech32.substring(tx.receiver.bech32.length - 8)}"
                        }

                        val tokenAmount = formatTokenLabel(tx, isReceive)

                        TransactionItemViewModel(
                            title,
                            dateTimeFormatted,
                            tokenAmount,
                            isReceive,
                            tx.status
                        )
                    }

                    transactionItemModels.postValue(itemViewModels)
                    displayNoTransactions.postValue(itemViewModels.isEmpty())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun formatUsdLabel(tx: TransactionOnNetwork, aeroPrice: Double, egldPrice: Double, isReceive: Boolean): String? {
        if (tx.value == BigInteger.ZERO && (tx.esdtAmount == null || tx.esdtAmount == "0")) {
            return ""
        }

        val plusOrMinus = if (isReceive) { "+" } else { "-" }

        // If value is zero must be a ESDT transfer, could also be a smart contract call
        // TODO handle SC case
        var amount = ""
        var price = 0.0

        if (tx.value == BigInteger.ZERO) {
            amount = tx.esdtAmount!!.formatTokenBalance(6)
            price = aeroPrice
        } else {
            amount = tx.value.toString().formatTokenBalance(6)
            price = egldPrice
        }

        println("TX AMOUNT: $amount")
        println("$plusOrMinus$${String.format("%.2f", amount.toBigDecimal() * price.toBigDecimal())}")
        return "$plusOrMinus$${String.format("%.2f", amount.toBigDecimal() * price.toBigDecimal())}"
    }

    private fun formatTokenLabel(tx: TransactionOnNetwork, isReceive: Boolean): String {
        if (tx.value == BigInteger.ZERO && (tx.esdtAmount == null || tx.esdtAmount == "0")) {
            return ""
        }

        val plusOrMinus = if (isReceive) { "+" } else { "-" }

        return if (tx.value == BigInteger.ZERO) {
            "$plusOrMinus${tx.esdtAmount?.formatTokenBalance(4)} ${tx.tokenId?.substring(0, tx.tokenId?.indexOf("-")!!)}"
        } else {
            "$plusOrMinus${tx.value.toString().formatTokenBalance(4)} EGLD"
        }
    }
}