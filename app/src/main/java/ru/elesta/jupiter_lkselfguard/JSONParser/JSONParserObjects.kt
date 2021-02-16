package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject

class JSONParserObjects {


    fun parseJsonCustomNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonCustomName =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("customName").toString()
        return jsonCustomName
    }

    fun parseJsonTypeIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonTypeID =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("typeID").toString()
        return jsonTypeID
    }

    fun parseJsonAlarmStatusAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("alarmStatus").toString()
        return jsonAlarmStatus
    }

    fun parseJsonGuardStatusAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonGuardStatus =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("guardStatus").toString()
        return jsonGuardStatus
    }

    fun parseJsonFaultStatusAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonFaultStatus =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("faultStatus").toString()
        return jsonFaultStatus
    }

    fun parseJsonObjectIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("objectID").toString()
        return jsonObjectID
    }

    fun parseJsonNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonName =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .get("name").toString()
        return jsonName
    }

    fun parseJsonCountryAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonCountry =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("country").toString()
        return jsonCountry
    }

    fun parseJsonCityAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonCity =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("city").toString()
        return jsonCity
    }

    fun parseJsonStreetAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonStreet =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("street").toString()
        return jsonStreet
    }

    fun parseJsonHouseAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonHouse =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("house").toString()
        return jsonHouse
    }

    fun parseJsonBuildingAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonBuilding =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("building").toString()
        return jsonBuilding
    }

    fun parseJsonFlatAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("flat").toString()
        return jsonFlat
    }

    fun parseJsonLatitudeAdd(text: String, i: Int): Double{
        val json = JSONObject(text)
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("location")
                .get("lat") as Double
        return jsonFlat
    }

    fun parseJsonLongitudeAdd(text: String, i: Int): Double{
        val json = JSONObject(text)
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONArray("objects")
                .getJSONObject(i)
                .getJSONObject("location")
                .get("lng") as Double
        return jsonFlat
    }







    fun parseJsonCustomNameUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonCustomName =
            json.getJSONObject("result")
                .getJSONObject("object")
                .get("customName").toString()
        return jsonCustomName
    }

    fun parseJsonAlarmStatusUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonAlarmStatus =
            json.getJSONObject("result")
                .getJSONObject("object")
                .get("alarmStatus").toString()
        return jsonAlarmStatus
    }

    fun parseJsonGuardStatusUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonGuardStatus =
            json.getJSONObject("result")
                .getJSONObject("object")
                .get("guardStatus").toString()
        return jsonGuardStatus
    }

    fun parseJsonFaultStatusUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonFaultStatus =
            json.getJSONObject("result")
                .getJSONObject("object")
                .get("faultStatus").toString()
        return jsonFaultStatus
    }

    fun parseJsonObjectIDUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonObjectID =
            json.getJSONObject("result")
                .getJSONObject("object")
                .get("objectID").toString()
        return jsonObjectID
    }

    fun parseJsonNameUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonName =
            json.getJSONObject("result")
                .getJSONObject("object")
                .get("name").toString()
        return jsonName
    }

    fun parseJsonCountryUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonCountry =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("address")
                .get("country").toString()
        return jsonCountry
    }

    fun parseJsonCityUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonCity =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("address")
                .get("city").toString()
        return jsonCity
    }

    fun parseJsonStreetUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonStreet =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("address")
                .get("street").toString()
        return jsonStreet
    }

    fun parseJsonHouseUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonHouse =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("address")
                .get("house").toString()
        return jsonHouse
    }

    fun parseJsonBuildingUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonBuilding =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("address")
                .get("building").toString()
        return jsonBuilding
    }

    fun parseJsonFlatUpdate(text: String): String{
        val json = JSONObject(text)
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("address")
                .get("flat").toString()
        return jsonFlat
    }

    fun parseJsonLatitudeUpdate(text: String): Double{
        val json = JSONObject(text)
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("location")
                .get("lat") as Double
        return jsonFlat
    }

    fun parseJsonLongitudeUpdate(text: String): Double{
        val json = JSONObject(text)
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONObject("object")
                .getJSONObject("location")
                .get("lng") as Double
        return jsonFlat
    }

}