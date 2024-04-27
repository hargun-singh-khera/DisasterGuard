package com.example.disasterguard

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

class AdminUserManagement : AppCompatActivity() {
    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var userList: ArrayList<UserModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    var userExists: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user_management)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Manage Users")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        empRecyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        progressBar = findViewById(R.id.progressBar)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!

        userList = arrayListOf<UserModel>()
        getAllUsers()

    }

    private fun getAllUsers() {
        empRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        tvLoadingData.visibility = View.GONE
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (snapshot.exists()){
                    for (userSnap in snapshot.children){
                        val user = userSnap.getValue(UserModel::class.java)
                        if (user?.userAdmin == false) {
                            userExists = true
                            userList.add(user)
                        }
                    }
                    if (userExists) {
                        val mAdapter = UserAdapter(this@AdminUserManagement, R.layout.user_item, userList, dbRef)
                        empRecyclerView.adapter = mAdapter
                        progressBar.visibility = View.GONE
                        empRecyclerView.visibility = View.VISIBLE
                        tvLoadingData.visibility = View.GONE
                    } else {
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