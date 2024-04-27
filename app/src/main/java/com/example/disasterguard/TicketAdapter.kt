package com.example.disasterguard

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(val context: Context, val resource: Int, val objects: ArrayList<RequestModel>, val ticketBitmapMap: HashMap<String, Bitmap>) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvTicketId = itemView.findViewById<TextView>(R.id.tvTicketId)
        val tvProblemType = itemView.findViewById<TextView>(R.id.tvProblemType)
        val tvLaptopModel = itemView.findViewById<TextView>(R.id.tvLaptopModel)
        val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

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
        holder.imageView.setImageBitmap(ticketBitmapMap[myObj.ticketId])
//        holder.imageView.setImageResource(R.drawable.img8)
        holder.tvTicketId.text = "Ticket ID: ${myObj.ticketId}"
        holder.tvProblemType.text = "Problem: ${myObj.problemDesc}"
        holder.tvLaptopModel.text = "Model: ${myObj.laptopModel}"
        if (myObj.reqCompleted!!) {
            holder.tvStatus.text = "Completed"
        }
        else {
            holder.tvStatus.text = "Pending"
        }
//        notifyDataSetChanged()
    }
}