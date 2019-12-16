package com.example.livedoorweather

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.StringBuilder

class DataBaseHelper(context : Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "weatherapp.info.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {

        //DBがない場合に実行
        val sb = StringBuilder()
        sb.append(" CREATE TABLE weatherinfo( ")
        sb.append(" _id INTEGER PRIMARY KEY, ")
        sb.append(" cityId TEXT, ")
        sb.append(" areaInfoTxt TEXT, ")
        sb.append(" upd_dt DATETIME ")
        sb.append(" ); ")
        val defineSql = sb.toString()
        sb.clear()
        db?.execSQL(defineSql)

        //cityId登録用のDBを作成しておく
        sb.append(" INSERT INTO weatherinfo(_id,upd_dt) VALUES(1,datetime()); ")
        val insertSql = sb.toString()
        db?.execSQL(insertSql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}