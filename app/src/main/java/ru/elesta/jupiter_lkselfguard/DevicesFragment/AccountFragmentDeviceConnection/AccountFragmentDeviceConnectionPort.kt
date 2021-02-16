package ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.jetbrains.anko.find
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.lang.Thread.sleep

class AccountFragmentDeviceConnectionPort: Fragment(), View.OnClickListener {

    lateinit var myView: View

    companion object{
        var portForConnection = ""
        var portWasGiven = false
    }

    private lateinit var connectionDeviceToObjectLabel_port: TextView
    private lateinit var listOfObjectsLabel: TextView
    private lateinit var objectsToAdd_port: Spinner
    private lateinit var createNewObject2_port: Button
    private lateinit var back_port: Button
    private lateinit var toGetPortButton: Button
    private lateinit var textView26_port: TextView
    private lateinit var textView27_port: TextView
    private lateinit var textView28_port: TextView
    private lateinit var IPTextView_port: TextView
    private lateinit var portTextView_port: TextView
    private lateinit var imageView11_port: ImageView
    private lateinit var circleTwo_port: ImageView
    private lateinit var pb_horizontal_port: ProgressBar
    private lateinit var createNewObjectLayout2: ScrollView
    private lateinit var createNewObjectButton2: Button

    private lateinit var closePortConnection: Button

    private lateinit var newNameText2: TextView
    private lateinit var newCustomNameText2: TextView
    private lateinit var newCountry2: TextView
    private lateinit var newCity2: TextView
    private lateinit var newStreet2: TextView
    private lateinit var newHouse2: TextView
    private lateinit var newBuilding2: TextView
    private lateinit var newFlat2: TextView

