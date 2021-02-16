package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject

class JSONParserCommonServiceClass {


    fun parseJsonServiceIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonServiceID =
            json.getJSONObject("result")
                .getJSONArray("services")
                .getJSONObject(i)
                .getString("serviceID")
        return jsonServiceID
    }

    fun parseJsonNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonName =
            json.getJSONObject("result")
                .getJSONArray("services")
                .getJSONObject(i)
                .getString("name")
        return jsonName
    }

    fun parseJsonDescriptionAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonDescription =
            json.getJSONObject("result")
                .getJSONArray("services")
                .getJSONObject(i)
                .getString("description")
        return jsonDescription
    }
}