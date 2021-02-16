package ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentResponsiblePersons

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.gson.JsonPrimitive
import kotlinx.android.synthetic.main.account_fragment_responsible_persons.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.mainSocketString
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.KeyClass
import ru.elesta.jupiter_lkselfguard.dataClasses.ResponsibleClass
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS.Companion.errorDescription
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS.Companion.errorFlag
import java.lang.StringBuilder
import java.lang.Thread.sleep
import java.security.NoSuchAlgorithmException

class AccountFragmentResponsiblePersons: Fragment(), View.OnClickListener {

    lateinit var createResponsibleName: Button
    lateinit var addNewResponsible: ConstraintLayout
    lateinit var createNewResponsible: Button
    lateinit var newFIOLayout: ConstraintLayout
    lateinit var newResponsibleMainLayout: ConstraintLayout
    lateinit var saveFIOButton: Button
    lateinit var tableOfResponsibles: TableLayout
    lateinit var removeResponsibleButton: Button
    lateinit var umkaNumberTextField: TextView
    lateinit var saveKeyButton: Button
    lateinit var keyNumberTextField: TextView
    lateinit var addNewKeyLayout: ConstraintLayout
    lateinit var addNewKeyButton: Button
    lateinit var layoutOfKeys: LinearLayout
    lateinit var removeKey: Button
    lateinit var myScrollView: ScrollView
    var tempResponsibleID = ""

    companion object{
        var changeFlagResponsible = false
    }

    var newSurname = ""
    var newFirstname = ""
    var newOtchestvo = ""

    var tempKeyID = ""
    var tempDeviceID = ""
    var tempValue = ""

    var keyArray = ArrayList<KeyClass>()

    var updateResponsible = false

    var login = ""

    lateinit var accessAlarmButton: CheckBox
    lateinit var accessUmka: CheckBox
    lateinit var writeTrackOfMovement: CheckBox

    lateinit var responsibleMainView: View

    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    var tempDeviceType = ""

