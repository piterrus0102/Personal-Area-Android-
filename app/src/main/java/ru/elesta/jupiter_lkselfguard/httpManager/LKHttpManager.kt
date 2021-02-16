package ru.elesta.jupiter_lkselfguard.httpManager

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.ForgetPasswordActivity.Companion.rememberPasswordOk
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity.Companion.selfGuardTokenLisense
import ru.elesta.jupiter_lkselfguard.Activities.RegistrationActivity.Companion.registrationOk
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

class LKHttpManager() {

    private var headerString: String? = null
    var context: Context? = null
    var json: String? = null
    private var lkAddress: String? = null
    private var lkPort: String? = null
    var connectionError: Boolean? = null

    companion object {
        var lkAddressForRegistration = ""
        var lkPortForRegistration = ""
        var lkAddressForRememberPassword = ""
        var lkPortForRememberPassword = ""
    }

    val JSON = MediaType.parse("application/json; charset=utf-8")

    constructor(headerString: String, lkAddress: String, lkPort: String, context: Context) : this(){
        this.context = context
        this.headerString = headerString
        this.lkAddress = lkAddress
        this.lkPort = lkPort
    }

    constructor(json: String, context: Context): this(){
        this.json = json
        this.context = context
    }

    fun isNetworkAvailableAndConnected(): Boolean {
        val runtime = Runtime.getRuntime()
        try {

            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return (exitValue == 0)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    fun authorizationKROS(pin: String?, url: String, headerString: String, context: Context, callback: (String) -> Unit){
        try {
            val urlForKros = if(pin == null) {
                URL("http://$url/weblk/mobile/login/")
            } else {
                URL("http://$url/weblk/login/")
            }
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder()
                .url(urlForKros)
                .addHeader("Authorization", "Basic $headerString")
                .addHeader("token", "undefined")
                .build()
            okHttpClient.newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    if (!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    if (isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                if(pin == null){
                                    Toast.makeText(context, "Связь с сервером самоохраны не установлена", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    callback("")
                    return
                }

                override fun onResponse(call: Call?, response: Response) {
                    Log.e("response auth KROS", response.code().toString())
                    if (response.code() == 409) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Авторизация не выполнена\nНет данных", Toast.LENGTH_LONG).show()
                            }
                        }
                        callback("")
                    }
                    if (response.code() == 403) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Авторизация не выполнена\nНеправильный логин или пароль", Toast.LENGTH_LONG).show()
                            }
                        }
                        callback("")
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.v("JSON", json)
                        val jsonObj = JSONObject(json!!)
                        if (JSONObject(json).get("result").equals("error")) {
                            Log.v("ERROR", JSONObject(json).get("description").toString())
                            val erMessage = JSONObject(json).get("description").toString()
                            doAsync {
                                uiThread {
                                    Toast.makeText(context, erMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                            callback("")
                            return
                        }
                        if (!JSONObject(json).get("result").equals("error")) {
                            callback(response.header("token")!!)
                        }
                    }
                }
            })
        }catch (e: MalformedURLException){
            Log.e("URLERROR", e.message.toString())
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            callback("")
            return
        }
        catch (e: NullPointerException) {
            Log.v("AUTHORIZATION", "IsNull")
            e.printStackTrace()
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            callback("")
            return
        }
        catch (e: RuntimeException) {
            Log.v("AUTHORIZATION", "runtime!")
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            callback("")
            return
        }
    }



