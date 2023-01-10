package org.aerovek.chartr.ui.account

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
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
import org.aerovek.chartr.util.DispatcherProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel(
    app: Application,
    environmentRepository: EnvironmentRepository,
    private val sharedPreferences: SharedPreferences,
    private val dispatcherProvider: DispatcherProvider,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val networkRepository: ElrondNetworkRepository,
    private val vmRepository: VmRepository
) : BaseViewModel(app) {
    val showNoBalanceMessage = LiveEvent<Unit>()
    val checkPermissions = LiveEvent<Unit>()
    val profileImageUri = MutableLiveData<Uri?>(null)
    val profileImageBitmap = MutableLiveData<Bitmap?>(null)
    val username = MutableLiveData("Anon")
    val accountType = MutableLiveData("")
    val tags = MutableLiveData<String?>(null)
    val businessName = MutableLiveData<String?>(null)
    val rating = MutableLiveData("0.0")
    val showProgressBar = MutableLiveData(false)
    val showLoading = MutableLiveData(true)
    val saveSuccessful = LiveEvent<Unit>()
    val showPilotForm = MutableLiveData(false)
    var profileImageFile: File? = null
    val categoryName = MutableLiveData("")
    private var selectedCategory = ""
    private var selectedAccountType = ""
    private var existingProfileUrl = ""
    private var currentRecordVersion = -1
    private lateinit var currentChartrAccount: ChartrAccount

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

    val categoryList = MutableLiveData(AppConstants.FLIGHT_CATEGORY_LIST)

    var categoryPosition = 0
        private set

    init {

        viewModelScope.launch(dispatcherProvider.IO) {
            retrieveAccount()
            showLoading.postValue(false)
        }
    }

    fun onCategorySelected(index: Int) {
        selectedCategory = categoryList.value!![index]
    }

    fun profileImageClicked() {
        checkPermissions.postValue(Unit)
    }

    private fun retrieveAccount() {
        val queryContractInput = QueryContractInput(
            scAddress = scAddress,
            funcName = "getAccount",
            args = listOf(wallet.publicKeyHex),
            caller = senderAddress,
            value = "0"
        )
        val result = vmRepository.getChartrAccount(queryContractInput)
        if (result != null) {
            currentChartrAccount = result

            selectedAccountType = result.accountType
            username.postValue(result.username)
            currentRecordVersion = result.recordVersion

            if (result.accountType == AppConstants.ACCOUNT_TYPE_PERSONAL_VALUE) {
                accountType.postValue("Personal Account")
                showPilotForm.postValue(false)
            } else {
                accountType.postValue("Business Account")
                showPilotForm.postValue(true)

                result.businessProfile?.businessCategory?.let { cat ->
                    categoryPosition = categoryList.value!!.indexOf(cat)
                    categoryName.postValue(cat)
                    selectedCategory = cat
                }

                result.businessProfile?.businessName?.let { bn ->
                    businessName.postValue(bn)
                }

                result.businessProfile?.searchTags?.let { tagsArray ->
                    tags.postValue(tagsArray.joinToString(","))
                }
            }

            // Retrieve the image from firebase if we have one
            result.profileImageUrl?.let {
                existingProfileUrl = it
                storageRef.child(it).downloadUrl.addOnSuccessListener { uri ->
                    profileImageUri.postValue(uri)
                }.addOnFailureListener { ex ->
                    ex.printStackTrace()
                }
            }
        }
    }

    fun save() {

        showProgressBar.postValue(true)
        viewModelScope.launch(dispatcherProvider.IO) {

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

                val config = networkRepository.getNetworkConfig()
                val senderAccount =
                    accountRepository.getAccount(Address.fromBech32(senderAddress))

                currentChartrAccount.profileImageUrl = if (profileImageFile != null) {
                    "chartr/images/$senderAddress/${profileImageFile!!.name}"
                } else {
                    currentChartrAccount.profileImageUrl
                }

                val timestampFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                timestampFormatter.timeZone = TimeZone.getDefault()

                currentChartrAccount.recordVersion = currentChartrAccount.recordVersion + 1
                currentChartrAccount.businessProfile?.businessCategory = selectedCategory
                currentChartrAccount.businessProfile?.searchTags = tags.value?.split(',')
                currentChartrAccount.businessProfile?.businessName = businessName.value ?: ""
                currentChartrAccount.timestamp = timestampFormatter.format(Date(System.currentTimeMillis()))

                val args = currentChartrAccount.toJsonArray().toHex()
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

                // We still don't know if this transaction was successful or not,
                // but if we have a hash at least we know it went through. If the tx
                // fails and we upload a new image to firebase that image probably will never get consumed
                // because the account won't have the new url to that image. So what will happen is that
                // the account will have the old url but the old image will have been deleted.
                if (tx.txHash.isNotEmpty()) {
                    println("CREATE ACCOUNT TX HASH: ${tx.txHash}")

                    if (profileImageBitmap.value != null && profileImageFile != null && !currentChartrAccount.profileImageUrl.isNullOrEmpty()) {
                        println("PROFILE IMAGE FIREBASE PATH: ${currentChartrAccount.profileImageUrl}")

                        val imgRef = storageRef.child(currentChartrAccount.profileImageUrl!!)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        profileImageBitmap.value!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                        val imgData = byteArrayOutputStream.toByteArray()

                        imgRef.putBytes(imgData).addOnFailureListener { ex ->
                            ex.printStackTrace()
                            saveSuccessful.postValue(Unit)
                            showProgressBar.postValue(false)
                        }.addOnSuccessListener {
                            if (existingProfileUrl.isNotEmpty()) {
                                // Remove the old image
                                val existingImgRef = storageRef.child(existingProfileUrl)
                                existingImgRef.delete().addOnSuccessListener {
                                    println("----> EXISTING IMAGE DELETED SUCCESSFULLY")
                                }.addOnFailureListener { ex ->
                                    println("----> FAILED TO DELETE EXISTING IMAGE: ${ex.message}")
                                    ex.printStackTrace()
                                }
                            }

                            println("IMAGE UPLOAD SUCCESSFUL")
                            it.metadata?.let { metadata ->
                                println("IMAGE UPLOADED: ${metadata.name} - ${metadata.path}")
                            }
                            saveSuccessful.postValue(Unit)
                            showProgressBar.postValue(false)
                        }

                    } else {
                        saveSuccessful.postValue(Unit)
                        showProgressBar.postValue(false)
                    }
                }
            }
        }
    }
}