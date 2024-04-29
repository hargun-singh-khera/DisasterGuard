package com.example.disasterguard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AdminTicketManagement : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ticketList: ArrayList<RequestModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    lateinit var ticketId: String
    lateinit var incidentType: String
    lateinit var incidentDesc: String
    lateinit var incidentLocation: String
    lateinit var incidentDate: String
    lateinit var incidentTime: String
    lateinit var emergencyLevel: String
    lateinit var submissionDate: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ticket_management)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Ticket Management")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        getAllTickets()
    }

    private fun getAllTickets() {
        recyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()

        ticketList = arrayListOf<RequestModel>()

        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        tvLoadingData.visibility = View.GONE
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                if (snapshot.exists()){
                    for (userSnap in snapshot.children){
                        val userTicketRef = userSnap.child("Requests")
                        for (ticketSnap in userTicketRef.children) {
                            val ticket = ticketSnap.getValue(RequestModel::class.java)
                            val reqCompleted = ticket!!.reqCompleted
                            if (reqCompleted == false) {
                                val mAdapter = TicketAdapter(this@AdminTicketManagement, R.layout.ticket_item, ticketList)
                                recyclerView.adapter = mAdapter

                                mAdapter.setOnItemClickListener(object : TicketAdapter.onItemClickListener{
                                    override fun onItemClick(position: Int) {
                                        val intent = Intent(this@AdminTicketManagement, AdminTicketResponse::class.java)
                                        userId = ticketList[position].userId!!
                                        ticketId = ticketList[position].ticketId!!
                                        incidentType = ticketList[position].incidentType!!
                                        incidentDesc = ticketList[position].incidentDesc!!
                                        incidentLocation = ticketList[position].incidentLocation!!
                                        incidentDate = ticketList[position].incidentDate!!
                                        incidentTime = ticketList[position].incidentTime!!
                                        emergencyLevel = ticketList[position].emergencyLevel!!
                                        submissionDate = ticketList[position].submissionDate!!

                                        intent.putExtra("userId", userId)
                                        intent.putExtra("ticketId", ticketId)
                                        intent.putExtra("incidentType", incidentType)
                                        intent.putExtra("incidentDesc", incidentDesc)
                                        intent.putExtra("incidentLocation", incidentLocation)
                                        intent.putExtra("incidentDate", incidentDate)
                                        intent.putExtra("incidentTime", incidentTime)
                                        intent.putExtra("emergencyLevel", emergencyLevel)
                                        intent.putExtra("submissionDate", submissionDate)

                                        startActivity(intent)
                                    }
                                })

                                progressBar.visibility = View.GONE
                                tvLoadingData.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                ticketList.add(ticket)
                            }
                        }
                    }
                    if (ticketList.isEmpty()) {
                        progressBar.visibility = View.GONE
                        tvLoadingData.visibility = View.VISIBLE
                        tvLoadingData.text = "No Record Found."
                    }
                }
                else {
                    progressBar.visibility = View.GONE
                    tvLoadingData.visibility = View.VISIBLE
                    tvLoadingData.text = "No Record Found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminTicketManagement, "Failed to load tickets.", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onResume() {
        super.onResume()
        getAllTickets()
    }

}