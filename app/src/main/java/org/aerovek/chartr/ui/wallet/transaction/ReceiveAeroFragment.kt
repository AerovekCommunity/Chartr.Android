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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.ReceiveAeroFragmentBinding
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseFragment
import org.koin.android.ext.android.inject


class ReceiveAeroFragment : BaseFragment() {
    private val sharedPreferences: SharedPreferences by inject()
    private val viewModel: ReceiveAeroViewModel by inject()
    private lateinit var binding: ReceiveAeroFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<ReceiveAeroFragmentBinding>(inflater, R.layout.receive_aero_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            val address = sharedPreferences.getString(AppConstants.UserPrefsKeys.WALLET_ADDRESS, "")
            viewModel.addressText.postValue(address)

            try {
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(address, BarcodeFormat.QR_CODE, 240, 240)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val black = ContextCompat.getColor(requireContext(), R.color.black)
                val white = ContextCompat.getColor(requireContext(), R.color.white)

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val color = if (bitMatrix.get(x, y)) { black } else { white }
                        bitmap.setPixel(x, y, color)
                    }
                }

                binding.qrImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            viewModel.addressCopied.observe(viewLifecycleOwner) { addressText ->
                val clipboardManager = ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)!!
                val clip = ClipData.newPlainText("address", addressText)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Address copied to clipboard", Toast.LENGTH_LONG).show()
            }
        }.root
    }

    companion object {
        fun newInstance() = ReceiveAeroFragment()
    }
}