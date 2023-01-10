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
import org.aerovek.chartr.databinding.ProfileFragmentBinding
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

class ProfileFragment : BaseFragment() {
    private val viewModel: ProfileViewModel by inject()
    private lateinit var binding: ProfileFragmentBinding
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
        return DataBindingUtil.inflate<ProfileFragmentBinding>(inflater, R.layout.profile_fragment, container, false).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner

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

            viewModel.saveSuccessful.observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), "Profile saved - status pending", Snackbar.LENGTH_SHORT).show()
            }

            viewModel.checkPermissions.observe(viewLifecycleOwner) {
                if (permissionsGranted()) {
                    showBottomSheet()
                } else {
                    askPermissions()
                }
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

        mediaChooserFragment.show(this@ProfileFragment.parentFragmentManager, "media_chooser_bottomsheet")
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
        showGenericDialog(
            requireContext(),
            DialogModel(
                title = R.string.request_camera_title,
                message = R.string.request_camera_for_profile,
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
        fun newInstance() = ProfileFragment()
        private const val MAX_BIO_CHARS = 2000
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}