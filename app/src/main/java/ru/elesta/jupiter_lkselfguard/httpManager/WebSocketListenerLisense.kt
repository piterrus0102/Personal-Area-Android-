package ru.elesta.jupiter_lkselfguard.httpManager

import android.util.Log
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity.Companion.selfGuardTokenLisense
import okhttp3.*
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionBarcode.Companion.toConnectionToObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.webSocketConnectionAborted
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.userInfoIsFinished
import ru.elesta.jupiter_lkselfguard.JSONParser.JSONParserCommonServiceClass
import ru.elesta.jupiter_lkselfguard.JSONParser.JSONParserContract
import ru.elesta.jupiter_lkselfguard.dataClasses.CommonServiceClass
import ru.elesta.jupiter_lkselfguard.dataClasses.ContractClass
import ru.elesta.jupiter_lkselfguard.main_fragments.AppSettingsFragment
import java.io.IOException
import java.util.concurrent.Executors

class WebSocketListenerLisense: WebSocketListener() {
    private val writeExecutor = Executors.newSingleThreadExecutor()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        writeExecutor.execute{
            try {
                val openMessage = JSONObject()
                    .put("action","authorization")
                    .put("result", JSONObject().put("token", selfGuardTokenLisense))
                webSocket.send(openMessage.toString())
                WebSocketFactory.getInstance().licenceSocket = webSocket
            } catch (e: IOException) {
                Log.e("WebSocketError","Unable to send messages: " + e.message)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.v("TextFromLisense", text)
        val json = JSONObject(text)
        if(json.getString("action").equals("login")){
            if(json.getString("result").equals("success")){
                MainActivity.webSocketOpenedFlag = true
            }
        }
        if(json.getString("action").equals("add")) {
            if(json.getJSONObject("result").has("userinfo")) {
                val responsibleID = JSONParserContract().parseJsonResponsibleIDAdd(text)
                val email = JSONParserContract().parseJsonEmailAdd(text)
                val phone = JSONParserContract().parseJsonPhoneAdd(text)
                val name = JSONParserContract().parseJsonNameAdd(text)
                val surname = JSONParserContract().parseJsonSurnameAdd(text)
                val patronymic = JSONParserContract().parseJsonPatronymicAdd(text)
                val services = JSONParserContract().parseJsonServices(text)
                WebSocketFactory.getInstance().licenseContract = ContractClass(responsibleID, email, phone, name, surname, patronymic, services)
                userInfoIsFinished = true
            }
        }
        if(json.getString("action").equals("add")) {
            if(json.getJSONObject("result").has("services")) {
                for(i in 0..json.getJSONObject("result").getJSONArray("services").length()-1) {
                    val serviceID = JSONParserCommonServiceClass().parseJsonServiceIDAdd(text, i)
                    val name = JSONParserCommonServiceClass().parseJsonNameAdd(text, i)
                    val description = JSONParserCommonServiceClass().parseJsonDescriptionAdd(text, i)
                    val service = CommonServiceClass(serviceID, name, description)
                    WebSocketFactory.getInstance().listOfCommonServices.add(service)
                }
            }
        }

        if(json.getString("action").equals("find")){
            if(json.getJSONObject("result").has("device")) {
                toConnectionToObject = true
            }
        }
        if(json.getString("action").equals("done")){
            if(json.getString("result").equals("OK")) {
                AppSettingsFragment.changePasswordFlag = true
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        WebSocketFactory.getInstance().licenceSocket = null
        MainActivity.webSocketOpenedFlag = false
        Log.v("CLOSE: ", code.toString() + " " + reason);
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        t.stackTrace
        Log.v("TAG", "License")
        writeExecutor.shutdown()
        webSocketConnectionAborted = true
    }
}