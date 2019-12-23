package com.example.livedoorweather

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    var _cityId : String = ""
    var _areaInfoTxt : String = ""
    private val _dbHelper = DataBaseHelper(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //表示用のスピナーを取得
        val spinner : Spinner = findViewById(R.id.areaSpinner)
        //エリア情報を取得
        val receiver = AreaInfoReceiver(applicationContext,spinner)
        receiver.execute()
        spinner.setFocusable(false)
        //スピナーに設定
        spinner.onItemSelectedListener = onAreaSpinnerItemSelectedListener(spinner)

        //2日後のお天気の初期状態としてローディングgif動画を設定
        val imgDay2 : ImageView = findViewById(R.id.imgDay2)
        val imgDay3 : ImageView = findViewById(R.id.imgDay3)

        imgDay2.setImageResource(R.drawable.loading)
        imgDay3.setImageResource(R.drawable.loading)

        //登録ボタンを取得
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener(onRegisterButtonClickListener())

        //前回登録している市町村情報があれば設定
        val registeredInfo = selectCityInfo()
        if(registeredInfo!=null){
            _cityId = registeredInfo[0]
            _areaInfoTxt = registeredInfo[1]
            val btnRegister : Button = findViewById(R.id.btnRegister)
            val tvWeatherTelop: TextView = findViewById(R.id.tvWeatherTelop)
            tvWeatherTelop.setText(_areaInfoTxt)
            btnRegister.performClick()
        }

    }

    private inner class onAreaSpinnerItemSelectedListener(spinner : Spinner) : AdapterView.OnItemSelectedListener{
        val _spinner : Spinner = spinner
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if(_spinner.isFocusable === false){
                _spinner.setFocusable(true)
                return
            }
            val item = parent?.getItemAtPosition(position) as? Map<String,String>
            _cityId = item?.get("cityId") ?: ""
            _areaInfoTxt = item?.get("areaInfoTxt") ?: ""
            //cityIdをDBに設定
            insertCityId(_cityId,_areaInfoTxt)
        }
    }

    private inner class onRegisterButtonClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            val regex = Regex("""^\d{6}$""")
            if(regex.matches(_cityId)){

                //cityIdからデータ取得用URLを生成
                val url = "http://weather.livedoor.com/forecast/webservice/json/v1?city=${_cityId}"

                val tvCityName = findViewById<TextView>(R.id.tvCityName)
                tvCityName.setText(_areaInfoTxt)

                //データ取得後の表示用画面部品を渡す
                val mainViewMap = mutableMapOf<String,Any>()

                val tvWeatherTelop = findViewById<TextView>(R.id.tvWeatherTelop)
                val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
                val imgMain = findViewById<ImageView>(R.id.imgMain)

                val imgDay2 = findViewById<ImageView>(R.id.imgDay2)
                val tvDay2 = findViewById<TextView>(R.id.tvDay2)
                val imgDay3 = findViewById<ImageView>(R.id.imgDay3)
                val tvDay3 = findViewById<TextView>(R.id.tvDay3)

                mainViewMap.put("telop",tvWeatherTelop)
                mainViewMap.put("desc",tvWeatherDesc)
                mainViewMap.put("imgMain",imgMain)
                mainViewMap.put("imgDay2",imgDay2)
                mainViewMap.put("tvDay2",tvDay2)
                mainViewMap.put("imgDay3",imgDay3)
                mainViewMap.put("tvDay3",tvDay3)

                //APIからデータを取得
                val receiver = WeatherInfoReceiver(MainViewInfo(mainViewMap))
                receiver.execute(url)

            }else{
                Toast.makeText(applicationContext,"エリアを選択してください。",Toast.LENGTH_LONG).show()
            }
        }
    }

    //データベースを更新cityIdを設定
    private fun  insertCityId(cityId : String,areaInfoTxt : String){
        val db = _dbHelper.writableDatabase
        val sql = "UPDATE weatherinfo SET cityId = ?,areaInfoTxt = ? WHERE _id = ? "
        val stmt = db.compileStatement(sql)
        stmt.bindString(1,cityId)
        stmt.bindString(2,areaInfoTxt)
        stmt.bindLong(3,1)
        stmt.executeUpdateDelete()
    }

    //データベースから登録されているcityIdを取得
    private fun selectCityInfo() : Array<String>{

        val db = _dbHelper.writableDatabase
        val sql = " SELECT * FROM weatherinfo WHERE _id = 1"
        val cursor = db.rawQuery(sql,null)

        cursor.moveToFirst()
        var index = cursor.getColumnIndex("cityId")
        val cityId = cursor.getString(index) ?: "270000"
        cursor.moveToFirst()
        index = cursor.getColumnIndex("areaInfoTxt")
        val areaInfoTxt = cursor.getString(index) ?: "大阪府"

        return arrayOf(cityId,areaInfoTxt)
    }

    //gif動画を再生
    private fun animateGifLoadingImg(img : ImageView){
        val objectAnimator = ObjectAnimator.ofFloat(img,"rotation",0f,360f)
        objectAnimator.duration = 3000
        objectAnimator.repeatCount = -1
        objectAnimator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _dbHelper.close()
        applicationContext.deleteDatabase(_dbHelper.databaseName)
    }

}