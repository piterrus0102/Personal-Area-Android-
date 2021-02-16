package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.dataClasses.AddressClass
import ru.elesta.jupiter_lkselfguard.dataClasses.KeyClass

class JSONParserResponsibles {

    fun parseJsonResponsibleIDAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .get("responsibleID").toString()
        return jsonEnvelopeID
    }

    fun parseJsonNameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .get("name").toString()
        return jsonEnvelopeID
    }

    fun parseJsonSurnameAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .get("surname").toString()
        return jsonEnvelopeID
    }

    fun parseJsonPatronymicAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .get("patronymic").toString()
        return jsonEnvelopeID
    }

    fun parseJsonLoginAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .get("login").toString()
        return jsonEnvelopeID
    }

    fun parseJsonAccessAlarmButtonAdd(text: String, i: Int): Boolean{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getBoolean("accessAlarmButton")
        return jsonEnvelopeID
    }

    fun parseJsonUmkaNumberAdd(text: String, i: Int): String{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getString("umkaID")
        return jsonEnvelopeID
    }

    fun parseJsonAccessUmkaAdd(text: String, i: Int): Boolean{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getBoolean("accessUmka")
        return jsonEnvelopeID
    }

    fun parseJsonAccessCustomerAccountAdd(text: String, i: Int): Boolean{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getBoolean("accessCustomerAccount")
        return jsonEnvelopeID
    }

    fun parseJsonWriteTrackOfMovementAdd(text: String, i: Int): Boolean{
        val json = JSONObject(text)
        val jsonEnvelopeID =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getBoolean("writeTrackOfMovement")
        return jsonEnvelopeID
    }

    /*fun parseJsonAddressAdd(text: String, i: Int): AddressClass{
        val json = JSONObject(text)
        val jsonCountry =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("country").toString()
        val jsonCity =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("city").toString()
        val jsonStreet =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("street").toString()
        val jsonHouse =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("house").toString()
        val jsonBuilding =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("building").toString()
        val jsonCorpus =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("corpus").toString()
        val jsonFlat =
            json.getJSONObject("result")
                .getJSONArray("responsibles")
                .getJSONObject(i)
                .getJSONObject("address")
                .get("flat").toString()
        val jsonAddress = AddressClass(jsonCountry,jsonCity,jsonStreet,jsonHouse,jsonBuilding,jsonCorpus, jsonFlat)
        return jsonAddress
    }*/

    fun parseJsonObjectsAdd(text: String, i: Int): Array<Int>{
        val json = JSONObject(text)
        val intArray = Array(json.getJSONObject("result")
            .getJSONArray("responsibles")
            .getJSONObject(i)
            .getJSONArray("objects").length(), {i -> i})
        var k = 0
        for(j in 0..json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("objects").length()-1){
            val r = json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("objects").get(k) as Int
            intArray.set(k,r)
            k++
        }
        return intArray
    }

    fun parseJsonDevicesAdd(text: String, i: Int): Array<Int>{
        val json = JSONObject(text)
        val intArray = Array(json.getJSONObject("result")
            .getJSONArray("responsibles")
            .getJSONObject(i)
            .getJSONArray("devices").length(), {i -> i})
        var k = 0
        for(j in 0..json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("devices").length()-1){
            val r = json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("devices").get(k) as Int
            intArray.set(k,r)
            k++
        }
        return intArray
    }

    fun parseJsonDeviceKeysAdd(text: String, i: Int): ArrayList<KeyClass>{
        val json = JSONObject(text)
        val keyArray = ArrayList<KeyClass>()
        var k = 0
        for(j in 0..json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("deviceKeys").length()-1){
            val keyID = json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("deviceKeys").getJSONObject(k).get("keyID").toString()
            val deviceID = json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("deviceKeys").getJSONObject(k).get("deviceID").toString()
            val value = json.getJSONObject("result").getJSONArray("responsibles").getJSONObject(i).getJSONArray("deviceKeys").getJSONObject(k).get("value").toString()
            keyArray.add(KeyClass(keyID,deviceID,value))
            k++
        }
        return keyArray
    }
}