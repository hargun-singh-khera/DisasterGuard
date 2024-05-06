package com.example.disasterguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class AdminNotifications : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var notificationList: ArrayList<AdminNotificationModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_notifications)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Notifications")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        getAllNotifications()
    }

    private fun getAllNotifications() {
        recyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()

        notificationList = arrayListOf<AdminNotificationModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                if (snapshot.exists()){
                    for (userSnap in snapshot.children) {
                        val user = userSnap.getValue(UserModel::class.java)
                        val userAdmin = user?.userAdmin!!
                        if (userAdmin) {
                            val notificationRef = userSnap.child("AdminNotifications")
                            for (notificationSnap in notificationRef.children) {
                                val notification = notificationSnap.getValue(AdminNotificationModel::class.java)
                                val mAdapter = AdminNotificationAdapter(this@AdminNotifications, R.layout.admin_notification, notificationList)
                                recyclerView.adapter = mAdapter

                                progressBar.visibility = View.GONE
                                tvLoadingData.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                notificationList.add(notification!!)

                            }
                        }

                    }
                    if (notificationList.isEmpty()) {
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
                Toast.makeText(this@AdminNotifications, "Failed to load tickets.", Toast.LENGTH_SHORT).show()
            }
        })

    }
}