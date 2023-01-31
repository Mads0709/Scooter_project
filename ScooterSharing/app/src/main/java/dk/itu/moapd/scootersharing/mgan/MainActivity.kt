package dk.itu.moapd.scootersharing.mgan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.WindowDecorActionBar
import androidx.core.view.WindowCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG = MainActivity::class.qualifiedName
    }

    //GUI variables
    private lateinit var scooterName: TextInputEditText
    private lateinit var scooterLocation: TextInputEditText
    private lateinit var startRideButton: Button



    private val scooter: Scooter = Scooter("","")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Edit texts
        scooterName = findViewById(R.id.nameTextFieldEdit)
        scooterLocation = findViewById(R.id.locationTextFieldEdit)

        //Buttons
        startRideButton = findViewById(R.id.startRide_button)

        //Action
        startRideButton.setOnClickListener {
            if(scooterName.text?.isNotEmpty() == true && scooterLocation.text?.isNotEmpty() == true){

                //Update the scooter attributes

                val name = scooterName.text.toString().trim()
                val location = scooterLocation.text.toString().trim()
                //set the name and location of the given values
                scooter.setName(name)
                scooter.setLocation(location)

                //show text in log
                scooterName.setText(" ")
                scooterLocation.setText(" ")
                showMessage()

            }

        }
    }

    private fun showMessage() {
        Log.d(TAG, scooter.toString())
    }
}