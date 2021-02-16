package ru.elesta.jupiter_lkselfguard.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragment
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.DevicesFragment.DevicesFragment
import ru.elesta.jupiter_lkselfguard.EventsFragment.EventsFragment
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory


class LKActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var myHandler: Handler
    private lateinit var myRunnable: Runnable
    companion object{
        var mainSocketString = ""
        lateinit var headLabelValue: TextView
        var loadingOfObjectsFinished = false
        var barcodeFinished = false
        var loadingOfEventsFinished = false
        var webSocketConnectionAborted = false
    }

    private lateinit var homeButton: Button
    private lateinit var objectsButton: Button
    private lateinit var devicesButton: Button
    //private lateinit var eventsButton: Button КНОПКА СОБЫТИЙ САМООХРАНЫ
    private lateinit var accountButton: Button

    private var countOfBackPressed = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        val shared = getSharedPreferences("settings", Context.MODE_PRIVATE)
        var darkTheme = shared.getBoolean("dark", true)
        if(darkTheme){
            setTheme(R.style.DarkAppTheme)
        }
        else{
            setTheme(R.style.LightAppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lk)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
        headLabelValue = findViewById(R.id.headLabel)
        homeButton = findViewById(R.id.homeButton)
        devicesButton = findViewById(R.id.devicesButton)
        //eventsButton = findViewById(R.id.eventsButton) инициализация кнопки событий
        objectsButton = findViewById(R.id.objectsButton)
        accountButton = findViewById(R.id.accountButton)
        homeButton.setOnClickListener(this)
        devicesButton.setOnClickListener(this)
        objectsButton.setOnClickListener(this)
        //eventsButton.setOnClickListener(this) слушатель кнопки событий
        accountButton.setOnClickListener(this)


        if(darkTheme){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                homeButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_white, this.theme), null,null)
                devicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_devices_button_white, this.theme), null,null)
                //eventsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_events_button_white, this.theme), null,null) это для поздних версий андроида белая иконка
                objectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_objects_white_36dp, this.theme), null,null)
                accountButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_account_button_white, this.theme), null,null)
            } else {
                homeButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_white), null,null)
                devicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_devices_button_white), null,null)
                objectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_objects_white_36dp), null,null)
                //eventsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_events_button_white), null,null) это для ранних версий андроида белая иконка
                accountButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_account_button_white), null,null)
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                homeButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_black, this.theme), null,null)
                objectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_objects_black_36dp, this.theme), null,null)
                devicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_devices_button_black, this.theme), null,null)
                //eventsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_events_button_black, this.theme), null,null) это для поздних версий андроида черная иконка
                accountButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_account_button_black, this.theme), null,null)
            } else {
                homeButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_black), null,null)
                objectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_objects_black_36dp), null,null)
                devicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_devices_button_black), null,null)
                //eventsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_events_button_black), null,null) это для ранних версий андроида черная иконка
                accountButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_account_button_black), null,null)
            }
        }
        homeButton.alpha = 0.5F
        myHandler = Handler()
        myRunnable = Runnable {
            myHandler.postDelayed(myRunnable, 300)
            if (loadingOfObjectsFinished) {
                load()
                loadingOfObjectsFinished = false
                myHandler.removeCallbacks(myRunnable)
            }
        }
        myHandler.post(myRunnable)

        val message = JSONObject()
            .put("action","get")
            .put("result", JSONObject().put("objects",""))
        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message.toString())
        val message1 = JSONObject()
            .put("action","get")
            .put("result", JSONObject().put("devices",""))
        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
        val message2 = JSONObject()
            .put("action","get")
            .put("result", JSONObject()
                .put("events",JSONObject()
                    .put("limit", 20).put("object", -1).put("envelopeID", -1)))
        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message2.toString())

        val message4 = JSONObject()
            .put("action","get")
            .put("result", JSONObject().put("contract",""))
        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message4.toString())
        for (i in WebSocketFactory.getInstance().setOfDevices) {
            val message5 = JSONObject()
                .put("action", "get")
                .put("result", JSONObject().put("device", i.deviceID.toInt()))
            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message5.toString())
        }
    }



    private fun load(){
        defaultColors()
        val ft = supportFragmentManager.beginTransaction()
        if(WebSocketFactory.getInstance().setOfDevices.isEmpty()){
            devicesButton.alpha = 1F
            headLabelValue.text = "Приборы"
            val devicesFrag = DevicesFragment()
            ft.add(R.id.fragmentContainer, devicesFrag, "devices")
            ft.addToBackStack("devices")
            ft.commit()
        } else {
            objectsButton.alpha = 1F
            headLabelValue.text = "Объекты"
            val homeFrag = HomeFragment()
            ft.add(R.id.fragmentContainer, homeFrag, "objects")
            ft.addToBackStack("objects")
            ft.commit()
        }
    }

    override fun onClick(v: View) {
        defaultColors()
        val ft = supportFragmentManager.beginTransaction()
        when(v.id){
            R.id.homeButton -> {
                WebSocketFactory.getInstance().krosContract = null
                //WebSocketFactory.getInstance().setOfEvents.clear() РАСКОММЕНТИРОВАТЬ ПРИ ВОЗВРАТЕ СОБЫТИЙ
                val intent = Intent(this, AllObjectsAndServicesActivity::class.java)
                startActivity(intent)
            }
            R.id.objectsButton -> {
                chooseButton(objectsButton)
                headLabelValue.text = "Объекты"
                val homeFrag = HomeFragment()
                ft.replace(R.id.fragmentContainer, homeFrag, "objects")
                ft.addToBackStack("objects")
                ft.commit()
            }

            R.id.devicesButton -> {
                chooseButton(devicesButton)
                val devicesFrag = DevicesFragment()
                headLabelValue.text = "Приборы"
                for (i in WebSocketFactory.getInstance().setOfDevices) {
                    val message = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("device", i.deviceID.toInt()))
                    WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message.toString())
                }
                ft.replace(R.id.fragmentContainer, devicesFrag, "devices")
                ft.addToBackStack("devices")
                ft.commit()
            }

