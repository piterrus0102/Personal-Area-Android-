package ru.elesta.jupiter_lkselfguard.DevicesFragment

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.jetbrains.anko.wrapContent
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.headLabelValue
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.mainSocketString
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionBarcode
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionPort
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.lang.Thread.sleep


class DevicesFragment : Fragment(), View.OnClickListener {



    var tempObjectName = ""
    var tempDeviceID = ""
    lateinit var changeObjectButton: Button
    lateinit var unbindAllObjects: Button
    lateinit var deviceCardLinearLayout: LinearLayout
    lateinit var myHandler1: Handler
    lateinit var myRunnable1: Runnable

    lateinit var tableOfDevices: TableLayout

    lateinit var addNewDevice: ConstraintLayout
    lateinit var addNewDevice2: ConstraintLayout
    lateinit var myView: View
    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    lateinit var myHandlerxxx: Handler
    lateinit var myRunnablexxx: Runnable

    lateinit var adapter: ArrayAdapter<String>
    lateinit var objects: ArrayList<String>

    var initialObject = ""

    var mainViewIsVisible = true
    var deviceCardIsVisible = false

    companion object{
        var changeFlagDeviceCard = false
        var changeFlagDevice = false
        var loadingOfDevicesOneFinished = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_devices, null)
        myView = v
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        mainViewIsVisible = true
        addNewDevice = v.findViewById(R.id.addNewDevice)
        addNewDevice.setOnClickListener{
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(
                R.id.fragmentContainer,
                AccountFragmentDeviceConnectionBarcode(), "deviceconnectionbarcode"
            )
            headLabelValue.text = "Подключение прибора"
            ft?.addToBackStack(null)
            ft?.commit()
        }

        addNewDevice2 = v.findViewById(R.id.addNewDevice2)
        addNewDevice2.setOnClickListener{
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(
                R.id.fragmentContainer,
                AccountFragmentDeviceConnectionPort(), "deviceconnectionport"
            )
            headLabelValue.text = "Подключение прибора"
            ft?.addToBackStack(null)
            ft?.commit()
        }
        tableOfDevices = v.findViewById(R.id.tableOfDevices)
        getDevices()
        createRunnable()
        changeObjectButton = v.findViewById(R.id.changeObjectButton)
        changeObjectButton.setOnClickListener(this)
        unbindAllObjects = v.findViewById(R.id.unbindAllObjects)
        unbindAllObjects.setOnClickListener(this)
        deviceCardLinearLayout = v.findViewById(R.id.deviceCardLinearLayout)
        deviceCardLinearLayout.visibility = View.GONE

