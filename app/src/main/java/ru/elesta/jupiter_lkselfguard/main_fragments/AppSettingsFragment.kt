package ru.elesta.jupiter_lkselfguard.main_fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.SaveGuardDataClass
import ru.elesta.jupiter_lkselfguard.dataClasses.ServiceClass
import ru.elesta.jupiter_lkselfguard.dataClasses.UserClass
import ru.elesta.jupiter_lkselfguard.helpers.DBHelper
import ru.elesta.jupiter_lkselfguard.helpers.DBHelperKROS
import ru.elesta.jupiter_lkselfguard.helpers.PreloaderClass
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS
import java.io.IOException
import java.lang.StringBuilder
import java.net.URL
import java.security.NoSuchAlgorithmException

class AppSettingsFragment: Fragment(), View.OnClickListener {

    lateinit var switchColorTheme: SwitchCompat
    lateinit var switchPushNotifications: SwitchCompat
    lateinit var colorThemeTextView: TextView

    lateinit var oldPasswordTextField: TextView
    lateinit var newPasswordTextField: TextView

    lateinit var changeOldNewPasswordButton: Button

    lateinit var exitButton: Button

    private var setOfKrosUsers = ArrayList<SaveGuardDataClass>()

    private var flag = false

    private var pushFlag = true

    private lateinit var myHandler: Handler
    private lateinit var myRunnable: Runnable

    companion object {
        val instance = AppSettingsFragment()
        var changePasswordFlag = false
    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("ServicesFragment", "onCreateView")
        val shared = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        var darkTheme = shared.getBoolean("dark", true)
        pushFlag = shared.getBoolean("push", true)
        if(darkTheme){
            requireContext().setTheme(R.style.DarkAppTheme)
        }
        else{
            requireContext().setTheme(R.style.LightAppTheme)
        }

        val v = inflater.inflate(R.layout.app_settings_fragment, null)
        oldPasswordTextField = v.findViewById(R.id.oldPasswordTextField)
        newPasswordTextField = v.findViewById(R.id.newPasswordTextField2)
        changeOldNewPasswordButton = v.findViewById(R.id.changeOldNewPassword)
        exitButton = v.findViewById(R.id.exitButton)
        exitButton.setOnClickListener(this)
        changeOldNewPasswordButton.setOnClickListener(this)
        switchColorTheme = v.findViewById(R.id.switchColorTheme)
        switchPushNotifications = v.findViewById(R.id.switchPushNotifications)
        colorThemeTextView = v.findViewById(R.id.switchColorThemeTextView)
        colorThemeTextView.text = if (darkTheme) "Темная тема" else "Светлая тема"
        switchColorTheme.isChecked = darkTheme
        switchPushNotifications.isChecked = shared.getBoolean("push", true)

        switchColorTheme.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                requireContext().setTheme(R.style.LightAppTheme)
                darkTheme = true
                val shared1 = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val editor1 = shared1.edit()
                editor1.putBoolean("dark", darkTheme)
                editor1.apply()
            }
            else{
                requireContext().setTheme(R.style.DarkAppTheme)
                darkTheme = false
                val shared2 = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val editor2 = shared2.edit()
                editor2.putBoolean("dark", darkTheme)
                editor2.apply()
            }
            AllObjectsAndServicesActivity.themeWillChanged = true
            startActivity(Intent(requireContext(), AllObjectsAndServicesActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        val dbKrosHelper = DBHelperKROS(requireContext())
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

                setOfKrosUsers.add(
                    SaveGuardDataClass(c.getInt(idColIndex),
                    c.getString(serverPinColIndex).toString(),
                    c.getString(userLoginColIndex),
                    c.getString(userPasswordColIndex),
                    c.getString(serviceIDColIndex))
                )
            } while (c.moveToNext())
        } else
            Log.d("DB", "0 rows")
        c.close()

        switchPushNotifications.setOnCheckedChangeListener{ buttonView, isChecked ->
            pushFlag = isChecked
            buttonView.isChecked = isChecked
            changeNotifications()
        }

