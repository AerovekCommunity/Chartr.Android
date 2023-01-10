package org.aerovek.chartr.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.HomeFragmentBinding
import org.aerovek.chartr.ui.BaseFragment
import org.aerovek.chartr.util.DialogModel
import org.aerovek.chartr.util.NavigationObserver
import org.aerovek.chartr.util.showGenericDialog
import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {
    private val viewModel: HomeViewModel by inject()
    private lateinit var binding: HomeFragmentBinding
    //private val args: HomeFragmentArgs by navArgs()
    private lateinit var permissionsLauncher: ActivityResultLauncher<String>
    private var googleMap: GoogleMap? = null
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var showPinPad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted && permissionsGranted() && checkLocationServicesEnabled()) {
                setupMap()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<HomeFragmentBinding>(inflater, R.layout.home_fragment, container, false).apply {

            binding = this
            viewModel = viewModel
            lifecycleOwner = this@HomeFragment.viewLifecycleOwner

            /* TODO reinstate the map after we discuss use cases for it
            if (googleMap == null) {
                if (permissionsGranted()) {
                    setupMap()
                } else {
                    requestLocationPermissions()
                }
            }
             */

            searchTextField.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
            }

            setupBackPressListener {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
        }.root
    }

    override fun onMyLocationClick(location: Location) {
        println("USER ALTITUDE: ${location.altitude}")
        println("USER LAT: ${location.latitude}")
        println("USER LONG: ${location.longitude}")
        println("USER LOCATION ACCURACY: ${location.accuracy}")
    }

    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    override fun onMapReady(map: GoogleMap) {
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isScrollGesturesEnabled = false
        googleMap = map

        map.setOnMyLocationButtonClickListener(this@HomeFragment)
        map.setOnMyLocationClickListener(this@HomeFragment)

        if (checkLocationServicesEnabled()) {
            loadUserLocation()
        }
    }

    private fun checkLocationServicesEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showLocationPermissionDialog()
        } else {
            permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLocationPermissionDialog() {
        showGenericDialog(
            requireContext(),
            DialogModel(
                title = null,
                message = R.string.request_location_permissions,
                positive = R.string.continue_title,
                negative = R.string.cancel,
                positiveFun = { permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                negativeFun = { },
                exitVisibility = View.VISIBLE
            ), dismissListener = null
        )
    }

    @SuppressLint("MissingPermission")
    private fun loadUserLocation() {
        googleMap?.isMyLocationEnabled = true
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener { location ->
            location?.let { loc ->
                googleMap?.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition(
                            LatLng(
                                loc.latitude,
                                loc.longitude
                            ), 10f, 0f, 0f
                        )
                    )
                )
            }
        }.addOnFailureListener { ex ->
            ex.printStackTrace()
        }
    }

    private fun permissionsGranted() = REQUIRED_PERMISSIONS.all {
        println("PERMISSIONS GRANTED:$it - ${ContextCompat.checkSelfPermission(requireContext(), it)}")
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).toTypedArray()

        fun newInstance() = HomeFragment()
    }
}