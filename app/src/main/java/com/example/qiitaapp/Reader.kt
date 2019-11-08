package com.example.qiitaapp

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class Reader : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = intent
        val uri = i.getStringExtra("URI")
        setContentView(R.layout.reader)
        val web = findViewById<WebView>(R.id.webView)
        web.loadUrl(uri)
        web.settings.javaScriptEnabled = true
    }
}