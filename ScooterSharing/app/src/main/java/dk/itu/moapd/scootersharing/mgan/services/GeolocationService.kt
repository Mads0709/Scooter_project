package dk.itu.moapd.scootersharing.mgan.services

import android.Manifest
import android.app.*
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.*
import java.util.*


/*
This CODE is inspired from https://github.com/android/location-samples/blob/432d3b72b8c058f220416958b444274ddd186abd/LocationUpdatesForegroundService/app/src/main/java/com/google/android/gms/location/sample/locationupdatesforegroundservice/LocationUpdatesService.java
by frankgh
 */
class GeolocationService : Service() {
    /**
     * Provides access to the Fused Location Provider API.
     */
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    /**
     * Callback for changes in location.
     */
    private lateinit var mLocationCallback: LocationCallback

    private var updateFunc: (Double, Double, String) -> Unit = {_: Double, _: Double, _: String -> }

    companion object {
        private const val PACKAGENAME = "dk.itu.moapd.scootersharing.mgan.services"
        val EXTRA_LOCATION: String = PACKAGENAME + ".location"

        val ACTION_BROADCAST: String = PACKAGENAME + ".broadcast"

        private const val  EXTRA_STARTED_FROM_NOTIFICATION: String = PACKAGENAME + ".started_from_notification";
        val TAG = GeolocationService::class.java.simpleName
        private const val KEY_REQUESTING_LOCATION_UPDATES = "requesting location updates"


        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    }
    override fun onBind(intent: Intent?): IBinder? {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()")
        startLocationAware()
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        fun getService(): GeolocationService = this@GeolocationService
    }


    private fun setAddress(latitude: Double, longitude: Double) {
        if (!Geocoder.isPresent())
            return

        // Create the `Geocoder` instance.
        val geocoder = Geocoder(this, Locale.getDefault())


        // Return an array of Addresses that attempt to describe the area immediately surrounding
        // the given latitude and longitude.
        if (Build.VERSION.SDK_INT >= 33) {
            // After `Tiramisu Android OS`, it is needed to use a listener to avoid blocking the main
            // thread waiting for results.
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { address ->
                    //binding.addressTextField?.editText?.setText(address)
                }

            }
            geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
        }
        else
            geocoder.getFromLocation(latitude, longitude, 1)?.let {  addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { address ->
                    //binding.addressTextField?.editText?.setText(address)
                    updateFunc(latitude, longitude, address)
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
        PermissionChecker.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PermissionChecker.PERMISSION_GRANTED

    /**
     * Start the location-aware instance and defines the callback to be called when the GPS sensor
     * provides a new user's location.
     */
    private fun startLocationAware() {

        // Show a dialog to ask the user to allow the application to access the device's location.
        // Start receiving location updates.
        mFusedLocationClient = LocationServices
            .getFusedLocationProviderClient(this)

        // Initialize the `LocationCallback`.
        mLocationCallback = object : LocationCallback() {

            /**
             * This method will be executed when `FusedLocationProviderClient` has a new location.
             *
             * @param locationResult The last known location.
             */
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Updates the user interface components with GPS data location.
                locationResult.lastLocation?.let { location ->
                    setAddress(location.latitude, location.longitude)
                }
            }
        }
    }



    /**
     * Subscribes this application to get the location changes via the `locationCallback()`.
     */
    public fun subscribeToLocationUpdates(initFunc: (Location) -> Unit, updateFunc: (Double, Double, String) -> Unit) {
        // Check if the user allows the application to access the location-aware resources.
        if (checkPermission())
            return

        this.updateFunc = updateFunc

        // Sets the accuracy and desired interval for active location updates.
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5)
            .build()

        val locationResult = mFusedLocationClient.lastLocation
        locationResult.addOnCompleteListener {task ->
            if(task.isSuccessful) {
                val lastLocationFound = task.result
                initFunc(lastLocationFound)
            }
        }
        // Subscribe to location changes.
        mFusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback, Looper.getMainLooper()
        )
    }


    /**
     * Unsubscribes this application of getting the location changes from  the `locationCallback()`.
     */
    public fun unsubscribeToLocationUpdates() {
        // Unsubscribe to location changes.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

}