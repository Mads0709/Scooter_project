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

package dk.itu.moapd.scootersharing.mgan.activites.mgan
import android . content . Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java . util . Random
import kotlin . collections . ArrayList

/**
 * class responsible for storing scooter information for each registered scooter.
 * @param context The current context of the RidesDB class.
 */

class RidesDB private constructor ( context : Context) {
    /**
    A list to store all the Scooters.
     */
    private val rides = ArrayList <Scooter>()
    private lateinit var database: DatabaseReference

    /**
     * A set of static attributes used in this class.
     */
    companion object : RidesDBHolder<RidesDB, Context>(:: RidesDB )

    /**
     * add these three scooters when the app is initialised
     */

    /**
     * Get the current list of scooters
     * @return A list of all scooters added to the list
     */
    fun getRidesList () : List <Scooter> {
        return rides
    }

    /**
     * Add a scooter to the list
     * @param name the name of the scooter
     * @param location the location of the scooter
     */
    /*fun addScooter ( name : String, location : String ) {
        rides.add(Scooter(name, location))
    }

     */

    /**
     * Remove a scooter from the list
     * @param name the name of the scooter
     */
  /*  fun deleteScooter(name: String){
        val scooter = getScooterByName(name)
        if (scooter != Scooter("","")){
            rides.remove(scooter)
        }

   */


    /**
     * Find a scooter in the list by a given name
     * @param name the name of the scooter
     * @return a Scooter from the list.
     */
    /*fun getScooterByName(name: String): Scooter {
        for (s in rides){
            if (name == s.name)
                return s
        }
        return Scooter("","")
    }


     */
    /**
     * Update the location for last scooter in the list
     * @param location updated location of the scooter
     */
    fun updateCurrentScooter ( location : String ) {
        rides.last().location = location
    }

    /**
     * Get the last scooter in the list
     * @return The last scooter in the list
     */
    fun getCurrentScooter () : Scooter {
        return rides.last()
    }

    /**
     * Get the string information for the last scooter in the list.
     * @return a string representation of last scooter's information in the list.
     */
    fun getCurrentScooterInfo () : String {
        return getCurrentScooter().toString()
    }
    /**
     * Generate a random timestamp in the last 365 days .
     *
     * @return A random timestamp in the last year .
     */
    private fun randomDate () : Long {
        val random = java.util.Random()
        val now = System . currentTimeMillis ()
        val year = random . nextDouble () * 1000 * 60 * 60 * 24 * 365
        return ( now - year ) . toLong ()
    }

    /*fun writeNewUser(name: String, location: String, timestamp: Long) {
        database = Firebase.database.reference
        val scooter = Scooter(name, location, timestamp)

        database.child("scooters").child(name).setValue(scooter)
    }

     */
}

/**
 * A singleton to make sure you only have one instance of the RidesDB class.
 */
open class RidesDBHolder < out T : Any , in A >( creator : ( A ) -> T ) {
    private var creator : (( A ) -> T ) ? = creator
    @Volatile private var instance : T ? = null
    fun get (arg: A) : T {
        val checkInstance = instance
        if ( checkInstance != null )
            return checkInstance
        return synchronized ( this ) {
            3
            val checkInstanceAgain = instance
            if ( checkInstanceAgain != null )
                checkInstanceAgain
            else {
                val created = creator !!( arg )
                instance = created
                creator = null
                created
            }
        }
    }
}
