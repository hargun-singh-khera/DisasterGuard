package com.example.disasterguard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EmergencyContact : AppCompatActivity() {
    lateinit var animBlink: Animation
    lateinit var tvHeading: TextView
    lateinit var cardViewPolice: CardView
    lateinit var cardViewFire: CardView
    lateinit var cardViewMedical: CardView
    lateinit var callPolice: ImageView
    lateinit var callFire: ImageView
    lateinit var callMedical: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contact)

        tvHeading = findViewById(R.id.tvHeading)
        cardViewPolice = findViewById(R.id.cardViewPolice)
        cardViewFire = findViewById(R.id.cardViewFire)
        cardViewMedical = findViewById(R.id.cardViewMedical)
        callPolice = findViewById(R.id.callPolice)
        callFire = findViewById(R.id.callFire)
        callMedical = findViewById(R.id.callMedical)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Emergency Contacts")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        callPolice.setOnClickListener {
            startCallActivity()
        }

        callFire.setOnClickListener {
            startCallActivity()
        }

        callMedical.setOnClickListener {
            startCallActivity()
        }

        showAnimation()

    }

    private fun showAnimation() {
        animBlink = AnimationUtils.loadAnimation(this, R.anim.move_up)
        tvHeading.startAnimation(animBlink)
        cardViewPolice.startAnimation(animBlink)
        cardViewFire.startAnimation(animBlink)
        cardViewMedical.startAnimation(animBlink)
    }

    private fun startCallActivity() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel: +911234567890"))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        showAnimation()
    }
}