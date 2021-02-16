package ru.elesta.jupiter_lkselfguard.helpers

import java.text.SimpleDateFormat


class TimeConverter {

    fun convertTime(time: String):String{

        val sdf = SimpleDateFormat("HH:mm:ss")
        val result = sdf.format(time.toLong())
        return result.toString()
    }

    fun convertDateForLongTime(time: String): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val result = sdf.format(time.toLong())
        return result
    }

    fun convertContractDate(time: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
        val date = df.parse(time)
        df.applyPattern("dd.MM.yyyy")
        val result = df.format(date)
        return result
    }

}