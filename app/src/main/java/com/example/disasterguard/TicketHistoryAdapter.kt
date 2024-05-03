package com.example.disasterguard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketHistoryAdapter(val context: Context, val resource: Int, val objects: ArrayList<RequestModel>) : RecyclerView.Adapter<TicketHistoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvHeading = itemView.findViewById<TextView>(R.id.tvHeading)
        val tvEmergencyLevelStatus = itemView.findViewById<TextView>(R.id.tvEmergencyLevelStatus)
        val tvProblemDesc = itemView.findViewById<TextView>(R.id.tvProblemDesc)
        val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        val tvRequestId = itemView.findViewById<TextView>(R.id.tvRequestId)
        val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        val tvRemarks = itemView.findViewById<TextView>(R.id.tvRemarks)
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
        val level = objects[position].emergencyLevel
        val reqCompleted = objects[position].reqCompleted

        if (reqCompleted == true) {
            holder.tvStatus.text = "Completed"
            holder.tvStatus.setTextColor(Color.parseColor("#80FF00"))
            holder.tvRemarks.visibility = View.VISIBLE
            holder.tvRemarks.text = "Remarks: ${myObj.remarks}"
        } else {
            holder.tvStatus.text = "In Progress"
        }

        if (level == "High") {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#FF001E"))
        } else if (level == "Medium") {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#F39C05"))
        } else {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#44F305"))
        }

        holder.tvRequestId.text = "Request ID: ${myObj.ticketId}"
        holder.tvHeading.text = "${myObj.incidentType} Incident"
        holder.tvEmergencyLevelStatus.text = "${myObj.emergencyLevel}"
        holder.tvProblemDesc.text = "${myObj.incidentDesc}"
        holder.tvLocation.text = "Location: ${myObj.incidentLocation}"
        holder.tvDate.text = "Date: ${myObj.incidentDate}"
        holder.tvTime.text = "Time: ${myObj.incidentTime}"


    }
}