package ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.mainSocketString
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.changeFlagObject
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.tempObject
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.ObjectClass
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.lang.Thread.sleep

class HomeFragmentInfo: Fragment(), View.OnClickListener {


    lateinit var homeFragmentInfoView: View
    lateinit var btnNameEdit: Button
    lateinit var btnCustomNameEdit: Button
    lateinit var btnCountryEdit: Button
    lateinit var btnCityEdit: Button
    lateinit var btnStreetEdit: Button
    lateinit var btnHouseEdit: Button
    lateinit var btnBuildingEdit: Button
    lateinit var btnFlatEdit: Button

    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    lateinit var label: TextView
    lateinit var text: TextView
    lateinit var button: Button

    lateinit var removeObjectButton: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v("HomeFragmentInfo", "onCreateView")
        Log.v("---", "-----------------------------------------")
        val v = inflater.inflate(R.layout.home_fragment_info, null)
        homeFragmentInfoView = v
        btnNameEdit = v.findViewById(R.id.editName)
        btnCustomNameEdit = v.findViewById(R.id.editCustomName)
        btnCountryEdit = v.findViewById(R.id.editCountry)
        btnCityEdit = v.findViewById(R.id.editCity)
        btnStreetEdit = v.findViewById(R.id.editStreet)
        btnHouseEdit = v.findViewById(R.id.editHouse)
        btnBuildingEdit = v.findViewById(R.id.editBuilding)
        btnFlatEdit = v.findViewById(R.id.editFlat)
        btnNameEdit.setOnClickListener(this)
        btnCustomNameEdit.setOnClickListener(this)
        btnCountryEdit.setOnClickListener(this)
        btnCityEdit.setOnClickListener(this)
        btnStreetEdit.setOnClickListener(this)
        btnHouseEdit.setOnClickListener(this)
        btnBuildingEdit.setOnClickListener(this)
        btnFlatEdit.setOnClickListener(this)
        label = v.findViewById(R.id.editFieldLabel)
        text = v.findViewById(R.id.editFieldText)
        button = v.findViewById(R.id.saveEditInfo)
        button.setOnClickListener(this)
        removeObjectButton = v.findViewById(R.id.removeObject)
        removeObjectButton.setOnClickListener(this)
        getObject(tempObject!!)
        startRunnable()
        return v
    }

    fun startRunnable(){
        myHandler = Handler()
        myRunnable = Runnable{
            myHandler.postDelayed(myRunnable,300)
            if(changeFlagObject == true){
                changeFlagObject = false
                Log.v("VSVS","VSE")
                getObject(tempObject!!)
            }
        }
        myHandler.post(myRunnable)
    }

    fun getObject(tempObject: ObjectClass){
        if(!WebSocketFactory.getInstance().setOfObjects.isEmpty()) {
            for (i in WebSocketFactory.getInstance().setOfObjects) {
                if(tempObject.objectID.equals(i.objectID)){
                    homeFragmentInfoView.findViewById<TextView>(R.id.nameText).text = i.name
                    homeFragmentInfoView.findViewById<TextView>(R.id.customNameText).text = i.customName
                    homeFragmentInfoView.findViewById<TextView>(R.id.countryText).text = i.country
                    homeFragmentInfoView.findViewById<TextView>(R.id.cityText).text = i.city
                    homeFragmentInfoView.findViewById<TextView>(R.id.streetText).text = i.street
                    homeFragmentInfoView.findViewById<TextView>(R.id.houseText).text = i.house
                    homeFragmentInfoView.findViewById<TextView>(R.id.buildingText).text = i.building
                    homeFragmentInfoView.findViewById<TextView>(R.id.flatText).text = i.flat
                }
            }
        }
    }

    override fun onClick(v: View?) {
        //homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.objectInfoMainLayout).invalidate()
        when(v!!.id){
            R.id.editName -> {
                label.text = "Название"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editCustomName -> {
                label.text = "Псевдоним"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editCountry -> {
                label.text = "Страна"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editCity -> {
                label.text = "Город"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editStreet -> {
                label.text = "Улица"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editHouse -> {
                label.text = "Дом"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editBuilding -> {
                label.text = "Строение"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.editFlat -> {
                label.text = "Квартира"
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.VISIBLE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.GONE
            }
            R.id.saveEditInfo -> {
                for(i in WebSocketFactory.getInstance().setOfObjects){
                    if(i.objectID == tempObject?.objectID){
                        if(label.text.equals("Название")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",text.text)
                                        .put("country", i.country)
                                        .put("city",i.city)
                                        .put("street", i.street)
                                        .put("house",i.house)
                                        .put("building", i.building)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Псевдоним")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", text.text)
                                        .put("name",i.name)
                                        .put("country", i.country)
                                        .put("city",i.city)
                                        .put("street", i.street)
                                        .put("house",i.house)
                                        .put("building", i.building)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Страна")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",i.name)
                                        .put("country", text.text)
                                        .put("city",i.city)
                                        .put("street", i.street)
                                        .put("house",i.house)
                                        .put("building", i.building)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Город")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",i.name)
                                        .put("country", i.country)
                                        .put("city",text.text)
                                        .put("street", i.street)
                                        .put("house",i.house)
                                        .put("building", i.building)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Улица")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",i.name)
                                        .put("country", i.country)
                                        .put("city",i.city)
                                        .put("street", text.text)
                                        .put("house",i.house)
                                        .put("building", i.building)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Дом")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",i.name)
                                        .put("country", i.country)
                                        .put("city",i.city)
                                        .put("street", i.street)
                                        .put("house",text.text)
                                        .put("building", i.building)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Строение")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",i.name)
                                        .put("country", i.country)
                                        .put("city",i.city)
                                        .put("street", i.street)
                                        .put("house",i.house)
                                        .put("building", text.text)
                                        .put("flat", i.flat)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                        if(label.text.equals("Квартира")){
                            val message1 = JSONObject()
                                .put("action","edit")
                                .put("result", JSONObject()
                                    .put("object", JSONObject()
                                        .put("objectID",i.objectID)
                                        .put("customName", i.customName)
                                        .put("name",i.name)
                                        .put("country", i.country)
                                        .put("city",i.city)
                                        .put("street", i.street)
                                        .put("house",i.house)
                                        .put("building", i.building)
                                        .put("flat", text.text)))
                            WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        }
                    }
                }
                homeFragmentInfoView.findViewById<ConstraintLayout>(R.id.editInfoMainLayout).visibility = View.GONE
                homeFragmentInfoView.findViewById<ScrollView>(R.id.infoMainLayout).visibility = View.VISIBLE
            }
            R.id.removeObject -> {
                val mBuilder = AlertDialog.Builder(context!!)
                mBuilder.setTitle("Вы уверены что хотите удалить объект?")
                mBuilder.setCancelable(false)
                mBuilder.setPositiveButton("Да", DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                    val message1 = JSONObject()
                        .put("action","delete")
                        .put("result", JSONObject()
                            .put("object", tempObject!!.objectID.toInt()))
                    WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                    sleep(500)
                    activity?.findViewById<Button>(R.id.homeButton)?.performClick()
                })
                mBuilder.setNegativeButton("Нет", DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                })
                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("HomeFragmentInfo", "onDestroyView")
        Log.v("---", "-----------------------------------------")
    }


    override fun onPause() {
        super.onPause()
        Log.v("HomeFragmentInfo", "onPause")
        Log.v("---", "-----------------------------------------")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v("HomeFragmentInfo", "onAttach")
        Log.v("---", "-----------------------------------------")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("HomeFragmentInfo", "onActivityCreated")
        Log.v("---", "-----------------------------------------")
    }

    override fun onStart() {
        super.onStart()
        Log.v("HomeFragmentInfo", "onStart")
        Log.v("---", "-----------------------------------------")
    }

    override fun onResume() {
        super.onResume()
        Log.v("HomeFragmentInfo", "onResume")
        Log.v("---", "-----------------------------------------")
    }


    override fun onStop() {
        super.onStop()
        Log.v("HomeFragmentInfo", "onStop")
        Log.v("---", "-----------------------------------------")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("HomeFragmentInfo", "onDestroy")
        Log.v("---", "-----------------------------------------")
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("HomeFragmentInfo", "onDetach")
        Log.v("---", "-----------------------------------------")
    }
}