    var objects = ArrayList<String>()
    var spinner: Spinner? = null
    var tempObjectName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.account_fragment_device_connection_port, null)

        connectionDeviceToObjectLabel_port = v.findViewById(R.id.connectionDeviceToObjectLabel_port)
        listOfObjectsLabel = v.findViewById(R.id.listOfObjectsLabel)
        objectsToAdd_port = v.findViewById(R.id.objectsToAdd_port)
        createNewObject2_port = v.findViewById(R.id.createNewObject_port)
        back_port = v.findViewById(R.id.back_port)
        toGetPortButton = v.findViewById(R.id.toGetPortButton)
        textView26_port = v.findViewById(R.id.textView26_port)
        textView27_port = v.findViewById(R.id.textView27_port)
        textView28_port = v.findViewById(R.id.textView28_port)
        IPTextView_port = v.findViewById(R.id.IPTextView_port)
        portTextView_port = v.findViewById(R.id.portTextView_port)
        imageView11_port = v.findViewById(R.id.imageView11_port)
        circleTwo_port = v.findViewById(R.id.circleTwo_port)
        pb_horizontal_port = v.findViewById(R.id.pb_horizontal_port)
        createNewObjectLayout2 = v.findViewById(R.id.createNewObjectScrollView2)

        newNameText2 = v.findViewById(R.id.newNameText2)
        newCustomNameText2 = v.findViewById(R.id.newCustomNameText2)
        newCountry2 = v.findViewById(R.id.newCountryText2)
        newCity2 = v.findViewById(R.id.newCityText2)
        newStreet2 = v.findViewById(R.id.newStreetText2)
        newHouse2 = v.findViewById(R.id.newHouseText2)
        newBuilding2 = v.findViewById(R.id.newBuildingText2)
        newFlat2 = v.findViewById(R.id.newFlatText2)
        closePortConnection = v.findViewById(R.id.closePortConnection)
        closePortConnection.setOnClickListener(this)
        createNewObject2_port.setOnClickListener(this)
        back_port.setOnClickListener(this)
        toGetPortButton.setOnClickListener(this)
        createNewObjectButton2 = v.findViewById(R.id.createNewObjectButton2)
        createNewObjectButton2.setOnClickListener(this)

        spinner = v.findViewById(R.id.objectsToAdd_port)
        for(i in WebSocketFactory.getInstance().setOfObjects){
            if(i.typeID == "11"){
                continue
            }
            if(i.customName == ""){
                objects.add(i.name)
            }
            else{
                objects.add(i.customName)
            }
        }
        val adapter = ArrayAdapter(activity!!.applicationContext, R.layout.custom_simple_spinner_item, objects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
        spinner!!.setOnItemSelectedListener(itemSelectedListener)

        myView = v
        return v
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.createNewObject_port -> {
                myView.findViewById<ConstraintLayout>(R.id.deviceConnectionPrimaryLayout2).visibility = View.GONE
                createNewObjectLayout2.visibility = View.VISIBLE
                LKActivity.headLabelValue.text = "Новый объект"
            }
            R.id.back_port -> {
                activity?.findViewById<Button>(R.id.devicesButton)?.performClick()
            }
            R.id.createNewObjectButton2 -> {
                val message = JSONObject()
                    .put("action","create")
                    .put("result", JSONObject()
                        .put("object", JSONObject()
                            .put("customName",newCustomNameText2.text.toString())
                            .put("name", newNameText2.text.toString())
                            .put("country",newCountry2.text.toString())
                            .put("city", newCity2.text.toString())
                            .put("street", newStreet2.text.toString())
                            .put("house",newHouse2.text.toString())
                            .put("building",newBuilding2.text.toString())
                            .put("flat",newFlat2.text.toString())))
                WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message.toString())
                sleep(1000)
                objects.clear()
                for(i in WebSocketFactory.getInstance().setOfObjects){
                    if(i.typeID == "11"){
                        continue
                    }
                    if(i.customName == ""){
                        objects.add(i.name)
                    }
                    else{
                        objects.add(i.customName)
                    }
                }
                val adapter = ArrayAdapter(activity!!.applicationContext, R.layout.custom_simple_spinner_item, objects)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner!!.adapter = adapter
                spinner!!.setOnItemSelectedListener(itemSelectedListener)
                myView.findViewById<ConstraintLayout>(R.id.deviceConnectionPrimaryLayout2).visibility = View.VISIBLE
                createNewObjectLayout2.visibility = View.GONE
            }

            R.id.toGetPortButton -> {
                postProgress(100)
                circleTwo_port.setImageResource(R.drawable.small_yellow_circle)
                textView26_port.visibility = View.VISIBLE
                textView27_port.visibility = View.VISIBLE
                textView28_port.visibility = View.VISIBLE
                IPTextView_port.visibility = View.VISIBLE
                portTextView_port.visibility = View.VISIBLE
                closePortConnection.visibility = View.VISIBLE
                toGetPortButton.visibility = View.INVISIBLE
                var objectToAdd = ""
                for(j in WebSocketFactory.getInstance().setOfObjects){
                    if(tempObjectName == j.customName || tempObjectName == j.name){
                        objectToAdd = j.objectID
                    }
                }
                val message = JSONObject()
                    .put("action","bind")
                    .put("result", JSONObject().put("objectID",objectToAdd).put("deviceID", -1))
                WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message.toString())
                var i = 0
                while(i < 300 && !portWasGiven){
                    i++
                    sleep(100)
                }
                portWasGiven = false
                IPTextView_port.text = "jupiter8.ru"
                portTextView_port.text =
                    portForConnection
                toGetPortButton.visibility = View.VISIBLE
            }

            R.id.closePortConnection -> {
                activity?.findViewById<Button>(R.id.devicesButton)?.performClick()
            }
        }
    }

    private fun postProgress(progress: Int) {
        pb_horizontal_port.setProgress(progress)
    }

    val itemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            tempObjectName = parent?.getItemAtPosition(0) as String
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            tempObjectName = parent?.getItemAtPosition(position) as String
        }

    }
}