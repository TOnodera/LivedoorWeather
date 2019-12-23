package com.example.livedoorweather

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

class ImageGetTask(private var _image: ImageView) : AsyncTask<String,Void,Bitmap?>(){

    override fun doInBackground(vararg params: String?): Bitmap? {
        var image : Bitmap?
        try{

            val url = URL(params[0])
            val istream : InputStream = url.openStream()
            image = BitmapFactory.decodeStream(istream)
            return image

        }catch (e : MalformedURLException){

        }catch (e : IOException){

        }
        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        _image.setImageBitmap(result)
    }

}