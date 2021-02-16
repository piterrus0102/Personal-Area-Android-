package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject

class JSONParserSections {

    fun parseJsonDeviceIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("deviceID").toString()
        return jsonDeviceID
    }

    fun parseJsonAlarmStatusAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("alarmStatus").toString()
        return jsonAlarmStatus
    }

    fun parseJsonGuardStatusAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonGuardStatus =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("guardStatus").toString()
        return jsonGuardStatus
    }

    fun parseJsonFaultStatusAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonFaultStatus =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("faultStatus").toString()
        return jsonFaultStatus
    }

    fun parseJsonIndexOfSectionAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonIndexOfSection =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("index").toString()
        return jsonIndexOfSection
    }

    fun parseJsonObjectIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("objectID").toString()
        return jsonObjectID
    }

    fun parseJsonSectionIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("sectionID").toString()
        return jsonObjectID
    }

    fun parseJsonCustomNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONArray("sections")
                .getJSONObject(i)
                .get("customName").toString()
        return jsonObjectID
    }









    fun parseJsonDeviceIDUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("deviceID").toString()
        return jsonDeviceID
    }

    fun parseJsonAlarmStatusUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("alarmStatus").toString()
        return jsonAlarmStatus
    }

    fun parseJsonGuardStatusUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonGuardStatus =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("guardStatus").toString()
        return jsonGuardStatus
    }

    fun parseJsonFaultStatusUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonFaultStatus =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("faultStatus").toString()
        return jsonFaultStatus
    }

    fun parseJsonIndexOfSectionUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonIndexOfSection =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("index").toString()
        return jsonIndexOfSection
    }

    fun parseJsonObjectIDUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("objectID").toString()
        return jsonObjectID
    }

    fun parseJsonSectionIDUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("sectionID").toString()
        return jsonObjectID
    }

    fun parseJsonCustomNameUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONObject("section")
                .get("customName").toString()
        return jsonObjectID
    }
}