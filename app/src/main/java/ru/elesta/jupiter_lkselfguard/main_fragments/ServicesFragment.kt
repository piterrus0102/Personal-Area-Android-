package ru.elesta.jupiter_lkselfguard.main_fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import okhttp3.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.GUARD_SERVICE_ID
//import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.KROS_ONLINE_PIN
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.SELFGUARD_SERVICE_ID
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.userInfoIsFinished
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.CommonServiceClass
import ru.elesta.jupiter_lkselfguard.dataClasses.ObjectClass
import ru.elesta.jupiter_lkselfguard.dataClasses.SaveGuardDataClass
import ru.elesta.jupiter_lkselfguard.dataClasses.ServiceClass
import ru.elesta.jupiter_lkselfguard.helpers.DBHelperKROS
import ru.elesta.jupiter_lkselfguard.helpers.PreloaderClass
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS
import java.io.IOException
import java.net.URL

class ServicesFragment: Fragment(), View.OnClickListener {

    companion object {
        var servicesArray = ArrayList<CommonServiceClass>()
        val instance = ServicesFragment()
    }

    lateinit var dbKrosHelper: DBHelperKROS

    lateinit var guardServiceLayout: ConstraintLayout

    lateinit var addServiceButton: Button
    //lateinit var exitButton: Button
    lateinit var tableOfServices: TableLayout
    lateinit var myView: View

    lateinit var servicesConstraintLayout: ConstraintLayout

    lateinit var dbHelper: DBHelperKROS
    private var setOfKrosUsers = ArrayList<SaveGuardDataClass>()

    var checkConnect = false

    lateinit var handler: Handler
    lateinit var runnable: Runnable
    var chosenService = ""

