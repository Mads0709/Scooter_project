package dk.itu.moapd.scootersharing.mgan.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.activites.mgan.Scooter

class QRFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private val TAG = QRFragment::class.java.simpleName

    private lateinit var database: DatabaseReference
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
                                if(!scooter?.used!!){
                                    if (scooter != null) {
                                        Log.d(TAG, "Retrieved scooter data: $scooter")
                                        scooterRef.setValue(scooter)

                                        Log.d(TAG,  "Scooter is now used: $scooter")
                                        MaterialAlertDialogBuilder(requireActivity())
                                            .setTitle(getString(R.string.start_ride_dialog_title))
                                            .setMessage(getString(R.string.start_ride_dialog_support) + " " + scooterName)
                                            .setNeutralButton(getString(R.string.start_ride_cancel)) { dialog, which ->
                                                findNavController().navigate(R.id.action_fragmentQR_to_fragmentPicture)
                                                scooter.used = false
                                                scooterRef.setValue(scooter)
                                            }
                                            .setPositiveButton(getString(R.string.start_ride_confirm)) { dialog, which ->
                                                scooter.used = true
                                                scooterRef.setValue(scooter)
                                                val args = Bundle()
                                                args.putString("scooters", scooterName)
                                                findNavController().navigate(R.id.action_fragmentQR_to_fragmentPicture, args)

                                                Log.d(TAG, "debug name of the scooter is args ${args.getString("scooters")}")
                                            }
                                            .show()
                                    }

                                }
                                else {
                                    Log.d(TAG, "Scooter is used")
                                    MaterialAlertDialogBuilder(requireActivity())
                                        .setTitle(getString(R.string.scooter_is_used_title))
                                        .setNeutralButton(getString(R.string.scooter_is_used_ok)) { dialog, which ->
                                            findNavController().navigate(R.id.action_fragmentQR_to_mainFragment)
                                        }
                                        .show()
                                }

                            } else {
                                Log.d(TAG, "Scooter not found in database")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(TAG, "Database query cancelled")
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





}