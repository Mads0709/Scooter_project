package dk.itu.moapd.scootersharing.mgan.fragments

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMapBinding
import dk.itu.moapd.scootersharing.mgan.services.GeolocationService
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var map : GoogleMap? = null
    private var usermarker : Marker? = null

    private val callback = OnMapReadyCallback {googleMap ->
        map = googleMap
        // Get a reference to the "scooters" node in the database
        val scootersRef = database.child("scooters")

        // Attach a listener to the "scooters" node to retrieve the data
        scootersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Iterate over the child nodes of the "scooters" node to retrieve the latitude and longitude data
                for (scooterSnapshot in snapshot.children) {
                    val latitude = scooterSnapshot.child("latitude").value as Double
                    val longitude = scooterSnapshot.child("longitude").value as Double
                    val name = scooterSnapshot.child("name").value as String
                    val isused = scooterSnapshot.child("used").value as Boolean

                    // Create a LatLng object and add a marker to the map
                    val location = LatLng(latitude, longitude)

                    if (!isused)
                        map?.addMarker(MarkerOptions().position(location).title(name))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
                Log.d(TAG, "Could not get data from Firebase realtime")
            }
        })
    }

    private val TAG = MapFragment::class.java.simpleName

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
    }


    private var _binding: FragmentMapBinding? = null
    private val binding
        get() = checkNotNull(_binding)


    // A reference to the service used to get location updates.
    private var mService: GeolocationService? = null

    // Tracks the bound state of the service.
    private var mBound = false

    // Monitors the state of the connection to the service.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: GeolocationService.LocalBinder = service as GeolocationService.LocalBinder
            mService = binder.getService()
            mBound = true
            subscribeToService()
            Log.i(TAG, "Service works")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService?.unsubscribeToLocationUpdates()
            mService = null
            mBound = false
            Log.i(TAG, "Service does not work")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestUserPermissions()

        Intent(context, GeolocationService::class.java).also { intent ->
            requireActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
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

    private fun updateUI(lat : Double, long: Double, address: String)  {
        binding.apply {
            latitudeTextField?.editText?.setText(lat.toString())
            longitudeTextField?.editText?.setText(long.toString())
            addressTextField?.editText?.setText(address)
        }
    }

    /**
     * Create a set of dialogs to show to the users and ask them for permissions to get the device's
     * resources.
     */
    private fun requestUserPermissions() {

        // An array with location-aware permissions.
        val permissions: ArrayList<String> = ArrayList()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Check which permissions is needed to ask to the user.
        val permissionsToRequest = permissionsToRequest(permissions)

        // Show the permissions dialogs to the user.
        if (permissionsToRequest.size > 0){
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            )
            {
                usersPermissions ->
                val allGranted = usersPermissions.all { it.value }
                if (allGranted)
                    subscribeToService()
            }
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }

    }

    private fun subscribeToService(){
        mService?.subscribeToLocationUpdates(
            {
                lastLocation ->
                     // Change the color of the default marker to blue
                    val blueMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude,), 18f))
                    usermarker = map?.addMarker(MarkerOptions().position(LatLng(lastLocation.latitude, lastLocation.longitude)).title("My position").icon(blueMarker))
                    updateUI(lastLocation.latitude, lastLocation.longitude , "")
            },
            {
                lat, long, address ->
                    usermarker?.position = LatLng(lat, long)
                    updateUI(lat, long , address)
            }


        )
    }

}