    var keyUpdate = false
    lateinit var tempResponsibleClass: ResponsibleClass

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.account_fragment_responsible_persons, null)
        responsibleMainView = v
        tableOfResponsibles = v.findViewById(R.id.tableOfResponsibles)
        startRunnable()
        myScrollView = v.findViewById(R.id.myScrollView)
        createResponsibleName = v.findViewById(R.id.createResponsibleName)
        createNewResponsible = v.findViewById(R.id.createNewResponsible)
        addNewResponsible = v.findViewById(R.id.addNewResponsible)
        newFIOLayout = v.findViewById(R.id.newFIOLayout)
        addNewResponsible.setOnClickListener(this)
        createResponsibleName.setOnClickListener(this)
        createNewResponsible.setOnClickListener(this)
        newResponsibleMainLayout = v.findViewById(R.id.newResponsibleMainLayout)
        umkaNumberTextField = v.findViewById(R.id.umkaNumberTextField)
        umkaNumberTextField.visibility = View.GONE
        myScrollView.visibility = View.GONE
        newFIOLayout.visibility = View.GONE
        saveFIOButton = v.findViewById(R.id.saveFIOButton)
        saveFIOButton.setOnClickListener(this)
        removeResponsibleButton = v.findViewById(R.id.removeResponsible)
        removeResponsibleButton.setOnClickListener(this)
        accessAlarmButton = v.findViewById(R.id.accessAlarmButton)
        accessUmka = v.findViewById(R.id.accessUmka)
        writeTrackOfMovement = v.findViewById(R.id.writeTrackOfMovement)
        writeTrackOfMovement.visibility = View.GONE
        saveKeyButton = v.findViewById(R.id.saveKey)
        saveKeyButton.setOnClickListener(this)
        keyNumberTextField = v.findViewById(R.id.keyNumberTextField)
        addNewKeyLayout = v.findViewById(R.id.addNewKey)
        addNewKeyLayout.visibility = View.GONE
        addNewKeyButton = v.findViewById(R.id.addNewKeyButton)
        addNewKeyButton.setOnClickListener(this)
        layoutOfKeys = v.findViewById(R.id.layoutOfKeys)
        removeKey = v.findViewById(R.id.removeKey)
        removeKey.setOnClickListener(this)

        accessAlarmButton.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){
                writeTrackOfMovement.visibility = View.VISIBLE
                newLoginTextField.visibility = View.VISIBLE
                newPasswordTextField.visibility = View.VISIBLE
            }
            if(!b){
                writeTrackOfMovement.visibility = View.GONE
                newLoginTextField.visibility = View.GONE
                newPasswordTextField.visibility = View.GONE
            }
            if(accessUmka.isChecked){
                writeTrackOfMovement.visibility = View.VISIBLE
            }
        }
        accessUmka.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){
                umkaNumberTextField.visibility = View.VISIBLE
                writeTrackOfMovement.visibility = View.VISIBLE
            }
            if(!b){
                umkaNumberTextField.visibility = View.GONE
                writeTrackOfMovement.visibility = View.GONE
            }
            if(accessAlarmButton.isChecked){
                writeTrackOfMovement.visibility = View.VISIBLE
            }
        }
        val spinner = responsibleMainView.findViewById<Spinner>(R.id.devices)
        val devices = ArrayList<String>()
        for(q in WebSocketFactory.getInstance().setOfDevicesOne){
            devices.add(q.barcode)
        }
        val adapter = ArrayAdapter(activity!!.applicationContext, R.layout.custom_simple_spinner_item, devices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setOnItemSelectedListener(itemSelectedListener)
        return v
    }

    fun startRunnable(){
        myHandler = Handler()
        myRunnable = Runnable {
            myHandler.postDelayed(myRunnable,50)
            if(changeFlagResponsible == true){
                myHandler.removeCallbacks(myRunnable)
                Log.v("CHANGE_FLAG_RESP", "FALSE")
                changeFlagResponsible = false
                cleanTable()
                getResponsibles()
            }
        }
        myHandler.post(myRunnable)
    }

    fun cleanTable(){
        tableOfResponsibles.removeAllViewsInLayout()
    }

    fun updateKeys(i: ResponsibleClass?){
        for(q in 0..keyArray.size-1){
            val textView = TextView(activity)
            val lpTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lpTV.setMargins(5,10,5,10)
            textView.layoutParams = lpTV
            textView.text = "Прибор " + keyArray[q].deviceID + " Ключ " + keyArray[q].value
            textView.textSize = 14F
            textView.setTextColor(Color.WHITE)
            textView.setOnClickListener{
                if (i != null) {
                    tempKeyID = i.deviceKeys[q].keyID
                    tempDeviceID = i.deviceKeys[q].deviceID
                    tempValue = i.deviceKeys[q].value
                }
                responsibleMainView.findViewById<ConstraintLayout>(R.id.myScrollView).visibility = View.GONE
                responsibleMainView.findViewById<ConstraintLayout>(R.id.addNewKey).visibility = View.VISIBLE
                removeKey.visibility = View.VISIBLE
                keyUpdate = true
            }
            layoutOfKeys.addView(textView)
        }
    }

    fun getResponsibles(){
        var k = 0
        val display: Display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val height: Int = size.y
        for (i in WebSocketFactory.getInstance().setOfResponsibles) {
            val tableRow = TableRow(activity)
            val lp = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
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
            tableRow.setPadding(10,10,10,10)

            val innerConstraintLayout = ConstraintLayout(requireContext())
            val innerConstraintLayoutLP = TableRow.LayoutParams((tableOfResponsibles.width * 0.99).toInt(), (0.06976744186 * height).toInt())
            innerConstraintLayout.layoutParams = innerConstraintLayoutLP

            val FIO = TextView(activity)
            val FIOLP = ConstraintLayout.LayoutParams(0,0)
            FIO.layoutParams = FIOLP
            FIO.text = i.surname + " " + i.name + " " + i.patronymic
            if(darkTheme) {
                FIO.setTextColor(Color.WHITE)
            }
            else{
                FIO.setTextColor(Color.BLACK)
            }
            FIO.textSize = 14F
            FIO.gravity = Gravity.CENTER_VERTICAL

            val imageView = ImageView(activity)
            val imageViewLP = ConstraintLayout.LayoutParams(0,0)
            imageView.layoutParams = imageViewLP
            if(darkTheme) {
                imageView.setImageResource(R.drawable.ic_responsible_white)
            }
            else{
                FIO.setTextColor(Color.BLACK)
                imageView.setImageResource(R.drawable.ic_responsible_black)
            }
            imageView.alpha = 0.38F

            tableRow.addView(innerConstraintLayout)
            innerConstraintLayout.addView(FIO)
            innerConstraintLayout.addView(imageView)

            innerConstraintLayout.id = View.generateViewId()
            FIO.id = View.generateViewId()
            imageView.id = View.generateViewId()

            val constraintSet = ConstraintSet()
            constraintSet.clone(innerConstraintLayout)
            constraintSet.connect(imageView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 20)
            constraintSet.connect(imageView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 5)
            constraintSet.connect(imageView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 5)
            constraintSet.connect(FIO.id, ConstraintSet.LEFT, imageView.id, ConstraintSet.RIGHT, 20)
            constraintSet.connect(FIO.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 5)
            constraintSet.connect(FIO.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 5)
            constraintSet.connect(FIO.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 20)

            constraintSet.constrainHeight(imageView.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.setDimensionRatio(imageView.id, "1:1")
            constraintSet.constrainHeight(FIO.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainWidth(FIO.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.applyTo(innerConstraintLayout)


            tableRow.setOnClickListener{
                Log.v("Responsible", "CLICK_ROW")
                responsibleMainView.findViewById<Button>(R.id.createNewResponsible).text = "Сохранить"
                responsibleMainView.findViewById<Button>(R.id.removeResponsible).visibility = View.VISIBLE
                addNewKeyButton.visibility = View.VISIBLE
                layoutOfKeys.visibility = View.VISIBLE
                newSurname = i.surname
                newFirstname = i.name
                newOtchestvo = i.patronymic
                accessAlarmButton.isChecked = i.accessAlarmButton
                if(accessAlarmButton.isChecked){
                    newLoginTextField.visibility = View.VISIBLE
                    newPasswordTextField.visibility = View.VISIBLE
                } else {
                    newLoginTextField.visibility = View.GONE
                    newPasswordTextField.visibility = View.GONE
                }
                accessUmka.isChecked = i.accessUmka
                writeTrackOfMovement.isChecked = i.writeTrackOfMovement
                login = i.login
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.VISIBLE
                responsibleMainView.findViewById<ConstraintLayout>(R.id.addNewResponsible).visibility = View.GONE
                responsibleMainView.findViewById<TextView>(R.id.newFIOTextField).text = i.surname + " " + i.name + " " + i.patronymic
                responsibleMainView.findViewById<TextView>(R.id.surnameTextField).text = i.surname
                responsibleMainView.findViewById<TextView>(R.id.firstnameTextField).text = i.name
                responsibleMainView.findViewById<TextView>(R.id.otchestvoTextField).text = i.patronymic
                responsibleMainView.findViewById<TextView>(R.id.newLoginTextField).text = i.login
                responsibleMainView.findViewById<TextView>(R.id.umkaNumberTextField).text = i.umkaID
                responsibleMainView.findViewById<CheckBox>(R.id.accessAlarmButton).isChecked = i.accessAlarmButton
                responsibleMainView.findViewById<CheckBox>(R.id.accessUmka).isChecked = i.accessUmka
                responsibleMainView.findViewById<CheckBox>(R.id.writeTrackOfMovement).isChecked = i.writeTrackOfMovement
                tempResponsibleClass = i
                for(q in 0..i.deviceKeys.size-1){
                    keyArray.add(i.deviceKeys[q])
                    val textView = TextView(activity)
                    val lpTV = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    lpTV.setMargins(5,10,5,10)
                    textView.layoutParams = lpTV
                    for (j in WebSocketFactory.getInstance().setOfDevices){
                        if(i.deviceKeys[q].deviceID == j.deviceID){
                            textView.text = "Прибор " + j.barcode + " Ключ " + i.deviceKeys[q].value
                        }
                    }
                    textView.textSize = 14F
                    textView.setTextColor(Color.WHITE)
                    textView.setOnClickListener{
                        tempKeyID = i.deviceKeys[q].keyID
                        tempDeviceID = i.deviceKeys[q].deviceID
                        tempValue = i.deviceKeys[q].value
                        responsibleMainView.findViewById<TextView>(R.id.keyNumberTextField).text = tempValue
                        responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.GONE
                        responsibleMainView.findViewById<ConstraintLayout>(R.id.addNewKey).visibility = View.VISIBLE
                        removeKey.visibility = View.VISIBLE
                        keyUpdate = true
                    }
                    layoutOfKeys.addView(textView)
                }

                tempResponsibleID = i.responsibleID
                updateResponsible = true
            }
            tableOfResponsibles.addView(tableRow, k)
            k++
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.createResponsibleName -> {
                newFIOLayout.visibility = View.VISIBLE
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.GONE
            }
            R.id.addNewResponsible -> {
                Log.v("Responsible", "ADD_NEW")
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.VISIBLE
                responsibleMainView.findViewById<ConstraintLayout>(R.id.addNewResponsible).visibility = View.GONE
            }
            R.id.addNewKeyButton ->{
                addNewKeyLayout.visibility = View.VISIBLE
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.GONE
            }
            R.id.saveKey ->{
                var tempDeviceID1 = ""
                val tempValue1 = responsibleMainView.findViewById<TextView>(R.id.keyNumberTextField).text.toString()
                for(i in WebSocketFactory.getInstance().setOfDevices){
                    if(tempDeviceType == i.barcode){
                        tempDeviceID1 = i.deviceID
                    }
                }
                if(!keyUpdate){
                    keyArray.add(KeyClass("-1", tempDeviceID1,tempValue1))
                }
                if(keyUpdate){
                    keyUpdate = false
                    for(q in 0..keyArray.size-1){
                        if(keyArray[q].keyID == tempKeyID){
                            keyArray.removeAt(q)
                            keyArray.add(KeyClass(tempKeyID, tempDeviceID1, tempValue1))
                        }
                    }
                }
                layoutOfKeys.removeAllViews()
                if(updateResponsible) {
                    updateKeys(tempResponsibleClass)
                } else {
                    updateKeys(null)
                }
                addNewKeyLayout.visibility = View.GONE
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.VISIBLE
                responsibleMainView.findViewById<TextView>(R.id.keyNumberTextField).text = ""
            }
            R.id.removeKey -> {
                addNewKeyLayout.visibility = View.GONE
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.VISIBLE
                responsibleMainView.findViewById<TextView>(R.id.keyNumberTextField).text = ""
                val r = keyArray.size-1
                for(q in 0..r){
                    if(keyArray[q].keyID == tempKeyID){
                        keyArray.removeAt(q)
                        break
                    }
                }
                layoutOfKeys.removeAllViews()
                updateKeys(tempResponsibleClass)
            }
            R.id.saveFIOButton -> {
                newFIOLayout.visibility = View.GONE
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.VISIBLE
                newSurname = responsibleMainView.findViewById<EditText>(R.id.surnameTextField).text.toString()
                newFirstname = responsibleMainView.findViewById<EditText>(R.id.firstnameTextField).text.toString()
                newOtchestvo = responsibleMainView.findViewById<EditText>(R.id.otchestvoTextField).text.toString()
                responsibleMainView.findViewById<TextView>(R.id.newFIOTextField).text = "$newSurname $newFirstname $newOtchestvo"
            }

            R.id.createNewResponsible -> {
                if((responsibleMainView.findViewById<TextView>(R.id.newPasswordTextField).text.isEmpty() || responsibleMainView.findViewById<TextView>(R.id.newLoginTextField).text.isEmpty()) && updateResponsible == false && accessAlarmButton.isChecked){
                    runOnUiThread {
                        Toast.makeText(context, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                    }
                    return
                } else if(responsibleMainView.findViewById<TextView>(R.id.newLoginTextField).text.isEmpty() && updateResponsible == true) {
                    runOnUiThread {
                        Toast.makeText(context, "Введите логин", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                if(responsibleMainView.findViewById<TextView>(R.id.surnameTextField).text.isEmpty() || responsibleMainView.findViewById<TextView>(R.id.firstnameTextField).text.isEmpty()){
                    runOnUiThread {
                        Toast.makeText(context, "Введите фамилию и имя", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val umkaNumber = umkaNumberTextField.text.toString()

                login = responsibleMainView.findViewById<TextView>(R.id.newLoginTextField).text.toString()
                val passwordBefore = responsibleMainView.findViewById<TextView>(R.id.newPasswordTextField).text.toString()
                var password = convertPassword(passwordBefore)
                if(passwordBefore.equals("")){
                    password = ""
                }

                val listOfObjects = JSONArray()
                for(i in WebSocketFactory.getInstance().setOfObjects){
                    val newJSONObject = JsonPrimitive(i.objectID.toLong())
                    listOfObjects.put(newJSONObject)
                }
                var responsibleIDsend = ""
                if(tempResponsibleID == ""){
                    responsibleIDsend = "-1"
                }
                else{
                    responsibleIDsend = tempResponsibleID
                }
                val listOfKeys = JSONArray()
                for(i in 0..keyArray.size-1){
                    val newJSONKey = JSONObject().put("keyID", keyArray[i].keyID).put("deviceID",keyArray[i].deviceID).put("value", keyArray[i].value)
                    listOfKeys.put(i, newJSONKey)
                }

                val message = JSONObject()
                    .put("action","edit")
                    .put("result",
                                        JSONObject().put("responsible",
                                                                            JSONObject()
                                                                                        .put("responsibleID", responsibleIDsend)
                                                                                        .put("surname", newSurname)
                                                                                        .put("name", newFirstname)
                                                                                        .put("patronymic", newOtchestvo)
                                                                                        .put("accessCustomerAccount", false)
                                                                                        .put("accessAlarmButton", accessAlarmButton.isChecked)
                                                                                        .put("accessUmka", accessUmka.isChecked)
                                                                                        .put("writeTrackOfMovement",writeTrackOfMovement.isChecked)
                                                                                        .put("login", login)
                                                                                        .put("password", password)
                                                                                        .put("objects", listOfObjects)
                                                                                        .put("deviceKeys",listOfKeys)
                                                                                        .put("umkaID", umkaNumber)))
                WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message.toString())
                Log.v("Message", message.toString())
                sleep(500)
                if(errorFlag){
                    Toast.makeText(context, errorDescription, Toast.LENGTH_SHORT).show()
                    errorFlag = false
                    return
                }
                keyArray.clear()

                updateResponsible = false
                newSurname = ""
                newFirstname = ""
                newOtchestvo = ""
                responsibleMainView.findViewById<CheckBox>(R.id.accessAlarmButton).isChecked = false
                responsibleMainView.findViewById<CheckBox>(R.id.accessUmka).isChecked = false
                responsibleMainView.findViewById<CheckBox>(R.id.writeTrackOfMovement).isChecked = false
                responsibleMainView.findViewById<EditText>(R.id.surnameTextField).text.clear()
                responsibleMainView.findViewById<EditText>(R.id.firstnameTextField).text.clear()
                responsibleMainView.findViewById<EditText>(R.id.otchestvoTextField).text.clear()
                responsibleMainView.findViewById<TextView>(R.id.newFIOTextField).text = ""
                responsibleMainView.findViewById<EditText>(R.id.newLoginTextField).text.clear()
                responsibleMainView.findViewById<TextView>(R.id.newPasswordTextField).text = ""
                addNewKeyButton.visibility = View.GONE
                layoutOfKeys.visibility = View.GONE
                val message4 = JSONObject()
                    .put("action","get")
                    .put("result", JSONObject().put("responsibles",""))
                WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message4.toString())
                startRunnable()
                responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.GONE
                responsibleMainView.findViewById<ConstraintLayout>(R.id.addNewResponsible).visibility = View.VISIBLE
            }
            R.id.removeResponsible ->{
                val mBuilder = AlertDialog.Builder(context!!)
                mBuilder.setTitle("Вы действительно хотите удалить абонента?")
                mBuilder.setCancelable(false)
                mBuilder.setPositiveButton("Да", DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                    WebSocketFactory.getInstance().setOfResponsibles.clear()
                    val message4 = JSONObject()
                        .put("action","delete")
                        .put("result", JSONObject().put("responsible",JSONObject().put("responsibleID", tempResponsibleID)))
                    WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message4.toString())
                    val message5 = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject().put("responsibles",""))
                    WebSocketFactory.getInstance().mapSockets.get(mainSocketString)?.send(message5.toString())
                    sleep(500)
                    startRunnable()
                    responsibleMainView.findViewById<ScrollView>(R.id.myScrollView).visibility = View.GONE
                    responsibleMainView.findViewById<ConstraintLayout>(R.id.addNewResponsible).visibility = View.VISIBLE
                    responsibleMainView.findViewById<Button>(R.id.removeResponsible).visibility = View.GONE
                })
                mBuilder.setNegativeButton("Нет") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("OnDest", "yes")
        myHandler.removeCallbacks(myRunnable)
    }


    val itemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            parent?.setSelection(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            tempDeviceType = parent?.getItemAtPosition(position) as String
        }

    }
}