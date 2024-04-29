package com.example.disasterguard

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DisasterGuides : AppCompatActivity() {
    lateinit var animBlink: Animation
    lateinit var tvHeading: TextView
    lateinit var cardViewEarthquake: CardView
    lateinit var cardViewHurricane: CardView
    lateinit var cardViewFlood: CardView
    lateinit var btnEarthquakeGuide: Button
    lateinit var btnFloodGuide: Button
    lateinit var btnHurricaneGuide: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_guides)

        tvHeading = findViewById(R.id.tvHeading)
        cardViewEarthquake = findViewById(R.id.cardViewEarthquake)
        cardViewHurricane = findViewById(R.id.cardViewHurricane)
        cardViewFlood = findViewById(R.id.cardViewFlood)
        btnEarthquakeGuide = findViewById(R.id.btnEarthquakeGuide)
        btnFloodGuide = findViewById(R.id.btnFloodGuide)
        btnHurricaneGuide = findViewById(R.id.btnHurricaneGuide)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Disaster Guides")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        btnEarthquakeGuide.setOnClickListener {
            startNewActivity("https://www.cdc.gov/disasters/earthquakes/prepared.html")
        }

        btnFloodGuide.setOnClickListener {
            startNewActivity("https://www.cdc.gov/disasters/floods/readiness.html")
        }

        btnHurricaneGuide.setOnClickListener {
            startNewActivity("https://www.cdc.gov/disasters/hurricanes/before.html")
        }

        showAnimation()

    }

    private fun startNewActivity(url: String) {
        val intent = Intent(this, ShowGuides::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    private fun showAnimation() {
        animBlink = AnimationUtils.loadAnimation(this, R.anim.move_up)
        tvHeading.startAnimation(animBlink)
        cardViewEarthquake.startAnimation(animBlink)
        cardViewHurricane.startAnimation(animBlink)
        cardViewFlood.startAnimation(animBlink)
    }

    override fun onResume() {
        super.onResume()
        showAnimation()
    }
}