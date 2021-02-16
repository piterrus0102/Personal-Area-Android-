package ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.AccountFragment.BarcodeScanner
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.barcodeFinished
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.R.drawable
import ru.elesta.jupiter_lkselfguard.R.layout
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.lang.Thread.sleep


class AccountFragmentDeviceConnectionBarcode: Fragment(), View.OnClickListener {


    lateinit var viewOfDeviceConnection: View
    lateinit var deviceConnectionMainLayout: ConstraintLayout
    private var progress = 0
    private var pbHorizontal: ProgressBar? = null
    private var pbHorizontal2: ProgressBar? = null
    lateinit var btnFindDevice: Button
    lateinit var btnToConnectionToObject: Button
    lateinit var createNewObject2: Button
    lateinit var btnBack1: Button
    lateinit var btnBack2: Button
    lateinit var btnBack3: Button
    lateinit var toFinalConnection: Button
    lateinit var finalConnection: Button
    lateinit var barcodeText: TextView



    lateinit var createNewObjectLayout: ScrollView
    lateinit var createNewObject: Button
    lateinit var newNameText: EditText
    lateinit var newCustomNameText: EditText
    lateinit var newCountry: EditText
    lateinit var newCity: EditText
    lateinit var newStreet: EditText
    lateinit var newHouse: EditText
    lateinit var newBuilding: EditText
    lateinit var newFlat: EditText

    var accessToCameraFlag = false
    var spinner: Spinner? = null
    var tempObjectName = ""
    var tempDeviceName = ""
    var objects = ArrayList<String>()
    companion object{
        var textFromBarcodeScanner = ""
        var toConnectionToObject = false
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(layout.account_fragment_device_connection_barcode, null)
        viewOfDeviceConnection = v
        deviceConnectionMainLayout = v.findViewById(R.id.deviceConnectionMainLayout)
        pbHorizontal = v.findViewById(R.id.pb_horizontal)
        pbHorizontal2 = v.findViewById(R.id.pb_horizontal2)
        btnFindDevice = v.findViewById(R.id.findDevice)
        btnFindDevice.setOnClickListener(this)
        createNewObject2 = v.findViewById(R.id.createNewObject2)
        createNewObject2.setOnClickListener(this)
        btnBack1 = v.findViewById(R.id.back1)
        btnBack1.setOnClickListener(this)
        btnBack2 = v.findViewById(R.id.back2)
        btnBack2.setOnClickListener(this)
        btnBack3 = v.findViewById(R.id.back3)
        btnBack3.setOnClickListener(this)
        toFinalConnection = v.findViewById(R.id.toFinalConnection)
        toFinalConnection.setOnClickListener(this)
        finalConnection = v.findViewById(R.id.connectionButton)
        finalConnection.setOnClickListener(this)
        btnToConnectionToObject = v.findViewById(R.id.toConnectionToObject)
        btnToConnectionToObject.setOnClickListener(this)
        barcodeText = v.findViewById(R.id.barcodeTextField)


        createNewObject = v.findViewById(R.id.createNewObjectButton)
        createNewObject.setOnClickListener(this)
        newNameText = v.findViewById(R.id.newNameText)
        newCustomNameText = v.findViewById(R.id.newCustomNameText)
        newCountry = v.findViewById(R.id.newCountryText)
        newCity = v.findViewById(R.id.newCityText)
        newStreet = v.findViewById(R.id.newStreetText)
        newHouse = v.findViewById(R.id.newHouseText)
        newBuilding = v.findViewById(R.id.newBuildingText)
        newFlat = v.findViewById(R.id.newFlatText)
        createNewObjectLayout = v.findViewById(R.id.createNewObjectScrollView)



        if(barcodeFinished){
            barcodeFinished = false
            v.findViewById<TextView>(R.id.barcodeTextField).text =
                textFromBarcodeScanner
        }
        spinner = v.findViewById(R.id.objectsToAdd)
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
        /*if(AccountFragmentAccountSettings.darkTheme){
            spinner!!.setBackgroundColor(Color.parseColor("#272930"))
        }
        else{
            spinner!!.setBackgroundColor(Color.parseColor("#e6e6e6"))
        }*/
        return v
    }


