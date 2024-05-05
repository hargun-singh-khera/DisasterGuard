package com.example.disasterguard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class AdminNotificationAdapter(val context: Context, val resource: Int, val objects: ArrayList<AdminNotificationModel>) : RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder>() {
    lateinit var bitmap: Bitmap
    lateinit var progressDialog: ProgressDialog
    lateinit var dbRef: DatabaseReference
    lateinit var ref: StorageReference

    lateinit var incidentType: String
    lateinit var incidentDesc: String
    lateinit var incidentLocation: String
    lateinit var incidentDate: String
    lateinit var incidentTime: String
    lateinit var emergencyLevel: String
    lateinit var submissionDate: String

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName = itemView.findViewById<TextView>(R.id.tvUserName)
        val btnView = itemView.findViewById<Button>(R.id.btnView)
        val profileImage = itemView.findViewById<ImageView>(R.id.profileImage)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
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
        hideView(holder)
        val myObj = objects[position]
        val userId = myObj.userId
        val ticketId = myObj.ticketId

        holder.tvUserName.text = "${myObj.userName}"

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests").child(ticketId!!)
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val ticket = snapshot.getValue(RequestModel::class.java)
                    incidentType = ticket?.incidentType!!
                    incidentDesc = ticket.incidentDesc!!
                    incidentLocation = ticket.incidentLocation!!
                    incidentDate = ticket.incidentDate!!
                    incidentTime = ticket.incidentTime!!
                    emergencyLevel = ticket.emergencyLevel!!
                    submissionDate = ticket.submissionDate!!
                    val reqCompleted = ticket.reqCompleted!!
                    if (reqCompleted) {
                        holder.btnView.setOnClickListener {
                            Toast.makeText(context, "This request has been closed now.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        holder.btnView.setOnClickListener {
            val intent = Intent(context, AdminTicketResponse::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("ticketId", ticketId)
            intent.putExtra("incidentType", incidentType)
            intent.putExtra("incidentDesc", incidentDesc)
            intent.putExtra("incidentLocation", incidentLocation)
            intent.putExtra("incidentDate", incidentDate)
            intent.putExtra("incidentTime", incidentTime)
            intent.putExtra("emergencyLevel", emergencyLevel)
            intent.putExtra("submissionDate", submissionDate)
            context.startActivity(intent)
        }

        showView(holder)

//        getUserImage(holder, userId)
    }

//    private fun getUserImage(holder: ViewHolder, userId: String) {
//        showProgressBar()
//
//        ref = FirebaseStorage.getInstance().getReference("Users/Image").child(userId)
//        val localFile = File.createTempFile("tempImage", "jpg")
//        ref.getFile(localFile).addOnSuccessListener {
//            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//            Log.d("Bitmap2", bitmap.toString())
//
//            val exif = ExifInterface(localFile.absolutePath)
//            val orientation = exif.getAttributeInt(
//                ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED
//            )
//
//            // Rotate the bitmap if needed
//            bitmap = when (orientation) {
//                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
//                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
//                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
//                else -> bitmap
//            }
//
//            holder.profileImage.setImageBitmap(bitmap)
//            hideProgressBar()
//        } .addOnFailureListener {
//            hideProgressBar()
//        }
//    }

    // Function to rotate the bitmap
    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    private fun hideView(holder: ViewHolder) {
        showProgressBar()
        holder.cardView.visibility = View.GONE
    }

    private fun showView(holder: ViewHolder) {
        hideProgressBar()
        holder.cardView.visibility = View.VISIBLE
    }
}