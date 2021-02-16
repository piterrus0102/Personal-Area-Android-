package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject

class JSONParserTrack {

    fun parseJsonLatAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonLat =
            json.getJSONObject("result")
                .getJSONArray("track")
                .getJSONObject(i)
                .get("lat").toString()
        return jsonLat
    }

    fun parseJsonLngAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonLng =
            json.getJSONObject("result")
                .getJSONArray("track")
                .getJSONObject(i)
                .get("lng").toString()
        return jsonLng
    }

    fun parseJsonTimeAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonTime =
            json.getJSONObject("result")
                .getJSONArray("track")
                .getJSONObject(i)
                .get("time").toString()
        return jsonTime
    }

}