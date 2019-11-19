package com.example.qiitaapp

import android.annotation.SuppressLint
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
    private lateinit var searchBox: EditText
    private lateinit var searchButton: Button
    private lateinit var prevButton:Button
    private lateinit var nextButton:Button
    private lateinit var nowDetail:TextView
    private var handler = Handler()
    private var page:Int = 1
    private var links:Array<String?> = arrayOfNulls(30)
    private var searching:Boolean = false
    private var cache:String? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        searchBox = findViewById(R.id.search_box)
        searchButton = findViewById(R.id.search_button)
        prevButton = findViewById(R.id.prev)
        nextButton = findViewById(R.id.next)
        nowDetail = findViewById(R.id.now_detail)
        nowDetail.text = "now shown : daily popular article"
        task = GetData(listView, this, null ,null, null)
        task.execute(1)
        listView.setOnItemClickListener { _, _, i, _ ->
            onItemClick(i, links)
        }
        searchButton.setOnClickListener {
            val keyWord:String? = searchBox.text.toString()
            if (!keyWord.isNullOrBlank()){
                cache = keyWord
                page = 1
                task = GetData(listView, this, true, keyWord, page.toString())
                task.execute(1)
                searching = true
                nowDetail.text = "now shown : search$keyWord #$page"
            }
        }
        prevButton.setOnClickListener {
            page--
            if(searching && page > 0 && !cache.isNullOrBlank()) {
                task = GetData(listView, this, true, cache, page.toString())
                task.execute(1)
                searching = true
                nowDetail.text = "now shown : search$cache #$page"
            }else if(page < 1){
                page = 1
            }
        }
        nextButton.setOnClickListener {
            page++
            println(searching)
            println(page)
            if(searching && page > 0 && !cache.isNullOrBlank()) {
                println("fuck you")
                task = GetData(listView, this, true, cache, page.toString())
                task.execute(1)
                nowDetail.text = "now shown : search$cache #$page"
                searching = true

            }else if(page < 1){
                page = 1
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
            task = GetData(listView, this, null, null, null)
            task.execute(1)
            page = 1
            searching = false
            cache = null
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
