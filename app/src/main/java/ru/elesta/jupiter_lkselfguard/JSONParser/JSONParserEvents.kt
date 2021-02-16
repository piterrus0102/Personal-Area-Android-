package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject

class JSONParserEvents {

    fun parseJsonEnvelopeIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("envelopeID").toString()
        return jsonEnvelopeID
    }

    fun parseJsonBGColorAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("bgColor").toString()
        return jsonAlarmStatus
    }

    fun parseJsonMessageAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonMessage =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("message").toString()
        return jsonMessage
    }

    fun parseJsonReceiveTimeAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonReceiveTime =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("receiveTime").toString()
        return jsonReceiveTime
    }

    fun parseJsonObjectIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("objectID").toString()
        return jsonObjectID
    }

    fun parseJsonCustomNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonCustomName =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("customName").toString()
        return jsonCustomName
    }

    fun parseJsonObjectNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonObjectName =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("objectName").toString()
        return jsonObjectName
    }

    fun parseJsonDeviceID(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("deviceID").toString()
        return jsonDeviceID
    }

    fun parseSectionAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonSection =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("section").toString()
        return jsonSection
    }

    fun parseSensorAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonSensor =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("sensor").toString()
        return jsonSensor
    }

    fun parseInfoAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonInfo =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("info").toString()
        return jsonInfo
    }

    fun parseDeviceNumberAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonDeviceNumber =
            json.getJSONObject("result")
                .getJSONArray("events")
                .getJSONObject(i)
                .get("deviceNumber").toString()
        return jsonDeviceNumber
    }










    fun parseJsonEnvelopeIDAdd(text: String): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("envelopeID").toString()
        return jsonEnvelopeID
    }

    fun parseJsonBGColorAdd(text: String): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("bgColor").toString()
        return jsonAlarmStatus
    }

    fun parseJsonMessageAdd(text: String): String{
        val json = JSONObject(text)
        val jsonMessage =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("message").toString()
        return jsonMessage
    }

    fun parseJsonReceiveTimeAdd(text: String): String{
        val json = JSONObject(text)
        val jsonReceiveTime =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("receiveTime").toString()
        return jsonReceiveTime
    }

    fun parseJsonObjectIDAdd(text: String): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("objectID").toString()
        return jsonObjectID
    }

    fun parseJsonCustomNameAdd(text: String): String{
        val json = JSONObject(text)
        val jsonCustomName =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("customName").toString()
        return jsonCustomName
    }

    fun parseJsonObjectNameAdd(text: String): String{
        val json = JSONObject(text)
        val jsonObjectName =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("objectName").toString()
        return jsonObjectName
    }

    fun parseJsonDeviceID(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("deviceID").toString()
        return jsonDeviceID
    }

    fun parseSectionAdd(text: String): String{
        val json = JSONObject(text)
        val jsonSection =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("section").toString()
        return jsonSection
    }

    fun parseSensorAdd(text: String): String{
        val json = JSONObject(text)
        val jsonSensor =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("sensor").toString()
        return jsonSensor
    }

    fun parseInfoAdd(text: String): String{
        val json = JSONObject(text)
        val jsonInfo =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("info").toString()
        return jsonInfo
    }

    fun parseDeviceNumberAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceNumber =
            json.getJSONObject("result")
                .getJSONObject("event")
                .get("deviceNumber").toString()
        return jsonDeviceNumber
    }

}