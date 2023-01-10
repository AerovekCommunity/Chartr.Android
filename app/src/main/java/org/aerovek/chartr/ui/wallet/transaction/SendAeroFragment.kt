package org.aerovek.chartr.ui.wallet.transaction

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.aerovek.chartr.R
import org.aerovek.chartr.data.exceptions.ElrondException
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.databinding.SendAeroFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.qrscanner.QRDismissListener
import org.aerovek.chartr.ui.qrscanner.QRScannerFragment
import org.aerovek.chartr.util.DialogModel
import org.aerovek.chartr.util.showGenericDialog
import org.koin.android.ext.android.inject
import java.lang.Exception
import java.math.BigDecimal

class SendAeroFragment : BaseFragment() {
    private val viewModel: SendAeroViewModel by inject()
    lateinit var binding: SendAeroFragmentBinding
    private var continueMenuItem: MenuItem? = null
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted && permissionsGranted()) {
                showScannerView()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<SendAeroFragmentBinding>(inflater, R.layout.send_aero_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.toolbar_continue, menu)
                    continueMenuItem = menu.findItem(R.id.action_continue)

                    if (binding.recipientEditText.text != null && binding.amountEditText.text != null) {
                        continueMenuItem?.isEnabled = binding.recipientEditText.text!!.isNotEmpty() && binding.amountEditText.text!!.isNotEmpty()
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_continue -> {
                            if (isValidAddress(viewModel.recipientAddressText.value ?: "")) {
                                val confirmFragment = ConfirmTransferFragment.newInstance()
                                val asset = if (sendAeroCheckbox.isChecked) {
                                    "AERO"
                                } else { "EGLD" }

                                confirmFragment.setup(object : ConfirmTransferDismissListener {
                                    override fun onDismiss(didCompleteTransaction: Boolean) {
                                        if (!didCompleteTransaction) {
                                            showGenericDialog(
                                                requireContext(),
                                                DialogModel(
                                                    title = null,
                                                    message = R.string.transaction_failed,
                                                    positive = R.string.ok,
                                                    negative = R.string.dismiss,
                                                    positiveFun = { findNavController().navigateUp() },
                                                    negativeFun = { findNavController().navigateUp() },
                                                    exitVisibility = View.VISIBLE
                                                ), dismissListener = null
                                            )
                                        } else {
                                            findNavController().navigateUp()
                                        }
                                    }
                                },
                                    viewModel.amountText.value!!,
                                    viewModel.usdAmountText.value!!,
                                    viewModel.recipientAddressText.value!!,
                                    asset)

                                confirmFragment.show(this@SendAeroFragment.parentFragmentManager, "confirm_transaction_frag")

                            } else {
                                Toast.makeText(requireContext(), "Invalid wallet address!", Toast.LENGTH_LONG).show()
                                viewModel.recipientAddressText.postValue("")
                            }
                        }
                    }
                    return true
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

            binding.recipientEditText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun afterTextChanged(p0: Editable?) { }

                override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                    sequence?.toString()?.let {
                        viewModel.recipientAddressText.postValue(sequence.toString())
                        continueMenuItem?.isEnabled = it.isNotEmpty()
                    }
                }
            })

            binding.amountEditText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                    sequence?.toString()?.let {
                        val amount = try {
                            it.toBigDecimal()
                        } catch (e: Exception) {
                            BigDecimal.ZERO
                        }

                        if (it.isNotEmpty()) { //&& amount > BigDecimal.ZERO) {
                            println("DOUBLE AMOUNT = $amount")
                            val usdAmount = if (binding.sendAeroCheckbox.isChecked) {
                                (amount * viewModel.aeroPrice.toBigDecimal())
                            } else {
                                (amount * viewModel.egldPrice.toBigDecimal())
                            }

                            viewModel.usdAmountText.postValue("~ $${String.format("%.2f", usdAmount)}")
                            continueMenuItem?.isEnabled = viewModel.recipientAddressText.value!!.isNotEmpty()
                        } else {
                            viewModel.usdAmountText.postValue("")
                            viewModel.amountText.postValue("")
                            continueMenuItem?.isEnabled = false
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) { }
            })

            viewModel.sendAeroChecked.observe(viewLifecycleOwner) { isChecked ->
                binding.sendEgldCheckbox.isChecked = !isChecked
                if (!isChecked) {
                    viewModel.amountText.postValue("")
                    viewModel.usdAmountText.postValue("")
                }
            }

            viewModel.sendEgldChecked.observe(viewLifecycleOwner) { isChecked ->
                binding.sendAeroCheckbox.isChecked = !isChecked
                if (!isChecked) {
                    viewModel.amountText.postValue("")
                    viewModel.usdAmountText.postValue("")
                }
            }

            viewModel.showInvalidAddress.observe(viewLifecycleOwner) {
                Snackbar.make(requireContext(), requireView(), getString(R.string.invalid_address), Snackbar.LENGTH_LONG).show()
            }

            viewModel.scannerTapped.observe(viewLifecycleOwner) {
                if (permissionsGranted()) {
                    showScannerView()
                } else {
                    askPermissions()
                }
            }
        }.root
    }

    private fun showScannerView() {
        val scannerView = QRScannerFragment()
        scannerView.setup(object : QRDismissListener {
            override fun onDismiss(walletAddress: String?) {
                walletAddress?.let {
                    if (isValidAddress(walletAddress)) {
                        binding.recipientEditText.setText(walletAddress)
                    } else {
                        binding.recipientEditText.text?.clear()
                        Toast.makeText(requireContext(), "Invalid address scanned, please try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        scannerView.show(this@SendAeroFragment.parentFragmentManager, "qr_scanner_view")
    }


    private fun askPermissions() {
        showGenericDialog(
            requireContext(),
            DialogModel(
                title = R.string.request_camera_title,
                message = R.string.request_camera_for_qr,
                positive = R.string.ok,
                negative = R.string.cancel,
                positiveFun = { permissionsLauncher.launch(Manifest.permission.CAMERA) },
                negativeFun = { },
                exitVisibility = View.VISIBLE
            ), dismissListener = null
        )
    }

    private fun permissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        fun newInstance() = SendAeroFragment()
    }

    private fun isValidAddress(addressText: String): Boolean {
        return try {
            Address.fromBech32(addressText)
            true
        } catch (e: ElrondException.CannotCreateBech32AddressException) {
            false
        }
    }
}