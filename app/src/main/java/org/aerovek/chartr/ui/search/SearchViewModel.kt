package org.aerovek.chartr.ui.search

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.cache.ChartrAccountsCache
import org.aerovek.chartr.data.model.ChartrAccount
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.repository.elrond.VmRepository
import org.aerovek.chartr.data.util.toHex
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.util.DispatcherProvider

class SearchViewModel(
    app: Application,
    private val dispatcherProvider: DispatcherProvider,
    private val vmRepository: VmRepository,
    private val environmentRepository: EnvironmentRepository
) : BaseViewModel(app) {

    val showLoading = MutableLiveData(true)
    val viewReady = LiveEvent<List<ChartrAccount>>()

    init {
        retrieveAccounts(false)
    }

    fun retrieveAccounts(fetchNew: Boolean) {
        viewModelScope.launch(dispatcherProvider.IO) {
            val contractInput = QueryContractInput(
                scAddress = environmentRepository.selectedElrondEnvironment.scAddress,
                funcName = "accountList",
                args = listOf(AppConstants.ACCOUNT_TYPE_BUSINESS_VALUE.toHex()),
                caller = AppConstants.ACCOUNT_RETRIEVAL_ADDRESS,
                value = "0"
            )

            val businessAccounts = if (fetchNew || ChartrAccountsCache.businessAccounts.isEmpty()) {
                vmRepository.getChartrAccountList(contractInput) ?: listOf()
            } else {
                ChartrAccountsCache.businessAccounts
            }.groupBy { item ->
                item.username
            }.map {
                it.value.sortedByDescending { account ->
                    account.recordVersion
                }.take(1)[0]
            }

            viewReady.postValue(businessAccounts)
        }
    }
}