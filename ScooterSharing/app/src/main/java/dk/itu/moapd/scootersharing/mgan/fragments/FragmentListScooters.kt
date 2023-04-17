package dk.itu.moapd.scootersharing.mgan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.activites.mgan.RidesDB
import dk.itu.moapd.scootersharing.mgan.activites.mgan.Scooter
import dk.itu.moapd.scootersharing.mgan.adapter.CustomArrayAdapter
import dk.itu.moapd.scootersharing.mgan.adapter.ItemClickListener
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentListScootersBinding
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentListScooters.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentListScooters : Fragment(), ItemClickListener {

    companion object{
        private lateinit var adapter: CustomArrayAdapter
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var _binding: FragmentListScootersBinding? = null
    private val binding
        get() = checkNotNull(_binding)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
        // intialize firebase realtime
        database =
            Firebase.database("https://moapd-2023-e061c-default-rtdb.europe-west1.firebasedatabase.app/").reference
        //customAlertDialogView = LayoutInflater.from(requireContext())
        //  .inflate(R.layout.fragment_update_ride, binding.root, false)

        auth.currentUser?.let {
            val query = database.child("scooters")
                .orderByChild("createdAt")
            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()
            //Create the custom adapter to populate the adapter
            adapter = CustomArrayAdapter(this, options)
        }
    }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            _binding = FragmentListScootersBinding.inflate(inflater, container, false)
            return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onItemClickListener(dummy: Scooter, position: Int) {
        TODO("Not yet implemented")
    }
}

