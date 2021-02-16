package ru.elesta.jupiter_lkselfguard.httpManager

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentResponsiblePersons.AccountFragmentResponsiblePersons.Companion.changeFlagResponsible
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionPort.Companion.portForConnection
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionPort.Companion.portWasGiven
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.loadingOfEventsFinished
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.loadingOfObjectsFinished
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.webSocketConnectionAborted
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity.Companion.webSocketServiceOpenedFlag
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.SELFGUARD_SERVICE_ID
import ru.elesta.jupiter_lkselfguard.DevicesFragment.DevicesFragment.Companion.changeFlagDevice
import ru.elesta.jupiter_lkselfguard.DevicesFragment.DevicesFragment.Companion.changeFlagDeviceCard
import ru.elesta.jupiter_lkselfguard.DevicesFragment.DevicesFragment.Companion.loadingOfDevicesOneFinished
import ru.elesta.jupiter_lkselfguard.EventsFragment.EventsFragment.Companion.addMainEvent
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.changeFlagObject
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.changeFlagSection
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.tempObject
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.addEvent
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.loadingOfLocalEventsFinished
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.loadingOfSectionsFinished
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.loadingOfTrackIsFinished
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.showRemoveAlarmButton
//import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.sizeOfEventsOneArrayJson
import ru.elesta.jupiter_lkselfguard.JSONParser.*
import ru.elesta.jupiter_lkselfguard.dataClasses.*
import java.io.IOException
import java.sql.Time
import java.util.concurrent.Executors


class WebSocketListenerKROS(token: String, count: String): WebSocketListener() {

    companion object{
        var errorDescription = ""
        var errorFlag = false
    }
    private var innerToken = token
    private var innerCount = count
    var k = 1

