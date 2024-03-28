package com.example.disasterguard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class IntroScreen : AppCompatActivity() {
    lateinit var btnStart: Button
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)

        btnStart = findViewById(R.id.btnStart)
        auth = FirebaseAuth.getInstance()

        btnStart.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }
}