    fun authorization(callback: (Boolean) -> Unit){
        val url: URL
        try {
            url = URL("http://$lkAddress:$lkPort/login/")
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder().url(url).addHeader("Authorization", "Basic $headerString").addHeader("token", "undefined").build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    if (!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    if (isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Нет связи с сервером лицензирования", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    callback(true)
                    return
                }

                override fun onResponse(call: Call?, response: Response) {
                    Log.e("response auth LICENSE", response.code().toString())
                    if(response.code() == 401){
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Авторизация не выполнена\nНеправильный логин или пароль", Toast.LENGTH_LONG).show()
                            }
                        }
                        callback(true)
                    }
                    if(response.code() == 405){
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Авторизация не выполнена\n405 ошибка", Toast.LENGTH_LONG).show()
                            }
                        }
                        callback(true)
                    }
                    if (response.code() == 409) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Авторизация не выполнена\nНет данных", Toast.LENGTH_LONG).show()
                            }
                        }
                        callback(true)
                    }
                    if (response.code() == 403) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Авторизация не выполнена\nНеправильный логин или пароль", Toast.LENGTH_LONG).show()
                            }
                        }
                        callback(true)
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.v("JSON", json)
                        val jsonObj = JSONObject(json!!)
                        if (JSONObject(json).get("result").equals("error")) {
                            Log.v("ERROR", JSONObject(json).get("description").toString())
                            val erMessage = JSONObject(json).get("description").toString()
                            doAsync {
                                uiThread {
                                    Toast.makeText(context, erMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                            callback(true)
                            return
                        }
                        if (!JSONObject(json).get("result").equals("error")) {
                            if (jsonObj.getJSONObject("result").get("service").equals("selfGuard")) {
                                //selfGuardTokenKROS = jsonObj.getJSONObject("result").get("token").toString()
                                selfGuardTokenLisense = response.header("token")!!
                                callback(false)
                            } else {
                                doAsync {
                                    uiThread {
                                        Toast.makeText(context, "Неверный логин или пароль для данного сервиса", Toast.LENGTH_LONG).show()
                                    }
                                }
                                callback(true)
                                return
                            }
                            //waitingFlag = true
                        }
                    }
                }
            })
        }catch (e: MalformedURLException){
            Log.e("URLERROR", e.message.toString())
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            callback(true)
            return
        }
        catch (e: NullPointerException) {
            Log.v("AUTHORIZATION", "IsNull")
            e.printStackTrace()
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            callback(true)
            return
        }
        catch (e: RuntimeException) {
            Log.v("AUTHORIZATION", "runtime!")
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            callback(true)
            return
        }
    }

    fun forgetPassword(){
        try {
            var url = URL("http://jupiter8.ru:9700/user/forget")
            if(lkAddressForRememberPassword != "" && lkPortForRememberPassword != ""){
                url = URL("http://$lkAddressForRememberPassword:$lkPortForRememberPassword/user/forget")
            }
            Log.e("urlForForgetPassword", url.toString())
            val okHttpClient = OkHttpClient()
            val body = FormBody.create(JSON, json)
            val request: Request = Request.Builder().url(url).post(body).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.v("forgetRequest", "OnFailure")
                    if (!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Нет связи с сервером лицензирования", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    return
                }

                override fun onResponse(call: Call?, response: Response) {
                    Log.v("forgetRequest", response.code().toString())
                    if (response.code() == 409) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Операция не выполнена\nНет данных", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (response.code() == 403) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Операция не выполнена\nНеправильный логин или почта", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.v("JSON", json)
                        if (JSONObject(json).get("result").equals("error")) {
                            Log.v("ERROR", JSONObject(json).get("description").toString())
                            val erMessage = JSONObject(json).get("description").toString()
                            doAsync {
                                uiThread {
                                    Toast.makeText(context, erMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                            return
                        } else {
                            Log.v("rememberPassword", "OK")
                            rememberPasswordOk = true
                        }
                    }
                }
            })
        }catch (e: MalformedURLException){
            Log.e("URLERROR", e.message.toString())
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        catch (e: NullPointerException) {
            Log.v("AUTHORIZATION", "IsNull")
            e.printStackTrace()
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        catch (e: RuntimeException) {
            Log.v("AUTHORIZATION", "runtime!")
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
    }

    fun registration(){
        try {
            var url = URL("http://jupiter8.ru:9700/user/registration")
            if(lkAddressForRegistration != "" && lkPortForRegistration != ""){
                url = URL("http://$lkAddressForRegistration:$lkPortForRegistration/user/registration")
            }
            val okHttpClient = OkHttpClient()
            val body = FormBody.create(JSON, json)
            val request: Request = Request.Builder().url(url).post(body).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.v("registration", "OnFailure")
                    if (!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Нет связи с сервером лицензирования", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    return
                }

                override fun onResponse(call: Call?, response: Response) {
                    Log.v("registration", response.code().toString())
                    if (response.code() == 409) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Регистрация не выполнена\nНет данных", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (response.code() == 403) {
                        doAsync {
                            uiThread {
                                Toast.makeText(context, "Регистрация не выполнена\nНеправильный логин или пароль", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.v("JSON", json)
                        val jsonObj = JSONObject(json!!)
                        if (JSONObject(json).get("result").equals("error")) {
                            Log.v("ERROR", JSONObject(json).get("description").toString())
                            val erMessage = JSONObject(json).get("description").toString()
                            doAsync {
                                uiThread {
                                    Toast.makeText(context, erMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                            return
                        } else {
                            Log.v("registration", "OK")
                            registrationOk = true
                        }
                    }
                }
            })
        }catch (e: MalformedURLException){
            Log.e("URLERROR", e.message.toString())
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        catch (e: NullPointerException) {
            Log.v("AUTHORIZATION", "IsNull")
            e.printStackTrace()
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        catch (e: RuntimeException) {
            Log.v("AUTHORIZATION", "runtime!")
            doAsync {
                uiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
    }
}