package com.example.disasterguard

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserTicketAdapter(val context: Context, val resource: Int, val objects: ArrayList<RequestModel>, var dbRef: DatabaseReference) : RecyclerView.Adapter<UserTicketAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvHeading = itemView.findViewById<TextView>(R.id.tvHeading)
        val tvEmergencyLevelStatus = itemView.findViewById<TextView>(R.id.tvEmergencyLevelStatus)
        val tvProblemDesc = itemView.findViewById<TextView>(R.id.tvProblemDesc)
        val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        val btnEdit = itemView.findViewById<ImageView>(R.id.btnEdit)
        val btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val myObj = objects[position]
        val ticketId = myObj.ticketId!!
        val userId = myObj.userId!!
        val incidentType = myObj.incidentType!!
        val incidentDesc = myObj.incidentDesc!!
        val incidentLocation = myObj.incidentLocation!!
        val incidentDate = myObj.incidentDate!!
        val incidentTime = myObj.incidentTime!!
        val emergencyLevel = myObj.emergencyLevel

        if (emergencyLevel == "High") {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#FF001E"))
        } else if (emergencyLevel == "Medium") {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#F39C05"))
        } else {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#44F305"))
        }

        holder.tvHeading.text = "${myObj.incidentType} Incident"
        holder.tvEmergencyLevelStatus.text = "${emergencyLevel}"
        holder.tvProblemDesc.text = "${myObj.incidentDesc}"
        holder.tvLocation.text = "Location: ${myObj.incidentLocation}"
        holder.tvDate.text = "Date: ${myObj.incidentDate}"
        holder.tvTime.text = "Time: ${myObj.incidentTime}"

        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, DisasterRequestUpdate::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("ticketId", ticketId)
            intent.putExtra("incidentType", incidentType)
            intent.putExtra("incidentDesc", incidentDesc)
            intent.putExtra("incidentLocation", incidentLocation)
            intent.putExtra("incidentDate", incidentDate)
            intent.putExtra("incidentTime", incidentTime)
            context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener {
            deleteAlert(userId, ticketId)
        }

    }

    private fun deleteTicketItem(userId: String, ticketId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Requests").child(ticketId)
        dbRef.removeValue().addOnSuccessListener {
            Toast.makeText(context, "Ticket record deleted successfully", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAlert(userId: String, ticketId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete this request?")
        builder.setTitle("Confirm Deletion!")
        builder.setCancelable(false)

        builder.setPositiveButton("Yes") { dialog, which ->
            deleteTicketItem(userId, ticketId)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}