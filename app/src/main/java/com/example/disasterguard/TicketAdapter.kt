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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(val context: Context, val resource: Int, val objects: ArrayList<RequestModel>) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvHeading = itemView.findViewById<TextView>(R.id.tvHeading)
        val tvEmergencyLevelStatus = itemView.findViewById<TextView>(R.id.tvEmergencyLevelStatus)
        val tvProblemDesc = itemView.findViewById<TextView>(R.id.tvProblemDesc)
        val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
//        val btnResolve = itemView.findViewById<TextView>(R.id.btnResolve)


        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        return ViewHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val myObj = objects[position]
        val level = objects[position].emergencyLevel

        if (level == "High") {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#FF001E"))
        } else if (level == "Medium") {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#F39C05"))
        } else {
            holder.tvEmergencyLevelStatus.setTextColor(Color.parseColor("#44F305"))
        }

        holder.tvHeading.text = "${myObj.incidentType} Incident"
        holder.tvEmergencyLevelStatus.text = "${myObj.emergencyLevel}"
        holder.tvProblemDesc.text = "${myObj.incidentDesc}"
        holder.tvLocation.text = "Location: ${myObj.incidentLocation}"
        holder.tvDate.text = "Date: ${myObj.incidentDate}"
        holder.tvTime.text = "Time: ${myObj.incidentTime}"

    }
}