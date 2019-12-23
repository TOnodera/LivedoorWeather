package com.example.livedoorweather

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.SimpleAdapter
import android.widget.Spinner
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory



//表示可能なエリア情報を取得する為のクラス
class AreaInfoReceiver(applicationContext : Context, spinner: Spinner) : AsyncTask<Void, String, Document>(){

    val _applicationContext : Context
    val _areaSpinner : Spinner
    init{
        _applicationContext = applicationContext
        _areaSpinner = spinner
    }

    //エリア情報を取得するためのURL
    val areaXmlUrl : String = "http://weather.livedoor.com/forecast/rss/primary_area.xml"

    override fun doInBackground(vararg params: Void?): Document {

        val url = URL(areaXmlUrl)
        val con = url.openConnection() as HttpURLConnection

        con.requestMethod = "GET"
        con.connect()
        val stream = con.inputStream
        val document : Document = inputStreamToDocument(stream)
        con.disconnect()
        stream.close()

        return document
    }

    override fun onPostExecute(result: Document) {

        val elements : Element = getDocumentElement(result)
        val prefNodes : NodeList = elements.getElementsByTagName("pref")
        val areaInfoAll : MutableList<MutableMap<String,String>> = getAreaInfo(prefNodes)

        /**
         * 描画テスト　機能はテスト後に分ける
         */
        showAreaSpinner(areaInfoAll)
    }


    //文字列をDocumentオブジェクトに変換
    private fun inputStreamToDocument(xmlStream : InputStream) : Document {

        val factory : DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        val builder : DocumentBuilder = factory.newDocumentBuilder()
        val document : Document = builder.parse(xmlStream)

        return document

    }

    private fun getDocumentElement(document : Document) : Element {
        val element : Element = document.documentElement
        return element
    }

    private fun getAreaInfo(prefNodes : NodeList) : MutableList<MutableMap<String,String>> {

        //APIから取得したエリア情報をリストオブジェクトにして返すデータ構造としては
        //{"ID" : {name "エリア名",url: "URL"}}にしたいので
        //MutableList<MutableMap<MutableMap<String,Array>>にしてある。

        var result: MutableList<MutableMap<String,String>> = mutableListOf()

        for (i in 0..prefNodes.length) {
            if (prefNodes.item(i) != null) {

                val prefNode = prefNodes.item(i) as Element
                val areaName = prefNode.getAttribute("title")
                //警報URLを取得
                val warnNode = prefNode.getElementsByTagName("warn")
                var warnSource = ""
                if (warnNode.item(0) != null) {
                    val warnTag = warnNode.item(0) as Element
                    warnSource = warnTag.getAttribute("source")
                }

                //市町村名・ID・URLを取得
                val cityNode = prefNode.getElementsByTagName("city")
                for (j in 0..cityNode.length) {
                    if (cityNode.item(j) != null) {

                        val rowData : MutableMap<String, String> = mutableMapOf()
                        val cityNodeItem = cityNode.item(j) as Element
                        val cityId = cityNodeItem.getAttribute("id")
                        val cityName = cityNodeItem.getAttribute("title")
                        val cityUrl = cityNodeItem.getAttribute("source")

                        rowData.put("areaName",areaName)
                        rowData.put("areaWarnUrl",warnSource)
                        rowData.put("cityName",cityName)
                        rowData.put("cityUrl",cityUrl)
                        rowData.put("cityId",cityId)

                        result.add(rowData)
                    }
                }
            }
        }

        return result
    }

    private fun showAreaSpinner(areaInfoAll : MutableList<MutableMap<String,String>>){


        /**
         * 要検討：表示するデータなににする？とりあえず地域名と市町村名を連結して表示。キーはIDにする。
         */
        var listForSimpleAdapter : MutableList<MutableMap<String,String>> = mutableListOf()
        var from = arrayOf("cityId","areaInfoTxt")
        var to = intArrayOf(android.R.id.text1,android.R.id.text1)
        for(areaInfo in areaInfoAll){

            val areaInfoMap = mutableMapOf<String,String>()
            var cityId = "${areaInfo.get("cityId")}"
            var areaInfoTxt = "${areaInfo.get("cityName")} [${areaInfo.get("areaName")}]"

            areaInfoMap.put("cityId",cityId)
            areaInfoMap.put("areaInfoTxt",areaInfoTxt)

            listForSimpleAdapter.add(areaInfoMap)

        }

        val adapter = SimpleAdapter(_applicationContext,listForSimpleAdapter,android.R.layout.simple_spinner_item,from,to)
        _areaSpinner.adapter = adapter

    }

}