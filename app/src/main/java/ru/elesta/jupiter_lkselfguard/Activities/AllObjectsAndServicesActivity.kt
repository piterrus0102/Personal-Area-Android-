package ru.elesta.jupiter_lkselfguard.Activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.elesta.jupiter_lkselfguard.main_fragments.AllObjectsFragment
import ru.elesta.jupiter_lkselfguard.main_fragments.AppSettingsFragment
import ru.elesta.jupiter_lkselfguard.main_fragments.ServicesFragment

class AllObjectsAndServicesActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var allObjectsButton: Button
    lateinit var servicesButton: Button
    lateinit var appSettingsButton: Button

    lateinit var allObjectsAndServicesConstraintLayout: ConstraintLayout

    lateinit var runnable: Runnable
    lateinit var handler: Handler

    lateinit var fragmentManager: FragmentManager

    companion object {
        var userInfoIsFinished = false
        val SELFGUARD_SERVICE_ID = "1"
        val GUARD_SERVICE_ID = "2"
        var themeWillChanged = false
        //val KROS_ONLINE_PIN = 777L //1L
    }


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
        setContentView(R.layout.activity_all_objects_and_services)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        allObjectsAndServicesConstraintLayout = findViewById(R.id.allObjectsAndServicesConstraintLayout)
        allObjectsButton = findViewById(R.id.allObjectsButton)
        servicesButton = findViewById(R.id.servicesButton)
        appSettingsButton = findViewById(R.id.appSettingsButton)
        allObjectsButton.setOnClickListener(this)
        servicesButton.setOnClickListener(this)
        appSettingsButton.setOnClickListener(this)

        if(darkTheme){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                allObjectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_white, this.theme), null,null)
                servicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_services_button_white, this.theme), null,null)
                appSettingsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_app_settings_white, this.theme), null,null)
            } else {
                allObjectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_white), null,null)
                servicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_services_button_white), null,null)
                appSettingsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_app_settings_white), null,null)
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                allObjectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_black, this.theme), null,null)
                servicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_services_button_black, this.theme), null,null)
                appSettingsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_app_settings_black, this.theme), null,null)
            } else {
                allObjectsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_home_button_black), null,null)
                servicesButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_services_button_black), null,null)
                appSettingsButton.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.ic_app_settings_black), null,null)
            }
        }

        fragmentManager = supportFragmentManager
    }

    override fun onResume() {
        super.onResume()
        if(themeWillChanged){
            themeWillChanged = false
            defaultColors()
            chooseButton(appSettingsButton)
            val appSettingsFrag = AppSettingsFragment()
            val childFt = fragmentManager.beginTransaction()
            childFt.replace(R.id.fragmentContainerServices, appSettingsFrag)
            childFt.commit()
            return
        }
        load()
    }

    private fun load(){
        defaultColors()
        val ft = fragmentManager.beginTransaction()
        if (WebSocketFactory.getInstance().licenseContract.services.isEmpty()) {
            servicesButton.alpha = 1F
            val servicesFrag = ServicesFragment()
            ft.add(R.id.fragmentContainerServices, servicesFrag)
            ft.addToBackStack("services")
            ft.commit()
        } else {
            allObjectsButton.alpha = 1F
            val allObjectsFrag = AllObjectsFragment()
            if(fragmentManager.backStackEntryCount > 0) {
                ft.replace(R.id.fragmentContainerServices, allObjectsFrag)
            } else {
                ft.add(R.id.fragmentContainerServices, allObjectsFrag)
            }
            ft.addToBackStack("allobjects")
            ft.commit()
        }
    }

    override fun onClick(v: View) {
        defaultColors()
        val ft = supportFragmentManager.beginTransaction()
        when(v.id){
            R.id.allObjectsButton -> {
                chooseButton(allObjectsButton)
                val allObjectsFrag = AllObjectsFragment()
                for(i in WebSocketFactory.getInstance().licenseContract.services) {
                    val message = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("objects", ""))
                    WebSocketFactory.getInstance().mapSockets[i.id]?.send(message.toString())
                }
                ft.replace(R.id.fragmentContainerServices, allObjectsFrag, "allobjects")
                ft.addToBackStack("allobjects")
                ft.commit()
            }
            R.id.servicesButton -> {
                chooseButton(servicesButton)
                val servicesFrag = ServicesFragment()
                ft.replace(R.id.fragmentContainerServices, servicesFrag, "services")
                ft.addToBackStack("services")
                ft.commit()
            }
            R.id.appSettingsButton -> {
                chooseButton(appSettingsButton)
                val appServicesFragment = AppSettingsFragment()
                ft.replace(R.id.fragmentContainerServices, appServicesFragment, "appsettings")
                ft.addToBackStack("appsettings")
                ft.commit()
            }
        }
    }

    private fun defaultColors(){
        allObjectsButton.alpha = 0.5F
        servicesButton.alpha = 0.5F
        appSettingsButton.alpha = 0.5F
    }
    private fun chooseButton(button: Button){
        button.alpha = 1F
    }
}
