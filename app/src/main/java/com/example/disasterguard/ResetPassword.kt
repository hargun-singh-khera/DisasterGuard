package com.example.disasterguard

import android.app.ProgressDialog
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var etPass: EditText
    lateinit var etConfPass: EditText
    lateinit var btnReset: Button
    lateinit var progressDialog: ProgressDialog
    lateinit var animBlink: Animation
    lateinit var imageView: ImageView
    lateinit var tvHeading: TextView
    lateinit var tvHeading2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Reset Password")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etPass = findViewById(R.id.etPass)
        etConfPass = findViewById(R.id.etConfPass)
        btnReset = findViewById(R.id.btnReset)
        auth = FirebaseAuth.getInstance()
//        resetPasswordLayout = findViewById(R.id.resetPasswordLayout)
        imageView = findViewById(R.id.imageView)
        tvHeading = findViewById(R.id.tvHeading)
        tvHeading2 = findViewById(R.id.tvHeading2)

        btnReset.setOnClickListener {
            resetUserPassword()
        }
        showAnimation()
    }

    private fun showAnimation() {
        animBlink = AnimationUtils.loadAnimation(this, R.anim.move_up)
        imageView.startAnimation(animBlink)
        tvHeading.startAnimation(animBlink)
        tvHeading2.startAnimation(animBlink)
        etPass.startAnimation(animBlink)
        etConfPass.startAnimation(animBlink)
    }

    override fun onResume() {
        super.onResume()
        showAnimation()
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Changing your account password....")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    private fun resetUserPassword() {
        val pass = etPass.text.toString()
        val cpass = etConfPass.text.toString()
        if (pass.isEmpty()) {
            Toast.makeText(this@ResetPassword, "Please enter your new password.", Toast.LENGTH_SHORT).show()
        }
        else if (cpass.isEmpty()) {
            Toast.makeText(this@ResetPassword, "Please enter confirm password", Toast.LENGTH_SHORT).show()
        }
        else if (pass != cpass) {
            Toast.makeText(this@ResetPassword, "New Password and Confirm Password doesn't matched.", Toast.LENGTH_SHORT).show()
        }
        else {
            showProgressBar()
            val currentUser = auth.currentUser!!
            currentUser.updatePassword(pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this@ResetPassword, "Password updated successfully.", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                    auth.signOut()
                }
            }.addOnFailureListener {
                Toast.makeText(this@ResetPassword, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}