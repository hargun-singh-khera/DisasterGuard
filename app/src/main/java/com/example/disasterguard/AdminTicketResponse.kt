package com.example.disasterguard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    lateinit var tvUserNameDesc: TextView
    lateinit var tvUserEmailDesc: TextView
    lateinit var tvUserMobileDesc: TextView

    lateinit var bitmap: Bitmap

    lateinit var storageReference: StorageReference
    lateinit var progressDialog: ProgressDialog

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
        tvUserNameDesc = findViewById(R.id.tvUserNameDesc)
        tvUserEmailDesc = findViewById(R.id.tvUserEmailDesc)
        tvUserMobileDesc = findViewById(R.id.tvUserMobileDesc)

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

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        setValuesToView()
        
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
            val updates = HashMap<String, Any>()
            updates["reqCompleted"] = true
            updates["remarks"] = remarks
            dbRef.child("Requests").child(ticketId).updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Your response has been recorded successfully", Toast.LENGTH_SHORT).show()
                sendNotificationToUser(userId, ticketId)
                finish()
            }.addOnFailureListener{ error ->
                Toast.makeText(this, "Error while responding ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendNotificationToUser(currentUserId: String, ticketId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserModel::class.java)
                    val userId = userData?.userId!!
                    val date = getCurrentDate()

                    val notificationRef = dbRef.child("Notifications")
                    val notificationId = notificationRef.push().key!!
                    val messageResponse = "Dear User, The resolution of your request/query has been provided. Check under Dashboard -> Track Requests section and give valuable feedback."
                    val notification = UserNotificationModel(currentUserId, ticketId, messageResponse, date)

                    notificationRef.child(notificationId).setValue(notification).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Assuming this code is inside an activity, change to your actual context
                            Toast.makeText(this@AdminTicketResponse, "Your request has been received as notification.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }



    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun setValuesToView() {
        showProgressBar()
        tvIncidentDesc.text = incidentType
        tvLocationDesc.text = incidentLocation
        tvDateDesc.text = incidentDate
        tvTimeDesc.text = incidentTime
        tvProblemDesc.text = incidentDesc
        tvEmergencyLevelDesc.text = emergencyLevel

        if (emergencyLevel == "High") {
            tvEmergencyLevelDesc.setTextColor(Color.parseColor("#FF001E"))
        } else if (emergencyLevel == "Medium") {
            tvEmergencyLevelDesc.setTextColor(Color.parseColor("#F39C05"))
        } else {
            tvEmergencyLevelDesc.setTextColor(Color.parseColor("#44F305"))
        }

        tvSubmitted.text = "Submitted on: ${submissionDate}"
        getUserDetails()

        storageReference = FirebaseStorage.getInstance().getReference("Users/${userId}/Tickets/").child(ticketId)
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            imageView.visibility = View.VISIBLE
            imageView.setImageBitmap(bitmap)
            hideProgressBar()
        } .addOnFailureListener {
            hideProgressBar()
        }

    }
    
    private fun getUserDetails() {
        dbRef.child("Requests").child(ticketId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val ticketData = snapshot.getValue(RequestModel::class.java)
                    val userId = ticketData?.userId
                    dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
                    dbRef.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val userData = snapshot.getValue(UserModel::class.java)
                                val userName = userData?.userName
                                val userEmail = userData?.userEmail
                                val userMobile = userData?.userMobileNumber
                                tvUserNameDesc.text = userName
                                tvUserEmailDesc.text = userEmail
                                tvUserMobileDesc.text = userMobile
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.hide()
    }
}