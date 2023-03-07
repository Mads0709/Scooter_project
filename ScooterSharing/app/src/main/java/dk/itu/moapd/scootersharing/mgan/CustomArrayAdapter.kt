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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.mgan.databinding.ListRidesBinding

/**
 * A class to customize an adapter with a `ViewHolder` to populate dataset about scooter information into a `ListView`.
 */
class CustomArrayAdapter(
    private val data: RidesDB) :
    RecyclerView.Adapter<CustomArrayAdapter.ViewHolder>() {

    /**
     * An internal view holder class used to represent the layout that shows a single `Scooter`
     * instance in the `ListView`.
     */
    class ViewHolder(private val binding: ListRidesBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(scooter :Scooter){
            binding.listItemName.text = scooter.name
            binding.listItemLocation.text = scooter.location
            binding.listItemTimestamp.text = scooter.timestamp.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(
            inflater, parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount() = data.getRidesList().size
    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        val scooter = data.getRidesList()[position]
        holder.bind(scooter)
    }

}