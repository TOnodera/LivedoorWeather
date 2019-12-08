package com.example.livedoorweather

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
import kotlin.coroutines.coroutineContext
import com.example.livedoorweather.MainActivity as ComExampleLivedoorweatherMainActivity


//表示可能なエリア情報を取得する為のクラス
class AreaInfoReceiver(spinner : Spinner) : AsyncTask<Void, String, Document>(){

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
        val areaInfoAll : MutableList<MutableMap<String,MutableList<MutableMap<String,String>>>> = getAreaInfo(prefNodes)

        /**
         * 描画テスト　機能はテスト後に分ける
         */
        showAreaSpinner(areaInfoAll)
    }

    private fun inputStreamToString(stream : InputStream?) : String{

        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream,"UTF-8"))
        var line : String?

        do{
            line = reader.readLine()
            if(line!=null){
                sb.append(line)
            }
        }while(line!=null)

        reader.close()
        return sb.toString()

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

    private fun getAreaInfo(prefNodes : NodeList) : MutableList<MutableMap<String,MutableList<MutableMap<String,String>>>> {

        //APIから取得したエリア情報をリストオブジェクトにして返すデータ構造としては
        //{"ID" : {name "エリア名",url: "URL"}}にしたいので
        //MutableList<MutableMap<MutableMap<String,Array>>にしてある。

        val result: MutableList<MutableMap<String, MutableList<MutableMap<String, String>>>> = mutableListOf()
        var areaInfo: MutableMap<String, MutableList<MutableMap<String, String>>>
        var areaName: MutableMap<String, String>
        var areaWarnUrl: MutableMap<String, String> = mutableMapOf()
        var resultList: MutableList<MutableMap<String, String>> = mutableListOf()

        for (i in 0..prefNodes.length) {



            if (prefNodes.item(i) != null) {

                val prefNode = prefNodes.item(i) as Element

                //地域名を取得
                areaName = mutableMapOf("areaName" to prefNode.getAttribute("title"))

                //警報URLを取得
                val warnNode = prefNode.getElementsByTagName("warn")
                if (warnNode.item(0) != null) {
                    val warnTag = warnNode.item(0) as Element
                    areaWarnUrl = mutableMapOf("areaWarnUrl" to warnTag.getAttribute("source"))
                }

                //市町村名・ID・URLを取得
                val cityNode = prefNode.getElementsByTagName("city")
                for (j in 0..cityNode.length) {
                    if (cityNode.item(j) != null) {

                        val cityNodeItem = cityNode.item(j) as Element
                        val cityId = cityNodeItem.getAttribute("id")
                        val cityName =
                            mutableMapOf("cityName" to cityNodeItem.getAttribute("title"))
                        val cityUrl = mutableMapOf("cityUrl" to cityNodeItem.getAttribute("source"))

                        resultList.add(areaName)
                        resultList.add(cityName)
                        resultList.add(areaWarnUrl)
                        resultList.add(cityUrl)

                        areaInfo = mutableMapOf(cityId to resultList)
                        result.add(areaInfo)
                    }
                }
            }
        }
        return result
    }

    private fun showAreaSpinner(areaInfoAll : MutableList<MutableMap<String,MutableList<MutableMap<String,String>>>>){

        /**
         * 要検討：表示するデータなににする？とりあえず地域名と市町村名を連結して表示。キーはIDにする。
         */
        var listForSimpleAdapter : MutableList<Map<String,String>> = mutableListOf()
        var cityMap : MutableMap<String,String> = mutableMapOf()
        for(areaInfo in areaInfoAll){
            for(detail in areaInfo){
                val cityId = detail.key
                val areaName = detail.value[0]["areaName"]
                val cityName = detail.value[1]["cityName"]
                //val areaWarnUrl = detail.value[2]["areaWarnUrl"]
                //val cityUrl = detail.value[3]["cityUrl"] //（id）.xmlの形でデータが提供されるのであればこの情報もいらない？
                val areainfoTxt = "${areaName}:${cityName}"
                val areaInfoMap = mutableMapOf(cityId to areainfoTxt)
                listForSimpleAdapter.add(areaInfoMap)
            }
        }


    }

}