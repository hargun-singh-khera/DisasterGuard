package com.example.disasterguard

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SupportTicket : AppCompatActivity() {
    lateinit var tvHeading: TextView
    lateinit var etDescription: EditText
    lateinit var etLocation: EditText
    lateinit var etDate: EditText
    lateinit var etTime: EditText
    lateinit var ivImgUpload: ImageView
    lateinit var btnSubmitRequest: Button
    lateinit var cal: Calendar
    lateinit var dropdownEmergency: Spinner
    lateinit var dropdownIncidentType: Spinner
    lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences

    private var userId: String?= null
    lateinit var ticketId: String
    val fileName = "userType"

    var optionEmergencyLevel = ""
    var optionIncident = ""

    var fileUri: Uri?= null
    lateinit var getImage: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_ticket)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Report Disaster")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        tvHeading = findViewById(R.id.tvHeading)
        etDescription = findViewById(R.id.etDescription)
        etLocation = findViewById(R.id.etLocation)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        ivImgUpload = findViewById(R.id.ivImgUpload)
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest)
        dropdownEmergency = findViewById(R.id.dropdownEmergency)
        dropdownIncidentType = findViewById(R.id.dropdownIncidentType)

        cal = Calendar.getInstance()

        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests")
        ticketId = dbRef.push().key!!

        etDate.setOnClickListener {
            getDate()
        }

        etTime.setOnClickListener {
            getTime()
        }

        incidentDropdown()

        emergencyDropdown()

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    fileUri = it
                }
            })

        ivImgUpload.setOnClickListener {
            Toast.makeText(this, "Upload image clicked", Toast.LENGTH_SHORT).show()
            getImage.launch("image/*")
        }

        btnSubmitRequest.setOnClickListener {
            submitForm()
        }

    }

    private fun uploadImage() {
        if (fileUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Uploading your image...")
            progressDialog.show()
            if (userId != null) {
                val ref: StorageReference = FirebaseStorage.getInstance().getReference("Users/${userId}/Tickets/").child(ticketId)
                ref.putFile(fileUri!!).addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@SupportTicket, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@SupportTicket, " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            Toast.makeText(this@SupportTicket, "File uri null found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDate() {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            etDate.setText("${dayOfMonth}/$month/$year")
        }, year, month, day).show()
    }

    private fun getTime() {
        val timePicker = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            etTime.setText(timeFormat.format(cal.time))
        }
        TimePickerDialog(this, timePicker, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    private fun emergencyDropdown() {
        val emergencyLevel = arrayOf("Low", "Medium", "High")


        if (dropdownEmergency!=null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emergencyLevel)
            dropdownEmergency.adapter = adapter
            dropdownEmergency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    optionEmergencyLevel = emergencyLevel[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    private fun incidentDropdown() {
        val incidentyType = arrayOf("Fire", "Flood", "Earthquake", "Accident", "Medical Emergency")


        if (dropdownIncidentType!=null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, incidentyType)
            dropdownIncidentType.adapter = adapter
            dropdownIncidentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    optionIncident = incidentyType[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    private fun submitForm() {
        val description = etDescription.text.toString()
        val location = etLocation.text.toString()
        val date = etDate.text.toString()
        val time = etTime.text.toString()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please provide the description of the incident.", Toast.LENGTH_SHORT).show()
        }
        else if (location.isEmpty()) {
            Toast.makeText(this, "Please provide the location of the incident.", Toast.LENGTH_SHORT).show()
        }
        else if (date.isEmpty()) {
            Toast.makeText(this, "Please provide the date of the incident.", Toast.LENGTH_SHORT).show()
        }
        else if (time.isEmpty()) {
            Toast.makeText(this, "Please provide a time for the incident.", Toast.LENGTH_SHORT).show()
        }
        else {
            val currentDate = DateFormat.getDateInstance().format(cal.time)
            val editor = sharedPreferences.edit()
            editor.putString("submissionDate", currentDate)
            editor.apply()

            val submissionDate = currentDate.toString()

            val ticket = RequestModel(userId, ticketId, optionIncident, description, location, date, time, "", optionEmergencyLevel, submissionDate)
            dbRef.child(ticketId).setValue(ticket).addOnCompleteListener {
                if (it.isSuccessful) {
                    uploadImage()
                    sendNotificationToAdmin(userId!!, ticketId)
                    Toast.makeText(this@SupportTicket, "Your request has been received. Our team will get back to you shortly.", Toast.LENGTH_SHORT).show()
                    etDescription.text.clear()
                    etLocation.text.clear()
                    etDate.text.clear()
                    etTime.text.clear()
                }
            }.addOnFailureListener {
                Toast.makeText(this@SupportTicket, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotificationToAdmin(currentUserId: String, ticketId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val userData = userSnap.getValue(UserModel::class.java)
                        val userId = userData?.userId!!
                        val userName = userData.userName!!
                        val userAdmin = userData.userAdmin!!
                        if (userAdmin) {
                            val notificationRef = dbRef.child(userId).child("AdminNotifications")
                            val notificationId = notificationRef.push().key!!
                            val notification = AdminNotificationModel(currentUserId, ticketId, userName)
                            notificationRef.child(notificationId).setValue(notification).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this@SupportTicket, "Your request has been received as notification.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}