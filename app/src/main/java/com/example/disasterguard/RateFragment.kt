package com.example.disasterguard

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import com.google.android.material.snackbar.Snackbar


class RateFragment : Fragment() {
    lateinit var progressDialog: ProgressDialog
    lateinit var ratingBar: RatingBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rate, container, false)
        ratingBar = view.findViewById(R.id.ratingBar)
        val btnSendRating = view.findViewById<Button>(R.id.btnSendRating)


        btnSendRating.setOnClickListener {
            showProgressBar()
            Handler(Looper.getMainLooper()).postDelayed({
                hideProgressBar()
                val snackbar = Snackbar.make(it, "Rating sent successfully", Snackbar.LENGTH_LONG)
                snackbar.setAction("Ok") {

                }
                snackbar.setTextColor(resources.getColor(R.color.white))
                snackbar.setActionTextColor(resources.getColor(R.color.cyan))
                snackbar.setBackgroundTint(resources.getColor(R.color.black))
                snackbar.show()
            },2600)
        }

        return view
    }

    private fun showProgressBar() {
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Sending your rating ${ratingBar.rating}/${ratingBar.numStars}...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }
}