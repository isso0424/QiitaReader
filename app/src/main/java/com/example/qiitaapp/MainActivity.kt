package com.example.qiitaapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.os.Handler
import android.widget.*

class MainActivity : AppCompatActivity(),Runnable{
    lateinit var listView:ListView
    lateinit var status:TextView
    lateinit var task:GetData<AppCompatActivity>
    var handler = Handler()
    var links:Array<String?> = arrayOfNulls(30)
    lateinit var daily: Button
    lateinit var weekly:Button
    lateinit var monthly:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        status = findViewById(R.id.status)
        task = GetData(listView, this, "daily", status)
        status.setText("Loading")
        task.execute(1)
        daily = findViewById<Button>(R.id.daily)
        weekly = findViewById<Button>(R.id.weekly)
        monthly = findViewById<Button>(R.id.monthly)
        daily.setOnClickListener{
            task = GetData(listView, this, "daily", status)
            status.setText("Loading")
            task.execute(1)
        }
        weekly.setOnClickListener {
            task = GetData(listView, this, "weekly", status)
            status.setText("Loading")
            task.execute(1)
        }
        monthly.setOnClickListener {
            task = GetData(listView, this, "monthly", status)
            status.setText("Loading")
            task.execute(1)
        }
        listView.setOnItemClickListener { adapterView, view, i, l ->
            onItemClick(adapterView, view, i, l, links)
        }
        handler.post(this)
    }
    private fun onItemClick(adpterView: AdapterView<*>, view: View, i: Int, l: Long, links: Array<String?>){
        if (links[i].isNullOrBlank()) return
        var selectedLink = links[i]
        var uri = Uri.parse(selectedLink)
        var intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
    override fun run(){
        links = task.linkArray.copyOf(30)
        listView = findViewById<ListView>(R.id.listView)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            onItemClick(adapterView, view, i, l, links)
        }
        handler.postDelayed(this, 2000)
    }
}
