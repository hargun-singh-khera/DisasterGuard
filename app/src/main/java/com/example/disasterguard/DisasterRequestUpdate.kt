package com.example.disasterguard

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DisasterRequestUpdate : AppCompatActivity() {
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

    lateinit var incidentType: String
    lateinit var incidentDesc: String
    lateinit var incidentLocation: String
    lateinit var incidentDate: String
    lateinit var incidentTime: String
    lateinit var emergencyLevel: String

    var optionEmergencyLevel = ""
    var optionIncident = ""

    var fileUri: Uri?= null
    lateinit var getImage: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_request_update)

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

        userId = intent.getStringExtra("userId")!!
        ticketId = intent.getStringExtra("ticketId")!!
        incidentType = intent.getStringExtra("incidentType")!!
        incidentDesc = intent.getStringExtra("incidentDesc")!!
        incidentLocation = intent.getStringExtra("incidentLocation")!!
        incidentDate = intent.getStringExtra("incidentDate")!!
        incidentTime = intent.getStringExtra("incidentTime")!!
        emergencyLevel = intent.getStringExtra("emergencyLevel")!!

        cal = Calendar.getInstance()

        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests")


        etDate.setOnClickListener {
            getDate()
        }

        etTime.setOnClickListener {
            getTime()
        }

        setValuesToView()

        incidentDropdown()

        emergencyDropdown()

        // Set selection for incident type spinner
        val incidentTypePosition = (dropdownIncidentType.adapter as ArrayAdapter<String>).getPosition(incidentType)
        dropdownIncidentType.setSelection(incidentTypePosition)

        // Set selection for emergency level spinner
        val emergencyLevelPosition = (dropdownEmergency.adapter as ArrayAdapter<String>).getPosition(emergencyLevel)
        dropdownEmergency.setSelection(emergencyLevelPosition)

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

    private fun setValuesToView() {
        etDescription.setText(incidentDesc)
        etLocation.setText(incidentLocation)
        etDate.setText(incidentDate)
        etTime.setText(incidentTime)
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
                    Toast.makeText(this@DisasterRequestUpdate, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@DisasterRequestUpdate, " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }
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
                    Toast.makeText(this@DisasterRequestUpdate, "Your request has been updated successfully.", Toast.LENGTH_SHORT).show()
                    etDescription.text.clear()
                    etLocation.text.clear()
                    etDate.text.clear()
                    etTime.text.clear()
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this@DisasterRequestUpdate, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}