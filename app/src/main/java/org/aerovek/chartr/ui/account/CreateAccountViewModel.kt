package org.aerovek.chartr.ui.account

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.cache.CountryCache
import org.aerovek.chartr.data.model.ChartrAccount
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.transaction.Transaction
import org.aerovek.chartr.data.model.elrond.wallet.Wallet
import org.aerovek.chartr.data.repository.elrond.AccountRepository
import org.aerovek.chartr.data.repository.elrond.ElrondNetworkRepository
import org.aerovek.chartr.data.repository.elrond.TransactionRepository
import org.aerovek.chartr.data.repository.elrond.VmRepository
import org.aerovek.chartr.data.util.toHex
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel
import org.aerovek.chartr.ui.MainActivityViewModel
import org.aerovek.chartr.util.DispatcherProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.StringBuilder
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class CreateAccountViewModel(
    app: Application,
    private val environmentRepository: EnvironmentRepository,
    private val sharedPreferences: SharedPreferences,
    private val dispatcherProvider: DispatcherProvider,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val networkRepository: ElrondNetworkRepository,
    private val vmRepository: VmRepository,
    private val mainActivityViewModel: MainActivityViewModel
    ) : BaseViewModel(app) {

    val showRequiredFieldsWarning = LiveEvent<Unit>()
    val usernameExists = LiveEvent<Unit>()
    val showNoBalanceMessage = LiveEvent<Unit>()
    val checkPermissions = LiveEvent<Unit>()
    val profileImageUri = MutableLiveData<Uri?>(null)
    val profileImageBitmap = MutableLiveData<Bitmap?>(null)
    val username = MutableLiveData("")
    val businessName = MutableLiveData<String?>(null)
    val tags = MutableLiveData<String?>(null)
    val showProgressBar = MutableLiveData(false)
    val saveSuccessful = LiveEvent<Unit>()
    val showPilotForm = MutableLiveData(false)
    var profileImageFile: File? = null
    private var selectedAccountType = AppConstants.ACCOUNT_TYPE_PERSONAL_VALUE
    private var selectedCategory = ""

    private val firebaseStorage = FirebaseStorage.getInstance("gs://aerovek-aviation.appspot.com")
    private val storageRef = firebaseStorage.reference

    private val senderAddress = sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, null) ?: ""
    private val scAddress = environmentRepository.selectedElrondEnvironment.scAddress
    private val wallet: Wallet = Wallet.createFromPrivateKey(
        sharedPreferences.getString(
            AppConstants.UserPrefsKeys.WALLET_PRIVATE_KEY,
            null
        ) ?: ""
    )

    val accountTypeList: LiveData<List<String>> = MutableLiveData(listOf("Business", "Personal"))
    val countryList = MutableLiveData(listOf(""))
    val categoryList = MutableLiveData(AppConstants.FLIGHT_CATEGORY_LIST)

    // Store off the country names
    private val countryNameList = CountryCache.countryList.map {
        it.name
    }

    var accountTypePosition = 1
        private set

    var countryPosition = 0
        private set

    var categoryPosition = 0
        private set

    init {
        countryList.postValue(countryNameList)
        println("DEFAULT ACCOUNT TYPE: $selectedAccountType")
    }

    fun changeProfileButtonClicked() {
        checkPermissions.postValue(Unit)
    }

    fun onAccountTypeSelected(index: Int) {
        if (index == 0) {
            showPilotForm.postValue(true)
            selectedAccountType = AppConstants.ACCOUNT_TYPE_BUSINESS_VALUE
        } else {
            showPilotForm.postValue(false)
            selectedAccountType = AppConstants.ACCOUNT_TYPE_PERSONAL_VALUE
            clearPilotDetails()
        }
    }

    fun onCategorySelected(index: Int) {
        selectedCategory = categoryList.value!![index]
    }

    fun save() {

        if (username.value == null || username.value!!.isEmpty() || selectedAccountType.isEmpty()) {
            showRequiredFieldsWarning.postValue(Unit)
            return
        }

        showProgressBar.postValue(true)
        viewModelScope.launch(dispatcherProvider.IO) {
            if (checkUsernameExists(username.value!!)) {
                usernameExists.postValue(Unit)
                showProgressBar.postValue(false)
            } else {

                val address = Address.fromBech32(
                    sharedPreferences.getString(
                        AppConstants.UserPrefsKeys.WALLET_ADDRESS,
                        null
                    )!!
                )
                val egldBalance = try {
                    val account = accountRepository.getAccount(address)
                    account.balance
                } catch (e: Exception) {
                    BigInteger.ZERO
                }

                if (egldBalance == BigInteger.ZERO) {
                    showNoBalanceMessage.postValue(Unit)
                    showProgressBar.postValue(false)
                } else {

                    val profileImgUrl = if (profileImageFile != null) {
                        "chartr/images/$senderAddress/${profileImageFile!!.name}"
                    } else { "" }

                    val config = networkRepository.getNetworkConfig()
                    val senderAccount =
                        accountRepository.getAccount(Address.fromBech32(senderAddress))

                    val timestampFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                    timestampFormatter.timeZone = TimeZone.getDefault()

                    val userAccount = ChartrAccount(
                        id = senderAddress,
                        accountType = "personal",
                        profileImageUrl = profileImgUrl,
                        username = username.value!!,
                        recordVersion = 0,
                        timestamp = timestampFormatter.format(Date(System.currentTimeMillis()))
                    )

                    val args = userAccount.toJsonArray().toHex()

                    val dataField = "createAccount@$args@${username.value?.toHex()}@${selectedAccountType.toHex()}"

                    // Estimate the cost of transaction https://docs.elrond.com/developers/gas-and-fees/egld-transfers/
                    // This doesn't work at all, so multiply by 10 should cover it in case the cost estimate endpoint fails below
                    var transactionCost = 50000L
                    transactionCost += (1500 * dataField.length) * 10

                    val transaction = Transaction(
                        sender = Address.fromBech32(senderAddress),
                        receiver = Address.fromBech32(scAddress),
                        value = BigInteger.ZERO,
                        data = dataField,
                        chainID = config.chainID,
                        gasPrice = config.minGasPrice,
                        version = config.minTransactionVersion,
                        nonce = senderAccount.nonce
                    )

                    val gasLimit = try {
                        val cost =
                            transactionRepository.estimateCostOfTransaction(transaction).toLong()
                        if (cost > 0L) {
                            cost
                        } else {
                            transactionCost
                        }
                    } catch (e: Exception) {
                        transactionCost
                    }

                    val tx = transactionRepository.sendTransaction(
                        transaction.copy(gasLimit = gasLimit),
                        wallet
                    )
                    println("CREATE ACCOUNT TX HASH: ${tx.txHash}")

                    if (tx.txHash.isNotEmpty()) {
                        println("CREATE ACCOUNT TX HASH: ${tx.txHash}")
                        // Save the account type to user prefs, this let's us know whether to show the create account button or not
                        // If this value exists it means the user already created an account
                        sharedPreferences
                            .edit()
                            .putString(AppConstants.UserPrefsKeys.ACCOUNT_TYPE, selectedAccountType)
                            .apply()

                        if (profileImageBitmap.value != null && profileImageFile != null) {
                            val imgRef = storageRef.child(profileImgUrl)
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            profileImageBitmap.value!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                            val imgData = byteArrayOutputStream.toByteArray()

                            imgRef.putBytes(imgData).addOnFailureListener { ex ->
                                ex.printStackTrace()
                                saveSuccessful.postValue(Unit)
                                showProgressBar.postValue(false)
                                mainActivityViewModel.postAccountCreated()
                            }.addOnSuccessListener {
                                println("IMAGE UPLOAD SUCCESSFUL")
                                it.metadata?.let { metadata ->
                                    println("IMAGE UPLOADED: ${metadata.name} - ${metadata.path}")
                                }
                                mainActivityViewModel.postAccountCreated()
                                saveSuccessful.postValue(Unit)
                                showProgressBar.postValue(false)
                            }

                        } else {
                            mainActivityViewModel.postAccountCreated()
                            saveSuccessful.postValue(Unit)
                            showProgressBar.postValue(false)
                        }
                    } else {
                        error("TX hash was null")
                    }
                }
            }
        }
    }

    private fun checkUsernameExists(userName: String): Boolean {
        val byteArray = userName.toByteArray(StandardCharsets.UTF_8)

        val sb = StringBuilder()
        for (byte in byteArray) {
            sb.append(String.format("%02X", byte))
        }

        val userNameHex = sb.toString()

        val queryContractInput = QueryContractInput(
            scAddress = environmentRepository.selectedElrondEnvironment.scAddress,
            funcName = "getAddress",
            args = listOf(userNameHex),
            caller = senderAddress,
            value = "0"
        )

        return try {
            // If this succeeds that means it found an existing username,
            // otherwise an exception is thrown (not sure why, need to investigate that)
            vmRepository.queryContractString(queryContractInput)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun clearPilotDetails() {
        businessName.postValue(null)
        tags.postValue(null)
        countryPosition = 0
        countryList.postValue(countryNameList)
        categoryPosition = 0
        categoryList.postValue(AppConstants.FLIGHT_CATEGORY_LIST)
    }
}