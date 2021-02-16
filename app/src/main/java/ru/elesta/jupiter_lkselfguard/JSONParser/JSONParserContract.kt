package ru.elesta.jupiter_lkselfguard.JSONParser

import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.dataClasses.ServiceClass

class JSONParserContract {

    fun parseJsonResponsibleIDAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("userinfo")
                .get("responsibleID").toString()
        return jsonDeviceID
    }

    fun parseJsonEmailAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("userinfo")
                .get("email").toString()
        return jsonDeviceID
    }

    fun parseJsonPhoneAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("userinfo")
                .get("phone").toString()
        return jsonDeviceID
    }

    fun parseJsonNameAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("userinfo")
                .get("name").toString()
        return jsonDeviceID
    }

    fun parseJsonSurnameAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("userinfo")
                .get("surname").toString()
        return jsonDeviceID
    }

    fun parseJsonPatronymicAdd(text: String): String{
        val json = JSONObject(text)
        val jsonDeviceID =
            json.getJSONObject("result")
                .getJSONObject("userinfo")
                .get("patronymic").toString()
        return jsonDeviceID
    }
    fun parseJsonServices(text: String): ArrayList<ServiceClass>{
        val json = JSONObject(text)
        val arrayOfServices = ArrayList<ServiceClass>()
        for(i in 0 until json.getJSONObject("result").getJSONObject("userinfo").getJSONArray("services").length()) {
            val urls = ArrayList<String>()
            val serviceID = json.getJSONObject("result").getJSONObject("userinfo").getJSONArray("services").getJSONObject(i).getString("serviceID")
            val id = json.getJSONObject("result").getJSONObject("userinfo").getJSONArray("services").getJSONObject(i).getString("id")
            val pin = json.getJSONObject("result").getJSONObject("userinfo").getJSONArray("services").getJSONObject(i).getLong("pin")
            for(j in 0 until json.getJSONObject("result").getJSONObject("userinfo").getJSONArray("services").getJSONObject(i).getJSONArray("urls").length()){
                val url = json.getJSONObject("result")
                    .getJSONObject("userinfo")
                    .getJSONArray("services")
                    .getJSONObject(i).getJSONArray("urls")
                    .getJSONObject(j).getString("url")
                urls.add(url)
            }
            val service = ServiceClass(id, serviceID, pin, urls)
            arrayOfServices.add(service)
        }
        return arrayOfServices
    }
}