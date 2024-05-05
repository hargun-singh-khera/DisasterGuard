package com.example.disasterguard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class UserNotificationAdapter(val context: Context, val resource: Int, val objects: ArrayList<UserNotificationModel>) : RecyclerView.Adapter<UserNotificationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading = itemView.findViewById<TextView>(R.id.tvHeading)
        val tvResponse = itemView.findViewById<TextView>(R.id.tvResponse)
        val dateOfEntry = itemView.findViewById<TextView>(R.id.dateOfEntry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myObj = objects[position]
        holder.tvHeading.text = "Ticket ID: ${myObj.ticketId} update"
        holder.tvResponse.text = "${myObj.messageResponse}"
        holder.dateOfEntry.text = "Entry Date: ${myObj.dateOfEntry}"
    }
}