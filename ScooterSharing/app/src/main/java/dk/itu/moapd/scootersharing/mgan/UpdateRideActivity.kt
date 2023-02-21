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
import dk.itu.moapd.scootersharing.mgan.databinding.ActivityUpdateRideBinding

/**
 * An activity class with methods to manage the main activity of the ScooterSharing application.
 */
class UpdateRideActivity : AppCompatActivity() {

    companion object{
        lateinit var ridesDB : RidesDB
    }

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityUpdateRideBinding

    /**
     * A 'Scooter' to store the scooter information
     */
    private val scooter: Scooter = Scooter("","")

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * `setContentView(int)` to inflate the activity's UI, using `findViewById()` to
     * programmatically interact with widgets in the UI, calling
     * `managedQuery(android.net.Uri, String[], String, String[], String)` to retrieve cursors for
     * data being displayed, etc.
     *
     * You can call `finish()` from within this function, in which case `onDestroy()` will be
     * immediately called after `onCreate()` without any of the rest of the activity lifecycle
     * (`onStart()`, `onResume()`, onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in `onSaveInstanceState()`.
     * <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(this)
        //Action
        binding = ActivityUpdateRideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            updateRideButton.setOnClickListener{
                if (nameTextFieldEdit.text?.isNotEmpty() == true && locationTextFieldEdit.text?.isNotEmpty() == true) {

                    //Update the scooter attributes

                    val name = nameTextFieldEdit.text.toString().trim()
                    val location = locationTextFieldEdit.text.toString().trim()
                    //set the name and location of the given values
                    ridesDB.addScooter(name, location)

                    //reset textfield after adding scotter
                    nameTextFieldEdit.setText("")
                    locationTextFieldEdit.setText("")
                    showMessage()
                } else if (nameTextFieldEdit.text?.isNotEmpty() == false && locationTextFieldEdit.text?.isNotEmpty() == true) {
                    showMessage()
                }
            }
        }
    }

    /**
     * making the snackbar popup that interacts with the xml and displays the scooter toString() method in the snakcbar
     */
    private fun showMessage() {
        Snackbar.make(binding.root, ridesDB.getCurrentScooterInfo(), Snackbar.LENGTH_SHORT).show();
    }
}