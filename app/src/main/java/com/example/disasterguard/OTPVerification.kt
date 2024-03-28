package com.example.disasterguard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OTPVerification : AppCompatActivity() {
    lateinit var btnVerify: Button
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    lateinit var storedVerificationId:String
    private lateinit var dbRef: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressDialog: ProgressDialog
    val fileName = "userType"

    lateinit var etOtp1: EditText
    lateinit var etOtp2: EditText
    lateinit var etOtp3: EditText
    lateinit var etOtp4: EditText
    lateinit var etOtp5: EditText
    lateinit var etOtp6: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        etOtp1 = findViewById(R.id.etOtp1)
        etOtp2 = findViewById(R.id.etOtp2)
        etOtp3 = findViewById(R.id.etOtp3)
        etOtp4 = findViewById(R.id.etOtp4)
        etOtp5 = findViewById(R.id.etOtp5)
        etOtp6 = findViewById(R.id.etOtp6)

        setEditTextListeners()

        setupEditText(etOtp1)
        setupEditText(etOtp2)
        setupEditText(etOtp3)
        setupEditText(etOtp4)
        setupEditText(etOtp5)
        setupEditText(etOtp6)

        btnVerify = findViewById(R.id.btnVerify)
        auth = FirebaseAuth.getInstance()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)

        storedVerificationId = intent.getStringExtra("storedVerificationId")!!
        userId = intent.getStringExtra("userId")!!
//        Toast.makeText(this, "storedVerificationId otp: ${storedVerificationId}", Toast.LENGTH_SHORT).show()

        btnVerify.setOnClickListener {
            val et1 = etOtp1.text.toString()
            val et2 = etOtp2.text.toString()
            val et3 = etOtp3.text.toString()
            val et4 = etOtp4.text.toString()
            val et5 = etOtp5.text.toString()
            val et6 = etOtp6.text.toString()

            if (et1.isEmpty() || et2.isEmpty() || et3.isEmpty() || et4.isEmpty() || et5.isEmpty() || et6.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyUserMobile()
            }
        }
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Verifying your OTP...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }


    private fun saveUserData(name: String, email: String, number: String) {
//        Toast.makeText(this@OTPVerification, "User details saved", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "UserId: ${userId}", Toast.LENGTH_SHORT).show()
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", false)
        editor.putString("userName", name)
        editor.apply()
        val user = UserModel(userId, name, email, number, false)
        dbRef.child(userId!!).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this@OTPVerification, "User Registration Successful", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this@OTPVerification, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verifyUserMobile() {
        showProgressBar()
        val et1 = etOtp1.text.toString()
        val et2 = etOtp2.text.toString()
        val et3 = etOtp3.text.toString()
        val et4 = etOtp4.text.toString()
        val et5 = etOtp5.text.toString()
        val et6 = etOtp6.text.toString()

        val otp = et1 + et2 + et3 + et4 + et5 + et6
//        Toast.makeText(this@OTPVerification, "OTP Entered: ${otp}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this@OTPVerification, "Verify user mobile", Toast.LENGTH_SHORT).show()
        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
//        Toast.makeText(this@OTPVerification, "Credentials: ${credential}", Toast.LENGTH_SHORT).show()
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val name = intent.getStringExtra("name")!!
                val email = intent.getStringExtra("email")!!
                val number = intent.getStringExtra("number")!!
                val pass = intent.getStringExtra("pass")!!

                val editor = sharedPreferences.edit()
                editor.putString("pass", pass)
//                Toast.makeText(this@OTPVerification, "OTP Password saved locally", Toast.LENGTH_SHORT).show()
                editor.apply()

                saveUserData(name, email, number)
                hideProgressBar()
                val intent = Intent(this@OTPVerification, UserDashboard::class.java)
                startActivity(intent)
                finish()
            }
            else {
                hideProgressBar()
                // Verification failed, display a message and update the UI
                if (it.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setupEditText(editText: EditText) {
        editText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                // Handle delete button press
                if (editText.text.isNotEmpty()) {
                    editText.text.delete(editText.text.length - 1, editText.text.length)
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun setEditTextListeners() {
        etOtp1.addTextChangedListener(GenericTextWatcher(etOtp1, etOtp2))
        etOtp2.addTextChangedListener(GenericTextWatcher(etOtp2, etOtp3))
        etOtp3.addTextChangedListener(GenericTextWatcher(etOtp3, etOtp4))
        etOtp4.addTextChangedListener(GenericTextWatcher(etOtp4, etOtp5))
        etOtp5.addTextChangedListener(GenericTextWatcher(etOtp5, etOtp6))
    }

    private class GenericTextWatcher(private val currentView: EditText, private val nextView: EditText) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            if (text.length == 1) {
                nextView.requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }
    }
}