package dk.itu.moapd.scootersharing.mgan.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import android.location.Address
import android.os.Looper
import dk.itu.moapd.scootersharing.mgan.R
import com.google.android.gms.location.*
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMapBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private val callback = OnMapReadyCallback {googleMap ->
        val amager = LatLng(55.6590778, 12.5908447)
        googleMap.addMarker(MarkerOptions().position(amager).title("Marker in Amager"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(amager))
    }

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
    }

    private var _binding: FragmentMapBinding? = null
    private val binding
        get() = checkNotNull(_binding)
    /**
     * The primary instance for receiving location updates.
     */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * This callback is called when `FusedLocationProviderClient` has a new `Location`.
     */
    private lateinit var locationCallback: LocationCallback

    override fun onResume() {
        super.onResume()
        subscribeToLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        unsubscribeToLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        startLocationAware()
    }



    private fun startLocationAware() {

        // Show a dialog to ask the user to allow the application to access the device's location.
        requestUserPermissions()

        // Start receiving location updates.
        fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())

        // Initialize the `LocationCallback`.
        locationCallback = object : LocationCallback() {

            /**
             * This method will be executed when `FusedLocationProviderClient` has a new location.
             *
             * @param locationResult The last known location.
             */

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Updates the user interface components with GPS data location.
                locationResult.lastLocation?.let { location ->
                    updateUI(location)
                }
            }
        }
    }

    private fun requestUserPermissions() {

        // An array with location-aware permissions.
        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Check which permissions is needed to ask to the user.
        val permissionsToRequest = permissionsToRequest(permissions)

        // Show the permissions dialogs to the user.
        if (permissionsToRequest.size > 0)
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSIONS_RESULT
            )
    }

    /**
     * Create an array with the permissions to show to the user.
     *
     * @param permissions An array with the permissions needed by this applications.
     *
     * @return An array with the permissions needed to ask to the user.
     */
    private fun permissionsToRequest(permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (checkSelfPermission(
                    requireContext(),
                    permission
                ) != PermissionChecker.PERMISSION_GRANTED)
                result.add(permission)
        return result
    }

    private fun updateUI(location: Location) {
        binding.apply {
            latitudeTextField?.editText?.setText(location.latitude.toString())
            longitudeTextField?.editText?.setText(location.longitude.toString())
            timeTextField?.editText?.setText(location.time.toDateString())
        }
        setAddress(location.latitude, location.longitude)
    }

    private fun Long.toDateString() : String {
        val date = Date(this)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }

    private fun setAddress(latitude: Double, longitude: Double) {
        if (!Geocoder.isPresent())
            return

        // Create the `Geocoder` instance.
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        // After `Tiramisu Android OS`, it is needed to use a listener to avoid blocking the main
        // thread waiting for results.
        val geocodeListener = Geocoder.GeocodeListener { addresses ->
            addresses.firstOrNull()?.toAddressString()?.let { address ->
                binding.addressTextField?.editText?.setText(address)
            }
        }

        // Return an array of Addresses that attempt to describe the area immediately surrounding
        // the given latitude and longitude.
        if (Build.VERSION.SDK_INT >= 33)
            geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
        else
            geocoder.getFromLocation(latitude, longitude, 1)?.let {  addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { address ->
                    binding.addressTextField?.editText?.setText(address)
                }
            }
    }

    /**
     * Converts the `Address` instance into a `String` representation.
     *
     * @return A `String` with the current address.
     */
    private fun Address.toAddressString() : String {
        val address = this

        // Create a `String` with multiple lines.
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(address.getAddressLine(0)).append("\n")
            append(address.postalCode).append(" ")
            append(address.locality).append("\n")
            append(address.countryName)
        }

        return stringBuilder.toString()
    }

    /**
     * This method checks if the user allows the application uses all location-aware resources to
     * monitor the user's location.
     *
     * @return A boolean value with the user permission agreement.
     */
    private fun checkPermission() =
        checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PermissionChecker.PERMISSION_GRANTED &&
                checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED


    /**
     * Subscribes this application to get the location changes via the `locationCallback()`.
     */
    private fun subscribeToLocationUpdates() {

        // Check if the user allows the application to access the location-aware resources.
        if (checkPermission())
            return

        // Sets the accuracy and desired interval for active location updates.
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5)
            .build()

        // Subscribe to location changes.
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    /**
     * Unsubscribes this application of getting the location changes from  the `locationCallback()`.
     */
    private fun unsubscribeToLocationUpdates() {
        // Unsubscribe to location changes.
        fusedLocationProviderClient
            .removeLocationUpdates(locationCallback)
    }

    /*
    override fun onMapReady(googleMap: GoogleMap) {

        // Check if the user allows the application to access the location-aware resources.
        if (checkPermission())
            return

        // Show the current device's location as a blue dot.
        googleMap.isMyLocationEnabled = true

        // Set the default map type.
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // Setup the UI settings state.
        googleMap.uiSettings.apply {
            isCompassEnabled = true
            isIndoorLevelPickerEnabled = true
            isMyLocationButtonEnabled = true
            isRotateGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }

        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)
    }

     */








}