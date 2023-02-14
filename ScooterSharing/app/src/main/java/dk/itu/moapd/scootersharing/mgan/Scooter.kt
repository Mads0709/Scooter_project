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

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * An data class that holds twp properties
 * @property name
 * @property location
 */
data class Scooter (var name : String,
                    var location: String,
                    var timestamp: Long = System.currentTimeMillis()) {

    /**
     * A method that returns the scooter name and location
     * @return a string representation of the name and location
     */
    override fun toString(): String {
        return "[Scooter] $name is placed at $location the timestamp is:." + toDate()
    }

    fun toDate (): String {
        var date2 = SimpleDateFormat("dd/MM/yy hh:mm a")
        val netDate = Date(this.timestamp)
        val date = date2.format(netDate)
        return date
    }
}
