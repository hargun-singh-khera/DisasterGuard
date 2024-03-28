package com.example.disasterguard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class RegisterScreen : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etPass: EditText
    lateinit var etConfPass: EditText
    lateinit var imageView: ImageView
    lateinit var tvHeading: TextView
    lateinit var tvHeading2: TextView
    lateinit var relativeLayout: RelativeLayout
    lateinit var btnSignup: Button
    lateinit var tvLogin: TextView
    lateinit var animBlink: Animation
    lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
//    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressDialog: ProgressDialog

    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
//    val fileName = "userType"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPass = findViewById(R.id.etPass)
        etConfPass = findViewById(R.id.etConfPass)
        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvLogin)
        tvHeading = findViewById(R.id.tvHeading)
        tvHeading2 = findViewById(R.id.tvHeading2)
        relativeLayout = findViewById(R.id.relativeLayout)
        imageView = findViewById(R.id.imageView)


        showAnimation()

        progressDialog = ProgressDialog(this)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
//        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)

        initializeCallback()

        btnSignup.setOnClickListener {
            signUpUser()
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        showAnimation()
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val isEmailVerified = user.isEmailVerified
                Toast.makeText(this@RegisterScreen, "Email Verified onResume: ${isEmailVerified}", Toast.LENGTH_SHORT).show()
                checkUserEmailVerification()
            }
        }
    }

    private fun showAnimation() {
        animBlink = AnimationUtils.loadAnimation(this, R.anim.move_up)
        imageView.startAnimation(animBlink)
        tvHeading.startAnimation(animBlink)
        tvHeading2.startAnimation(animBlink)
        etName.startAnimation(animBlink)
        etEmail.startAnimation(animBlink)
        etMobileNumber.startAnimation(animBlink)
        etPass.startAnimation(animBlink)
        etConfPass.startAnimation(animBlink)
        relativeLayout.startAnimation(animBlink)
    }

    private fun signUpUser() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val number = etMobileNumber.text.toString()
        val pass = etPass.text.toString()
        val cpass = etConfPass.text.toString()
        if (name.isEmpty() || email.isEmpty() || number.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
            Toast.makeText(this@RegisterScreen, "All fields are mandatory", Toast.LENGTH_SHORT).show()
        }
        else if (number.length != 10) {
            Toast.makeText(this@RegisterScreen, "Mobile number must be of 10 characters. Please enter a valid mobile number.", Toast.LENGTH_SHORT).show()
            etMobileNumber.text.clear()
        }
        else if (pass != cpass) {
            Toast.makeText(this@RegisterScreen, "Password and Confirm Password doesn't matches", Toast.LENGTH_SHORT).show()
            etPass.text.clear()
            etConfPass.text.clear()
        }
        else if (pass.length < 6) {
            Toast.makeText(this@RegisterScreen, "Password length should be at least 6 characters.", Toast.LENGTH_SHORT).show()
            etPass.text.clear()
            etConfPass.text.clear()
        }
        else {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        checkUserEmailVerification()
                    }?.addOnFailureListener {
                        Toast.makeText(this, "Failed to send email verification", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterScreen, "Failed to create user", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this@RegisterScreen, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar() {
        progressDialog.setMessage("Email Verification Sent....")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }


    private fun checkUserEmailVerification() {
        val user = auth.currentUser
        showProgressBar()
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.setMessage("Verifying for your email....")
        },1000)
        val isEmailVerified = user?.isEmailVerified
        Toast.makeText(this@RegisterScreen, "Email verified: ${isEmailVerified}", Toast.LENGTH_SHORT).show()
        if (user != null && isEmailVerified!!) {
            Toast.makeText(this@RegisterScreen, "User Email Verified", Toast.LENGTH_SHORT).show()
            verifyOtp()
        }
        else {
            Toast.makeText(this@RegisterScreen, "Please verify your email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeCallback() {
        Toast.makeText(this@RegisterScreen, "Callback initialized", Toast.LENGTH_SHORT).show()
        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("Verification Status", "Verification Completed")
                Toast.makeText(this@RegisterScreen, "Verification Completed", Toast.LENGTH_SHORT).show()
                hideProgressBar()

                startActivity(Intent(this@RegisterScreen, UserDashboard::class.java))
                finish()
            }
            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("Verification Failed" , "onVerificationFailed $e")
                Toast.makeText(this@RegisterScreen, "Verification Failed", Toast.LENGTH_SHORT).show()
            }
            // On code is sent by the firebase this method is called in here we start a new activity where user can enter the OTP
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("Code Sent","onCodeSent: $verificationId")
                Toast.makeText(this@RegisterScreen, "Code sent", Toast.LENGTH_SHORT).show()
                storedVerificationId = verificationId
                resendToken = token
                progressDialog.setMessage("Sending OTP to your number...")
                // Start a new activity using intent also send the storedVerificationId using intent we will use this id to send the otp back to firebase
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val number = etMobileNumber.text.toString()
                val pass = etPass.text.toString()
                Toast.makeText(this@RegisterScreen, "Password: ${pass}", Toast.LENGTH_SHORT).show()
                val userId = auth.currentUser?.uid

                val intent = Intent(applicationContext,OTPVerification::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("userId", userId)
                intent.putExtra("name", name)
                intent.putExtra("email", email)
                intent.putExtra("number", number)
                intent.putExtra("pass", pass)

                etName.text.clear()
                etEmail.text.clear()
                etMobileNumber.text.clear()
                etPass.text.clear()
                etConfPass.text.clear()

                startActivity(intent)
                finish()
            }
        }
    }

    private fun verifyOtp() {
        Toast.makeText(this@RegisterScreen, "Verify otp method", Toast.LENGTH_SHORT).show()
        var number = etMobileNumber.text.trim().toString()
        // get the phone number and append the country cde with it
        number = "+91$number"
        Toast.makeText(this@RegisterScreen, "Number: ${number}", Toast.LENGTH_SHORT).show()
        Toast.makeText(this@RegisterScreen, "Calling send verification code", Toast.LENGTH_SHORT).show()
        sendVerificationCode(number)
    }

    // this method sends the verification code and starts the callback of verification which is implemented above in onCreate
    private fun sendVerificationCode(number: String) {
        Toast.makeText(this@RegisterScreen, "Send verification code", Toast.LENGTH_SHORT).show()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("CSE227" , "Auth started")
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}