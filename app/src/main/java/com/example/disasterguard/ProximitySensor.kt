package com.example.disasterguard

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProximitySensor : AppCompatActivity() {
    lateinit var sensorStatusTV: TextView
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    lateinit var linearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximity_sensor)

        sensorStatusTV = findViewById(R.id.tvSensorStatus)
        linearLayout = findViewById(R.id.linearLayout)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!

        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            sensorManager.registerListener(proximitySensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event!!.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    linearLayout.setBackgroundColor(Color.RED)
                } else {
                    linearLayout.setBackgroundColor(Color.BLUE)
                }
            }
        }
    }
}