        return v
    }

    @ExperimentalStdlibApi
    fun changeNotifications(){
        if(WebSocketFactory.getInstance().licenseContract.services.isEmpty()){
            Toast.makeText(requireContext(), "У вас нет подключенных сервисов", Toast.LENGTH_SHORT).show()
            return
        }

        val myObservable = Observable.create(ObservableOnSubscribe<ServiceClass> {
            for(i in WebSocketFactory.getInstance().licenseContract.services){
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

    @ExperimentalStdlibApi
    val mySubscriber = object : Observer<ServiceClass> {


        override fun onNext(t: ServiceClass) {
            if (t.serviceID == AllObjectsAndServicesActivity.SELFGUARD_SERVICE_ID) {
                val mainPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val login = mainPref.getString("login", "")!!
                val password = mainPref.getString("password", "")!!
                val headerString = encoded64(login, password)
                val url = t.urls[0]
                askAuth(null, url, headerString, pushFlag)
            }
            if (t.serviceID == AllObjectsAndServicesActivity.GUARD_SERVICE_ID) {
                for(i in 0 until setOfKrosUsers.size){
                    Log.e("ID", t.id)
                    Log.e("ID", setOfKrosUsers[i].socketNumber)
                    if(t.id == setOfKrosUsers[i].socketNumber){
                        val headerString = encoded64(setOfKrosUsers[i].login, setOfKrosUsers[i].password)
                        Observable.fromIterable(t.urls)
                            .subscribe({ url ->
                                askAuth(t.pin.toString(), url, headerString, pushFlag)
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
            Log.e("Subscriber", "Push notifications are $pushFlag")
            val shared2 = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor2 = shared2.edit()
            editor2.putBoolean("push", pushFlag)
            editor2.apply()
        }
        override fun onSubscribe(d: Disposable?) {}

    }

    private fun askAuth(pin: String?, urlForConnect: String, headerString: String, pushFrag: Boolean){
        val url = URL("http://$urlForConnect/weblk/push?token=${MainActivity.pushToken}&status=$pushFrag&source=EG")
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .header("Authorization", "Basic $headerString")
            .url(url)
            .build()
        Log.e("URL", urlForConnect)
        Log.e("HeaderString", headerString)
        okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("String", "$pin $urlForConnect push enabled/disabled is Failure")
                flag = true
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("String", "$pin $urlForConnect push enabled/disabled is onResponse with code ${response.code()}")
                flag = true
            }

        })
    }

    @ExperimentalStdlibApi
    fun encoded64 (login: String, password: String): String {
        val resultString = "$login:$password"
        val data = resultString.encodeToByteArray()
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }


    @ExperimentalStdlibApi
    override fun onClick(v: View) {
        when(v.id){
            R.id.exitButton -> {
                val sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val sharedEdit = sharedPref.edit()
                for (i in WebSocketFactory.getInstance().licenseContract.services){
                    for (j in i.urls){
                        var headerString = ""
                        if(i.serviceID == AllObjectsAndServicesActivity.SELFGUARD_SERVICE_ID){
                            val login = sharedPref.getString("login", "")!!
                            val password = sharedPref.getString("password", "")!!
                            headerString = encoded64(login, password)
                        }
                        if(i.serviceID == AllObjectsAndServicesActivity.GUARD_SERVICE_ID){
                            for (k in setOfKrosUsers){
                                if(i.id == k.socketNumber){
                                    headerString = encoded64(k.login, k.password)
                                    break
                                }
                            }
                        }
                        val r = sharedPref.getString("tokenpush", "")!!
                        val url = URL("http://$j/weblk/push?token=$r&status=false&source=EG")
                        val okHttpClient = OkHttpClient()
                        val request: Request = Request.Builder().url(url).addHeader("Authorization", "Basic $headerString").addHeader("token", "undefined").build()
                        okHttpClient.newCall(request).enqueue(object: Callback{
                            override fun onFailure(call: Call, e: IOException) {
                                //
                            }

                            override fun onResponse(call: Call, response: Response) {
                                //
                            }

                        })
                    }
                }
                sharedEdit.putBoolean("autoLogin", false)
                sharedEdit.apply()
                WebSocketFactory.getInstance().licenceSocket = null
                WebSocketFactory.getInstance().mapSockets.clear()
                WebSocketFactory.getInstance().licenseContract = null
                WebSocketFactory.getInstance().setOfObjects.clear()
                WebSocketFactory.getInstance().commonSetOfObjects.clear()
                WebSocketFactory.getInstance().setOfEvents.clear()
                WebSocketFactory.getInstance().setOfDevices.clear()
                WebSocketFactory.getInstance().commonSetOfDevices.clear()
                WebSocketFactory.getInstance().setOfSections.clear()
                WebSocketFactory.getInstance().setOfEventsOne.clear()
                WebSocketFactory.getInstance().setOfResponsibles.clear()
                WebSocketFactory.getInstance().setOfDevicesOne.clear()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            }

            R.id.changeOldNewPassword -> {
                if(oldPasswordTextField.text.isEmpty() || newPasswordTextField.text.isEmpty()){
                    Toast.makeText(requireContext(), "Заполните все поля для смены пароля", Toast.LENGTH_SHORT).show()
                    return
                }
                val oldPassword = convertPassword(oldPasswordTextField.text.toString())
                val newPassword = newPasswordTextField.text.toString()
                val message = JSONObject()
                    .put("action","edit")
                    .put("result", JSONObject().put("password", JSONObject().put("newpassword",newPassword).put("oldpassword", oldPassword)))
                WebSocketFactory.getInstance().licenceSocket.send(message.toString())
                myHandler = Handler()
                myRunnable = Runnable {
                    Log.e("myRunnable", "Run")
                    if(changePasswordFlag){
                        changePasswordFlag = false
                        changePassword(newPassword)
                        runOnUiThread {
                            Toast.makeText(requireContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                        }
                        myHandler.removeCallbacks(myRunnable)
                        Log.e("myRunnable", "Stop")
                    } else {
                        myHandler.postDelayed(myRunnable, 100)
                    }
                }
                myHandler.post(myRunnable)
                Log.e("myRunnable", "Start")

            }
        }
    }

    fun changePassword(newPassword: String){
        val dbHelper = DBHelper(requireContext())

        val db1 = dbHelper.readableDatabase
        val c = db1.query("users", null, null, null, null, null, null)
        if (c.moveToFirst()) {
            val userLoginColIndex = c.getColumnIndex("userLogin")
            val userPasswordColIndex = c.getColumnIndex("userPassword")
            do {
                if (c.getString(userLoginColIndex) == MainActivity.login) {
                    val db = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put(DBHelper.Entry.USER_LOGIN, MainActivity.login)
                        put(DBHelper.Entry.USER_PASSWORD, newPassword)
                    }

                    db?.update(DBHelper.Entry.TABLE_NAME, values, "userLogin = ?", arrayOf(MainActivity.login))
                    break
                }
            } while (c.moveToNext())
        } else
            Log.d("DB", "0 rows")
        c.close()
    }

    fun convertPassword(password: String): String{
        try {
            val digest = java.security.MessageDigest.getInstance("MD5")
            digest.update(password.toByteArray())
            val messageDigest = digest.digest()

            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest){
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while(h.length < 2){
                    h = "0" + h
                }
                hexString.append(h)
            }
            return hexString.toString()
        } catch(e: NoSuchAlgorithmException){
            e.printStackTrace()
        }
        return ""
    }
}