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
package org.aerovek.chartr.ui.account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
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
import org.aerovek.chartr.databinding.CreateAccountFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.ui.media.MediaChooserDismissListener
import org.aerovek.chartr.ui.media.MediaChooserFragment
import org.aerovek.chartr.util.DialogModel
import org.aerovek.chartr.util.showGenericDialog
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateAccountFragment : BaseFragment() {
    private val viewModel: CreateAccountViewModel by inject()
    private lateinit var binding: CreateAccountFragmentBinding
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private var currentPhotoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted && permissionsGranted()) {
                showBottomSheet()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.let { intent ->
                intent.extras?.let { bundle ->
                    val bitmap = bundle.get("data") as Bitmap

                    currentPhotoFile?.let {
                        viewModel.profileImageFile = it
                    }

                    viewModel.profileImageBitmap.postValue(bitmap)
                }
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { uri ->
                println("GALLERY IMAGE URI: ${uri.path}")

                val bitmap = if (Build.VERSION.SDK_INT < 29) {
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }

                var newBitmap = Bitmap.createBitmap(bitmap)
                newBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true)

                // Create a new file so we can rename it
                val originalFile = File(uri.path!!)
                val newFile = try {
                    createImageFile()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                    null
                }

                originalFile.renameTo(newFile!!)
                println("New Filname: ${newFile.name}")

                viewModel.profileImageFile = newFile
                viewModel.profileImageBitmap.postValue(newBitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<CreateAccountFragmentBinding>(inflater, R.layout.create_account_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

            setupBackPressListener { findNavController().navigateUp() }

            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.toolbar_save, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_save -> {
                            viewModel.save()
                        }
                    }
                    return true
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

            viewModel.showRequiredFieldsWarning.observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), getString(R.string.create_account_required_fields_message), Snackbar.LENGTH_SHORT).show()
            }

            viewModel.saveSuccessful.observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), "Profile submitted - status pending", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.homeFragment, false)
            }

            viewModel.checkPermissions.observe(viewLifecycleOwner) {
                if (permissionsGranted()) {
                    showBottomSheet()
                } else {
                    askPermissions()
                }
            }

            viewModel.usernameExists.observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), getString(R.string.username_exists), Snackbar.LENGTH_LONG).show()
            }

            viewModel.showNoBalanceMessage.observe(viewLifecycleOwner) {
                showGenericDialog(
                    requireContext(),
                    DialogModel(
                        title = null,
                        message = R.string.network_fee_required_message,
                        positive = R.string.ok,
                        negative = R.string.cancel,
                        positiveFun = { },
                        negativeFun = { findNavController().popBackStack(R.id.homeFragment, false) },
                        exitVisibility = View.VISIBLE
                    ), dismissListener = null
                )
            }

        }.root
    }

    private fun showBottomSheet() {
        val mediaChooserFragment = MediaChooserFragment()
        mediaChooserFragment.setDismissListener(object : MediaChooserDismissListener {
            override fun onDismiss(chosenType: Int?) {
                chosenType?.let {
                    when (it) {
                        MediaChooserFragment.TYPE_TAKE_PHOTO -> openCamera()
                        MediaChooserFragment.TYPE_CHOOSE_FROM_LIBRARY -> openGallery()
                    }
                }
            }
        })

        mediaChooserFragment.show(this@CreateAccountFragment.parentFragmentManager, "media_chooser_bottomsheet")
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            val photoFile = try {
                createImageFile()
            } catch (e: IOException) {
                println("ERROR CREATING IMAGE FILE in EditAccountFragment:137 - ${e.printStackTrace()}")
                null
            }

            photoFile?.also {
                currentPhotoFile = it
                cameraLauncher.launch(this)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(timeStamp, ".jpg", storageDir).apply {
            currentPhotoPath = this.absolutePath
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(MediaStore.INTENT_ACTION_MEDIA_SEARCH)
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        galleryLauncher.launch(Intent.createChooser(galleryIntent, "Select image")) //galleryIntent)
    }


    private fun askPermissions() {
        val dialogMessage = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            R.string.request_exteral_storage
        } else {
            R.string.request_camera_for_profile
        }

        showGenericDialog(
            requireContext(),
            DialogModel(
                title = R.string.request_camera_title,
                message = dialogMessage,
                positive = R.string.ok,
                negative = R.string.cancel,
                positiveFun = {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        permissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        permissionsLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                negativeFun = { },
                exitVisibility = View.VISIBLE
            ), dismissListener = null
        )

    }

    private fun permissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val MAX_BIO_CHARS = 2000
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}