package com.example.livedoorweather

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class WeatherInfoReceiver(mainViewInfo : MainViewInfo) : AsyncTask<String, String, String>() {
    val _mainViewInfo : MainViewInfo = mainViewInfo
    override fun doInBackground(vararg params: String?) : String {

        val urlStr = params[0]

        //接続
        val url = URL(urlStr)
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.connect()
        val stream = con.inputStream
        val result = inputStreamToString(stream)
        con.disconnect()
        stream.close()

        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        //取得データを分解
        val rootJSON = JSONObject(result)
        val descriptionJSON = rootJSON.getJSONObject("description")
        val desc = descriptionJSON.getString("text")
        val forecasts = rootJSON.getJSONArray("forecasts")
        val forecastNow = forecasts.getJSONObject(0)
        val telop = forecastNow.getString("telop")

        //今日の画像
        var forecast = forecasts.getJSONObject(0)
        var imageInfo = forecast.getJSONObject("image")
        var imageGetTask = ImageGetTask(_mainViewInfo.imgMain)
        imageGetTask.execute(imageInfo.getString("url"))
        //明日の画像
        forecast = forecasts.getJSONObject(1)
        imageInfo = forecast.getJSONObject("image")
        imageGetTask = ImageGetTask(_mainViewInfo.imgDay2)
        imageGetTask.execute(imageInfo.getString("url"))
        val tvDay2Telop = forecast.getString("telop")
        _mainViewInfo.tvDay2.setText(tvDay2Telop)
        //明後日の画像
        forecast = forecasts.getJSONObject(2)
        imageInfo = forecast.getJSONObject("image")
        imageGetTask = ImageGetTask(_mainViewInfo.imgDay3)
        imageGetTask.execute(imageInfo.getString("url"))
        val tvDay3Telop = forecast.getString("telop")
        _mainViewInfo.tvDay3.setText(tvDay3Telop)
        //分解したデータを画面部品にはめる
        _mainViewInfo.telop.setText(telop)
        _mainViewInfo.desc.setText(desc)



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
}