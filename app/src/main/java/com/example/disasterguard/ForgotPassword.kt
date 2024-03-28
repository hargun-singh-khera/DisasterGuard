package com.example.disasterguard

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var etEmail: EditText
    lateinit var btnReset: Button
    lateinit var imageView: ImageView
    lateinit var tvHeading: TextView
    lateinit var tvHeading2: TextView
    lateinit var animBlink: Animation
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        etEmail = findViewById(R.id.etEmail)
        btnReset = findViewById(R.id.btnReset)
        imageView = findViewById(R.id.imageView)
        tvHeading = findViewById(R.id.tvHeading)
        tvHeading2 = findViewById(R.id.tvHeading2)

        showAnimation()

        progressDialog = ProgressDialog(this)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
            else {
                showProgressBar()
                resetUserPassword(email)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showAnimation()
    }

    private fun showAnimation() {
        animBlink = AnimationUtils.loadAnimation(this, R.anim.move_up)
        imageView.startAnimation(animBlink)
        tvHeading.startAnimation(animBlink)
        tvHeading2.startAnimation(animBlink)
        etEmail.startAnimation(animBlink)
        btnReset.startAnimation(animBlink)
    }

    private fun showProgressBar() {
        progressDialog.setTitle("Sending")
        progressDialog.setMessage("Sending Password Reset Link...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    private fun resetUserPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                // used for delay of activity
                Handler(Looper.getMainLooper()).postDelayed({
                    hideProgressBar()
                    finish()
                },1000)
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}