package com.example.qiitaapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*

class MainActivity : AppCompatActivity(),Runnable{
    private lateinit var listView:ListView
    private lateinit var status:TextView
    private lateinit var task:GetData<AppCompatActivity>
    private var handler = Handler()
    private var links:Array<String?> = arrayOfNulls(30)
    private lateinit var daily: Button
    private lateinit var weekly:Button
    private lateinit var monthly:Button
    private val res = resources
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        status = findViewById(R.id.status)
        task = GetData(listView, this, status)
        GetData.scope = "daily"
        status.text = res.getString(R.string.loading)
        task.execute(1)
        daily = findViewById(R.id.daily)
        weekly = findViewById(R.id.weekly)
        monthly = findViewById(R.id.monthly)
        daily.setOnClickListener{
            GetData.scope = "daily"
            task = GetData(listView, this, status)
            status.text = res.getString(R.string.loading)
            task.execute(1)
        }
        weekly.setOnClickListener {
            GetData.scope = "weekly"
            task = GetData(listView, this, status)
            status.text = res.getString(R.string.loading)
            task.execute(1)
        }
        monthly.setOnClickListener {
            GetData.scope = "monthly"
            task = GetData(listView, this, status)
            status.text = res.getString(R.string.loading)
            task.execute(1)
        }
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
        val intent = Intent(Intent.ACTION_VIEW, uri)
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
