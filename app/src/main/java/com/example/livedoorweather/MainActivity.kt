package com.example.livedoorweather


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    var _cityId : String = ""
    var _cityName : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //表示用のスピナーを取得
        val spinner : Spinner = findViewById(R.id.areaSpinner)
        //エリア情報を取得
        val receiver = AreaInfoReceiver(applicationContext,spinner)
        receiver.execute()
        //スピナーに設定
        spinner.onItemSelectedListener = onAreaSpinnerItemSelectedListener()

        //登録ボタンを取得
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener(onRegisterButtonClickListener())

    }

    private inner class onAreaSpinnerItemSelectedListener() : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val item = parent?.getItemAtPosition(position) as? Map<String,String>
            _cityId = item?.get("cityId") ?: ""

        }
    }

    private inner class onRegisterButtonClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            val regex = Regex("""^\d{6}$""")
            if(regex.matches(_cityId)){

            }else{
                Toast.makeText(applicationContext,"エリアを選択してください。",Toast.LENGTH_LONG).show()
            }
        }
    }

}