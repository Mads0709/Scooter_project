package dk.itu.moapd.scootersharing.mgan.fragments

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.mgan.activites.mgan.Scooter
import dk.itu.moapd.scootersharing.mgan.adapter.ItemClickListener
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentPictureFragmentBinding
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.math.roundToInt


class Picture_fragment : Fragment(), ItemClickListener {
    private var _binding: FragmentPictureFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference



    }








    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPictureFragmentBinding.inflate(inflater, container, false)
        // Showing the last taken image.

        scooter?.last_photo = photoName

        return binding.root
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->
        updatePhoto(photoName)
        uploadPhotoToFirebaseStorage(photoName)





    }
    private var photoName: String? = null

    private var scooter: Scooter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoName = "IMG_${Date()}.JPG"
        val photoFile = File(
            requireContext().applicationContext.filesDir,
            photoName
        )
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "dk.itu.moapd.scootersharing.mgan.fragments.Picture_fragment",
            photoFile
        )
        takePhoto.launch(photoUri)
        updatePhoto(photoName)
        uploadPhotoToFirebaseStorage(scooter?.last_photo)
        database.child("scooters").child("CPH1").child("last_photo").setValue(photoName)

    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.scooterPhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.scooterPhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.scooterPhoto.setImageBitmap(scaledBitmap)
                    binding.scooterPhoto.tag = photoFileName
                    uploadPhotoToFirebaseStorage(photoFileName)





                }
            } else {
                binding.scooterPhoto.setImageBitmap(null)
                binding.scooterPhoto.tag = null
            }
        }
    }

    fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
// Read in the dimensions of the image on disk
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val srcWidth = options.outWidth.toFloat()
        val srcHeight = options.outHeight.toFloat()
// Figure out how much to scale down by
        val sampleSize = if (srcHeight <= destHeight && srcWidth <= destWidth) {
            1
        } else {
            val heightScale = srcHeight / destHeight
            val widthScale = srcWidth / destWidth
            minOf(heightScale, widthScale).roundToInt()
        }
// Read in and create final bitmap
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        })
    }

    override fun onItemClickListener(dummy: Scooter, position: Int) {
        TODO("Not yet implemented")
    }

    private fun uploadPhotoToFirebaseStorage(photoFileName: String?) {
        // Get a reference to the Firebase Storage
        val storage = Firebase.storage("gs://moapd-2023-e061c.appspot.com")
        // Create a reference to the photo file in the app's private storage
        val photoFile = photoFileName?.let {
            File(requireContext().applicationContext.filesDir, it)
        }
        // Check if the photo file exists
        if (photoFile?.exists() == true) {
            // Create a reference to the Firebase Storage bucket where you want to store the photo
            val storageRef = storage.reference.child("scooters/").child("name")
            // Create an InputStream from the photo file
            val stream = FileInputStream(photoFile)
            // Upload the photo to Firebase Storage
            val uploadTask = storageRef.putStream(stream)
            // Add an OnCompleteListener to the uploadTask to handle the result
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The photo was successfully uploaded to Firebase Storage
                    // Get the download URL for the photo and store it in the scooter object
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        scooter?.last_photo = uri.toString()
                    }
                } else {
                    // There was an error uploading the photo to Firebase Storage
                    // Log the error message
                    task.exception?.message?.let { Log.e("Picture_fragment", it) }
                }
            }
        }
    }

}