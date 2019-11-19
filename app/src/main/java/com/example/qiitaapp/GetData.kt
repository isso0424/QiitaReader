package com.example.qiitaapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.widget.ListView
import android.widget.SimpleAdapter
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
    private val search: Boolean?, private val searchBox:String?, private val pageNum:String?): AsyncTask<Int, Int, MutableList<Map<String, String>>>() where T : AppCompatActivity{
    @SuppressLint("StaticFieldLeak")
    private val activity:Activity = activity
    private var detailLists = mutableListOf<Map<String, String>>()
    var linkArray: Array<String?> = arrayOfNulls(31)
    override fun doInBackground(vararg p0: Int?): MutableList<Map<String, String>> {
        detailLists.clear()
            val baseUrl = "https://qiita.com"
        if (search == true){
            val document = Jsoup.connect("https://qiita.com/search").data("q", searchBox, "page", pageNum).get()
            val error = document.select("#main > div > div > div.searchResultContainer_main > div.searchResultContainer_empty > div.searchResultContainer_emptyDescription")
            if (!error.isNullOrEmpty()) {
                detailLists.add(mapOf("title" to "記事が見つかりませんでした", "author" to "", "url" to ""))
                return detailLists
            }
            detailLists.add(mapOf("title" to "タイトル", "author" to "筆者", "url" to "", "detail" to "詳細", "good" to "いいねの数"))
            val elements = document.select("#main > div > div > div.searchResultContainer_main > div")
            for (i in 1..2)elements.remove(elements[0])
            for (d in elements){
                val title = d.select("div > div.searchResult_main > h1").text()
                if (title.isNullOrBlank()) break
                val detail = d.select("div > div.searchResult_snippet").text()
                val user = d.select("div > div.searchResult_main > div.searchResult_header > a").text()
                val good = d.select("div > div.searchResult_sub > ul > li")[0].text()
                val url = baseUrl + d.select("div > div.searchResult_main > h1 > a").attr("href")
                detailLists.add(mapOf("title" to title, "detail" to detail, "author" to user, "url" to url, "good" to good))
            }
        }
            else{
            detailLists.add(mapOf("title" to "タイトル", "author" to "筆者", "url" to ""))
            val document = Jsoup.connect("https://qiita.com/?scope=weekly").get()
            println(document)
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
    }
}