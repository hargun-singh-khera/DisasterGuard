package com.example.disasterguard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ShowGuides : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_guides)

        webView = findViewById(R.id.webview)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        progressBar = findViewById(R.id.progressBar)

        val url = intent.getStringExtra("url").toString()

        // used for delay of screen activity
        Handler(Looper.getMainLooper()).postDelayed({
            webView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        },1800)

        webView.loadUrl(url)

    }
}