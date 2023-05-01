package dk.itu.moapd.scootersharing.mgan.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.CompoundBarcodeView
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.activites.mgan.Scooter


/**
 * A simple [Fragment] subclass.
 * Use the [QRFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val CAMERA_REQUEST_CODE = 101
class QRFragment : Fragment() {
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var codeScanner: CodeScanner
    private val TAG = QRFragment::class.java.simpleName

    private lateinit var database: DatabaseReference

    private var isScanning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        database =
            Firebase.database("https://moapd-2023-e061c-default-rtdb.europe-west1.firebasedatabase.app/").reference
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback { result ->
            activity.runOnUiThread {
                val scooterName = result.text // Get the text from the QR code
                val scooterRef = FirebaseDatabase.getInstance().getReference("scooters").child(scooterName)
                database.child("scooters").orderByChild("name").equalTo(scooterName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val scooter = snapshot.children.first().getValue(Scooter::class.java)
                                if (scooter != null) {
                                    Log.d(TAG, "Retrieved scooter data: $scooter")
                                    scooter.isUsed = true
                                    scooterRef.setValue(scooter)
                                    Log.d(TAG,  "Scooter is now used: $scooter")
                                    MaterialAlertDialogBuilder(requireActivity())
                                        .setTitle(getString(R.string.start_ride_dialog_title))
                                        .setMessage(getString(R.string.start_ride_dialog_support) + " " + scooterName)
                                        .setNeutralButton(getString(R.string.start_ride_cancel)) { dialog, which ->
                                            findNavController().navigate(R.id.action_fragmentQR_to_mainFragment)
                                            scooter.isUsed = false
                                            scooterRef.setValue(scooter)
                                        }
                                        .setPositiveButton(getString(R.string.start_ride_confirm)) { dialog, which ->
                                            findNavController().navigate(R.id.action_fragmentQR_to_fragmentShowScooter)
                                        }
                                        .show()
                                }
                            } else {
                                Log.d(TAG, "Scooter not found in database")
                            }
                            isScanning = false
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(TAG, "Database query cancelled")
                            isScanning = false
                        }
                    })

            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }


    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "You need to ask for permission", Toast.LENGTH_SHORT)

                } else{
                    //succes
                }
            }
        }
    }

    private fun getScooterData(scooterId: String) {
        val scootersRef = database.child("scooters")
        val scooterQuery = scootersRef.orderByChild("id").equalTo(scooterId)

        scooterQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (scooterSnapshot in dataSnapshot.children) {
                    val scooter = scooterSnapshot.getValue(Scooter::class.java)
                    if (scooter != null) {
                        // Update the isUsed field to true
                        scooterSnapshot.ref.child("isUsed").setValue(true)
                        // Launch the ShowScooterFragment and pass the scooter data as an extra
                        val intent = Intent(requireContext(), ShowScooterFragment::class.java)
                        startActivity(intent)
                        return
                    }
                }
                // If no scooter was found, show an error message
                Toast.makeText(requireContext(), "Scooter not found", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

}