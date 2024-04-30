package com.example.disasterguard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginScreen : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPass: EditText
    lateinit var btnSignin: Button
    lateinit var tvRegister: TextView
    lateinit var imageView: ImageView
    lateinit var tvHeading: TextView
    lateinit var tvHeading2: TextView
    lateinit var relativeLayout: RelativeLayout
    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var animBlink: Animation
    var isAdmin: Boolean ?= false
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressDialog: ProgressDialog
    lateinit var tvForgotPassword: TextView
    val fileName = "userType"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        btnSignin = findViewById(R.id.btnSignin)
        tvRegister = findViewById(R.id.tvRegister)
        imageView = findViewById(R.id.imageView)
        tvHeading = findViewById(R.id.tvHeading)
        tvHeading2 = findViewById(R.id.tvHeading2)
        relativeLayout = findViewById(R.id.relativeLayout)

        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)

        showAnimation()

        btnSignin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterScreen::class.java))
//            finish()
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
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
        etPass.startAnimation(animBlink)
        tvForgotPassword.startAnimation(animBlink)
        btnSignin.startAnimation(animBlink)
        relativeLayout.startAnimation(animBlink)
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Signing")
        progressDialog.setMessage("Checking your credentials....")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    private fun loginUser() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
        }
        else {
            showProgressBar()
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                if(it.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val isEmailVerified = auth.currentUser?.isEmailVerified
                        if (isEmailVerified!!) {
                            checkUserAndLogin(email)
                            hideProgressBar()
                        }
                        else {
                            etEmail.text.clear()
                            etPass.text.clear()
                            hideProgressBar()
                            Toast.makeText(this@LoginScreen, "Please verify your email to continue", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                hideProgressBar()
                Toast.makeText(this@LoginScreen, "Invalid Login Credentials", Toast.LENGTH_SHORT).show()
                etEmail.text.clear()
                etPass.text.clear()
            }
        }
    }
    private fun checkUserAndLogin(email: String) {
        dbRef.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@LoginScreen, "Email: ${email}", Toast.LENGTH_SHORT).show()
                    for (userSnap in snapshot.children) {
//                        Toast.makeText(this@LoginScreen, "How many in loop", Toast.LENGTH_SHORT).show()
                        val user = userSnap.getValue(UserModel::class.java)
                        isAdmin = user?.userAdmin
                        // saving the userType for further login process of app
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isAdmin", isAdmin!!)
                        editor.putString("userName", user?.userName)
                        editor.apply()
                        if (isAdmin!!) {
//                            Toast.makeText(this@LoginScreen, "Admin User", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginScreen, AdminDashboard::class.java))
                            finish()
                        } else {
//                            Toast.makeText(this@LoginScreen, "Normal User", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginScreen, UserDashboard::class.java))
                            finish()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}