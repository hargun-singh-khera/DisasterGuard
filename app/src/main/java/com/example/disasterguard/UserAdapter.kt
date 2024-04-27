package com.example.disasterguard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(val context: Context, val resource: Int, val objects: ArrayList<UserModel>, var dbRef: DatabaseReference) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    lateinit var auth: FirebaseAuth
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)
        val tvContact = itemView.findViewById<TextView>(R.id.tvContact)
        val btnDelete = itemView.findViewById<Button>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        auth = FirebaseAuth.getInstance()
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val myObj = objects[position]
        holder.tvName.text = "Name: ${myObj.userName}"
        holder.tvEmail.text = "Email: ${myObj.userEmail}"
        holder.tvContact.text = "Contact No: ${myObj.userMobileNumber}"
        holder.btnDelete.setOnClickListener {
            confirmDeleteUserAccount(position)
        }
    }

    private fun deleteUser(position: Int) {
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val userId = objects[position].userId
        dbRef.child(userId!!).removeValue().addOnSuccessListener {
            objects.removeAt(position)
            notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(context, "An Error Occurred " + it.message, Toast.LENGTH_SHORT).show()
        }

        auth = FirebaseAuth.getInstance()

    }

    private fun confirmDeleteUserAccount(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete this user account ?")
        builder.setTitle("Confirm Deletion!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> deleteUser(position)
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}