    fun checkPermission(){
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 123)
        }
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            val barcodeFrag = BarcodeScanner()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.fragmentContainer, barcodeFrag)
            ft?.addToBackStack(null)
            ft?.commit()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 123){
            for (i in 0..permissions.size-1) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == Manifest.permission.CAMERA) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        val barcodeFrag = BarcodeScanner()
                        val ft = activity?.supportFragmentManager?.beginTransaction()
                        ft?.replace(R.id.fragmentContainer, barcodeFrag)
                        ft?.addToBackStack(null)
                        ft?.commit()
                    } else {
                        return
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.findDevice -> {
                checkPermission()
            }
            R.id.toConnectionToObject ->{
                val s = viewOfDeviceConnection.findViewById<TextView>(R.id.barcodeTextField).text
                val message3 = JSONObject()
                    .put("action","find")
                    .put("result", JSONObject().put("device",s))
                WebSocketFactory.getInstance().licenceSocket.send(message3.toString())
                sleep(500)
                if(toConnectionToObject == false){
                    viewOfDeviceConnection.findViewById<ImageView>(R.id.noCorrectBarcodeImageView).setVisibility(View.VISIBLE)
                    viewOfDeviceConnection.findViewById<TextView>(R.id.noCorrectBarcodeTextView).setVisibility(View.VISIBLE)
                }
                if(toConnectionToObject == true){
                    tempDeviceName = viewOfDeviceConnection.findViewById<TextView>(R.id.barcodeTextField).text.toString()
                    viewOfDeviceConnection.findViewById<TextView>(R.id.barcodeTextField).isEnabled = false
                    progress = progress + 100
                    postProgress(progress)
                    toConnectionToObject = false
                    viewOfDeviceConnection.findViewById<ImageView>(R.id.noCorrectBarcodeImageView).setVisibility(View.GONE)
                    viewOfDeviceConnection.findViewById<TextView>(R.id.noCorrectBarcodeTextView).setVisibility(View.GONE)
                    viewOfDeviceConnection.findViewById<Button>(R.id.toConnectionToObject).setVisibility(View.GONE)
                    viewOfDeviceConnection.findViewById<Button>(R.id.back1).setVisibility(View.GONE)
                    viewOfDeviceConnection.findViewById<Button>(R.id.findDevice).setVisibility(View.GONE)
                    var m = viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal).layoutParams
                    m.height = 100
                    viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal).layoutParams = m
                    viewOfDeviceConnection.findViewById<ImageView>(R.id.circleTwo).setImageResource(drawable.small_yellow_circle)
                    viewOfDeviceConnection.findViewById<TextView>(R.id.connectionDeviceToObjectLabel).setVisibility(View.VISIBLE)
                    viewOfDeviceConnection.findViewById<TextView>(R.id.textView18).setVisibility(View.VISIBLE)
                    viewOfDeviceConnection.findViewById<Button>(R.id.back2).setVisibility(View.VISIBLE)
                    viewOfDeviceConnection.findViewById<Button>(R.id.toFinalConnection).setVisibility(View.VISIBLE)
                    viewOfDeviceConnection.findViewById<Button>(R.id.createNewObject2).setVisibility(View.VISIBLE)
                    viewOfDeviceConnection.findViewById<Spinner>(R.id.objectsToAdd).setVisibility(View.VISIBLE)
                }
            }
            R.id.back1 -> {
                activity?.findViewById<Button>(R.id.devicesButton)?.performClick()
            }
            R.id.back2 -> {
                viewOfDeviceConnection.findViewById<TextView>(R.id.barcodeTextField).isEnabled = true
                progress = progress - 100
                postProgress(progress)
                viewOfDeviceConnection.findViewById<ImageView>(R.id.noCorrectBarcodeImageView).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.noCorrectBarcodeTextView).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.toConnectionToObject).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back1).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.findDevice).setVisibility(View.VISIBLE)
                var m = viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal).layoutParams
                m.height = 180
                viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal).layoutParams = m
                viewOfDeviceConnection.findViewById<ImageView>(R.id.circleTwo).setImageResource(drawable.small_gray_circle)
                viewOfDeviceConnection.findViewById<TextView>(R.id.textView18).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back2).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.toFinalConnection).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.createNewObject2).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<ImageView>(R.id.noCorrectBarcodeImageView).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.noCorrectBarcodeTextView).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Spinner>(R.id.objectsToAdd).setVisibility(View.GONE)
            }
            R.id.toFinalConnection ->{
                progress = progress + 100
                postProgress2(progress)
                var m = viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal2).layoutParams
                m.height = 80
                viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal2).layoutParams = m
                viewOfDeviceConnection.findViewById<ImageView>(R.id.circleThree).setImageResource(drawable.small_yellow_circle)
                viewOfDeviceConnection.findViewById<TextView>(R.id.textView18).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back2).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.toFinalConnection).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.createNewObject2).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back2).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.toFinalConnection).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.createNewObject2).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back3).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.connectionButton).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.addingDevice).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.addingObject).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Spinner>(R.id.objectsToAdd).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.addingObject).text = tempObjectName
                viewOfDeviceConnection.findViewById<TextView>(R.id.addingDevice).text = tempDeviceName
            }

            R.id.back3 ->{
                progress = progress - 100
                postProgress2(progress)
                var m = viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal2).layoutParams
                m.height = 180
                viewOfDeviceConnection.findViewById<ProgressBar>(R.id.pb_horizontal2).layoutParams = m
                viewOfDeviceConnection.findViewById<ImageView>(R.id.circleThree).setImageResource(drawable.small_gray_circle)
                viewOfDeviceConnection.findViewById<TextView>(R.id.textView18).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back2).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.toFinalConnection).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.createNewObject2).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back2).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.toFinalConnection).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.createNewObject2).setVisibility(View.VISIBLE)
                viewOfDeviceConnection.findViewById<Button>(R.id.back3).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Button>(R.id.connectionButton).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.addingDevice).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<TextView>(R.id.addingObject).setVisibility(View.GONE)
                viewOfDeviceConnection.findViewById<Spinner>(R.id.objectsToAdd).setVisibility(View.VISIBLE)
            }
            R.id.connectionButton ->{
                var objectToAdd = ""
                for(j in WebSocketFactory.getInstance().setOfObjects){
                    if(tempObjectName == j.customName || tempObjectName == j.name){
                        objectToAdd = j.objectID
                    }
                }
                if(objectToAdd == ""){
                    val message = JSONObject()
                        .put("action","create")
                        .put("result", JSONObject()
                            .put("object",JSONObject()
                                .put("customName","Новый объект")
                                .put("name", "Новый объект")
                                .put("country","")
                                .put("city", "")
                                .put("street", "")
                                .put("house", "")
                                .put("building", "")
                                .put("flat","")))
                    WebSocketFactory.getInstance().mapSockets[LKActivity.mainSocketString]?.send(message.toString())
                    sleep(500)
                    objectToAdd = WebSocketFactory.getInstance().setOfObjects[0].objectID
                }
                val message = JSONObject()
                    .put("action","bind")
                    .put("result", JSONObject().put("object",objectToAdd).put("device",tempDeviceName))
                WebSocketFactory.getInstance().licenceSocket.send(message.toString())
                activity?.findViewById<Button>(R.id.devicesButton)?.performClick()
            }
            R.id.createNewObject2 ->{
                viewOfDeviceConnection.findViewById<ConstraintLayout>(R.id.deviceConnectionPrimaryLayout).visibility = View.GONE
                createNewObjectLayout.visibility = View.VISIBLE
                LKActivity.headLabelValue.text = "Новый объект"
            }
            R.id.createNewObjectButton -> {
                val message = JSONObject()
                    .put("action","create")
                    .put("result", JSONObject()
                        .put("object",JSONObject()
                            .put("customName",newCustomNameText.text.toString())
                            .put("name", newNameText.text.toString())
                            .put("country",newCountry.text.toString())
                            .put("city", newCity.text.toString())
                            .put("street", newStreet.text.toString())
                            .put("house",newHouse.text.toString())
                            .put("building",newBuilding.text.toString())
                            .put("flat",newFlat.text.toString())))
                WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message.toString())
                sleep(500)
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
                viewOfDeviceConnection.findViewById<ConstraintLayout>(R.id.deviceConnectionPrimaryLayout).visibility = View.VISIBLE
                createNewObjectLayout.visibility = View.GONE
            }
        }
    }


    private fun postProgress(progress: Int) {
        pbHorizontal?.setProgress(progress)
    }
    private fun postProgress2(progress: Int) {
        pbHorizontal2?.setProgress(progress)
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