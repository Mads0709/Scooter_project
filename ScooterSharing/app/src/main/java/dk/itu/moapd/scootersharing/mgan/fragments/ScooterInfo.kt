package dk.itu.moapd.scootersharing.mgan.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.mgan.databinding.FragmentScooterInfoBinding
import java.lang.Math.abs
import java.lang.Math.sqrt
import kotlin.math.sqrt


class ScooterInfo : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var linearAcceleration: Sensor

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

    //public class Accelerometer implements SensorEventListener { ...
    override fun onSensorChanged(se: SensorEvent) {
        if (se == null) {
            Log.d(TAG, "SensorEvent is null")
            return
        }

        binding.apply {
            val acceleration = sqrt(se.values[0]*se.values[0] + se.values[1]*se.values[1] + se.values[2]*se.values[2])
            xValueInput?.editText?.setText((abs(acceleration*3.6)).toString())


            //Log.d(TAG, "Sensor X: " + se.values[0] + "Y" + se.values[1] + "Z" + se.values[2])
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG, "Sensor accuracy changed:")
    }

}

