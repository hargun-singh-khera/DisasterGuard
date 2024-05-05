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

class SafetyTips : AppCompatActivity() {
    lateinit var animBlink: Animation
    lateinit var tvHeading: TextView
    lateinit var tvHeading1: TextView
    lateinit var tvHeading2: TextView
    lateinit var tvHeading3: TextView

    lateinit var cardViewEarthquake: CardView
    lateinit var cardViewHurricane: CardView
    lateinit var cardViewFlood: CardView
    lateinit var btnEarthquakeGuide: Button
    lateinit var btnFloodGuide: Button
    lateinit var btnHurricaneGuide: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_tips)

        tvHeading = findViewById(R.id.tvHeading)
        tvHeading1 = findViewById(R.id.tvHeading1)
        tvHeading2 = findViewById(R.id.tvHeading2)
        tvHeading3 = findViewById(R.id.tvHeading3)
        cardViewEarthquake = findViewById(R.id.cardViewEarthquake)
        cardViewHurricane = findViewById(R.id.cardViewHurricane)
        cardViewFlood = findViewById(R.id.cardViewFlood)
        btnEarthquakeGuide = findViewById(R.id.btnEarthquakeGuide)
        btnFloodGuide = findViewById(R.id.btnFloodGuide)
        btnHurricaneGuide = findViewById(R.id.btnHurricaneGuide)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Safety Tips")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val title1 = tvHeading1.text.toString()
        val title2 = tvHeading2.text.toString()
        val title3 = tvHeading3.text.toString()


        btnEarthquakeGuide.setOnClickListener {
            startNewActivity(title1, "https://www.ready.gov/earthquakes")
        }

        btnFloodGuide.setOnClickListener {
            startNewActivity(title2,"https://www.ready.gov/floods")
        }

        btnHurricaneGuide.setOnClickListener {
            startNewActivity(title3, "https://www.ready.gov/hurricanes")
        }

        showAnimation()

    }

    private fun startNewActivity(title: String, url: String) {
        val intent = Intent(this, ShowGuides::class.java)
        intent.putExtra("title", title)
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