//            R.id.eventsButton -> {
//                chooseButton(eventsButton)
//                val eventsFrag = EventsFragment() обработчик событий и переход на экран событий самоохраны
//                headLabelValue.text = "События"
//                ft.replace(R.id.fragmentContainer, eventsFrag, "events")
//                ft.addToBackStack("events")
//                ft.commit()
//            }

            R.id.accountButton -> {
                chooseButton(accountButton)
                val accountFrag = AccountFragment()
                headLabelValue.text = "Учетная запись"
                ft.replace(R.id.fragmentContainer, accountFrag, "account")
                ft.addToBackStack("account")
                ft.commit()
            }
        }
    }

    override fun onBackPressed() {
        for(frag in supportFragmentManager.fragments){
            if (frag.isVisible && hasBackStack(frag)) {
                if (popFragment(frag))
                    return
            }
        }
        if (supportFragmentManager.backStackEntryCount > 1) {
            if(countOfBackPressed > 0){
                countOfBackPressed = 0
            }
            val mainSupFM = supportFragmentManager
            val checkFragment = mainSupFM.getBackStackEntryAt(supportFragmentManager.backStackEntryCount-2).name
            defaultColors()
            if (checkFragment == "objects") {
                homeButton.alpha = 1F
                headLabelValue.text = "Объекты"
            }
            if (checkFragment == "devices") {
                devicesButton.alpha = 1F
                headLabelValue.text = "Приборы"
            }
            if (checkFragment == "events") {
                //eventsButton.alpha = 1F РАСКОММЕНТИРОВАТЬ ПРИ ВОЗВРАЩЕНИИ СОБЫТИЙ
                headLabelValue.text = "События"
            }
            if (checkFragment == "account") {
                accountButton.alpha = 1F
                headLabelValue.text = "Учетная запись"
            }
        }
        else {
            countOfBackPressed += 1
            if(countOfBackPressed < 2){
                runOnUiThread {
                    Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()
                }
            }
            if(countOfBackPressed >= 2) {
                countOfBackPressed = 0
                //WebSocketFactory.getInstance().krosSocket.close(1000,"Разавторизован")
                WebSocketFactory.getInstance().krosContract = null
                WebSocketFactory.getInstance().setOfObjects.clear()
                WebSocketFactory.getInstance().setOfEvents.clear()
                WebSocketFactory.getInstance().setOfDevices.clear()
                WebSocketFactory.getInstance().setOfSections.clear()
                WebSocketFactory.getInstance().setOfEventsOne.clear()
                WebSocketFactory.getInstance().setOfResponsibles.clear()
                WebSocketFactory.getInstance().setOfDevicesOne.clear()
            }
        }
        super.onBackPressed()
    }

    private fun hasBackStack(fragment: Fragment): Boolean {
        val childFragmentManager: FragmentManager = fragment.childFragmentManager
        return childFragmentManager.backStackEntryCount > 0
    }

    private fun popFragment(fragment: Fragment): Boolean {
        val fragmentManager: FragmentManager = fragment.childFragmentManager
        for (childFragment in fragmentManager.fragments) {
            if (childFragment.isVisible) {
                return if (hasBackStack(childFragment)) {
                    popFragment(childFragment)
                } else {
                    fragmentManager.popBackStack()
                    true
                }
            }
        }
        return false
    }



    private fun defaultColors(){
        objectsButton.alpha = 0.5F
        devicesButton.alpha = 0.5F
        //eventsButton.alpha = 0.5F РАСКОММЕНТИРОВАТЬ ПРИ ВОЗВРАЩЕНИИ СОБЫТИЙ
        accountButton.alpha = 0.5F
    }

    private fun chooseButton(button: Button){
        button.alpha = 1F
    }

}
