package com.example.qiitaapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.widget.*

class MainActivity : AppCompatActivity(),Runnable{
    private lateinit var listView:ListView
    private lateinit var task:GetData<AppCompatActivity>
    private var handler = Handler()
    private var links:Array<String?> = arrayOfNulls(30)
    private lateinit var searchBox: EditText
    private lateinit var searchButton: Button
    private var searching = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        searchBox = findViewById(R.id.search_box)
        searchButton = findViewById(R.id.search_button)
        task = GetData(listView, this, null ,null)
        task.execute(1)
        listView.setOnItemClickListener { _, _, i, _ ->
            onItemClick(i, links)
        }
        searchButton.setOnClickListener {
            //TODO : Test this function
            val keyWord:String? = searchBox.text.toString()
            if (!keyWord.isNullOrBlank()){
                task = GetData(listView, this, true, keyWord)
                task.execute(1)
            }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            task = GetData(listView, this, null, null)
            task.execute(1)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
