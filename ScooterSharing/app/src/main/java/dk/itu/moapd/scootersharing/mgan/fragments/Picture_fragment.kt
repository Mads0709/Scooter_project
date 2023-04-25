package dk.itu.moapd.scootersharing.mgan.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModel
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMainBinding
import java.io.File
import java.util.*
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentPictureFragmentBinding
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [Picture_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Picture_fragment : Fragment() {
    private var _binding: FragmentPictureFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private lateinit var viewModel :ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPictureFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto ->


    }
    private var photoName: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


                val photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir,
                    photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "dk.itu.moapd.scootersharing.mgan.fragments.Picture_fragment",
                    photoFile
                )
                takePhoto.launch(photoUri)
                updatePhoto(photoName)
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

}