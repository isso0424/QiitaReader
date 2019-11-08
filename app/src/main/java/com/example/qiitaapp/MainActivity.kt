package com.example.qiitaapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*

class MainActivity : AppCompatActivity(),Runnable{
    private lateinit var listView:ListView
    private lateinit var task:GetData<AppCompatActivity>
    private var handler = Handler()
    private var links:Array<String?> = arrayOfNulls(30)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        task = GetData(listView, this)
        task.execute(1)
        listView.setOnItemClickListener { _, _, i, _ ->
            onItemClick(i, links)
        }
        handler.post(this)
    }
    private fun onItemClick(
        i: Int,
        links: Array<String?>
    ){
        if (links[i].isNullOrBlank()) return
        val selectedLink = links[i]
        val uri = Uri.parse(selectedLink)
        val intent = Intent(applicationContext, Reader::class.java)
        println(uri.toString())
        intent.putExtra("URI", uri.toString())
        startActivity(intent)
    }
    override fun run(){
        links = task.linkArray.copyOf(30)
        listView = findViewById(R.id.listView)
        listView.setOnItemClickListener { _, _, i, _ ->
            onItemClick(i, links)
        }
        handler.postDelayed(this, 2000)
    }
}