        return v
    }

    fun createRunnable(){
        myHandler = Handler()
        myRunnable = Runnable {
            myHandler.postDelayed(myRunnable, 500)
            if(changeFlagDevice == true && loadingOfDevicesOneFinished) {
                if(mainViewIsVisible == true) {
                    cleanTable()
                    getDevices()
                    changeFlagDevice = false
                }
            }
        }
        myHandler.post(myRunnable)
    }

    fun cleanTable(){
        tableOfDevices.removeAllViews()
    }

    override fun onResume() {
        super.onResume()
        myHandlerxxx = Handler()
        myRunnablexxx = Runnable {
            if(WebSocketFactory.getInstance().setOfDevices.isEmpty()) {
                addNewDevice.performClick()
            }
            myHandlerxxx.removeCallbacks(myRunnablexxx)
        }
        myHandlerxxx.postDelayed(myRunnablexxx, 200)

    }

    fun getDevices(){
        var k = 0
        if(!WebSocketFactory.getInstance().setOfDevices.isEmpty()) {
            for (i in WebSocketFactory.getInstance().setOfDevices) {
                val tableRow = TableRow(activity)
                val lp = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
                tableRow.isClickable = true
                lp.topMargin = 10
                tableRow.layoutParams = lp
                val shared = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val darkTheme = shared.getBoolean("dark", true)
                if(darkTheme) {
                    tableRow.setBackgroundResource(R.drawable.roundcornersectionsdarktheme)
                }
                else{
                    tableRow.setBackgroundResource(R.drawable.roundcornerssectionslighttheme)
                }
                tableRow.setPadding(10, 10, 10, 10)
                val imageOfGuard = ImageView(activity)
                imageOfGuard.setImageResource(R.drawable.ic_device_gray)
                imageOfGuard.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)
                val lp1 = TableRow.LayoutParams(wrapContent, wrapContent)
                lp1.rightMargin = 30
                lp1.gravity = Gravity.CENTER_VERTICAL
                imageOfGuard.layoutParams = lp1
                imageOfGuard.setPadding(20, 20, 20, 20)


                val innerLinearLayout = LinearLayout(activity)
                innerLinearLayout.orientation = LinearLayout.VERTICAL
                val lpInnerLinearLayout = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
                lpInnerLinearLayout.weight = 5F
                innerLinearLayout.layoutParams = lpInnerLinearLayout
                val textInfoDevice = TextView(activity)
                textInfoDevice.text = "Штрих-код " + i.barcode
                if(darkTheme) {
                    textInfoDevice.setTextColor(Color.WHITE)
                }
                else{
                    textInfoDevice.setTextColor(Color.BLACK)
                }
                textInfoDevice.textSize = 12F

                val innerInnerLinearLayout = LinearLayout(activity)
                val lpInnerInnerLinearLayout = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT)
                innerInnerLinearLayout.orientation = LinearLayout.HORIZONTAL
                lpInnerInnerLinearLayout.weight = 1F
                innerInnerLinearLayout.layoutParams = lpInnerInnerLinearLayout
                innerInnerLinearLayout.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)
                innerInnerLinearLayout.setPadding(5, 0, 5, 0)

                val deviceName = TextView(activity)
                deviceName.text = i.deviceID + "(" + i.type + ")"
                if(darkTheme) {
                    deviceName.setTextColor(Color.WHITE)
                }
                else{
                    deviceName.setTextColor(Color.BLACK)
                }
                deviceName.textSize = 12F

                val smallCircle = ImageView(activity)
                val textStatus = TextView(activity)
                val smallCircleLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                smallCircleLP.rightMargin = 5
                smallCircleLP.leftMargin = 15
                smallCircleLP.gravity = Gravity.CENTER_VERTICAL
                val textStatusLP = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                smallCircle.layoutParams = smallCircleLP
                textStatusLP.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                textStatus.layoutParams = textStatusLP
                for(j in WebSocketFactory.getInstance().setOfDevicesOne){
                    if(j.deviceID == i.deviceID) {
                        if(j.faultStatus == "NotFault"){
                            smallCircle.setImageResource(R.drawable.small_green_circle)
                            textStatus.text = "На связи"
                            textStatus.setTextColor(Color.parseColor("#1DB505"))
                        }
                        if(j.faultStatus == "Fault"){
                            smallCircle.setImageResource(R.drawable.small_red_circle)
                            textStatus.text = "Нет связи"
                            textStatus.setTextColor(Color.parseColor("#DB0014"))
                        }
                    }
                }

                innerLinearLayout.addView(deviceName)
                innerLinearLayout.addView(textInfoDevice)
                innerInnerLinearLayout.addView(smallCircle)
                innerInnerLinearLayout.addView(textStatus)
                tableRow.addView(imageOfGuard)
                tableRow.addView(innerLinearLayout)
                tableRow.addView(innerInnerLinearLayout)
                tableOfDevices.addView(tableRow, k)
                k++

                tableRow.setOnClickListener{
                    sleep(500)
                    val spinner = myView.findViewById<Spinner>(R.id.objects)
                    tempDeviceID = i.deviceID
                    objects = ArrayList()
                    for(s in WebSocketFactory.getInstance().setOfObjects){
                        if(s.typeID == "11") continue
                        if(s.customName == ""){
                            objects.add(s.name)
                        }
                        else{
                            objects.add(s.customName)
                        }
                    }
                    var firstObject = ""
                    for (a in WebSocketFactory.getInstance().setOfDevicesOne){
                        if (a.deviceID == tempDeviceID) {
                            if (!a.objects.isEmpty()){
                                firstObject = a.objects[0].toString()
                            }
                        }
                    }
                    for(a in WebSocketFactory.getInstance().setOfObjects){
                        if(a.objectID == firstObject){
                            initialObject = a.customName
                            if(initialObject == ""){
                                initialObject == a.name
                            }
                        }
                    }
                    for(j in 0..objects.size-1){
                        if(objects[j] == initialObject){
                            val temp = objects[0]
                            objects[0] = objects[j]
                            objects[j] = temp
                            break
                        }
                    }
                    tempObjectName = initialObject
                    adapter = ArrayAdapter(activity!!.applicationContext, R.layout.custom_simple_spinner_item, objects)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    spinner.setOnItemSelectedListener(itemSelectedListener)
                    mainViewIsVisible = false
                    deviceCardIsVisible = true
                    headLabelValue.text = i.type
                    val message1 = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("device", tempDeviceID.toInt()))
                    WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message1.toString())
                    getDevice()
                    startRunnable()
                    deviceCardLinearLayout.visibility = View.VISIBLE
                }
            }
        }
    }


    fun startRunnable(){
        myHandler1 = Handler()
        myRunnable1 = Runnable {
            if(changeFlagDeviceCard == true){
                if(deviceCardIsVisible == true) {
                    getDevice()
                    changeFlagDeviceCard = false
                }
            }
            myHandler1.postDelayed(myRunnable1, 500)
        }
        myHandler1.post(myRunnable1)
    }

    fun getDevice(){
        if(!WebSocketFactory.getInstance().setOfDevicesOne.isEmpty()) {
            for (i in WebSocketFactory.getInstance().setOfDevicesOne) {
                if(tempDeviceID == i.deviceID){
                    myView.findViewById<TextView>(R.id.deviceBarcodeText).text = i.barcode
                    if(i.guardStatus == "NotGuard"){
                        myView.findViewById<TextView>(R.id.guardStatusText).text = "Снят с охраны"
                    }
                    if(i.guardStatus == "Partial"){
                        myView.findViewById<TextView>(R.id.guardStatusText).text = "Частично взят"
                    }
                    if(i.guardStatus == "Guard"){
                        myView.findViewById<TextView>(R.id.guardStatusText).text = "Взят на охрану"
                    }
                    if(i.alarmStatus == "Alarm"){
                        myView.findViewById<TextView>(R.id.alarmStatusText).text = "В тревоге"
                    }
                    if(i.alarmStatus == "NotAlarm"){
                        myView.findViewById<TextView>(R.id.alarmStatusText).text = "Не в тревоге"
                    }
                    if(i.faultStatus == "NotFault"){
                        myView.findViewById<TextView>(R.id.faultStatusText).text = "Исправен"
                    }
                    if(i.faultStatus == "Fault"){
                        myView.findViewById<TextView>(R.id.faultStatusText).text = "Неисправен"
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.changeObjectButton -> {
                for (i in WebSocketFactory.getInstance().setOfObjects) {
                    if (i.customName == tempObjectName || i.name == tempObjectName) {
                        val message = JSONObject()
                            .put("action", "bind")
                            .put("result", JSONObject().put("objectID", i.objectID).put("deviceID", tempDeviceID.toInt()))
                        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message.toString())
                        WebSocketFactory.getInstance().setOfDevices.clear()
                        val message1 = JSONObject()
                            .put("action", "get")
                            .put("result", JSONObject().put("devices", ""))
                        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                        val message2 = JSONObject()
                            .put("action", "get")
                            .put("result", JSONObject().put("device", tempDeviceID.toInt()))
                        WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message2.toString())
                        createRunnable()
                        initialObject = ""
                        break
                    }
                }
                mainViewIsVisible = true
                deviceCardIsVisible = false
                adapter.clear()
                deviceCardLinearLayout.visibility = View.GONE
            }
            R.id.unbindAllObjects -> {
                val mBuilder = AlertDialog.Builder(context!!)
                mBuilder.setTitle("Отвязать прибор от всех объектов?")
                mBuilder.setCancelable(false)
                mBuilder.setPositiveButton("Да", DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    val message = JSONObject()
                        .put("action", "bind")
                        .put("result", JSONObject().put("objectID", -1).put("deviceID", tempDeviceID.toInt()))
                    WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message.toString())
                    WebSocketFactory.getInstance().setOfDevices.clear()
                    val message1 = JSONObject()
                        .put("action", "get")
                        .put("result", JSONObject().put("devices", ""))
                    WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message1.toString())
                    mainViewIsVisible = true
                    deviceCardIsVisible = false
                    deviceCardLinearLayout.visibility = View.GONE
                })
                mBuilder.setNegativeButton("Нет", DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                })
                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }
    }

    val itemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            tempObjectName = parent?.getItemAtPosition(position) as String
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("Devices", "OndesrroyView")
        myHandler.removeCallbacks(myRunnable)
    }
}
