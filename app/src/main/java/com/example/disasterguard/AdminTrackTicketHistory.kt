package com.example.disasterguard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

class AdminTrackTicketHistory : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ticketList: ArrayList<RequestModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_track_ticket_history)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Tickets History")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!

        ticketList = arrayListOf<RequestModel>()
        getAllTickets()

    }

    private fun getAllTickets() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.GONE
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    ticketList.clear()
                    for (userSnap in snapshot.children) {
                        val requestsRef = userSnap.child("Requests")
                        for (ticketSnap in requestsRef.children) {
                            val ticket = ticketSnap.getValue(RequestModel::class.java)
                            if (ticket?.reqCompleted == true) {
                                ticketList.add(ticket)
                            }
                        }
                    }
                    val mAdapter = TicketHistoryAdapter(this@AdminTrackTicketHistory, R.layout.admin_ticket_history_item, ticketList)
                    recyclerView.adapter = mAdapter

                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                } else {
                    progressBar.visibility = View.GONE
                    tvLoadingData.visibility = View.VISIBLE
                    tvLoadingData.text = "No Record Found."
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}