package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject

class JSONParserContractKROS {

    fun parseJsonNumberAdd(text: String): String{
        val json = JSONObject(text)
        val jsonNumber =
            json.getJSONObject("result")
                .getJSONObject("contract")
                .get("number").toString()
        return jsonNumber
    }

    fun parseJsonStatusAdd(text: String): String{
        val json = JSONObject(text)
        val jsonStatus =
            json.getJSONObject("result")
                .getJSONObject("contract")
                .get("status").toString()
        return jsonStatus
    }

    fun parseJsonDateStartAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDateStart =
            json.getJSONObject("result")
                .getJSONObject("contract")
                .get("dateStart").toString()
        return jsonDateStart
    }

    fun parseJsonDateEndAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDateEnd =
            json.getJSONObject("result")
                .getJSONObject("contract")
                .get("dateEnd").toString()
        return jsonDateEnd
    }
}