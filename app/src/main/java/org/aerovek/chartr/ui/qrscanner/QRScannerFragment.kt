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
package org.aerovek.chartr.ui.qrscanner

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.QrScannerFragmentBinding
import org.aerovek.chartr.util.DispatcherProvider
import org.aerovek.chartr.util.makeBottomsheetFullScreen
import org.koin.android.ext.android.inject
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerFragment : BottomSheetDialogFragment() {
    private val dispatcherProvider: DispatcherProvider by inject()
    private val viewModel: QRScannerViewModel by inject()
    private lateinit var binding: QrScannerFragmentBinding
    private lateinit var dismissListener: QRDismissListener
    private lateinit var cameraExecutor: ExecutorService
    lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var scannedAddressText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<QrScannerFragmentBinding>(inflater, R.layout.qr_scanner_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            startCamera()
        }.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.makeBottomsheetFullScreen(this, true)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener.onDismiss(scannedAddressText)
        cameraExecutor.shutdown()
    }

    fun setup(listener: QRDismissListener) {
        dismissListener = listener
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    viewModel.viewModelScope.launch(dispatcherProvider.Main) {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }
                }

            // Set back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val analyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    val analyzer = ImageAnalyzer { scannedText ->
                        this.scannedAddressText = scannedText
                        dismiss()
                    }
                    it.setAnalyzer(cameraExecutor, analyzer)
                }

            try {
                viewModel.viewModelScope.launch(dispatcherProvider.Main) {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(requireActivity(), cameraSelector, preview, analyzer)
                }
            } catch (e: Exception) {
                println("Error in camera listener - ERROR: ${e.message}\n\t STACK TRACE: ${e.printStackTrace()}")
            }

        }, cameraExecutor)
    }
}

internal class ImageAnalyzer(private val callback: (scannedText: String?) -> Unit) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = imageProxy.image?.let {
            InputImage.fromMediaImage(it, rotationDegrees)
        }!!

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)
        scanner.process(image).addOnSuccessListener { barcodes ->
            if (barcodes.size == 0) {
                imageProxy.close()
            } else {
                for (barcode in barcodes) {
                    println("Barcode display value: ${barcode.displayValue}")
                    if (barcode.valueType == Barcode.TYPE_TEXT) {
                        callback(barcode.displayValue)
                        imageProxy.close()
                    }
                }
            }
        }.addOnFailureListener { ex ->
            println("Failed to process input image: ERROR - ${ex.message} \n\t STACKTRACE: ${ex.printStackTrace()}")
            imageProxy.close()
        }
    }
}

interface QRDismissListener {
    fun onDismiss(walletAddress: String?)
}
