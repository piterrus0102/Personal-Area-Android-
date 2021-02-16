package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONArray
import org.json.JSONObject

class JSONParserDevices {


    fun parseJsonDeviceIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONArray("devices")
                .getJSONObject(i)
                .get("deviceID").toString()
        return jsonDeviceID
    }

    fun parseJsonTypeAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonType =
            json.getJSONObject("result")
                .getJSONArray("devices")
                .getJSONObject(i)
                .get("type").toString()
        return jsonType
    }

    fun parseJsonBarcodeAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonBarcode =
            json.getJSONObject("result")
                .getJSONArray("devices")
                .getJSONObject(i)
                .get("barcode").toString()
        return jsonBarcode
    }








    fun parseJsonDeviceIDAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("device")
                .get("deviceID").toString()
        return jsonDeviceID
    }

    fun parseJsonTypeAdd(text: String): String{
        val json = JSONObject(text)
        val jsonType =
            json.getJSONObject("result")
                .getJSONObject("device")
                .get("type").toString()
        return jsonType
    }

    fun parseJsonBarcodeAdd(text: String): String{
        val json = JSONObject(text)
        val jsonBarcode =
            json.getJSONObject("result")
                .getJSONObject("device")
                .get("barcode").toString()
        return jsonBarcode
    }

    fun parseJsonGuardStatusAdd(text: String): String{
        val json = JSONObject(text)
        val jsonGuardStatus =
            json.getJSONObject("result")
                .getJSONObject("device")
                .get("guardStatus").toString()
        return jsonGuardStatus
    }

    fun parseJsonAlarmStatusAdd(text: String): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONObject("device")
                .get("alarmStatus").toString()
        return jsonAlarmStatus
    }

    fun parseJsonFaultStatusAdd(text: String): String{
        val json = JSONObject(text)
        val jsonFaultStatus =
            json.getJSONObject("result")
                .getJSONObject("device")
                .get("faultStatus").toString()
        return jsonFaultStatus
    }

    fun parseJsonObjectsAdd(text: String): ArrayList<Long>{
        val json = JSONObject(text)
        val array = ArrayList<Long>()
        var jsonObjects: JSONArray
        if(json.getJSONObject("result").getJSONObject("device").has("objects")) {
            jsonObjects =
                json.getJSONObject("result")
                    .getJSONObject("device")
                    .getJSONArray("objects")
            if (jsonObjects.length() > 0) {
                for (i in 0..jsonObjects.length() - 1) {
                    array.add(jsonObjects.getLong(i))
                }
            }
        }
        return array
    }


}