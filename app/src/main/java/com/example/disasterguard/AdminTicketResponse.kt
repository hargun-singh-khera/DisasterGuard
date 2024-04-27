package com.example.disasterguard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminTicketResponse : AppCompatActivity() {
    lateinit var etResponse: EditText
    lateinit var btnSubmit: Button
    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var userId: String
    lateinit var ticketId: String
    lateinit var laptopModel: String
    lateinit var problemDesc: String
    lateinit var submissionDate: String
    lateinit var tvLaptopModel: TextView
    lateinit var tvProblem: TextView
    lateinit var tvSubmitted: TextView
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap
    lateinit var sharedPreferences: SharedPreferences
    val fileName = "userType"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ticket_response)

        etResponse = findViewById(R.id.etResponse)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvLaptopModel = findViewById(R.id.tvLaptopModel)
        tvProblem = findViewById(R.id.tvProblem)
        imageView = findViewById(R.id.imageView)
        tvSubmitted = findViewById(R.id.tvSubmitted)
        auth = FirebaseAuth.getInstance()

        userId = intent.getStringExtra("userId")!!
        ticketId = intent.getStringExtra("ticketId")!!
        laptopModel = intent.getStringExtra("laptopModel")!!
        problemDesc = intent.getStringExtra("problemDesc")!!

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        submissionDate = sharedPreferences.getString("submissionDate", null)!!

        tvSubmitted.text = "Submitted on: ${submissionDate}"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Ticket ID: ${ticketId}")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Retrieve the ByteArray from the intent
//        val byteArray = intent.getByteArrayExtra("bitmapPath")
////
////        // Convert the ByteArray back to a Bitmap object
//        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        val bitmapFilePath = intent.getStringExtra("bitmapPath")
        if (bitmapFilePath != null) {
            bitmap = BitmapFactory.decodeFile(bitmapFilePath)
            // Use the bitmap as needed
        }

        setValuesToView()

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests")

        btnSubmit.setOnClickListener {
            submitResponse()
        }
    }

    private fun submitResponse() {
        val remarks = etResponse.text.toString()
        if (remarks.isEmpty()) {
            Toast.makeText(this@AdminTicketResponse, "Please enter a response", Toast.LENGTH_SHORT).show()
        }
        else {
            val request = RequestModel(userId, ticketId, laptopModel, problemDesc, submissionDate, remarks,true)
            dbRef.child(ticketId!!).setValue(request).addOnSuccessListener {
                Toast.makeText(this, "Your response has been recorded successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener{ error ->
                Toast.makeText(this, "Error while responding ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
//        pushNotification(problemDesc, remarks)
    }

    private fun setValuesToView() {
        tvLaptopModel.text = "Laptop Model: " + laptopModel
        tvProblem.text = problemDesc
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        }

    }
}