package com.example.disasterguard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class IntroScreen : AppCompatActivity() {
    lateinit var btnStart: Button
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    val fileName = "userType"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)

        btnStart = findViewById(R.id.btnStart)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)

        val isVisited = sharedPreferences.getBoolean("homeVisited", false)

        val currentUser = auth.currentUser
        if (currentUser != null) {
//            if (isAdmin) {
//                startActivity(Intent(this, AdminDashboard::class.java))
//            }
            if (currentUser.isEmailVerified){
                startActivity(Intent(this, UserDashboard::class.java))
            }
            else {
                startActivity(Intent(this, LoginScreen::class.java))
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        else if (isVisited) {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

        btnStart.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("homeVisited", true)
            editor.apply()
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }
}