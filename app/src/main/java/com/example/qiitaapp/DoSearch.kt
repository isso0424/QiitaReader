package com.example.qiitaapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import org.jsoup.Jsoup

// TODO: Test this class
class DoSearch<T> constructor(
    @field:SuppressLint("StaticFieldLeak") private val listView: ListView, activity: T, private val searchBox: String): AsyncTask<Int, Int, MutableList<Map<String, String>>>() where T : AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private val activity: Activity = activity
    private var detailLists = mutableListOf<Map<String, String>>()
    var linkArray: Array<String?> = arrayOfNulls(31)
    override fun doInBackground(vararg p0: Int?): MutableList<Map<String, String>> {
        detailLists.clear()
        val baseUrl = "https://qiita.com"
        val document = Jsoup.connect("https://qiita.com/search").data("q", searchBox).get()
        println(document)
        val error = document.select("#main > div > div > div.searchResultContainer_main > div.searchResultContainer_empty > div.searchResultContainer_emptyDescription")
        if (error.isNullOrEmpty()) {
            detailLists.add(mapOf("title" to "記事が見つかりませんでした", "author" to "", "url" to ""))
            return detailLists
        }
        detailLists.add(mapOf("title" to "タイトル", "author" to "筆者", "url" to "", "detail" to "詳細", "good" to "いいねの数"))
        val elements = document.select("#main > div > div > div.searchResultContainer_main > div")
        for (i in 1..2)elements.remove(elements[0])
        for (d in elements){
            val title = d.select("div > div.searchResult_main > h1").text()
            val detail = d.select("div > div.searchResult_snippet").text()
            val user = d.select("div > div.searchResult_main > div.searchResult_header > a").text()
            val good = d.select("div > div.searchResult_sub > ul > li")[0].text()
            val url = baseUrl + d.select("div > div.searchResult_main > h1 > a").attr("href")
            detailLists.add(mapOf("title" to title, "detail" to detail, "author" to user, "url" to url, "good" to good))
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