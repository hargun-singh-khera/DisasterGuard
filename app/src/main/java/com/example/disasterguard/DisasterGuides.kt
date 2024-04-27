package com.example.disasterguard

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DisasterGuides : AppCompatActivity() {
    lateinit var animBlink: Animation
    lateinit var tvHeading: TextView
    lateinit var cardViewEarthquake: CardView
    lateinit var cardViewHurricane: CardView
    lateinit var cardViewFlood: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_guides)

        tvHeading = findViewById(R.id.tvHeading)
        cardViewEarthquake = findViewById(R.id.cardViewEarthquake)
        cardViewHurricane = findViewById(R.id.cardViewHurricane)
        cardViewFlood = findViewById(R.id.cardViewFlood)

        showAnimation()
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