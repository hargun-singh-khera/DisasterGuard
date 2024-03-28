package com.example.disasterguard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    lateinit var tv: TextView
    lateinit var appLogo: ImageView
    lateinit var animBlink: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        tv = findViewById(R.id.tv)
        appLogo = findViewById(R.id.appLogo)

        animBlink = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        tv.startAnimation(animBlink)
        appLogo.startAnimation(animBlink)

        // used for delay of splash screen activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, IntroScreen::class.java)
            startActivity(intent)
            finish()
        },500)
    }
}