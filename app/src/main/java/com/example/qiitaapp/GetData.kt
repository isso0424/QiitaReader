package com.example.qiitaapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.jsoup.Jsoup
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

// selector body > div.allWrapper > div.p-home.px-2.px-1\@s.pt-4.pt-1\@s > div > div.p-home_main.mb-3.mr-0\@s > div

data class Root(val trend:Trend, val scope: String)
data class Trend(val edges:List<Detail>)
data class Detail(val followingLikers:List<String>, val isLikedByViewer:Boolean, val isNewArrival:Boolean, val hasCodeBlock:Boolean, val node: Node)
data class Node(val createdAt:String, val likesCount:Int, val title:String, val uuid:String, val author:Author)
data class Author(val profileImageUrl:String, val urlName: String)

class GetData<T> constructor(
    @field:SuppressLint("StaticFieldLeak") private val listView: ListView, activity: T,
    @field:SuppressLint("StaticFieldLeak") private val status: TextView
): AsyncTask<Int, Int, MutableList<Map<String, String>>>() where T : AppCompatActivity{
    companion object{
        var scope: String = "daily"
    }
    @SuppressLint("StaticFieldLeak")
    private val activity:Activity = activity
    private var detailLists = mutableListOf<Map<String, String>>()
    var linkArray: Array<String?> = arrayOfNulls(31)
    override fun doInBackground(vararg p0: Int?): MutableList<Map<String, String>> {
        detailLists.clear()
        detailLists.add(mapOf("title" to "タイトル", "author" to "筆者", "url" to ""))
            val baseUrl = "https://qiita.com"
            val document = Jsoup.connect("https://qiita.com/?scope").data("scope", scope).get()
            println(document)
        println("https://qiita.com/?scope=$scope")
            val elements = document.select("body > div.allWrapper")
            val element = elements[0].getElementsByTag("div")
            val e = element[37].attr("data-hyperapp-props")
            val mapper = jacksonObjectMapper()
            val root = mapper.readValue<Root>(e)
            val edge = root.trend.edges
            println(root.scope)
            for (ed in edge) {
                val url = baseUrl + "/" + ed.node.author.urlName + "/items/" + ed.node.uuid
                val map = mapOf(
                    "title" to ed.node.title,
                    "author" to ed.node.author.urlName,
                    "url" to url
                )
                println(ed.node.title)
                detailLists.add(map)
            }
        return detailLists
    }

    @SuppressLint("SetTextI18n")
    override fun onPostExecute(result: MutableList<Map<String, String>>) {
        super.onPostExecute(result)
        val titles = mutableListOf<String>()
        val author = mutableListOf<String>()
        for ((i, detail) in result.withIndex()){
            titles.add(detail["title"].toString())
            author.add(detail["author"].toString())
            linkArray[i] = detail["url"].toString()
        }
        val adapter = SimpleAdapter(this.activity, result, R.layout.listcontent,
            arrayOf("title", "author"), intArrayOf(R.id.titles, R.id.author))
        this.listView.adapter = adapter
        status.text = "scope:$scope"
    }
}