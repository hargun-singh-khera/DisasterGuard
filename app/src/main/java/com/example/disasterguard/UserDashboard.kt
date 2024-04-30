package com.example.disasterguard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class UserDashboard : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var fab: FloatingActionButton
    lateinit var relativeLayout: RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var sensorManager: SensorManager
    lateinit var proximitySensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Dashboard")
        setSupportActionBar(toolbar)

        replaceFrameWithFragment(HomeFragment())

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)

        relativeLayout = findViewById(R.id.relativeLayout)

        bottomNavHandler()

        auth = FirebaseAuth.getInstance()

        fab.setOnClickListener {
            shareApp()
        }

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
                    showProximityAlert()
                }
            }
        }
    }

    fun showProximityAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("We have detected that your device is very close to an object. Please take appropriate action to ensure the safety of your device.")
        builder.setTitle("Alert!")
        builder.setCancelable(false)

        builder.setPositiveButton("Ok") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        exitAlert()
    }

    fun exitAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to close this app?")
        builder.setTitle("Exit Alert!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> finish()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.profile -> {
                bottomNavigationView.selectedItemId = R.id.profile
            }
            R.id.resetPassword -> {
                startActivity(Intent(this@UserDashboard, ResetPassword::class.java))
            }
            R.id.rate -> {
                bottomNavigationView.selectedItemId = R.id.rate
            }
            R.id.logout -> {
                logoutAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun replaceFrameWithFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frameLayout, fragment)
        fragTransaction.commit()
    }

    private fun bottomNavHandler() {
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    replaceFrameWithFragment(HomeFragment())
                    toolbar.setTitle("Dashboard")
                }
                R.id.rate -> {
                    toolbar.setTitle("Rate Us")
                    replaceFrameWithFragment(RateFragment())
                }
                R.id.profile -> {
                    replaceFrameWithFragment(ProfileFragment())
                    toolbar.setTitle("My Profile")
                }
                R.id.logout -> logoutAlert()
            }
            true
        }
    }

    fun logoutAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to logout ?")
        builder.setTitle("Logout Alert!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> logoutRedirect()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun logoutRedirect() {
        auth.signOut()
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT,"https://drive.google.com/file/d/1OiTAvjIXveHQBSxWcrPkcW78Q7Xj5vxK/view?usp=sharing")
        startActivity(Intent.createChooser(intent, "Share Link!"))
    }

}