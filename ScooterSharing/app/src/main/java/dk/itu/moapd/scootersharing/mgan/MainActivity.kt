package dk.itu.moapd.scootersharing.mgan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.WindowDecorActionBar
import androidx.core.view.WindowCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.google.android.material.textfield.TextInputEditText
import dk.itu.moapd.scootersharing.mgan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG = MainActivity::class.qualifiedName
    }

    private lateinit var binding: ActivityMainBinding

    private val scooter: Scooter = Scooter("","")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        //Action
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            startRideButton.setOnClickListener{
                if (nameTextFieldEdit.text?.isNotEmpty() == true && locationTextFieldEdit.text?.isNotEmpty() == true) {

                    //Update the scooter attributes

                    val name = nameTextFieldEdit.text.toString().trim()
                    val location = locationTextFieldEdit.text.toString().trim()
                    //set the name and location of the given values
                    scooter.name = name
                    scooter.location = location

                    //show text in log
                    nameTextFieldEdit.setText("")
                    locationTextFieldEdit.setText("")
                    showMessage()
                }
            }
        }
    }
    private fun showMessage() {
        Snackbar.make(binding.root, scooter.toString(), Snackbar.LENGTH_SHORT).show();
    }
}