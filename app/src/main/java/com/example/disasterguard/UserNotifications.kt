package com.example.disasterguard

import android.os.Bundle
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

class UserNotifications : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var notificationList: ArrayList<UserNotificationModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_notifications)

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
        val userId = auth.currentUser?.uid

        notificationList = arrayListOf<UserNotificationModel>()
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                if (snapshot.exists()){
                    val notificationRef = snapshot.child("Notifications")
                    for (notificationSnap in notificationRef.children) {
                        val notification = notificationSnap.getValue(UserNotificationModel::class.java)

                        val mAdapter = UserNotificationAdapter(this@UserNotifications, R.layout.user_notification, notificationList)
                        recyclerView.adapter = mAdapter

                        progressBar.visibility = View.GONE
                        tvLoadingData.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        notificationList.add(notification!!)
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
                Toast.makeText(this@UserNotifications, "Failed to load tickets.", Toast.LENGTH_SHORT).show()
            }
        })

    }
}