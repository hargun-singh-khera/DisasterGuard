package com.example.disasterguard

import android.app.ProgressDialog
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserAdapter(val context: Context, val resource: Int, val objects: ArrayList<UserModel>, var dbRef: DatabaseReference) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    lateinit var auth: FirebaseAuth
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)
        val tvContact = itemView.findViewById<TextView>(R.id.tvContact)
        val tvUserType = itemView.findViewById<TextView>(R.id.tvUserType)
        val btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)
        val btnMakeAdmin = itemView.findViewById<Button>(R.id.btnMakeAdmin)
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
        val userAdmin = myObj.userAdmin

        holder.tvName.text = "Name: ${myObj.userName}"
        holder.tvEmail.text = "Email: ${myObj.userEmail}"
        holder.tvContact.text = "Contact No: ${myObj.userMobileNumber}"


        val userId = auth.currentUser?.uid!!
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserModel::class.java)
                    val userAdmin = userData?.userAdmin
                    val superUser = userData?.superUser
                    if (!superUser!! && userAdmin!!) {
                        holder.btnMakeAdmin.visibility = View.GONE
                        holder.tvUserType.visibility = View.GONE
                    } else {
                        holder.btnMakeAdmin.visibility = View.VISIBLE
                        holder.tvUserType.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        if (userAdmin == true) {
            holder.tvUserType.text = "User Type: Admin User"
            holder.btnMakeAdmin.text = "Make Normal User"
        } else {
            holder.tvUserType.text = "User Type: Normal User"
            holder.btnMakeAdmin.text = "Make Admin User"
        }

        holder.btnDelete.setOnClickListener {
            confirmDeleteUserAccount(position)
        }
        holder.btnMakeAdmin.setOnClickListener {
            confirmMakeUserAdmin(position, userAdmin!!)
            notifyDataSetChanged()
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

    private fun changeUserType(position: Int, userAdmin: Boolean) {
        val userAdmin = !userAdmin
        val userId = objects[position].userId
        val userName = objects[position].userName
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        val updates = HashMap<String, Any>()
        updates["userAdmin"] = userAdmin
        dbRef.updateChildren(updates).addOnSuccessListener {
            if (userAdmin) {
                Toast.makeText(context, "${userName} is now an admin user.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${userName} is now a normal user.", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun confirmMakeUserAdmin(position: Int, userAdmin: Boolean) {
        val builder = AlertDialog.Builder(context)
        if (!userAdmin) {
            builder.setMessage("Are you sure you want to make this user account as admin?")
        } else {
            builder.setMessage("Are you sure you want to make this user account as normal user?")
        }

        builder.setTitle("Alert!")
        builder.setCancelable(false)

        builder.setPositiveButton("Yes") { dialog, which ->
            changeUserType(position, userAdmin)
        }

        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}