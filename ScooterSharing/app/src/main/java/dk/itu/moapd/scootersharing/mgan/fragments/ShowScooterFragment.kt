package dk.itu.moapd.scootersharing.mgan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.mgan.R

/**
 * A simple [Fragment] subclass.
 * Use the [ShowScooterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowScooterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_scooter, container, false)
    }

}