    private val writeExecutor = Executors.newSingleThreadExecutor()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        writeExecutor.execute{
            try {
                val openMessage = JSONObject()
                    .put("action","authorization")
                    .put("result", JSONObject().put("token", innerToken))
                webSocket.send(openMessage.toString())
                WebSocketFactory.getInstance().mapSockets.put(innerCount, webSocket)
            } catch (e: IOException) {
                Log.e("WebSocketError","Unable to send messages: " + e.message)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.v("TextFromKROS", text)
        val json = JSONObject(text)
        if(json.getString("action").equals("login")) {
            if (json.getString("result").equals("success")) {
                webSocketServiceOpenedFlag = true
            }
        }
        if(json.getString("action").equals("add")) {
            if(json.getJSONObject("result").has("objects")) {
                var id = ""
                for(f in WebSocketFactory.getInstance().licenseContract.services){
                    if(f.serviceID == SELFGUARD_SERVICE_ID){
                        id = f.id
                    }
                }
                for(i in 0..json.getJSONObject("result").getJSONArray("objects").length()-1) {
                    var add = true
                    var commonAdd = true
                    val objectID = JSONParserObjects().parseJsonObjectIDAdd(text, i)
                    val typeID = JSONParserObjects().parseJsonTypeIDAdd(text, i)
                    val customName = JSONParserObjects().parseJsonCustomNameAdd(text, i)
                    val alarmStatus = JSONParserObjects().parseJsonAlarmStatusAdd(text, i)
                    val guardStatus = JSONParserObjects().parseJsonGuardStatusAdd(text, i)
                    val faultStatus = JSONParserObjects().parseJsonFaultStatusAdd(text, i)
                    val name = JSONParserObjects().parseJsonNameAdd(text, i)
                    val country = JSONParserObjects().parseJsonCountryAdd(text, i)
                    val city = JSONParserObjects().parseJsonCityAdd(text, i)
                    val street = JSONParserObjects().parseJsonStreetAdd(text, i)
                    val house = JSONParserObjects().parseJsonHouseAdd(text, i)
                    val building = JSONParserObjects().parseJsonBuildingAdd(text, i)
                    val flat = JSONParserObjects().parseJsonFlatAdd(text, i)
                    val latitude = JSONParserObjects().parseJsonLatitudeAdd(text, i)
                    val longitude = JSONParserObjects().parseJsonLongitudeAdd(text, i)
                    for(j in WebSocketFactory.getInstance().setOfObjects){
                        if(j.objectID == objectID){
                            add = false
                        }
                    }
                    for(j in WebSocketFactory.getInstance().commonSetOfObjects){
                        if(j.objectID == objectID){
                            commonAdd = false
                        }
                    }
                    if(add && (id == this.innerCount)) {
                        WebSocketFactory.getInstance().setOfObjects.add(
                            ObjectClass(
                                objectID,
                                typeID,
                                customName,
                                alarmStatus,
                                guardStatus,
                                faultStatus,
                                name,
                                country,
                                city,
                                street,
                                house,
                                building,
                                flat, latitude, longitude, innerCount
                            )
                        )
                    }
                    if(commonAdd) {
                        WebSocketFactory.getInstance().commonSetOfObjects.add(
                            ObjectClass(
                                objectID,
                                typeID,
                                customName,
                                alarmStatus,
                                guardStatus,
                                faultStatus,
                                name,
                                country,
                                city,
                                street,
                                house,
                                building,
                                flat, latitude, longitude, innerCount
                            )
                        )
                    }
                }
                loadingOfObjectsFinished = true
            }
        }
        if(json.getString("action").equals("update")) {
            if(json.getJSONObject("result").has("object")) {
                val objectID = JSONParserObjects().parseJsonObjectIDUpdate(text)
                val customName = JSONParserObjects().parseJsonCustomNameUpdate(text)
                val alarmStatus = JSONParserObjects().parseJsonAlarmStatusUpdate(text)
                val guardStatus = JSONParserObjects().parseJsonGuardStatusUpdate(text)
                val faultStatus = JSONParserObjects().parseJsonFaultStatusUpdate(text)
                val name = JSONParserObjects().parseJsonNameUpdate(text)
                val country = JSONParserObjects().parseJsonCountryUpdate(text)
                val city = JSONParserObjects().parseJsonCityUpdate(text)
                val street = JSONParserObjects().parseJsonStreetUpdate(text)
                val house = JSONParserObjects().parseJsonHouseUpdate(text)
                val building = JSONParserObjects().parseJsonBuildingUpdate(text)
                val flat = JSONParserObjects().parseJsonFlatUpdate(text)
                val latitude = JSONParserObjects().parseJsonLatitudeUpdate(text)
                val longitude = JSONParserObjects().parseJsonLongitudeUpdate(text)
                for(j in WebSocketFactory.getInstance().commonSetOfObjects){
                    if(j.objectID == objectID){
                        if(j.customName != customName){
                            j.customName = customName
                            changeFlagObject = true
                            Log.v("NewCustom", j.customName)
                        }
                        if(j.alarmStatus != alarmStatus){
                            j.alarmStatus = alarmStatus
                            changeFlagObject = true
                            if(j.alarmStatus == "Alarm"){
                                showRemoveAlarmButton = true
                            }
                            if(j.alarmStatus == "NotAlarm"){
                                showRemoveAlarmButton = false
                            }
                        }
                        if(j.guardStatus != guardStatus){
                            j.guardStatus = guardStatus
                            changeFlagObject = true
                        }
                        if(j.faultStatus != faultStatus){
                            j.faultStatus = faultStatus
                            changeFlagObject = true
                        }
                        if(j.name != name){
                            j.name = name
                            changeFlagObject = true
                        }
                        if(j.country != country){
                            j.country = country
                            changeFlagObject = true
                        }
                        if(j.city != city){
                            j.city = city
                            changeFlagObject = true
                        }
                        if(j.street != street){
                            j.street = street
                            changeFlagObject = true
                        }
                        if(j.house != house){
                            j.house = house
                            changeFlagObject = true
                        }
                        if(j.building != building){
                            j.building = building
                            changeFlagObject = true
                        }
                        if(j.flat != flat){
                            j.flat = flat
                            changeFlagObject = true
                        }
                        if(j.latitude != latitude){
                            j.latitude = latitude
                            changeFlagObject = true
                        }
                        if(j.longitude != longitude){
                            j.longitude = longitude
                            changeFlagObject = true
                        }
                    }
                }
            }
        }
        if(json.getString("action").equals("add")) {
            if (json.getJSONObject("result").has("sections")) {
                WebSocketFactory.getInstance().setOfSections.clear()
                for(i in 0..json.getJSONObject("result").getJSONArray("sections").length()-1) {
                    var add = true
                    val deviceID = JSONParserSections().parseJsonDeviceIDAdd(text, i)
                    val alarmStatus = JSONParserSections().parseJsonAlarmStatusAdd(text, i)
                    val guardStatus = JSONParserSections().parseJsonGuardStatusAdd(text, i)
                    val faultStatus = JSONParserSections().parseJsonFaultStatusAdd(text, i)
                    val indexOfSection = JSONParserSections().parseJsonIndexOfSectionAdd(text, i)
                    val objectID = JSONParserSections().parseJsonObjectIDAdd(text, i)
                    val sectionID = JSONParserSections().parseJsonSectionIDAdd(text,i)
                    val customName = JSONParserSections().parseJsonCustomNameAdd(text,i)
                    for(j in WebSocketFactory.getInstance().setOfSections){
                        if(j.deviceID == deviceID && j.indexOfSection == indexOfSection){
                            add = false
                        }
                    }
                    if(add) {
                        Log.v("Добавили", "Razdel")
                        WebSocketFactory.getInstance().setOfSections.add(
                            SectionClass(
                                deviceID,
                                alarmStatus,
                                guardStatus,
                                faultStatus,
                                indexOfSection,
                                objectID,
                                sectionID,
                                customName,
                                innerCount
                            )
                        )
                    }
                }
                Log.v("Cекций в вебсокете", WebSocketFactory.getInstance().setOfSections.size.toString())
                Log.v("Загрузка", "окончена")
                loadingOfSectionsFinished = true
            }
        }
        if(json.getString("action").equals("update")) {
            if(json.getJSONObject("result").has("section")) {
                val objectID = JSONParserSections().parseJsonObjectIDUpdate(text)
                val alarmStatus = JSONParserSections().parseJsonAlarmStatusUpdate(text)
                val guardStatus = JSONParserSections().parseJsonGuardStatusUpdate(text)
                val faultStatus = JSONParserSections().parseJsonFaultStatusUpdate(text)
                val deviceID = JSONParserSections().parseJsonDeviceIDUpdate(text)
                val indexOfSection = JSONParserSections().parseJsonIndexOfSectionUpdate(text)
                val sectionID = JSONParserSections().parseJsonSectionIDUpdate(text)
                val customName = JSONParserSections().parseJsonCustomNameUpdate(text)
                for(j in WebSocketFactory.getInstance().setOfSections){
                    if(j.deviceID == deviceID && j.indexOfSection == indexOfSection){
                        if(j.alarmStatus != alarmStatus){
                            j.alarmStatus = alarmStatus
                            changeFlagSection = true
                        }
                        if(j.guardStatus != guardStatus){
                            j.guardStatus = guardStatus
                            changeFlagSection = true
                        }
                        if(j.faultStatus != faultStatus){
                            j.faultStatus = faultStatus
                            changeFlagSection = true
                        }
                        if(j.customName != customName){
                            j.customName = customName
                            changeFlagSection = true
                        }
                    }
                }
            }
        }
        if(json.getString("action").equals("add")) {
            if (json.getJSONObject("result").has("events")) {
                var id = ""
                for(f in WebSocketFactory.getInstance().licenseContract.services){
                    if(f.serviceID == SELFGUARD_SERVICE_ID){
                        id = f.id
                    }
                }
                //WebSocketFactory.getInstance().setOfEvents.clear()
                //WebSocketFactory.getInstance().setOfEventsOne.clear()
                for (i in 0..json.getJSONObject("result").getJSONArray("events").length() - 1) {
                    val envelopeID = JSONParserEvents().parseJsonEnvelopeIDAdd(text, i)
                    val bgColor = JSONParserEvents().parseJsonBGColorAdd(text, i)
                    val message = JSONParserEvents().parseJsonMessageAdd(text, i)
                    val receiveTime = JSONParserEvents().parseJsonReceiveTimeAdd(text, i)
                    val objectID = JSONParserEvents().parseJsonObjectIDAdd(text, i)
                    val customName = JSONParserEvents().parseJsonCustomNameAdd(text, i)
                    val objectName = JSONParserEvents().parseJsonObjectNameAdd(text, i)
                    val deviceID = JSONParserEvents().parseJsonDeviceID(text, i)
                    val section = JSONParserEvents().parseSectionAdd(text, i)
                    val sensor = JSONParserEvents().parseSensorAdd(text, i)
                    val info = JSONParserEvents().parseInfoAdd(text, i)
                    val deviceNumber = JSONParserEvents().parseDeviceNumberAdd(text, i)
                    if(id == innerCount) {
                        WebSocketFactory.getInstance().setOfEvents.add(
                            EventClass(
                                envelopeID,
                                bgColor,
                                message,
                                receiveTime,
                                objectID,
                                customName,
                                objectName,
                                deviceID,
                                section,
                                sensor,
                                info,
                                deviceNumber,
                                innerCount
                            )
                        )
                    }

                    if (tempObject?.objectID == objectID) {
                        WebSocketFactory.getInstance().setOfEventsOne.add(
                            EventClass(
                                envelopeID,
                                bgColor,
                                message,
                                receiveTime,
                                objectID,
                                customName,
                                objectName,
                                deviceID,
                                section,
                                sensor,
                                info,
                                deviceNumber,
                                innerCount
                            )
                        )
                    }
                }
                //sizeOfEventsOneArrayJson = WebSocketFactory.getInstance().setOfEventsOne.size
                loadingOfEventsFinished = true
                loadingOfLocalEventsFinished = true
            }
        }
        if(json.getString("action").equals("add")) {
            if(json.getJSONObject("result").has("event")) {
                Log.v("NEW EVENT", "ADD")
                val envelopeID = JSONParserEvents().parseJsonEnvelopeIDAdd(text)
                val bgColor = JSONParserEvents().parseJsonBGColorAdd(text)
                val message = JSONParserEvents().parseJsonMessageAdd(text)
                val receiveTime = JSONParserEvents().parseJsonReceiveTimeAdd(text)
                val objectID = JSONParserEvents().parseJsonObjectIDAdd(text)
                val customName = JSONParserEvents().parseJsonCustomNameAdd(text)
                val objectName = JSONParserEvents().parseJsonObjectNameAdd(text)
                val deviceID = JSONParserEvents().parseJsonDeviceID(text)
                val section = JSONParserEvents().parseSectionAdd(text)
                val sensor = JSONParserEvents().parseSensorAdd(text)
                val info = JSONParserEvents().parseInfoAdd(text)
                val deviceNumber = JSONParserEvents().parseDeviceNumberAdd(text)
                WebSocketFactory.getInstance().setOfEvents.add(0,EventClass(envelopeID, bgColor, message, receiveTime, objectID, customName, objectName, deviceID, section, sensor, info,deviceNumber, innerCount))
                if(tempObject?.objectID == objectID){
                    WebSocketFactory.getInstance().setOfEventsOne.add(0,EventClass(envelopeID, bgColor, message, receiveTime, objectID, customName, objectName, deviceID, section, sensor, info,deviceNumber, innerCount))
                }
                addEvent = true
                addMainEvent = true
            }
        }
        if(json.getString("action").equals("add")) {
            if(json.getJSONObject("result").has("devices")) {
                var id = ""
                for(f in WebSocketFactory.getInstance().licenseContract.services){
                    if(f.serviceID == SELFGUARD_SERVICE_ID){
                        id = f.id
                    }
                }
                for(i in 0..json.getJSONObject("result").getJSONArray("devices").length()-1) {
                    var add = true
                    var commonAdd = true
                    val deviceID = JSONParserDevices().parseJsonDeviceIDAdd(text, i)
                    val type = JSONParserDevices().parseJsonTypeAdd(text, i)
                    val barcode = JSONParserDevices().parseJsonBarcodeAdd(text, i)
                    for(j in WebSocketFactory.getInstance().setOfDevices){
                        if(j.deviceID == deviceID){
                            add = false
                        }
                    }
                    for(j in WebSocketFactory.getInstance().commonSetOfDevices){
                        if(j.deviceID == deviceID){
                            commonAdd = false
                        }
                    }
                    if(add && (id == innerCount)) {
                        WebSocketFactory.getInstance().setOfDevices.add(DeviceClass(deviceID, type, barcode, innerCount))
                        Log.v("DEVICES", "DEVICE WAS ADD")
                        changeFlagDevice = true
                    }
                    if(commonAdd){
                        WebSocketFactory.getInstance().commonSetOfDevices.add(DeviceClass(deviceID, type, barcode, innerCount))
                    }
                }
            }
        }

        if(json.getString("action").equals("get")) {
            if(json.getJSONObject("result").has("device")) {
                var add = true
                val deviceID = JSONParserDevices().parseJsonDeviceIDAdd(text)
                val type = JSONParserDevices().parseJsonTypeAdd(text)
                val barcode = JSONParserDevices().parseJsonBarcodeAdd(text)
                val guardStatus = JSONParserDevices().parseJsonGuardStatusAdd(text)
                val alarmStatus = JSONParserDevices().parseJsonAlarmStatusAdd(text)
                val faultStatus = JSONParserDevices().parseJsonFaultStatusAdd(text)
                val objects = JSONParserDevices().parseJsonObjectsAdd(text)
                for(j in WebSocketFactory.getInstance().setOfDevicesOne){
                    if(j.deviceID == deviceID){
                        add = false
                        if(j.alarmStatus != alarmStatus){
                            j.alarmStatus = alarmStatus
                            changeFlagDevice = true
                            changeFlagDeviceCard = true
                        }
                        if(j.guardStatus != guardStatus){
                            j.guardStatus = guardStatus
                            changeFlagDevice = true
                            changeFlagDeviceCard = true
                        }
                        if(j.faultStatus != faultStatus){
                            j.faultStatus = faultStatus
                            changeFlagDevice = true
                            changeFlagDeviceCard = true
                        }
                        if(j.objects != objects){
                            j.objects = objects
                            changeFlagDevice = true
                            changeFlagDeviceCard = true
                        }
                    }
                }
                if(add == true) {
                    Log.v("DEVICE", "DEVICE WAS ADD")
                    WebSocketFactory.getInstance().setOfDevicesOne.add(DeviceOneClass(deviceID, type, barcode,guardStatus,alarmStatus,faultStatus, objects, innerCount))
                }
                if(WebSocketFactory.getInstance().setOfDevices.size == WebSocketFactory.getInstance().setOfDevicesOne.size){
                    loadingOfDevicesOneFinished = true
                }
            }
        }
        if(json.getString("action").equals("get")) {
            if(json.getJSONObject("result").has("responsibles")) {
                for(i in 0..json.getJSONObject("result").getJSONArray("responsibles").length()-1) {
                    var add = true
                    val responsibleID = JSONParserResponsibles().parseJsonResponsibleIDAdd(text, i)
                    val name = JSONParserResponsibles().parseJsonNameAdd(text, i)
                    val surname = JSONParserResponsibles().parseJsonSurnameAdd(text, i)
                    val patronymic = JSONParserResponsibles().parseJsonPatronymicAdd(text, i)
                    val login = JSONParserResponsibles().parseJsonLoginAdd(text, i)
                    val accessAlarmButton = JSONParserResponsibles().parseJsonAccessAlarmButtonAdd(text, i)
                    val accessCustomerAccount = JSONParserResponsibles().parseJsonAccessCustomerAccountAdd(text, i)
                    val accessUmka = JSONParserResponsibles().parseJsonAccessUmkaAdd(text, i)
                    val umkaID = JSONParserResponsibles().parseJsonUmkaNumberAdd(text, i)
                    val writeTrackOfMovement = JSONParserResponsibles().parseJsonWriteTrackOfMovementAdd(text, i)
                    val objects = JSONParserResponsibles().parseJsonObjectsAdd(text, i)
                    val devices = JSONParserResponsibles().parseJsonDevicesAdd(text, i)
                    val deviceKeys = JSONParserResponsibles().parseJsonDeviceKeysAdd(text, i)
                    for(j in WebSocketFactory.getInstance().setOfResponsibles){
                        if(j.responsibleID == responsibleID){
                            add = false
                        }
                    }
                    if(add == true) {
                        WebSocketFactory.getInstance().setOfResponsibles.add(ResponsibleClass(responsibleID,name,surname,patronymic,login,accessAlarmButton,accessCustomerAccount,accessUmka, umkaID,writeTrackOfMovement,objects,devices, deviceKeys, innerCount))
                        Log.v("RESPONSIBLES", "RESPONSIBLE WAS ADD")
                    }
                    if(i == json.getJSONObject("result").getJSONArray("responsibles").length()-1) {
                        Log.v("RESP_FLAG", "BEGIN TRUE")
                        changeFlagResponsible = true
                    }
                }
            }
        }
        if (json.getString("action").equals("get")){
            if(json.getJSONObject("result").has("port")){
                portForConnection = json.getJSONObject("result").get("port").toString()
                portWasGiven = true
            }
        }

        if(json.getString("action").equals("add")){
            if(json.getJSONObject("result").has("contract")){
                val number = JSONParserContractKROS().parseJsonNumberAdd(text)
                val status = JSONParserContractKROS().parseJsonStatusAdd(text)
                val dateStart = JSONParserContractKROS().parseJsonDateStartAdd(text)
                val dateEnd = JSONParserContractKROS().parseJsonDateEndAdd(text)
                WebSocketFactory.getInstance().krosContract = ContractKROSClass(number, status, dateStart, dateEnd, innerCount)
            }
        }

        if(json.getString("action").equals("add")){
            if(json.getJSONObject("result").has("track")){
                for(i in 0..json.getJSONObject("result").getJSONArray("track").length()-1) {
                    val lat = JSONParserTrack().parseJsonLatAdd(text, i).toLong()
                    val lng = JSONParserTrack().parseJsonLngAdd(text, i).toLong()
                    val time = JSONParserTrack().parseJsonTimeAdd(text, i)
                    WebSocketFactory.getInstance().listOfCoordinates.add(TrackClass(lat, lng, time))
                }
                loadingOfTrackIsFinished = true
            }
        }

        if(json.getString("action").equals("error")) {
            if(json.getJSONObject("result").has("description")) {
                val description =
                    json.getJSONObject("result")
                        .get("description").toString()
                Log.v("Description", description)
                errorFlag = true
                errorDescription = description
            }
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        webSocketServiceOpenedFlag = false
        Log.v("CLOSE: ", code.toString() + " " + reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.v("TAG", "OnFailureKros")
        writeExecutor.shutdown()
        webSocketConnectionAborted = true
    }
}