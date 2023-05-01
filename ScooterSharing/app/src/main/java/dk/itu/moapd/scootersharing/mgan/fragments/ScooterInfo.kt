package dk.itu.moapd.scootersharing.mgan.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.wear.tiles.material.CircularProgressIndicator
import dk.itu.moapd.scootersharing.mgan.R
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentMapBinding
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentScooterInfoBinding
import java.lang.System.out

class ScooterInfo : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var linearAcceleration: Sensor
    lateinit var progressBar: CircularProgressIndicator

    private val TAG = ScooterInfo::class.java.simpleName

    private var _binding: FragmentScooterInfoBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val service = Context.SENSOR_SERVICE

        sensorManager = requireActivity().getSystemService(service) as SensorManager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL)

        linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "does it work")
        // Inflate the layout for this fragment
        _binding = FragmentScooterInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    //public class Accelerometer implements SensorEventListener { ...
    override fun onSensorChanged(se: SensorEvent) {
        if (se == null) {
            Log.d(TAG, "SensorEvent is null")
            return
        }

        binding.apply {
            xValueInput?.editText?.setText(se.values[0].toString())
            yValueInput?.editText?.setText(se.values[1].toString())
            zValueInput?.editText?.setText(se.values[2].toString())

            //Log.d(TAG, "Sensor X: " + se.values[0] + "Y" + se.values[1] + "Z" + se.values[2])
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG, "Sensor accuracy changed:")
    }

}

