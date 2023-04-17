package dk.itu.moapd.scootersharing.mgan.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.mgan.activites.mgan.RidesDB
import dk.itu.moapd.scootersharing.mgan.activites.mgan.Scooter
import dk.itu.moapd.scootersharing.mgan.databinding.ListRidesBinding



/**
 * A class to customize an adapter with a `ViewHolder` to populate dataset about scooter information into a `ListView`.
 */
class CustomArrayAdapter(private val itemClickListener: ItemClickListener,
                         options: FirebaseRecyclerOptions<Scooter>) :
    FirebaseRecyclerAdapter<Scooter, CustomArrayAdapter.ViewHolder>(options) {



    // Download and set an image into the ImageView.
    /**
     * An internal view holder class used to represent the layout that shows a single `Scooter`
     * instance in the `ListView`.
     */

    class ViewHolder(private val binding: ListRidesBinding) :
        RecyclerView.ViewHolder(binding.root){


        fun bind(scooter : Scooter){
           binding.listItemName.text = scooter.name
           binding.listItemLocation.text = scooter.location
           binding.listItemTimestamp.text = scooter.timestamp.toString()

            // Get the public thumbnail URL.
            val storage = Firebase.storage("gs://moapd-2023-e061c.appspot.com")
            val imageRef = storage.reference.child("scooters").child("scooter.jpg")


            // Download and set an image into the ImageView.
            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(itemView.context)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(binding.listItemImageId)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(
            inflater, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, scooter: Scooter) {
        holder.apply {
            bind(scooter)
            itemView.setOnLongClickListener {
                itemClickListener.onItemClickListener(scooter, position)
                true
            }
        }
    }





    //override fun getItemCount() = data.getRidesList().size
    /*override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        val scooter = data.getRidesList()[position]
        holder.bind(scooter)
    }
    */
}