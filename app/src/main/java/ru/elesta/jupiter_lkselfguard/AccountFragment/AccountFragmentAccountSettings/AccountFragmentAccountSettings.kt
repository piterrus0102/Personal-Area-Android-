package ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import okhttp3.*
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.io.IOException
import java.net.URL

class AccountFragmentAccountSettings: Fragment(), View.OnClickListener {


    lateinit var myView: View

    lateinit var accountSettingsTable: TableLayout
    lateinit var saveAccountSettings: Button
    //lateinit var exitFromAccountButton: Button

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val shared = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val v = inflater.inflate(R.layout.account_fragment_account_settings, null)
        myView = v
        accountSettingsTable = v.findViewById(R.id.accountSettingsTable)
//        exitFromAccountButton = v.findViewById(R.id.exitFromAccountButton)
//        exitFromAccountButton.setOnClickListener(this)
        saveAccountSettings = v.findViewById(R.id.saveAccountSettings)
        saveAccountSettings.setOnClickListener{
            val tempEmail = myView.findViewById<TextView>(R.id.emailText).text.toString()
            Log.v("QWE", tempEmail)
            val message1 = JSONObject()
                                        .put("action","edit")
                                        .put("result", JSONObject().put("userinfo",JSONObject()
                                                                                                          .put("name", myView.findViewById<TextView>(R.id.personNameText).text.toString())
                                                                                                          .put("surname", myView.findViewById<TextView>(R.id.personNameText).text.toString())
                                                                                                          .put("patronymic", myView.findViewById<TextView>(R.id.personNameText).text.toString())
                                                                                                          .put("phone", myView.findViewById<TextView>(R.id.phoneText).text.toString())
                                                                                                          .put("email", tempEmail)))
            Log.e("QQQ", message1.toString())
            WebSocketFactory.getInstance().licenceSocket.send(message1.toString())
            Log.e("LicenceSocket", WebSocketFactory.getInstance().licenceSocket.toString())
            val message3 = JSONObject()
                .put("action","get")
                .put("result", JSONObject().put("contract",""))
            WebSocketFactory.getInstance().licenceSocket.send(message3.toString())
            //activity?.findViewById<Button>(R.id.accountButton)?.performClick()
        }
        getContract()



        return v
    }

    fun getContract(){
        myView.findViewById<TextView>(R.id.personNameText).text = WebSocketFactory.getInstance().licenseContract.name
        myView.findViewById<TextView>(R.id.emailText).text = WebSocketFactory.getInstance().licenseContract.email
        myView.findViewById<TextView>(R.id.phoneText).text = WebSocketFactory.getInstance().licenseContract.phone
        if(WebSocketFactory.getInstance().licenseContract.name == ""){
            myView.findViewById<TextView>(R.id.personNameText).hint = "Нет данных"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accountSettingsTable.removeAllViewsInLayout()
    }

    override fun onClick(v: View) {
        when(v.id){
//            R.id.exitFromAccountButton -> {
//                WebSocketFactory.getInstance().krosContract = null
//                WebSocketFactory.getInstance().setOfEvents.clear()
//                val intent = Intent(requireContext(), AllObjectsAndServicesActivity::class.java)
//                startActivity(intent)
//            }
        }
    }

}