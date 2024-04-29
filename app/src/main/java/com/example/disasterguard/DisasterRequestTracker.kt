package com.example.disasterguard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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

class DisasterRequestTracker : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ticketList: ArrayList<RequestModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_request_tracker)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Track Requests")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.track_request_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.addRequest -> {
                startActivity(Intent(this, SupportTicket::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAllTickets() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.GONE
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Requests")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                if (snapshot.exists()){
                    for (ticketSnap in snapshot.children){
                        val ticket = ticketSnap.getValue(RequestModel::class.java)
                        val mAdapter = UserTicketAdapter(this@DisasterRequestTracker, R.layout.user_ticket_item, ticketList, dbRef)
                        recyclerView.adapter = mAdapter

                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        tvLoadingData.visibility = View.GONE
                        ticketList.add(ticket!!)
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
                TODO("Not yet implemented")
            }
        })
    }
}