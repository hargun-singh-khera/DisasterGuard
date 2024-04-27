package com.example.disasterguard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminDashboard : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var cardViewTicketManagement: CardView
    lateinit var cardViewUserManagement: CardView
    lateinit var sharedPreferences: SharedPreferences
    var fileName = "userType"
    lateinit var dbRef: DatabaseReference
    lateinit var ticketCount: TextView
    lateinit var userCount: TextView
    lateinit var progressDialog: ProgressDialog
    lateinit var valueEventListener: ValueEventListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        auth = FirebaseAuth.getInstance()
        cardViewTicketManagement = findViewById(R.id.cardViewTicketManagement)
        cardViewUserManagement = findViewById(R.id.cardViewUserManagement)
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)

        ticketCount = findViewById(R.id.ticketCount)

        userCount = findViewById(R.id.userCount)
        getUserCount()

        showProgressBar()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Dashboard")
        setSupportActionBar(toolbar)

        cardViewTicketManagement.setOnClickListener {
            startActivity(Intent(this, AdminTicketManagement::class.java))
        }

        cardViewUserManagement.setOnClickListener {
            startActivity(Intent(this, AdminUserManagement::class.java))
        }

        // Declare the ValueEventListener as a member variable
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count: Long = 0
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val userRef = userSnap.child("Requests")
                        for (requestSnap in userRef.children) {
                            val reqCompleted = requestSnap.child("reqCompleted").getValue(Boolean::class.java)
                            if (reqCompleted == false) {
                                count++
                            }
                        }
                    }
                    // Update UI with the count here
                    ticketCount.text = count.toString()
                    // If you want to hide a progress bar, call your hideProgressBar() function here
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.logout -> {
                logoutAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading....")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }


    override fun onResume() {
        super.onResume()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addValueEventListener(valueEventListener)
    }

    // Remove ValueEventListener when the admin dashboard is paused or stopped
    override fun onPause() {
        super.onPause()
        dbRef.removeEventListener(valueEventListener)
    }

    fun getUserCount() {
        var count:Long = 0
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val dbQuery = dbRef.orderByChild("userAdmin").equalTo(false)
        dbQuery.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    count = snapshot.childrenCount
                    userCount.text = count.toString()
                    hideProgressBar()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun logoutAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to logout ?")
        builder.setTitle("Logout Alert!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> logoutRedirect()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun logoutRedirect() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", false)
        editor.apply()
        auth.signOut()
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }
}