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
    lateinit var tvIncidentDesc: TextView
    lateinit var tvLocationDesc: TextView
    lateinit var tvDateDesc: TextView
    lateinit var tvTimeDesc: TextView
    lateinit var tvEmergencyLevelDesc: TextView
    lateinit var tvSubmitted: TextView
    lateinit var tvProblemDesc: TextView

    lateinit var incidentType: String
    lateinit var incidentLocation: String
    lateinit var incidentDesc: String
    lateinit var incidentDate: String
    lateinit var incidentTime: String
    lateinit var emergencyLevel: String
    lateinit var submissionDate: String
    lateinit var imageView: ImageView
//    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ticket_response)

        etResponse = findViewById(R.id.etResponse)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvIncidentDesc = findViewById(R.id.tvIncidentDesc)
        tvLocationDesc = findViewById(R.id.tvLocationDesc)
        tvDateDesc = findViewById(R.id.tvDateDesc)
        tvTimeDesc = findViewById(R.id.tvTimeDesc)
        tvEmergencyLevelDesc = findViewById(R.id.tvEmergencyLevelDesc)
        tvProblemDesc = findViewById(R.id.tvProblemDesc)
        imageView = findViewById(R.id.imageView)
        tvSubmitted = findViewById(R.id.tvSubmitted)
        auth = FirebaseAuth.getInstance()

        userId = intent.getStringExtra("userId")!!
        ticketId = intent.getStringExtra("ticketId")!!
        incidentType = intent.getStringExtra("incidentType")!!
        incidentLocation = intent.getStringExtra("incidentLocation")!!
        incidentDesc = intent.getStringExtra("incidentDesc")!!
        incidentDate = intent.getStringExtra("incidentDate")!!
        incidentTime = intent.getStringExtra("incidentTime")!!
        emergencyLevel = intent.getStringExtra("emergencyLevel")!!
        submissionDate = intent.getStringExtra("submissionDate")!!


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

//        val bitmapFilePath = intent.getStringExtra("bitmapPath")
//        if (bitmapFilePath != null) {
//            bitmap = BitmapFactory.decodeFile(bitmapFilePath)
//            // Use the bitmap as needed
//        }

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
            val request = RequestModel(userId, ticketId, incidentType, incidentDesc, incidentLocation, incidentDate, incidentTime, emergencyLevel, submissionDate,true)
            dbRef.child(ticketId!!).setValue(request).addOnSuccessListener {
                Toast.makeText(this, "Your response has been recorded successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener{ error ->
                Toast.makeText(this, "Error while responding ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setValuesToView() {
        tvIncidentDesc.text = incidentType
        tvLocationDesc.text = incidentLocation
        tvDateDesc.text = incidentDate
        tvTimeDesc.text = incidentTime
        tvProblemDesc.text = incidentDesc
        tvEmergencyLevelDesc.text = emergencyLevel

        tvSubmitted.text = "Submitted on: ${submissionDate}"

    }
}