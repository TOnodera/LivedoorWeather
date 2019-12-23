package com.example.livedoorweather
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text


class MainViewInfo(viewMap : MutableMap<String,Any>){

    val telop : TextView = viewMap["telop"] as TextView
    val desc : TextView = viewMap["desc"] as TextView
    val imgMain : ImageView = viewMap["imgMain"] as ImageView
    val imgDay2 : ImageView = viewMap["imgDay2"] as ImageView
    val tvDay2 : TextView = viewMap["tvDay2"] as TextView
    val imgDay3 : ImageView = viewMap["imgDay3"] as ImageView
    val tvDay3 : TextView = viewMap["tvDay3"] as TextView

}