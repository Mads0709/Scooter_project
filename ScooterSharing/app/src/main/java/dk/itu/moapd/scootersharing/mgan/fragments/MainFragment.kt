/**
 * * MIT License
 * Copyright (c) 2023 Christian Bank Lauridsen & Mads Greve Andersen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dk.itu.moapd.scootersharing.mgan.fragments

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.activites.LoginActivity
import dk.itu.moapd.scootersharing.mgan.adapter.CustomArrayAdapter
import dk.itu.moapd.scootersharing.mgan.activites.mgan.RidesDB
import dk.itu.moapd.scootersharing.mgan.activites.mgan.Scooter
import dk.itu.moapd.scootersharing.mgan.adapter.ItemClickListener
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMainBinding


/**
 * A fragment class with methods to manage the main fragment of the ScooterSharing application.
 */
class MainFragment : Fragment(), ItemClickListener {

    /**
     * A set of static attributes used in this fragment class.
     */
    companion object{
        lateinit var ridesDB : RidesDB
        private lateinit var adapter: CustomArrayAdapter
        private const val ALL_PERMISSIONS_RESULT = 1011
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = checkNotNull(_binding)


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    /**
     * The entry point of the Firebase Storage SDK.
     */
    private lateinit var storage: FirebaseStorage

    /**
     * The primary instance for receiving location updates.
     */
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * This callback is called when `FusedLocationProviderClient` has a new `Location`.
     */
    private lateinit var locationCallback: LocationCallback




    /**
     * Called when the fragment is starting. This is where most initialization should go: calling
     * `setContentView(int)` to inflate the fragment's UI, using `findViewById()` to
     * programmatically interact with widgets in the UI, calling
     * `managedQuery(android.net.Uri, String[], String, String[], String)` to retrieve cursors for
     * data being displayed, etc.
     *
     * You can call `finish()` from within this function, in which case `onDestroy()` will be
     * immediately called after `onCreate()` without any of the rest of the fragment lifecycle
     * (`onStart()`, `onResume()`, onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     *
     * @param savedInstanceState If the fragment is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in `onSaveInstanceState()`.
     * <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
        // intialize firebase realtime
        database = Firebase.database("https://moapd-2023-e061c-default-rtdb.europe-west1.firebasedatabase.app/").reference
        storage = Firebase.storage("gs://moapd-2023-e061c.appspot.com")

        // Start the location-aware method.
        startLocationAware()


        auth.currentUser?.let {
            val query = database.child("scooters")
                .orderByChild("createdAt")
            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()
            //Create the custom adapter to populate the adapter
            adapter = CustomArrayAdapter(this, options)
        }
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
            /*
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Updates the user interface components with GPS data location.
                locationResult.lastLocation?.let { location ->
                    updateUI(location)
                }
            }

             */
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
            if (checkSelfPermission(requireContext(),permission) != PermissionChecker.PERMISSION_GRANTED)
                result.add(permission)
        return result
    }

    /*private fun updateUI(location: Location) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            binding.contentMain.apply {
                latitudeTextField?.editText?.setText(location.latitude.toString())
                longitudeTextField?.editText?.setText(location.longitude.toString())
                timeTextField?.editText?.setText(location.time.toDateString())
            }
        else
            setAddress(location.latitude, location.longitude)
    }

     */



    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between `onCreate(Bundle)` and
     * `onViewCreated(View, Bundle)`. A default `View` can be returned by calling `Fragment(int)` in
     * your constructor. Otherwise, this method returns null.
     *
     * It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to `onViewCreated(View, Bundle)`.
     *
     * If you return a `View` from here, you will later be called in `onDestroyView()` when the view
     * is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     *      fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *      attached to. The fragment should not add the view itself, but this can be used to
     *      generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null)
            startLoginActivity()
    }


    /**
     * Called when the view previously created by `onCreateView()` has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created. This
     * is called after `onStop()` and before `onDestroy()`. It is called <em>regardless</em> of
     * whether `onCreateView()` returned a non-null view. Internally it is called after the view's
     * state has been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            with (binding){
                mainStartRideButton.setOnClickListener{
                    findNavController().navigate(R.id.action_mainFragment_to_startRideFragment)
                }

                mainUpdateRideButton.setOnClickListener{
                    findNavController().navigate(R.id.action_mainFragment_to_updateRideFragment)
                }
                mainDeleteRideButton.setOnClickListener{
                    findNavController().navigate(R.id.action_mainFragment_to_deleteRideFragment)
                }

                signOutButton.setOnClickListener{
                    auth.signOut()
                    startLoginActivity()
                    true
                }

                showListButton.setOnClickListener{
                    //Action
                    findNavController().navigate(R.id.action_mainFragment_to_fragmentListScooters)

                }
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter
            }
    }

    private fun startLoginActivity() {
        val intent = Intent(activity,
            LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }



    override fun onItemClickListener(scooter: Scooter, position: Int) {
        /*
        // Inflate Custom alert dialog view
        customAlertDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_update_ride, binding.root, false)

        // Launching the custom alert dialog
        launchUpdateAlertDialog(scooter, position)


         */
    }

   /* private fun launchUpdateAlertDialog(scooter: Scooter, position: Int) {
        // Get the edit text component.
        val editTextName = customAlertDialogView
            .findViewById<TextInputEditText>(R.id.edit_text_name)
        editTextName?.setText(scooter.name)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.dialog_update_title))
            .setMessage(getString(R.string.dialog_update_message))
            .setPositiveButton(getString(R.string.update_button)) { dialog, _ ->
                val name = editTextName?.text.toString()
                if (name.isNotEmpty()) {
                    scooter.name = name
                    scooter.timestamp = System.currentTimeMillis().toString()
                    adapter.getRef(position).setValue(scooter)
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    */

}