package ru.elesta.jupiter_lkselfguard.Activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.BaseColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.yandex.mapkit.MapKitFactory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.db.update
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.*
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.dataClasses.SaveGuardDataClass
import ru.elesta.jupiter_lkselfguard.dataClasses.ServiceClass
import ru.elesta.jupiter_lkselfguard.dataClasses.UserClass
import ru.elesta.jupiter_lkselfguard.helpers.DBHelper
import ru.elesta.jupiter_lkselfguard.helpers.DBHelperKROS
import ru.elesta.jupiter_lkselfguard.helpers.PreloaderClass
import ru.elesta.jupiter_lkselfguard.helpers.UserAdapter
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager.Companion.lkAddressForRegistration
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager.Companion.lkPortForRegistration
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerLisense
import java.io.IOException
import java.lang.Thread.sleep
import java.net.URL


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        var login = ""
        var password = ""
        var headerString = ""
        var selfGuardTokenLisense = ""
        var webSocketOpenedFlag = false
        var webSocketServiceOpenedFlag = false
        var pushToken = ""

        var lkAddress = ""
        var lkPort = ""
    }

    var flag = false

    lateinit var handler: Handler
    lateinit var runnable: Runnable

    private lateinit var loginTextField: TextView
    private lateinit var passwordTextField: TextView
    private lateinit var myLKHttpManager: LKHttpManager
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var serverLKAddressTextField: TextView
    private lateinit var serverLKPortTextField: TextView
    private lateinit var authButton: Button
    private lateinit var forgetButton: Button
    lateinit var authLayout: ConstraintLayout

    private lateinit var rememberLoginPassword: CheckBox
    private lateinit var autoLogInCheckbox: CheckBox

    private lateinit var logo: ImageView
    private lateinit var ejLabel: TextView

    lateinit var dbHelper: DBHelper
    lateinit var dbKrosHelper: DBHelperKROS

    private var openCount = 0
    private var closeCount = 0

    lateinit var myHandler: Handler

    private var setOfUsers = ArrayList<UserClass>()

    private var setOfKrosUsers = ArrayList<SaveGuardDataClass>()

    private var containLoginPassword = false
    private var changedPassword = false

    lateinit var registrationButton: Button

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        val shared = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val darkTheme = shared.getBoolean("dark", true)
        if(darkTheme){
            setTheme(R.style.DarkAppTheme)
        }
        else{
            setTheme(R.style.LightAppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authLayout = findViewById(R.id.authLayout)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        MapKitFactory.setApiKey("de173ec4-d20a-4cb4-bcfb-c5703b0f36a4")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = "12345"
            val channelName = "notification"
            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d("MainActivity", "Key: $key Value: $value")
            }
        }
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val mainShared = getSharedPreferences("settings", Context.MODE_PRIVATE)
                val editor = mainShared.edit()
                editor.putString("tokenpush", pushToken)
                editor.apply()
                pushToken = task.result?.token!!

                // Log and toast
                Log.d("MainActivityTOKEN", pushToken)
                //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })

        val m = shared.getBoolean("autoLogin", false)
        val saveLkAddress = shared.getString("lkAddress", "")
        val saveLkPort = shared.getString("lkPort", "")
        val saveLogin = shared.getString("login", "")
        val savePassword = shared.getString("password", "")
        if(m){
            if(WebSocketFactory.getInstance().licenceSocket == null) {
                PreloaderClass.instance.startPreloader(this, authLayout)
                headerString = encoded64(saveLogin!!, savePassword!!)
                myLKHttpManager = LKHttpManager(headerString, saveLkAddress!!, saveLkPort!!, this)
                myLKHttpManager.authorization {
                    if (it) {
                        PreloaderClass.instance.stopPreloader(authLayout)
                        return@authorization
                    }
                    if (!it) {
                        val requestLicense: Request
                        requestLicense = Request.Builder().url("ws://$saveLkAddress:$saveLkPort/selfguard").build()
                        val client = OkHttpClient()
                        val listenerLisense = WebSocketListenerLisense()
                        client.newWebSocket(requestLicense, listenerLisense)
                        client.dispatcher().executorService().shutdown()
                        var j = 0
                        while (!webSocketOpenedFlag && j < 100) {
                            sleep(100)
                            j++
                        }
                        if (webSocketOpenedFlag) {
                            //val intent = Intent(this, AllObjectsAndServicesActivity::class.java)
                            //startActivity(intent)
                            val message = JSONObject()
                                .put("action","get")
                                .put("result", JSONObject().put("services",""))
                            WebSocketFactory.getInstance().licenceSocket.send(message.toString())

                            val message1 = JSONObject()
                                .put("action","get")
                                .put("result", JSONObject().put("userinfo",""))
                            WebSocketFactory.getInstance().licenceSocket.send(message1.toString())
                            Log.e("Connection", "StartActivityAllObj")
                            webSocketsCreating()
                        }
                        if (!webSocketOpenedFlag) {
                            runOnUiThread {
                                Toast.makeText(this, "Соединение с сервером не установлено", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }


        registrationButton = findViewById(R.id.registrationButton)
        registrationButton.setOnClickListener{
            if (serverLKAddressTextField.visibility == View.VISIBLE){
                if(serverLKAddressTextField.text.isEmpty() || serverLKPortTextField.text.isEmpty()){
                    Toast.makeText(this, "Заполните все поля в верхней части экрана", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                lkAddressForRegistration = serverLKAddressTextField.text.toString()
                lkPortForRegistration = serverLKPortTextField.text.toString()
            }
            if (serverLKAddressTextField.visibility == View.GONE) {
                lkAddressForRegistration = "jupiter8.ru"
                //lkAddressForRegistration = "192.168.1.147"
                lkPortForRegistration = "9700"
            }
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        forgetButton = findViewById(R.id.forgetButton)
        forgetButton.setOnClickListener {
            if (serverLKAddressTextField.visibility == View.VISIBLE){
                if(serverLKAddressTextField.text.isEmpty() || serverLKPortTextField.text.isEmpty()){
                    Toast.makeText(this, "Заполните все поля в верхней части экрана", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                LKHttpManager.lkAddressForRememberPassword = serverLKAddressTextField.text.toString()
                LKHttpManager.lkPortForRememberPassword = serverLKPortTextField.text.toString()
            }
            if (serverLKAddressTextField.visibility == View.GONE) {
                LKHttpManager.lkPortForRememberPassword = "jupiter8.ru"
                //LKHttpManager.lkAddressForRememberPassword = "192.168.1.147"
                LKHttpManager.lkPortForRememberPassword = "9700"
            }
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
        loginTextField = findViewById(R.id.authLoginTextField)
        passwordTextField = findViewById(R.id.authPasswordTextField)
        logo = findViewById(R.id.logo)
        authLayout.setOnClickListener {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()?.getWindowToken(), 0)
            logo.performClick()
            logo.requestFocus()
        }
        serverLKAddressTextField = findViewById(R.id.serverLKAddressTextField)
        serverLKPortTextField = findViewById(R.id.serverLKPortTextField)
        rememberLoginPassword = findViewById(R.id.rememberLoginPassword)
        autoLogInCheckbox = findViewById(R.id.autoLogInCheckbox)
        dbHelper = DBHelper(this)
        logo.setOnClickListener{
            openCount++
            if(openCount == 7){
                serverLKAddressTextField.visibility = View.VISIBLE
                serverLKPortTextField.visibility = View.VISIBLE
                openCount = 0
            }
        }
        ejLabel = findViewById(R.id.authLabel)
        ejLabel.setOnClickListener{
            closeCount++
            if(closeCount == 3){
                serverLKAddressTextField.visibility = View.GONE
                serverLKPortTextField.visibility = View.GONE
                closeCount = 0
            }
        }
        authButton = findViewById(R.id.authSubmitButton)
        authButton.setOnClickListener(this)

        /*val authLayout = findViewById<ConstraintLayout>(R.id.authLayout).viewTreeObserver
        authLayout.addOnGlobalLayoutListener {
            marginTopLogo = findViewById<ConstraintLayout>(R.id.authLayout).height
            val logo = findViewById<ImageView>(R.id.logo)
            val layoutparams = logo.layoutParams as ViewGroup.MarginLayoutParams
            layoutparams.topMargin = (0.13 * marginTopLogo).toInt()
            val authLabel = findViewById<TextView>(R.id.authLabel)
            val layoutparamsAuthLabel = authLabel.layoutParams as ViewGroup.MarginLayoutParams
            layoutparamsAuthLabel.topMargin = (0.019 * marginTopLogo).toInt()
            val authLoginTextField = findViewById<TextView>(R.id.authLoginTextField)
            val layoutparamsAuthLoginTextField = authLoginTextField.layoutParams as ViewGroup.MarginLayoutParams
            layoutparamsAuthLoginTextField.topMargin = (0.09 * marginTopLogo).toInt()
            val authPasswordTextField = findViewById<TextView>(R.id.authPasswordTextField)
            val layoutparamsAuthPasswordTextField = authPasswordTextField.layoutParams as ViewGroup.MarginLayoutParams
            layoutparamsAuthPasswordTextField.topMargin = (0.01 * marginTopLogo).toInt()
            val authSubmitButton = findViewById<Button>(R.id.authSubmitButton)
            val layoutparamsAuthSubmitButton = authSubmitButton.layoutParams as ViewGroup.MarginLayoutParams
            layoutparamsAuthSubmitButton.topMargin = (0.012 * marginTopLogo).toInt()
            val authCopyrightLabel = findViewById<TextView>(R.id.authCopyrightLabel)
            val layoutparamsAuthCopyrightLabel = authCopyrightLabel.layoutParams as ViewGroup.MarginLayoutParams
            layoutparamsAuthCopyrightLabel.bottomMargin = (0.034 * marginTopLogo).toInt()
        }*/

        mRecyclerView = findViewById(R.id.myRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutParams.height = mRecyclerView.childCount * 20
        loginTextField.clearFocus()
        loginTextField.onFocusChangeListener = myFocus()
        loginTextField.addTextChangedListener(MyWatcher())

        if(darkTheme){
            loginTextField.setBackgroundResource(R.drawable.roundcornerstextviewloginpasswordscreen)
            passwordTextField.setBackgroundResource(R.drawable.roundcornerstextviewloginpasswordscreen)
        }
        else{
            loginTextField.setBackgroundResource(R.drawable.roundcornerstextviewloginpasswordscreenblack)
            passwordTextField.setBackgroundResource(R.drawable.roundcornerstextviewloginpasswordscreenblack)
        }
    }

    inner class MyWatcher: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if(s?.length == 0){
                mRecyclerView.visibility = View.VISIBLE
            }
            else{
                mRecyclerView.visibility = View.GONE
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if(s == ""){
                mRecyclerView.visibility = View.VISIBLE
            }
            else{
                mRecyclerView.visibility = View.GONE
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    inner class myFocus: View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if(hasFocus){
                mRecyclerView.visibility = View.VISIBLE
            }
            else{
                mRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setOfUsers.clear()
       // if (dbHelper != null) {
            val db = dbHelper.readableDatabase
            val c = db.query("users", null, null, null, null, null, null)

            if (c.moveToFirst()) {
                val idColIndex = c.getColumnIndex(BaseColumns._ID)
                val loginColIndex = c.getColumnIndex("userLogin")
                val passwordColIndex = c.getColumnIndex("userPassword")

                do {
                    Log.d(
                        "DB",
                        "ID = " + c.getInt(idColIndex) +
                                ", login = " + c.getString(loginColIndex) +
                                ", password = " + c.getString(passwordColIndex)
                    )

                    setOfUsers.add(UserClass(c.getInt(idColIndex), c.getString(loginColIndex), c.getString(passwordColIndex)))
                } while (c.moveToNext())
            } else
                Log.d("DB", "0 rows")
            c.close()
      //  }

        for(i in setOfUsers){
            Log.v("SetOfUsers", "${i.id} login: ${i.login} password: ${i.password}")
        }
        val adapter = UserAdapter(this, setOfUsers, loginTextField, passwordTextField, this)
        mRecyclerView.adapter = adapter
    }

    @ExperimentalStdlibApi
    override fun onClick(v: View?) {
        PreloaderClass.instance.startPreloader(this, authLayout)
        lkAddress = serverLKAddressTextField.text.toString()
        lkPort = serverLKPortTextField.text.toString()
        login = loginTextField.text.toString()
        password = passwordTextField.text.toString()
        /*lkAddress = "192.168.1.127"
        lkPort = "10431"
        login = "Oxrannik1"
        password = "Oxrannik1"*/
        //login = "ruslan13"
        //password = "Ruslan13"
        //login = "Vadim_1"
        //password = "Vadim_1"
        if(lkAddress == "" && lkPort == ""){
            //lkAddress = "192.168.1.147"
            lkAddress = "jupiter8.ru"
            lkPort = "9700"
        }
        if(login.equals("") || password.equals("")){
            PreloaderClass.instance.stopPreloader(authLayout)
            Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
            return
        }
        headerString = encoded64(login, password)
        myLKHttpManager = LKHttpManager(headerString, lkAddress, lkPort,this)
       // authButton.isEnabled = true
        myLKHttpManager.connectionError = null

        myLKHttpManager.authorization {
            if (it) {
                runOnUiThread {
                    PreloaderClass.instance.stopPreloader(authLayout)
                }
                return@authorization
            }
            if (!it) {
                Log.e("Connection", "MainActivityAuthHttp")
                val requestLicense = Request.Builder().url("ws://$lkAddress:$lkPort/selfguard").build()
                val client = OkHttpClient()
                val listenerLisense = WebSocketListenerLisense()
                client.newWebSocket(requestLicense, listenerLisense)
                client.dispatcher().executorService().shutdown()
                var j = 0
                Log.e("Connection", "WebSocketLicStart")
                while (!webSocketOpenedFlag && j < 100) {
                    sleep(100)
                    j++
                }
                if (!webSocketOpenedFlag) {
                    runOnUiThread {
                        PreloaderClass.instance.stopPreloader(authLayout)
                        Toast.makeText(this, "Соединение с сервером не установлено", Toast.LENGTH_SHORT).show()
                    }
                }
                if (webSocketOpenedFlag) {
                    webSocketOpenedFlag = false
                    Log.e("Connection", "WebSocketLicEnd")
                    if (rememberLoginPassword.isChecked) {
                        val db1 = dbHelper.readableDatabase
                        val c = db1.query("users", null, null, null, null, null, null)
                        if (c.moveToFirst()) {
                            val userLoginColIndex = c.getColumnIndex("userLogin")
                            val userPasswordColIndex = c.getColumnIndex("userPassword")
                            do {
                                if (c.getString(userLoginColIndex) == login && c.getString(userPasswordColIndex) != password) {
                                    changedPassword = true
                                }
                                if (c.getString(userLoginColIndex) == login && c.getString(userPasswordColIndex) == password) {
                                    containLoginPassword = true
                                }
                            } while (c.moveToNext())
                        } else
                            Log.d("DB", "0 rows")
                        c.close()
                        if (changedPassword) {
                            val db = dbHelper.writableDatabase
                            val values = ContentValues().apply {
                                put(DBHelper.Entry.USER_LOGIN, login)
                                put(DBHelper.Entry.USER_PASSWORD, password)
                            }

                            db?.update(DBHelper.Entry.TABLE_NAME, values, "userLogin = ?", arrayOf(login))
                            changedPassword = false
                            containLoginPassword = true
                        }
                        if (!containLoginPassword) {
                            val db = dbHelper.writableDatabase
                            val values = ContentValues().apply {
                                put(DBHelper.Entry.USER_LOGIN, login)
                                put(DBHelper.Entry.USER_PASSWORD, password)
                            }

                            db?.insert(DBHelper.Entry.TABLE_NAME, null, values)
                        }
                        containLoginPassword = false
                    }
                    var autoLogin = false
                    if (autoLogInCheckbox.isChecked) {
                        autoLogin = true
                    }
                    val shared1 = getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor1 = shared1.edit()
                    editor1.putString("lkAddress", lkAddress)
                    editor1.putString("lkPort", lkPort)
                    editor1.putString("login", login)
                    editor1.putString("password", password)
                    editor1.putBoolean("autoLogin", autoLogin)
                    editor1.apply()

                    val message = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject().put("services",""))
                    WebSocketFactory.getInstance().licenceSocket.send(message.toString())

                    val message1 = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject().put("userinfo",""))
                    WebSocketFactory.getInstance().licenceSocket.send(message1.toString())
                    Log.e("Connection", "StartActivityAllObj")
                    webSocketsCreating()
//                    val intent = Intent(this, AllObjectsAndServicesActivity::class.java)
//                    startActivity(intent)
                }
            }
        }
    }

    @ExperimentalStdlibApi
    fun webSocketsCreating(){
        dbKrosHelper = DBHelperKROS(this)
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


        var j = 0
        while (!AllObjectsAndServicesActivity.userInfoIsFinished && j < 100) {
            sleep(100)
            j++
        }
        if(AllObjectsAndServicesActivity.userInfoIsFinished) {
            AllObjectsAndServicesActivity.userInfoIsFinished = false


            val myObservable = Observable.create(ObservableOnSubscribe<ServiceClass> {
                for(i in WebSocketFactory.getInstance().licenseContract.services){
                    Log.e("SERVICES", WebSocketFactory.getInstance().licenseContract.services.size.toString())
                    it.onNext(i)
                    while(!flag){
                        Log.e("WhileFlag", "into cycle")
                    }
                    flag = false
                }
                Log.e("WhileFlag", "Complete")
                it.onComplete()
            })

            myObservable.subscribe(mySubscriber)
        }
    }

    @ExperimentalStdlibApi
    val mySubscriber = object : Observer<ServiceClass> {

        override fun onNext(t: ServiceClass) {
            if (t.serviceID == AllObjectsAndServicesActivity.SELFGUARD_SERVICE_ID) {
                val mainPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
                val login = mainPref.getString("login", "")!!
                val password = mainPref.getString("password", "")!!
                val headerString = encoded64(login, password)
                val url = t.urls[0]
                askAuth(null, url, headerString, t.id)
            }
            if (t.serviceID == AllObjectsAndServicesActivity.GUARD_SERVICE_ID) {
                for(i in 0 until setOfKrosUsers.size){
                    Log.e("ID", t.id)
                    Log.e("ID", setOfKrosUsers[i].socketNumber)
                    if(t.id == setOfKrosUsers[i].socketNumber){
                        val headerString = encoded64(setOfKrosUsers[i].login, setOfKrosUsers[i].password)
                        Observable.fromIterable(t.urls)
                            .subscribe({ url ->
                                askAuth(t.pin.toString(), url, headerString, t.id)
                            }, {

                            })
                        break
                    }
                    if(t.id != setOfKrosUsers[i].socketNumber && i == setOfKrosUsers.size - 1){
                        flag = true
                    }
                }
                if(setOfKrosUsers.isEmpty()){
                    flag = true
                }
            }
        }

        override fun onError(t: Throwable?) {}

        override fun onComplete() {
            runOnUiThread {
                PreloaderClass.instance.stopPreloader(authLayout)
                val intent = Intent(this@MainActivity, AllObjectsAndServicesActivity::class.java)
                startActivity(intent)
            }
        }
        override fun onSubscribe(d: Disposable?) {}

    }

    private fun askAuth(pin: String?, urlForConnect: String, headerString: String, count: String){
        LKHttpManager().authorizationKROS(pin, urlForConnect, headerString, this){ token ->
            if (token == ""){
                flag = true
            }
            if(token != "") {
                val requestKROS = Request.Builder().url("ws://$urlForConnect/websocket").build()
                val shared1 = getSharedPreferences("settings", Context.MODE_PRIVATE)
                val push = shared1?.getBoolean("push", true)

                val url = URL("http://$urlForConnect/weblk/push?token=$pushToken&status=$push&source=EG")
                val okHttpClient = OkHttpClient()
                val request = Request.Builder()
                    .header("Authorization", "Basic $headerString")
                    .url(url)
                    .build()
                Log.e("URL", urlForConnect)
                Log.e("HeaderString", headerString)
                okHttpClient.newCall(request).enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("String", "$pin $urlForConnect is Failure")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.e("String", "$pin $urlForConnect is onResponse with code ${response.code().toString()}")
                    }

                })

                val listenerKROS = WebSocketListenerKROS(token, count)
                val client = OkHttpClient()
                client.newWebSocket(requestKROS, listenerKROS)
                client.dispatcher().executorService().shutdown()
                var a = 0
                while (!MainActivity.webSocketServiceOpenedFlag && a < 100) {
                    sleep(100)
                    a++
                }
                if (!MainActivity.webSocketServiceOpenedFlag) {
                    runOnUiThread {
                        Toast.makeText(this, "Соединение с сервером не установлено", Toast.LENGTH_SHORT).show()
                    }
                }
                if (MainActivity.webSocketServiceOpenedFlag) {
                    MainActivity.webSocketServiceOpenedFlag = false

                    val message = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("objects", ""))
                    WebSocketFactory.getInstance().mapSockets[count]?.send(message.toString())
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
                    val message4 = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("contract", ""))
                    WebSocketFactory.getInstance().mapSockets[count]?.send(message4.toString())
                    var j = 0
                    while (!LKActivity.loadingOfObjectsFinished && j < 100) {
                        sleep(100)
                        j++
                    }
                    if(LKActivity.loadingOfObjectsFinished){
                        LKActivity.loadingOfObjectsFinished = false
                        flag = true
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    fun encoded64 (login: String, password: String): String {
        val resultString = "$login:$password"
        val data = resultString.encodeToByteArray()
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }
}