    lateinit var handleryy: Handler
    lateinit var runnableyy: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("ServicesFragment", "onCreateView")
        val v = inflater.inflate(R.layout.services_fragment, null)
        myView = v
        dbHelper = DBHelperKROS(requireContext())
        servicesConstraintLayout = v.findViewById(R.id.servicesConstraintLayout)
        addServiceButton = v.findViewById(R.id.addServiceButton)
        addServiceButton.setOnClickListener(this)
        tableOfServices = v.findViewById(R.id.tableOfServices)
        return v
    }

    override fun onResume() {
        super.onResume()
        Log.e("ServicesFragment", "Resume")
        handleryy = Handler()
        runnableyy = Runnable {
            if(WebSocketFactory.getInstance().licenseContract.services.isEmpty()){
                addServiceButton.performClick()
            }
            handleryy.removeCallbacks(runnableyy)
        }
        handleryy.postDelayed(runnableyy, 500)
        getServices()
    }

    @ExperimentalStdlibApi
    override fun onClick(v: View) {
        when (v.id) {
            R.id.addServiceButton -> {
                callPopup()
            }
        }
    }

    private fun cleanTable() {
        tableOfServices.removeAllViewsInLayout()
    }

    private fun getServices() {
        cleanTable()
        dbKrosHelper = DBHelperKROS(requireContext())
        setOfKrosUsers.clear()
        val db1 = dbKrosHelper.readableDatabase
        val c = db1.query("krosUsers", null, null, null, null, null, null)
        if (c.moveToFirst()) {
            val idColIndex = c.getColumnIndex(BaseColumns._ID)
            val userLoginColIndex = c.getColumnIndex("userLogin")
            val userPasswordColIndex = c.getColumnIndex("userPassword")
            val serverPinColIndex = c.getColumnIndex("serverPin")
            val serviceIDColIndex = c.getColumnIndex("serviceID")
            do {
                Log.d(
                    "DB",
                    "ID = " + c.getInt(idColIndex) +
                            ", login = " + c.getString(userLoginColIndex) +
                            ", password = " + c.getString(userPasswordColIndex) +
                            ", pin = " + c.getString(serverPinColIndex) +
                            ", serviceID" + c.getString(serviceIDColIndex)

                )

                setOfKrosUsers.add(SaveGuardDataClass(c.getInt(idColIndex),
                    c.getString(serverPinColIndex).toString(),
                    c.getString(userLoginColIndex),
                    c.getString(userPasswordColIndex),
                    c.getString(serviceIDColIndex)))
            } while (c.moveToNext())
        } else
            Log.d("DB", "0 rows")
        c.close()
        if (WebSocketFactory.getInstance().licenseContract.services.isNotEmpty()) {
            for (i in WebSocketFactory.getInstance().licenseContract.services) {
                var invalid = true
                var reconnectFlag = false
                val display: Display = requireActivity().windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val height: Int = size.y
                val width: Int = size.x
                val tableRow = TableRow(requireContext())
                tableRow.weightSum = 2F
                tableRow.setBackgroundColor(Color.RED)
                for (j in WebSocketFactory.getInstance().mapSockets){
                    if(j.key == i.id){
                        tableRow.setBackgroundColor(Color.parseColor("#1DB505"))
                        invalid = false
                        break
                    }
                }
                for(j in 0 until setOfKrosUsers.size){
                    if(i.id != setOfKrosUsers[j].socketNumber && j == setOfKrosUsers.size - 1 && invalid){
                        reconnectFlag = true
                    }
                }
                val lp = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
                tableRow.isClickable = true
                lp.topMargin = 10
                tableRow.layoutParams = lp
                tableRow.setPadding(10, 10, 10, 10)

                val innerConstraintLayout = ConstraintLayout(requireContext())
                val innerConstraintLayoutLP = TableRow.LayoutParams((width * 0.99).toInt(), (0.06976744186 * height).toInt())
                innerConstraintLayout.layoutParams = innerConstraintLayoutLP
                val service = TextView(requireContext())
                val serviceLP = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 0)
                service.layoutParams = serviceLP
                service.gravity = Gravity.CENTER_VERTICAL
                if (i.serviceID == SELFGUARD_SERVICE_ID) {
                    service.text = "САМООХРАНА"
                }
                if (i.serviceID == GUARD_SERVICE_ID) {
                    service.text = "ОХРАНА ${i.pin}"
                }
//                if (i.serviceID == GUARD_SERVICE_ID && i.pin == KROS_ONLINE_PIN) {
//                    service.text = "КРОС-Online"
//                }
                if(reconnectFlag && invalid){
                    val m = service.text.toString() + " Разавторизован"
                    service.text = m
                }
                else if(invalid){
                    val m = service.text.toString() + " Нет связи"
                    service.text = m
                }
                Log.v("ServiceText", i.serviceID)
                service.textSize = 18F
                service.setTextColor(Color.WHITE)

                val button = Button(requireContext())
                val buttonLP = TableRow.LayoutParams(0, (0.06976744186 * height).toInt())
                button.layoutParams = buttonLP
                button.background = resources.getDrawable(R.drawable.ym_ic_close)

                tableRow.addView(innerConstraintLayout)
                innerConstraintLayout.addView(service)
                innerConstraintLayout.addView(button)

                innerConstraintLayout.id = View.generateViewId()
                service.id = View.generateViewId()
                button.id = View.generateViewId()

                val constraintSet = ConstraintSet()
                constraintSet.clone(innerConstraintLayout)
                constraintSet.connect(service.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 20)
                constraintSet.connect(service.id, ConstraintSet.RIGHT, button.id, ConstraintSet.LEFT, 20)
                constraintSet.connect(service.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 5)
                constraintSet.connect(service.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 5)

                constraintSet.connect(button.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 20)
                constraintSet.connect(button.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 5)
                constraintSet.connect(button.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 5)

                constraintSet.constrainHeight(service.id, ConstraintSet.MATCH_CONSTRAINT)
                constraintSet.constrainWidth(service.id, ConstraintSet.MATCH_CONSTRAINT)
                constraintSet.constrainHeight(button.id, ConstraintSet.MATCH_CONSTRAINT)
                constraintSet.setDimensionRatio(button.id, "1")

                constraintSet.applyTo(innerConstraintLayout)

                tableRow.setOnClickListener {
                    if (i.serviceID == SELFGUARD_SERVICE_ID) {
                        LKActivity.mainSocketString = i.id
                        val intent = Intent(requireContext(), LKActivity::class.java)
                        startActivity(intent)
                    }
                }
                button.setOnClickListener {
                    var message = JSONObject()
                    if(i.serviceID == GUARD_SERVICE_ID) {
                        message = JSONObject()
                            .put("action", "edit")
                            .put(
                                "result", JSONObject()
                                    .put(
                                        "service", JSONObject()
                                            .put("serviceID", GUARD_SERVICE_ID)
                                            .put("enabled", false)
                                            .put("pin", i.pin)
                                    )
                            )
                    }
                    if(i.serviceID == SELFGUARD_SERVICE_ID){
                        message = JSONObject()
                            .put("action", "edit")
                            .put(
                                "result", JSONObject()
                                    .put(
                                        "service", JSONObject()
                                            .put("serviceID", SELFGUARD_SERVICE_ID)
                                            .put("enabled", false)
                                            .put("pin", -1L)
                                    )
                            )
                    }

                    WebSocketFactory.getInstance().licenceSocket.send(message.toString())
                    var k = 0
                    while (!userInfoIsFinished && k < 100) {
                        Thread.sleep(100)
                        k++
                    }
                    if (!userInfoIsFinished) {
                        runOnUiThread {
                            Toast.makeText(requireContext(), "Не удалось отключить сервис", Toast.LENGTH_SHORT).show()
                        }
                        return@setOnClickListener
                    }
                    if(userInfoIsFinished){
                        userInfoIsFinished = false
                        val m = ArrayList<ObjectClass>()
                        for (q in WebSocketFactory.getInstance().commonSetOfObjects){
                            m.add(q)
                        }
                        for(a in m) {
                            if(a.socketNumber == i.id){
                                WebSocketFactory.getInstance().commonSetOfObjects.remove(a)
                            }
                        }
                        m.clear()
                        for(a in WebSocketFactory.getInstance().licenseContract.services) {
                            if(a.pin == i.pin) {
                                WebSocketFactory.getInstance().mapSockets[a.id]?.cancel()
                                WebSocketFactory.getInstance().mapSockets.remove(a.id)
                                break
                            }
                        }
                        dbHelper.readableDatabase
                        val db1 = dbHelper.readableDatabase
                        val c = db1.query("krosUsers", null, null, null, null, null, null)
                        if (c.moveToFirst()) {
                            val serviceIDColIndex = c.getColumnIndex("serviceID")
                            do {
                                if(c.getString(serviceIDColIndex) == i.id){
                                    db1.delete(DBHelperKROS.Entry.TABLE_NAME, DBHelperKROS.Entry.SERVICE_ID+"="+i.id, null)
                                }
                            } while (c.moveToNext())
                        } else
                            Log.d("DB", "0 rows")
                        c.close()
                        getServices()
                    }
                }
                tableOfServices.addView(tableRow)
            }
        }

    }

    @ExperimentalStdlibApi
    fun callPopup() {
        val myInflater = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = myInflater.inflate(R.layout.add_service_window, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true
        val selfGuardServiceLayout = popupView.findViewById<ConstraintLayout>(R.id.selfGuardServiceLayout)
        guardServiceLayout = popupView.findViewById(R.id.guardServiceLayout)
        //val krosServiceLayout = popupView.findViewById<ConstraintLayout>(R.id.KROSServiceLayout)
        var guardServiceFlag = false
        //var krosServiceFlag = false
        var selfGuardServiceFlag = false
        selfGuardServiceLayout.setOnClickListener {
            it.setBackgroundColor(Color.LTGRAY)
            guardServiceLayout.setBackgroundColor(Color.TRANSPARENT)
            //krosServiceLayout.setBackgroundColor(Color.TRANSPARENT)
            guardServiceFlag = false
            //krosServiceFlag = false
            selfGuardServiceFlag = true
        }
        guardServiceLayout.setOnClickListener {
            it.setBackgroundColor(Color.LTGRAY)
            selfGuardServiceLayout.setBackgroundColor(Color.TRANSPARENT)
            //krosServiceLayout.setBackgroundColor(Color.TRANSPARENT)
            guardServiceFlag = true
            //krosServiceFlag = false
            selfGuardServiceFlag = false
        }
//        krosServiceLayout.setOnClickListener {
//            it.setBackgroundColor(Color.LTGRAY)
//            guardServiceLayout.setBackgroundColor(Color.TRANSPARENT)
//            selfGuardServiceLayout.setBackgroundColor(Color.TRANSPARENT)
//            krosServiceFlag = true
//            guardServiceFlag = false
//            selfGuardServiceFlag = false
//        }
        val cancelButton = popupView.findViewById<Button>(R.id.cancelButton)
        val connectToServiceButton = popupView.findViewById<Button>(R.id.connectToServiceButton)
        cancelButton.setOnClickListener {
            popupWindow.dismiss()
        }
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        connectToServiceButton.setOnClickListener {
            if (!selfGuardServiceFlag && !guardServiceFlag/* && !krosServiceFlag*/) {
                Toast.makeText(requireContext(), "Выберите сервис для подключения", Toast.LENGTH_SHORT).show()
            }
            if (selfGuardServiceFlag) {
                connectSelfGuard(selfGuardServiceLayout){
                    Log.e("ServiceFragment", "SelfGuard getServices()")
                    if(it) {
                        runOnUiThread {
                            popupWindow.dismiss()
                            for (i in WebSocketFactory.getInstance().licenseContract.services) {
                                if(i.serviceID == SELFGUARD_SERVICE_ID)
                                LKActivity.mainSocketString = i.id
                                fragmentManager?.beginTransaction()?.remove(this)?.commit()
                                val intent = Intent(requireContext(), LKActivity::class.java)
                                startActivity(intent)
                            }
                            //getServices()
                        }
                    }
                }
            }
            if (guardServiceFlag/* || krosServiceFlag*/) {
//                var flag = false
//                if(krosServiceFlag){
//                    flag = true
//                }
                connectGuard(/*flag*/) {
                    Log.e("ServiceFragment", "Guard getServices()")
                    if(it) {
                        runOnUiThread {
                            PreloaderClass.instance.stopPreloader(servicesConstraintLayout)
                            popupWindow.dismiss()
                            getServices()
                        }
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    fun connectSelfGuard(selfGuardServiceLayout: ConstraintLayout, callback: (Boolean) -> Unit){
        for (i in WebSocketFactory.getInstance().licenseContract.services) {
            if (i.serviceID == SELFGUARD_SERVICE_ID) {
                selfGuardServiceLayout.isClickable = false
                Toast.makeText(requireContext(), "Этот сервис уже подключен к Вашей учетной записи", Toast.LENGTH_SHORT).show()
                callback(false)
                return
            }
        }

        val message = JSONObject()
            .put("action", "edit")
            .put(
                "result", JSONObject()
                    .put(
                        "service", JSONObject()
                            .put("serviceID", SELFGUARD_SERVICE_ID)
                            .put("enabled", true)
                            .put("pin", -1L)
                    )
            )
        WebSocketFactory.getInstance().licenceSocket.send(message.toString())

        var k = 0
        while (!userInfoIsFinished && k < 100) {
            Thread.sleep(100)
            k++
        }
        if (!userInfoIsFinished) {
            runOnUiThread {
                Toast.makeText(requireContext(), "Не удалось подключить сервис", Toast.LENGTH_SHORT).show()
            }
            callback(false)
            return
        }
        if (userInfoIsFinished) {
            userInfoIsFinished = false
            var urlForConnect = ""
            var count = ""
            for (i in WebSocketFactory.getInstance().licenseContract.services) {
                if (i.serviceID == SELFGUARD_SERVICE_ID) {
                    urlForConnect = i.urls[0]
                    count = i.id
                    break
                }
            }
            val mainPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
            val login = mainPref.getString("login", "")!!
            val password = mainPref.getString("password", "")!!
            val headerString = encoded64(login, password)

            LKHttpManager().authorizationKROS(null, urlForConnect, headerString, requireContext()){
                if(it != ""){
                    val tokenPush = mainPref.getString("tokenpush", "")
                    val url = URL("http://$urlForConnect/weblk/push?token=$tokenPush&status=true")
                    val okHttpClient = OkHttpClient()
                    val request = Request.Builder()
                        .header("Authorization", "Basic $headerString")
                        .url(url)
                        .build()
                    okHttpClient.newCall(request).enqueue(object: Callback{
                        override fun onFailure(call: Call, e: IOException) {
                            //
                        }

                        override fun onResponse(call: Call, response: Response) {
                            //
                        }
                    })

                    val requestKROS = Request.Builder().url("ws://$urlForConnect/websocket").build()
                    val listenerKROS = WebSocketListenerKROS(it, count)
                    val client = OkHttpClient()
                    client.newWebSocket(requestKROS, listenerKROS)
                    client.dispatcher().executorService().shutdown()
                    var j = 0
                    while (!MainActivity.webSocketServiceOpenedFlag && j < 100) {
                        Thread.sleep(100)
                        j++
                    }
                    if (!MainActivity.webSocketServiceOpenedFlag) {
                        runOnUiThread {
                            Toast.makeText(requireContext(), "Соединение с сервером не установлено", Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (MainActivity.webSocketServiceOpenedFlag) {
                        MainActivity.webSocketServiceOpenedFlag = false
                        servicesArray.add(CommonServiceClass(SELFGUARD_SERVICE_ID, "selfguard", "САМООХРАНА"))
                        runOnUiThread {
                            Toast.makeText(requireContext(), "Сервис самоохраны успешно подключен!", Toast.LENGTH_SHORT).show()
                        }

                        val message5 = JSONObject()
                            .put("action", "get")
                            .put("result", JSONObject().put("objects", "")).toString()
                        WebSocketFactory.getInstance().mapSockets[count]?.send(message5)
                        val message1 = JSONObject()
                            .put("action", "get")
                            .put("result", JSONObject().put("devices", ""))
                        WebSocketFactory.getInstance().mapSockets[count]?.send(message1.toString())
                        val message2 = JSONObject()
                            .put("action", "get")
                            .put(
                                "result", JSONObject()
                                    .put(
                                        "events", JSONObject()
                                            .put("limit", 20).put("object", -1).put("envelopeID", -1)
                                    )
                            )
                        WebSocketFactory.getInstance().mapSockets[count]?.send(message2.toString())
                        val message3 = JSONObject()
                            .put("action", "get")
                            .put("result", JSONObject().put("contract", ""))
                        WebSocketFactory.getInstance().mapSockets[count]?.send(message3.toString())
                        val message4 = JSONObject()
                            .put("action", "get")
                            .put("result", JSONObject().put("contract", ""))
                        WebSocketFactory.getInstance().mapSockets[count]?.send(message4.toString())

                        chosenService = SELFGUARD_SERVICE_ID
                        callback(true)
                    }
                }
                else{
                    callback(false)
                }
            }
        }
    }

    @ExperimentalStdlibApi
    fun connectGuard(/*krosBool: Boolean, */callback: (Boolean) -> Unit){
        val myInflaterInner = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupViewInner = myInflaterInner.inflate(R.layout.add_guard_window, null)
        val popupWindowInner = PopupWindow(popupViewInner, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        val pinServerActivationTextView = popupViewInner.findViewById<TextView>(R.id.pinServerActivationTextView)
        val pinTextView = popupViewInner.findViewById<TextView>(R.id.pinTextView)
        val loginActivationTextView = popupViewInner.findViewById<TextView>(R.id.loginActivationTextView)
        val addGuardWindowConstraintLayout = popupViewInner.findViewById<ConstraintLayout>(R.id.addGuardWindowConstraintLayout)
        val passwordActivationTextView = popupViewInner.findViewById<TextView>(R.id.passwordActivationTextView)
        val activateGuardServiceButton = popupViewInner.findViewById<TextView>(R.id.activateGuardServiceButton)
        val cancelGuardServiceButton = popupViewInner.findViewById<TextView>(R.id.cancelGuardServiceButton)
        popupWindowInner.isTouchable = true
        popupWindowInner.isFocusable = true
        cancelGuardServiceButton.setOnClickListener {
            popupWindowInner.dismiss()
        }
        var pin: String
        var login: String
        var password: String
//        if(krosBool){
//            pinServerActivationTextView.visibility = View.GONE
//            pinTextView.visibility = View.GONE
//        }
        popupWindowInner.showAtLocation(popupViewInner, Gravity.CENTER, 0, 0)
        activateGuardServiceButton.setOnClickListener {
            activateGuardServiceButton.isClickable = false
            /*pin = KROS_ONLINE_PIN.toString()
            if(!krosBool) {
                pin = pinServerActivationTextView.text.toString()
            }*/
            pin = pinServerActivationTextView.text.toString()
            login = loginActivationTextView.text.toString()
            password = passwordActivationTextView.text.toString()
            if (pin == "" || login == "" || password == "") {
                Toast.makeText(requireContext(), "Заполните все поля для подключения сервиса", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val message = JSONObject()
                .put("action", "edit")
                .put(
                    "result", JSONObject()
                        .put(
                            "service", JSONObject()
                                .put("serviceID", GUARD_SERVICE_ID)
                                .put("enabled", true)
                                .put("pin", pin)
                        )
                )
            WebSocketFactory.getInstance().licenceSocket.send(message.toString())
            PreloaderClass.instance.startPreloader(this.requireContext(), addGuardWindowConstraintLayout)
            var k = 0
            while (!userInfoIsFinished && k < 100) {
                Thread.sleep(100)
                Log.e("Checkpoint", "While")
                k++
            }
            if (!userInfoIsFinished) {
                runOnUiThread {
                    Log.e("Checkpoint", "failure")
                    Toast.makeText(requireContext(), "Не удалось подключить сервис", Toast.LENGTH_SHORT).show()
                    PreloaderClass.instance.stopPreloader(addGuardWindowConstraintLayout)
                    activateGuardServiceButton.isClickable = true
                }
                callback(false)
            }
            if (userInfoIsFinished) {
                Log.e("Checkpoint", "success")
                userInfoIsFinished = false
                for (i in WebSocketFactory.getInstance().licenseContract.services) {
                    if (i.serviceID == GUARD_SERVICE_ID && i.pin.toString() == pin) {
                        if(i.urls.isEmpty()){
                            Toast.makeText(requireContext(), "Array with urls is empty. Pin is ${i.pin}", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                        activate(login, password, pin, i, 0) {
                            if(it){
                                callback(true)
                                runOnUiThread {
                                    PreloaderClass.instance.stopPreloader(addGuardWindowConstraintLayout)
                                    activateGuardServiceButton.isClickable = true
                                    popupWindowInner.dismiss()
                                }
                            }
                        }
                        break
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    fun activate(login: String, password: String, pin: String, myClass: ServiceClass, index: Int, callback: ((Boolean) -> Unit)?){
        Log.e("Checkpoint", "activate")
        val headerString = encoded64(login, password)
        var url = ""
        try {
            url = myClass.urls[index]
        } catch (e: ArrayIndexOutOfBoundsException){
            callback!!(false)
            Toast.makeText(context, "Связь с сервером $pin не установлена", Toast.LENGTH_LONG).show()
            checkConnect = true
        }
        LKHttpManager().authorizationKROS(pin, url, headerString, requireContext()){
            if(it == ""){
                Log.e("Checkpoint", "activate Failure")
                var newIndex = index
                newIndex++
                activate(login, password, pin, myClass, newIndex, callback)
            }
            if(it != ""){
                Log.e("Checkpoint", "activate Success")
                val requestKROS = Request.Builder().url("ws://$url/websocket").build()
                val shared1 = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val tokenpush = shared1?.getString("tokenpush", "")
                val url2 = URL("http://$url/weblk/push?token=$tokenpush&status=true")
                val okHttpClient = OkHttpClient()
                val request = Request.Builder()
                    .header("Authorization", "Basic $headerString")
                    .url(url2)
                    .build()
                okHttpClient.newCall(request).execute()
                val listenerKROS = WebSocketListenerKROS(it, myClass.id)
                val client = OkHttpClient()
                client.newWebSocket(requestKROS, listenerKROS)
                client.dispatcher().executorService().shutdown()
                var n = 0
                while (!MainActivity.webSocketServiceOpenedFlag && n < 100) {
                    Thread.sleep(100)
                    n++
                }
                if (!MainActivity.webSocketServiceOpenedFlag) {
                    runOnUiThread {
                        Toast.makeText(requireContext(), "Соединение с сервером не установлено", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                if (MainActivity.webSocketServiceOpenedFlag) {
                    MainActivity.webSocketServiceOpenedFlag = false
                    val db = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put(DBHelperKROS.Entry.USER_LOGIN, login)
                        put(DBHelperKROS.Entry.USER_PASSWORD, password)
                        put(DBHelperKROS.Entry.SERVER_PIN, pin)
                        put(DBHelperKROS.Entry.SERVICE_ID, myClass.id)
                    }

                    db?.insert(DBHelperKROS.Entry.TABLE_NAME, null, values)

                    val messageq = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("objects", ""))
                    WebSocketFactory.getInstance().mapSockets[myClass.id]?.send(messageq.toString())
                    val message1 = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("devices", ""))
                    WebSocketFactory.getInstance().mapSockets[myClass.id]?.send(message1.toString())
                    val message2 = JSONObject()
                        .put("action", "get")
                        .put(
                            "result", JSONObject()
                                .put(
                                    "events", JSONObject()
                                        .put("limit", 20).put("object", -1).put("envelopeID", -1)
                                )
                        )
                    WebSocketFactory.getInstance().mapSockets[myClass.id]?.send(message2.toString())
                    val message4 = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("contract", ""))
                    WebSocketFactory.getInstance().mapSockets[myClass.id]?.send(message4.toString())

                    chosenService = GUARD_SERVICE_ID
                    if (callback != null) {
                        callback(true)
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    fun encoded64(login: String, password: String): String {
        val resultString = "$login:$password"
        val data = resultString.encodeToByteArray()
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }
}