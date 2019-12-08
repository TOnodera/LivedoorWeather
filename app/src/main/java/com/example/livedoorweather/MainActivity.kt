package com.example.livedoorweather


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //表示用のスピナーを取得
        val spinner : Spinner = findViewById(R.id.areaSpinner)
        //エリア情報を取得
        val receiver = AreaInfoReceiver(applicationContext,spinner)
        receiver.execute()

    }

}