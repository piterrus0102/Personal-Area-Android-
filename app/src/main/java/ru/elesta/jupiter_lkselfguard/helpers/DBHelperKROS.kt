package ru.elesta.jupiter_lkselfguard.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class DBHelperKROS(context: Context) : SQLiteOpenHelper(context, "LP_STORAGE1", null, 1) {

    object Entry : BaseColumns {
        const val TABLE_NAME = "krosUsers"
        const val USER_LOGIN = "userLogin"
        const val USER_PASSWORD = "userPassword"
        const val SERVER_PIN = "serverPin"
        const val SERVICE_ID = "serviceID"
    }

    private val SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
            Entry.TABLE_NAME + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Entry.USER_LOGIN + " TEXT, " +
            Entry.USER_PASSWORD + " TEXT, " +
            Entry.SERVER_PIN + " TEXT, " +
            Entry.SERVICE_ID + " TEXT" + ")"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Entry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase?) {
        Log.v("SQL_CREATE", SQL_CREATE_ENTRIES)
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}