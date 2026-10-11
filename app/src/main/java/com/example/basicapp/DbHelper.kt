package com.example.basicapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "users.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, birthday TEXT, email TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun insertUser(name: String, birthdayIso: String, email: String) {
        val values = ContentValues().apply {
            put("name", name)
            put("birthday", birthdayIso)
            put("email", email)
        }
        writableDatabase.insert("users", null, values)
    }

    // Returns list of (name, birthday ISO, email)
    fun getAllUsers(): List<Triple<String, String, String>> {
        val list = mutableListOf<Triple<String, String, String>>()
        readableDatabase.rawQuery("SELECT name, birthday, email FROM users ORDER BY id", null).use {
            while (it.moveToNext()) list.add(Triple(it.getString(0), it.getString(1), it.getString(2)))
        }
        return list
    }

    companion object {
        val MONTHS = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        // "1998-05-26" -> "May 26th 1998"
        fun pretty(iso: String): String {
            val (y, m, d) = iso.split("-").map { it.toInt() }
            val suffix = if (d in 11..13) "th" else when (d % 10) {
                1 -> "st"; 2 -> "nd"; 3 -> "rd"; else -> "th"
            }
            return "${MONTHS[m - 1]} $d$suffix $y"
        }
    }
}
