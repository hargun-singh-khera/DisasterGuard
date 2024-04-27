package com.example.disasterguard

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.Locale

class ProfileFragment : Fragment() {
    lateinit var tvName: TextView
    lateinit var tvEmail: TextView
    lateinit var tvMobile: TextView
    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var mobileEditText: EditText
    private lateinit var editProfileButton: Button
    lateinit var profileImage: CircleImageView
    lateinit var btnUpload: Button
    private lateinit var myLocation: TextView
    lateinit var progressBar: ProgressBar
    lateinit var progressBar3: ProgressBar
    lateinit var tvProgressBar: TextView
    lateinit var layout: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences

    private var inEditMode = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var userId: String
    lateinit var userEmail: String
    lateinit var userName: String
    lateinit var userMobileNumber: String
    lateinit var progressDialog: ProgressDialog
    lateinit var ref: StorageReference
    lateinit var bitmap: Bitmap

    var fileUri: Uri?= null
    lateinit var getImage: ActivityResultLauncher<String>

    val fileName = "userType"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvMobile = view.findViewById(R.id.tvMobile)
        emailEditText = view.findViewById(R.id.emailEditText)
        nameEditText = view.findViewById(R.id.nameEditText)
        mobileEditText = view.findViewById(R.id.mobileEditText)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        profileImage = view.findViewById(R.id.profileImage)
        btnUpload = view.findViewById(R.id.btnUpload)
        myLocation = view.findViewById(R.id.myLocation)

        progressBar = view.findViewById(R.id.progressBar)
        progressBar3 = view.findViewById(R.id.progressBar3)
        tvProgressBar = view.findViewById(R.id.tvProgressBar)
        layout = view.findViewById(R.id.layout)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!
//        Toast.makeText(requireContext(), "UserId: ${userId}", Toast.LENGTH_SHORT).show()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        sharedPreferences = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE)

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    fileUri = it
                }
                profileImage.setImageURI(fileUri)
            })

        getUserImage()
        getUserData()

        editProfileButton.setOnClickListener {
//            inEditMode = !inEditMode
            if (inEditMode) {
                inEditMode = false
//                Toast.makeText(requireContext(), "inEditMode1: ${inEditMode}", Toast.LENGTH_SHORT).show()
                editProfileButton.text = "Edit Profile"

                nameEditText.isEnabled = false
                emailEditText.isEnabled = false
                mobileEditText.isEnabled = false

                nameEditText.isFocusable = true

                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val mobile = mobileEditText.text.toString()

//                Toast.makeText(requireContext(), "Email: ${email}", Toast.LENGTH_SHORT).show()
//                Toast.makeText(requireContext(), "Mobile: ${mobile}", Toast.LENGTH_SHORT).show()

                btnUpload.visibility = View.GONE

//                Toast.makeText(requireContext(), "Uploading your image...", Toast.LENGTH_SHORT).show()
                updateUserDetails(name, email, mobile)
            } else {
                inEditMode = true
//                Toast.makeText(requireContext(), "inEditMode2: ${inEditMode}", Toast.LENGTH_SHORT).show()
                editProfileButton.text = "Save Profile"

                nameEditText.isEnabled = true
                emailEditText.isEnabled = false
                mobileEditText.isEnabled = false

                nameEditText.isFocusable = true
                nameEditText.isFocusableInTouchMode = true

                btnUpload.visibility = View.VISIBLE
                btnUpload.setOnClickListener {
                    getImage.launch("image/*")

                }
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getLocation() // Call this method to get the location

        return view
    }

    private fun getUserImage() {
//        Toast.makeText(requireContext(), "Inside of get User Image", Toast.LENGTH_SHORT).show()
//        val currentUserId = auth.currentUser?.uid
//        Toast.makeText(requireContext(), "Id: ${currentUserId}", Toast.LENGTH_SHORT).show()
        showProgressBar()
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading your profile image...")
        ref = FirebaseStorage.getInstance().getReference("Users/Image").child(userId)
        val localFile = File.createTempFile("tempImage", "jpg")
        ref.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            Log.d("Bitmap2", bitmap.toString())

            val exif = ExifInterface(localFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            // Rotate the bitmap if needed
            bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }

            profileImage.setImageBitmap(bitmap)
            hideProgressBar()
        } .addOnFailureListener {
            hideProgressBar()
//            Toast.makeText(requireContext(), "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to rotate the bitmap
    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Uploading")
        progressDialog.setMessage("Uploading your image...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    private fun uploadImage() {
//        Toast.makeText(requireContext(), "Inside of Upload Image", Toast.LENGTH_SHORT).show()
        if (fileUri != null) {
//            Toast.makeText(requireContext(), "FileUri not null", Toast.LENGTH_SHORT).show()
            showProgressBar()
            if (userId != null) {
                ref = FirebaseStorage.getInstance().getReference("Users/Image").child(userId)
                ref.putFile(fileUri!!).addOnSuccessListener {
                    hideProgressBar()
                    Toast.makeText(requireContext(), "Image Uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    hideProgressBar()
                    Toast.makeText(requireContext(), " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            hideProgressBar()
//            Toast.makeText(requireContext(), "File uri null found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserDetails(name: String, email: String, mobile: String) {
        val edit = sharedPreferences.edit()
        edit.putString("userName", name)
        edit.apply()

//        Toast.makeText(requireContext(), "Name: ${name}", Toast.LENGTH_SHORT).show()

        val user = UserModel(userId, name, email, mobile, false)
        dbRef.child(userId).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                uploadImage()
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update your profile at the moment.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideView() {
        tvName.visibility = View.GONE
        tvEmail.visibility = View.GONE
        tvMobile.visibility = View.GONE
        nameEditText.visibility = View.GONE
        emailEditText.visibility = View.GONE
        mobileEditText.visibility = View.GONE
        editProfileButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        tvProgressBar.visibility = View.VISIBLE
    }

    private fun showView() {
        progressBar.visibility = View.GONE
        tvProgressBar.visibility = View.GONE
        tvName.visibility = View.VISIBLE
        tvEmail.visibility = View.VISIBLE
        tvMobile.visibility = View.VISIBLE
        nameEditText.visibility = View.VISIBLE
        emailEditText.visibility = View.VISIBLE
        mobileEditText.visibility = View.VISIBLE
        editProfileButton.visibility = View.VISIBLE
    }

    fun getUserData() {
        hideView()
//        Toast.makeText(requireContext(), "Acquiring User Info", Toast.LENGTH_SHORT).show()
//        Toast.makeText(requireContext(), "User Id: ${userId}", Toast.LENGTH_SHORT).show()
        dbRef.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
//                    Toast.makeText(requireContext(), "Snapshot exists", Toast.LENGTH_SHORT).show()
                    for (userSnap in snapshot.children) {
                        val user = snapshot.getValue(UserModel::class.java)
                        userName = user?.userName!!
                        userEmail = user?.userEmail!!
                        userMobileNumber = user?.userMobileNumber!!

                        nameEditText.setText(userName)
                        emailEditText.setText(userEmail)
                        mobileEditText.setText(userMobileNumber)

//                        Toast.makeText(requireContext(), "Values set", Toast.LENGTH_SHORT).show()
                        showView()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "An error occurred " + error.message , Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getLocation() {
        myLocation.visibility = View.GONE
        progressBar3.visibility = View.VISIBLE
        if (checkPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address>?
                        list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (list != null) {
                            if (list.isNotEmpty()) {
                                progressBar3.visibility = View.GONE
                                myLocation.visibility = View.VISIBLE
                                myLocation.text = "${list[0].getAddressLine(0)}"
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionId)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }


}