package ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import org.jetbrains.anko.find
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.MapActivity
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.changeFlagSection
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.tempObject
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.helpers.TimeConverter
import ru.elesta.jupiter_lkselfguard.dataClasses.EventClass
import ru.elesta.jupiter_lkselfguard.helpers.PreloaderClass
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS.Companion.errorDescription
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketListenerKROS.Companion.errorFlag
import java.util.*


@RequiresApi(Build.VERSION_CODES.M)
class HomeFragmentSections: Fragment(), View.OnScrollChangeListener {


    companion object{

        //var sizeOfEventsOneArrayJson:Int? = null
        var addEvent = false
        var loadingOfLocalEventsFinished = false
        var loadingOfSectionsFinished = false
        var showRemoveAlarmButton: Boolean? = null
        var itIsAlarmButtonType = false
        var guardStatusIsUnknown = false
        var loadingOfTrackIsFinished = false
    }

    var allOnGuard = false
    var tempSection = ""

    private var localeSetOfEventsOne = ArrayList<EventClass>()

    lateinit var tableOfSections: TableLayout
    lateinit var tableOfEvents: TableLayout
    lateinit var editCustomNameLayout: ConstraintLayout
    lateinit var saveEditCustomNameSectionButton: Button
    lateinit var scrollView2: ScrollView
    lateinit var showMapButton: Button
    lateinit var sectionsLinearLayout: ConstraintLayout
    lateinit var scrollViewOfEvents: ScrollView

    lateinit var eventsOneLinearLayout: LinearLayout

    lateinit var currentObjectContraintLayout: ConstraintLayout

    lateinit var armAllButton: Button
    lateinit var sectionsView: View

    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    lateinit var myHandler2: Handler
    lateinit var myRunnable2: Runnable

    lateinit var myShowRemoveAlarmButtonHandler: Handler
    lateinit var myShowRemoveAlarmButtonRunnable: Runnable

    lateinit var myButtonHandler: Handler
    lateinit var myButtonRunnable: Runnable

    lateinit var myTrackHandler: Handler
    lateinit var myTrackRunnable: Runnable

    lateinit var myTableOfSectionsRunnable: Runnable
    lateinit var myTableOfSectionsHandler: Handler

    lateinit var myTableOfEventsRunnable: Runnable
    lateinit var myTableOfEventsHandler: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.home_fragment_sections, null)
        sectionsLinearLayout = v.findViewById(R.id.sectionsLinearLayout)
        sectionsView = v
        if(itIsAlarmButtonType){
            v.findViewById<Button>(R.id.guardAllButton).visibility = View.GONE
        }
        else{
            v.findViewById<Button>(R.id.guardAllButton).visibility = View.VISIBLE
        }
        if(guardStatusIsUnknown){
            v.findViewById<Button>(R.id.guardAllButton).visibility = View.GONE
        }
        else{
            v.findViewById<Button>(R.id.guardAllButton).visibility = View.VISIBLE
        }
        scrollView2 = v.findViewById(R.id.scrollView2)

        tableOfSections = v.findViewById(R.id.tableOfSections)
        tableOfEvents = v.findViewById(R.id.tableOfEvents)
        editCustomNameLayout = v.findViewById(R.id.editCustomNameLayout)
        showMapButton = v.findViewById(R.id.showMapButton)
        scrollViewOfEvents = v.findViewById(R.id.scrollViewOfEvents)
        currentObjectContraintLayout = v.findViewById(R.id.currentObjectContraintLayout)
        eventsOneLinearLayout = v.findViewById(R.id.eventsOneLinearLayout)
        showMapButton.setOnClickListener{
            val intent = Intent(activity, MapActivity::class.java)
            intent.putExtra("tempobject", tempObject?.objectID)
            if (tempObject?.typeID != "11") {
                activity?.startActivity(intent)
                return@setOnClickListener
            }
            val message1 = JSONObject()
                .put("action","get")
                .put("result", JSONObject()
                    .put("track", JSONObject()
                        .put("objectID", tempObject?.objectID?.toLong())))
            WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message1.toString())
            myTrackHandler = Handler()
            myTrackRunnable = Runnable {
                myTrackHandler.postDelayed(myTrackRunnable, 100)
                if(loadingOfTrackIsFinished){
                    loadingOfTrackIsFinished = false
                    myTrackHandler.removeCallbacks(myTrackRunnable)
                    activity?.startActivity(intent)
                }
            }
            myTrackHandler.post(myTrackRunnable)
        }
        saveEditCustomNameSectionButton = v.findViewById(R.id.saveEditSectionName)
        editCustomNameLayout.setOnClickListener{
            editCustomNameLayout.visibility = View.GONE
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(sectionsView.findViewById<TextView>(R.id.editSectionNameText).windowToken, 0)
        }
        saveEditCustomNameSectionButton.setOnClickListener{
            val message1 = JSONObject()
                .put("action","edit")
                .put("result", JSONObject()
                    .put("section", JSONObject()
                        .put("sectionID", tempSection)
                        .put("customName",v.findViewById<TextView>(R.id.editSectionNameText).text)))
            WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message1.toString())
            editCustomNameLayout.visibility = View.GONE

        }
        scrollViewOfEvents.setOnScrollChangeListener(this)

        sectionsView.findViewById<Button>(R.id.removeAlarmButton).setOnClickListener{
            val popupMenu = PopupMenu(activity, sectionsView.findViewById<Button>(R.id.removeAlarmButton))
            popupMenu.inflate(R.menu.popupmenu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu1 -> {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:102")
                        startActivity(intent)
                        true
                    }
                    R.id.menu2 -> {
                        val message1 = JSONObject()
                            .put("action","cancel")
                            .put("result", JSONObject()
                                .put("alarm", JSONObject()
                                    .put("objectID", tempObject?.objectID)))
                        WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message1.toString())
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        sectionsView.findViewById<ConstraintLayout>(R.id.guardNotGuardSectionLayout).setOnClickListener{
            sectionsView.findViewById<ConstraintLayout>(R.id.guardNotGuardSectionLayout).visibility = View.GONE
        }

        sectionsView.findViewById<Button>(R.id.guardNotGuardButton).setOnClickListener{
            PreloaderClass.instance.startPreloader(requireContext(), currentObjectContraintLayout)
            for(i in WebSocketFactory.getInstance().setOfSections) {
                if(i.sectionID == tempSection) {
                    if (i.guardStatus == "Guard") {
                        val message = JSONObject()
                            .put("action", "disarm")
                            .put(
                                "result", JSONObject().put("object", tempObject!!.objectID.toInt())
                                    .put("section", i.sectionID)
                            )
                        WebSocketFactory.getInstance().mapSockets[tempObject!!.socketNumber]?.send(message.toString())
                    }
                    if (i.guardStatus == "Partial") {
                        val message = JSONObject()
                            .put("action", "disarm")
                            .put(
                                "result", JSONObject().put("object", tempObject!!.objectID.toInt())
                                    .put("section", i.sectionID)
                            )
                        WebSocketFactory.getInstance().mapSockets[tempObject!!.socketNumber]?.send(message.toString())
                    }
                    if (i.guardStatus == "NotGuard") {
                        val message = JSONObject()
                            .put("action", "arm")
                            .put(
                                "result", JSONObject().put("object", tempObject!!.objectID.toInt())
                                    .put("section", i.sectionID)
                            )
                        WebSocketFactory.getInstance().mapSockets[tempObject!!.socketNumber]?.send(message.toString())
                    }
                }
            }
            sectionsView.findViewById<ConstraintLayout>(R.id.guardNotGuardSectionLayout).visibility = View.GONE
        }

//        myShowRemoveAlarmButtonHandler = Handler()
//        myShowRemoveAlarmButtonRunnable = Runnable {
//            if(showRemoveAlarmButton != null) {
//                if (showRemoveAlarmButton!!) {
//                    sectionsView.findViewById<Button>(R.id.removeAlarmButton).visibility = View.VISIBLE
//                }
//                if (!showRemoveAlarmButton!!) {
//                    sectionsView.findViewById<Button>(R.id.removeAlarmButton).visibility = View.GONE
//                }
//                myShowRemoveAlarmButtonHandler.postDelayed(myShowRemoveAlarmButtonRunnable, 50)
//            } else {
//                myShowRemoveAlarmButtonHandler.removeCallbacks(myShowRemoveAlarmButtonRunnable)
//            }
//        }
//        sectionsView.findViewById<Button>(R.id.removeAlarmButton).visibility = View.GONE
//        myShowRemoveAlarmButtonHandler.post(myShowRemoveAlarmButtonRunnable)
//
//        myTableOfSectionsHandler = Handler()
//        myTableOfSectionsRunnable = Runnable{
//            myTableOfSectionsHandler.postDelayed(myTableOfSectionsRunnable,200)
//            if(loadingOfSectionsFinished){
//                loadingOfSectionsFinished = false
//                getSections()
//                myTableOfSectionsHandler.removeCallbacks(myTableOfSectionsRunnable)
//            }
//        }
//        myTableOfSectionsHandler.post(myTableOfSectionsRunnable)
//
//        myTableOfEventsHandler = Handler()
//        myTableOfEventsRunnable = Runnable{
//            myTableOfEventsHandler.postDelayed(myTableOfEventsRunnable,200)
//            //if(sizeOfEventsOneArrayJson == WebSocketFactory.getInstance().setOfEventsOne.size){
//            if(loadingOfLocalEventsFinished){
//                loadingOfLocalEventsFinished = false
//                localeSetOfEventsOne.addAll(WebSocketFactory.getInstance().setOfEventsOne)
//                getEvents()
//                myTableOfEventsHandler.removeCallbacks(myTableOfEventsRunnable)
//            }
//        }
//        myTableOfEventsHandler.post(myTableOfEventsRunnable)
//
//        createSectionsRunnable()
//        createEventsRunnable()
//        createButtonRunnable()
        armAllButton = sectionsView.findViewById(R.id.guardAllButton)
        changeButton()
        armAllButton.setOnClickListener{
            PreloaderClass.instance.startPreloader(requireContext(), currentObjectContraintLayout)
            if(allOnGuard){
                val message = JSONObject()
                    .put("action","disarm")
                    .put("result", JSONObject().put("object", tempObject!!.objectID.toInt())
                        .put("section", -1))
                WebSocketFactory.getInstance().mapSockets[tempObject!!.socketNumber]?.send(message.toString())
            }
            else{
                val message = JSONObject()
                    .put("action","arm")
                    .put("result", JSONObject().put("object", tempObject!!.objectID.toInt())
                        .put("section", -1))
                WebSocketFactory.getInstance().mapSockets[tempObject!!.socketNumber]?.send(message.toString())
            }
        }
        for(i in WebSocketFactory.getInstance().setOfObjects) {
            if (tempObject!!.objectID == i.objectID){
                if(i.typeID == "11"){
                    v.findViewById<Button>(R.id.guardAllButton).visibility = View.GONE
                }
            }
        }
        return v
    }

    fun createButtonRunnable(){
        myButtonHandler = Handler()
        myButtonRunnable = Runnable {
            allOnGuard = true
            for(i in WebSocketFactory.getInstance().setOfSections){
               if(i.guardStatus == "Guard"){
                   continue
               }
               if(i.guardStatus == "NotGuard"){
                   allOnGuard = false
               }
            }
            changeButton()
            myButtonHandler.postDelayed(myButtonRunnable, 100)
        }
        myButtonHandler.post(myButtonRunnable)
    }

    fun createSectionsRunnable(){
        myHandler = Handler()
        myRunnable = Runnable {
            if(errorFlag){
                Toast.makeText(requireContext(), errorDescription, Toast.LENGTH_SHORT).show()
                errorFlag = false
                PreloaderClass.instance.stopPreloader(currentObjectContraintLayout)
            }
            if(changeFlagSection) {
                PreloaderClass.instance.stopPreloader(currentObjectContraintLayout)
                cleanSectionsTable()
                getSections()
                changeFlagSection = false
            }
            myHandler.postDelayed(myRunnable,100)
        }
        myHandler.post(myRunnable)
    }

    fun createEventsRunnable(){
        myHandler2 = Handler()
        myRunnable2 = Runnable {
            if(PreloaderClass.instance.flag){
                if(loadingOfLocalEventsFinished) {
                    loadingOfLocalEventsFinished = false
                    localeSetOfEventsOne.clear()
                    cleanEventsTable()
                    localeSetOfEventsOne.addAll(WebSocketFactory.getInstance().setOfEventsOne)
                    getEvents()
                    PreloaderClass.instance.stopPreloader(eventsOneLinearLayout)
                }
            }
            if(addEvent) {
                addEvent = false
                localeSetOfEventsOne.clear()
                cleanEventsTable()
                localeSetOfEventsOne.addAll(WebSocketFactory.getInstance().setOfEventsOne)
                getEvents()
            }
            myHandler2.postDelayed(myRunnable2,100)
        }
        myHandler2.post(myRunnable2)
    }

    fun cleanSectionsTable(){
        tableOfSections.removeAllViews()
    }

    fun cleanEventsTable(){
        tableOfEvents.removeAllViews()
    }

    fun getSections(){
        val display: Display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        Log.v("Получили", "Все секции")
        Log.v("Cекций в getSections", WebSocketFactory.getInstance().setOfSections.size.toString())
        //WebSocketFactory.getInstance().setOfSections.sortedBy { it.indexOfSection }
        val numberOfRows = Math.ceil((WebSocketFactory.getInstance().setOfSections.size.toDouble() / 2)).toInt()-1
        for(q in 0..numberOfRows) {
            val tableRow = TableRow(activity)
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.bottomMargin = 20
            tableRow.layoutParams = lp
            if (!WebSocketFactory.getInstance().setOfSections.isEmpty()) {
                for (i in WebSocketFactory.getInstance().setOfSections) {
                    val setOfSectionsIndex = WebSocketFactory.getInstance().setOfSections.indexOf(i)
                    if(setOfSectionsIndex == q || setOfSectionsIndex == q + Math.ceil((WebSocketFactory.getInstance().setOfSections.size.toDouble() / 2)).toInt()) {
                        var tempDeviceType = ""
                        for (j in WebSocketFactory.getInstance().commonSetOfDevices) {
                            if (i.deviceID == j.deviceID) {
                                tempDeviceType = j.type
                            }
                        }
                        val outerConstraintLayout = ConstraintLayout(requireContext())
                        val lpOuterConstraintLayout = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, (0.06566347469 * height).toInt())
                        lpOuterConstraintLayout.width = (tableOfSections.width / 2)-40
                        lpOuterConstraintLayout.rightMargin = 20
                        outerConstraintLayout.layoutParams = lpOuterConstraintLayout
                        val shared = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                        val darkTheme = shared.getBoolean("dark", true)
                        if(darkTheme) {
                            outerConstraintLayout.setBackgroundResource(R.drawable.roundcornersectionsdarktheme)
                        }
                        else{
                            outerConstraintLayout.setBackgroundResource(R.drawable.roundcornerssectionslighttheme)
                        }
                        outerConstraintLayout.setPadding(10, 10, 10, 10)

                        val numberOfSection = TextView(activity)
                        val lpNumberOfSection = TableRow.LayoutParams(0, 0)
                        lpNumberOfSection.rightMargin = 30
                        numberOfSection.layoutParams = lpNumberOfSection
                        numberOfSection.text = i.indexOfSection
                        numberOfSection.textSize = 25F
                        numberOfSection.gravity = Gravity.CENTER
                        if(darkTheme){
                            numberOfSection.setTextColor(Color.WHITE)
                        }
                        else{
                            numberOfSection.setTextColor(Color.BLACK)
                        }
                        numberOfSection.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)

                        val nameOfSection = TextView(activity)
                        nameOfSection.textSize = 12F
                        val lpNameOfSection = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, nameOfSection.lineHeight)
                        nameOfSection.layoutParams = lpNameOfSection
                        if(i.customName.equals("")){
                            nameOfSection.text = "Псевдоним" + i.indexOfSection
                        }
                        else{
                            nameOfSection.text = i.customName
                        }
                        if(darkTheme){
                            nameOfSection.setTextColor(Color.WHITE)
                        }
                        else{
                            nameOfSection.setTextColor(Color.BLACK)
                        }

                        val nameOfDevice = TextView(activity)
                        nameOfDevice.textSize = 10F
                        val lpNameOfDevice = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, nameOfDevice.lineHeight)
                        nameOfDevice.layoutParams = lpNameOfDevice
                        nameOfDevice.text = tempDeviceType
                        if(darkTheme){
                            nameOfDevice.setTextColor(Color.WHITE)
                        }
                        else{
                            nameOfDevice.setTextColor(Color.BLACK)
                        }

                        val statusOfSection = TextView(activity)
                        statusOfSection.textSize = 12F
                        val lpStatusOfSection = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, statusOfSection.lineHeight)
                        statusOfSection.layoutParams = lpStatusOfSection
                        statusOfSection.text = tempDeviceType
                        if(darkTheme){
                            statusOfSection.setTextColor(Color.WHITE)
                        }
                        else{
                            statusOfSection.setTextColor(Color.BLACK)
                        }

                        if(i.guardStatus == "NotGuard"){
                            statusOfSection.text = "Снят с охраны"
                            statusOfSection.setTextColor(Color.parseColor( "#696A6D"))
                        }

                        if(i.guardStatus == "Guard"){
                            statusOfSection.text = "На охране"
                            statusOfSection.setTextColor(Color.parseColor( "#1DB505"))
                        }

                        if(i.faultStatus == "Fault"){
                            statusOfSection.text = "Неисправен"
                            statusOfSection.setTextColor(Color.parseColor( "#FFC908"))
                        }
                        if(i.alarmStatus == "Alarm"){
                            statusOfSection.text = "Тревога"
                            statusOfSection.setTextColor(Color.parseColor( "#DB0014"))
                        }

                        outerConstraintLayout.addView(numberOfSection)
                        outerConstraintLayout.addView(nameOfSection)
                        outerConstraintLayout.addView(nameOfDevice)
                        outerConstraintLayout.addView(statusOfSection)
                        tableRow.addView(outerConstraintLayout)

                        numberOfSection.id = View.generateViewId()
                        nameOfSection.id = View.generateViewId()
                        nameOfDevice.id = View.generateViewId()
                        statusOfSection.id = View.generateViewId()
                        outerConstraintLayout.id = View.generateViewId()

                        //numberOfSection.layoutParams.height = (outerConstraintLayout.layoutParams.height * 0.8).toInt()
                        //numberOfSection.layoutParams.width = numberOfSection.layoutParams.height

                        val constraintSet = ConstraintSet()
                        constraintSet.clone(outerConstraintLayout)
                        constraintSet.connect(numberOfSection.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                        constraintSet.connect(numberOfSection.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                        constraintSet.connect(numberOfSection.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                        constraintSet.constrainHeight(numberOfSection.id, ConstraintSet.MATCH_CONSTRAINT)
                        constraintSet.setDimensionRatio(numberOfSection.id, "1:1")
                        constraintSet.connect(nameOfSection.id, ConstraintSet.LEFT, numberOfSection.id, ConstraintSet.RIGHT, 30)
                        constraintSet.connect(nameOfSection.id, ConstraintSet.TOP, numberOfSection.id, ConstraintSet.TOP, 0)
                        constraintSet.connect(nameOfDevice.id, ConstraintSet.LEFT, numberOfSection.id, ConstraintSet.RIGHT, 30)
                        constraintSet.centerVertically(nameOfDevice.id, numberOfSection.id)
                        constraintSet.connect(statusOfSection.id, ConstraintSet.LEFT, numberOfSection.id, ConstraintSet.RIGHT, 30)
                        constraintSet.connect(statusOfSection.id, ConstraintSet.BOTTOM, numberOfSection.id, ConstraintSet.BOTTOM, 0)
                        constraintSet.applyTo(outerConstraintLayout)


                        //numberOfSection.width = numberOfSection.height

                        outerConstraintLayout.setOnLongClickListener{
                            editCustomNameLayout.visibility = View.VISIBLE
                            tempSection = i.sectionID
                            sectionsView.findViewById<TextView>(R.id.editSectionNameText).text = i.customName
                            true
                        }
                        outerConstraintLayout.setOnClickListener{
                            sectionsView.findViewById<ConstraintLayout>(R.id.guardNotGuardSectionLayout).visibility = View.VISIBLE
                            tempSection = i.sectionID
                            if(!i.customName.equals("")) {
                                sectionsView.findViewById<TextView>(R.id.chosenSection).text = i.customName
                            }
                            else{
                                sectionsView.findViewById<TextView>(R.id.chosenSection).text = "Псевдоним" + i.indexOfSection
                            }
                            sectionsView.findViewById<TextView>(R.id.numberOfChosenSection).text = i.indexOfSection

                            sectionsView.findViewById<TextView>(R.id.numberOfChosenSection).layoutParams.height = (sectionsView.findViewById<ConstraintLayout>(R.id.outerViewForSectionGuard).layoutParams.height * 0.22891566265).toInt()
                            sectionsView.findViewById<TextView>(R.id.chosenSection).layoutParams.height = sectionsView.findViewById<TextView>(R.id.numberOfChosenSection).layoutParams.height
                            if(i.guardStatus == "Guard"){
                                sectionsView.findViewById<Button>(R.id.guardNotGuardButton).setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)
                                sectionsView.findViewById<Button>(R.id.guardNotGuardButton).text = "Cнять с охраны"
                            }
                            if(i.guardStatus == "Partial"){
                                sectionsView.findViewById<Button>(R.id.guardNotGuardButton).setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)
                                sectionsView.findViewById<Button>(R.id.guardNotGuardButton).text = "Cнять с охраны"
                            }
                            if(i.guardStatus == "NotGuard"){
                                sectionsView.findViewById<Button>(R.id.guardNotGuardButton).setBackgroundResource(R.drawable.roundcornersgreenbuttonondevicescreen)
                                sectionsView.findViewById<Button>(R.id.guardNotGuardButton).text = "Взять на охрану"
                            }

                        }

                    }
                }
            }
            tableOfSections.addView(tableRow)
        }
    }

    fun getEvents(){
        var k = 0
        val display: Display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        for (i in localeSetOfEventsOne) {
            //if(k>4) break
            val myTimeInMillis = Calendar.getInstance().timeInMillis
            val dayInMillis = 86400000
            val timeIntervalInMillis = myTimeInMillis - i.receiveTime.toLong()
            val timeIntervalInMillisToStartDay = myTimeInMillis % dayInMillis
            val countOfDays = ((timeIntervalInMillis - timeIntervalInMillisToStartDay + 86400000) / 86400000).toInt()

            val tableRow = TableRow(activity)
            val lp = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 5
            lp.bottomMargin = 5
            tableRow.layoutParams = lp
            tableRow.setPadding(5,5,5,5)
            val shared = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            val darkTheme = shared.getBoolean("dark", true)
            if(darkTheme) {
                tableRow.setBackgroundResource(R.drawable.roundcornersectionsdarktheme)
            }
            else{
                tableRow.setBackgroundResource(R.drawable.roundcornerssectionslighttheme)
            }

            val innerConstraintLayout = ConstraintLayout(requireContext())
            val innerConstraintLayoutLP = TableRow.LayoutParams((tableOfEvents.width * 0.99).toInt(), (0.04924760601 * height).toInt())
            innerConstraintLayout.layoutParams = innerConstraintLayoutLP

            val imageOfGuard = ImageView(activity)
            if (i.bgColor.equals("#858585")) {
                imageOfGuard.setImageResource(R.drawable.ic_lock_not_guard)
                imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            if (i.bgColor.equals("#4ea03e")) {
                imageOfGuard.setImageResource(R.drawable.ic_lock_guard)
                imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            if (i.bgColor.equals("#e0995e")) {
                imageOfGuard.setImageResource(R.drawable.ic_fault_event)
                imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            if (i.bgColor.equals("#e0675e")) {
                imageOfGuard.setImageResource(R.drawable.ic_bell_alarm)
                imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
                tableRow.setOnClickListener {
                    val intent = Intent(activity, MapActivity::class.java)
                    intent.putExtra("tempobject", tempObject?.objectID)
                    if (tempObject?.typeID != "11") {
                        activity?.startActivity(intent)
                        return@setOnClickListener
                    }
                    val message1 = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject()
                            .put("track", JSONObject()
                                .put("objectID", tempObject?.objectID?.toLong())))
                    WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message1.toString())
                    myTrackHandler = Handler()
                    myTrackRunnable = Runnable {
                        myTrackHandler.postDelayed(myTrackRunnable, 100)
                        if(loadingOfTrackIsFinished){
                            loadingOfTrackIsFinished = false
                            myTrackHandler.removeCallbacks(myTrackRunnable)
                            activity?.startActivity(intent)
                        }
                    }
                    myTrackHandler.post(myTrackRunnable)
                }
            }
            if (i.bgColor.equals("#ffffff") || i.bgColor.equals("#FFFFFF")) {
                imageOfGuard.setImageResource(R.drawable.ic_info_blue_icon)
                imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            val lp1 = TableRow.LayoutParams(0, 0)
            imageOfGuard.layoutParams = lp1
            imageOfGuard.setPadding(15,15,15,15)
            imageOfGuard.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)


            val textOfMessage = TextView(activity)
            var sensor = "шлейф " + i.sensor
            if(i.sensor == "-1"){
                sensor = ""
            }
            var razdel = ", раздел " + i.section + ", "
            if(i.section == "-1"){
                razdel = ""
                sensor = ""
            }
            var pribor = ", Прибор " + i.deviceNumber
            if(i.deviceNumber == "прибор не найден"){
                pribor = ""
            }
            for(j in WebSocketFactory.getInstance().setOfDevices){
                if(i.deviceID == j.deviceID){
                    pribor = j.type
                }
            }
            var objText = ""
            val obj = i.objectID
            for(j in WebSocketFactory.getInstance().setOfObjects){
                if(j.objectID == obj){
                    objText = j.customName
                }
            }
            textOfMessage.text = i.message  + " " + i.info + ", объект " + objText + " " + pribor + razdel + sensor
            if(darkTheme){
                textOfMessage.setTextColor(Color.WHITE)
            }
            else{
                textOfMessage.setTextColor(Color.BLACK)
            }
            val textOfMessageLP = TableRow.LayoutParams(0, 0)
            textOfMessage.layoutParams = textOfMessageLP
            textOfMessage.gravity = Gravity.CENTER_VERTICAL
            textOfMessage.textSize = 10F

            var dateForLabel = "${TimeConverter().convertDateForLongTime(i.receiveTime)} "
            if (countOfDays == 0) {
                dateForLabel = "Сегодня "
            }
            if (countOfDays == 1) {
                dateForLabel = "Вчера "
            }


            val receiveTime = TextView(activity)
            receiveTime.text = dateForLabel + TimeConverter().convertTime(i.receiveTime)
            if(darkTheme){
                receiveTime.setTextColor(Color.WHITE)
            }
            else{
                receiveTime.setTextColor(Color.BLACK)
            }
            receiveTime.textSize = 12F
            receiveTime.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)
            val receiveTimelp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 0)
            receiveTime.layoutParams = receiveTimelp
            receiveTime.gravity = Gravity.CENTER
            receiveTime.setPadding(10,0,10,0)
            tableRow.addView(innerConstraintLayout)
            innerConstraintLayout.addView(imageOfGuard)
            innerConstraintLayout.addView(textOfMessage)
            innerConstraintLayout.addView(receiveTime)
            innerConstraintLayout.id = View.generateViewId()
            imageOfGuard.id = View.generateViewId()
            textOfMessage.id = View.generateViewId()
            receiveTime.id = View.generateViewId()

            val constraintSet = ConstraintSet()
            constraintSet.clone(innerConstraintLayout)
            constraintSet.connect(imageOfGuard.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            constraintSet.connect(imageOfGuard.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(imageOfGuard.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.connect(receiveTime.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            constraintSet.connect(receiveTime.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(receiveTime.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraintSet.connect(textOfMessage.id, ConstraintSet.RIGHT, receiveTime.id, ConstraintSet.LEFT, 10)
            constraintSet.connect(textOfMessage.id, ConstraintSet.LEFT, imageOfGuard.id, ConstraintSet.RIGHT, 10)
            constraintSet.connect(textOfMessage.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(textOfMessage.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

            constraintSet.constrainHeight(imageOfGuard.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainHeight(receiveTime.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainHeight(textOfMessage.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainWidth(textOfMessage.id, ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.setDimensionRatio(imageOfGuard.id, "1:1")
            constraintSet.applyTo(innerConstraintLayout)

            tableOfEvents.addView(tableRow, k)
            k++
        }
    }

    fun changeButton(){
        if(allOnGuard == false){
            armAllButton.setBackgroundResource(R.drawable.roundcornersgreenbuttonondevicescreen)
            armAllButton.text = "Взять на охрану"
        }
        if(allOnGuard == true){
            armAllButton.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)
            armAllButton.text = "Снять с охраны"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("HomeFragmentSections", "onDestroyView")
        Log.v("---", "-----------------------------------------")
        cleanSectionsTable()
        cleanEventsTable()
        myHandler.removeCallbacks(myRunnable)
        myHandler2.removeCallbacks(myRunnable2)
        myButtonHandler.removeCallbacks(myButtonRunnable)
    }




    override fun onPause() {
        super.onPause()
        Log.v("HomeFragmentSections", "onPause")
        Log.v("---", "-----------------------------------------")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //WebSocketFactory.getInstance().setOfEventsOne.clear()
        Log.v("HomeFragmentSections", "onAttach")
        Log.v("---", "-----------------------------------------")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("HomeFragmentSections", "onActivityCreated")
        Log.v("---", "-----------------------------------------")
    }

    override fun onStart() {
        super.onStart()
        Log.v("HomeFragmentSections", "onStart")
        Log.v("---", "-----------------------------------------")
        myShowRemoveAlarmButtonHandler = Handler()
        myShowRemoveAlarmButtonRunnable = Runnable {
            if(showRemoveAlarmButton != null) {
                if (showRemoveAlarmButton!!) {
                    sectionsView.findViewById<Button>(R.id.removeAlarmButton).visibility = View.VISIBLE
                }
                if (!showRemoveAlarmButton!!) {
                    sectionsView.findViewById<Button>(R.id.removeAlarmButton).visibility = View.GONE
                }
                myShowRemoveAlarmButtonHandler.postDelayed(myShowRemoveAlarmButtonRunnable, 50)
            } else {
                myShowRemoveAlarmButtonHandler.removeCallbacks(myShowRemoveAlarmButtonRunnable)
            }
        }
        sectionsView.findViewById<Button>(R.id.removeAlarmButton).visibility = View.GONE
        myShowRemoveAlarmButtonHandler.post(myShowRemoveAlarmButtonRunnable)

        myTableOfSectionsHandler = Handler()
        myTableOfSectionsRunnable = Runnable{
            myTableOfSectionsHandler.postDelayed(myTableOfSectionsRunnable,200)
            if(loadingOfSectionsFinished){
                loadingOfSectionsFinished = false
                getSections()
                myTableOfSectionsHandler.removeCallbacks(myTableOfSectionsRunnable)
            }
        }
        myTableOfSectionsHandler.post(myTableOfSectionsRunnable)

        myTableOfEventsHandler = Handler()
        myTableOfEventsRunnable = Runnable{
            myTableOfEventsHandler.postDelayed(myTableOfEventsRunnable,200)
            //if(sizeOfEventsOneArrayJson == WebSocketFactory.getInstance().setOfEventsOne.size){
            if(loadingOfLocalEventsFinished){
                loadingOfLocalEventsFinished = false
                localeSetOfEventsOne.addAll(WebSocketFactory.getInstance().setOfEventsOne)
                getEvents()
                myTableOfEventsHandler.removeCallbacks(myTableOfEventsRunnable)
            }
        }
        myTableOfEventsHandler.post(myTableOfEventsRunnable)
        createSectionsRunnable()
        createEventsRunnable()
        createButtonRunnable()
    }

    override fun onResume() {
        super.onResume()
        Log.v("HomeFragmentSections", "onResume")
        Log.v("---", "-----------------------------------------")
    }


    override fun onStop() {
        super.onStop()
        Log.v("HomeFragmentSections", "onStop")
        Log.v("---", "-----------------------------------------")
        myHandler.removeCallbacks(myRunnable)
        myHandler2.removeCallbacks(myRunnable2)
        myShowRemoveAlarmButtonHandler.removeCallbacks(myShowRemoveAlarmButtonRunnable)
        myTableOfSectionsHandler.removeCallbacks(myTableOfSectionsRunnable)
        myTableOfEventsHandler.removeCallbacks(myTableOfEventsRunnable)
        myButtonHandler.removeCallbacks(myButtonRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("HomeFragmentSections", "onDestroy")
        Log.v("---", "-----------------------------------------")
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("HomeFragmentSections", "onDetach")
        Log.v("---", "-----------------------------------------")
    }

    override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        Log.e("Compare Events", "${scrollViewOfEvents.scrollY} || ${scrollViewOfEvents.getChildAt(0).height - scrollViewOfEvents.height}")
        if(scrollViewOfEvents.scrollY == (scrollViewOfEvents.getChildAt(0).height - scrollViewOfEvents.height)){
            if(!PreloaderClass.instance.flag) {
                PreloaderClass.instance.startPreloader(requireContext(), eventsOneLinearLayout)
            }
            LKActivity.loadingOfEventsFinished = false
            //localeSetOfEventsOne.clear()
            val message2 = JSONObject()
                .put("action","get")
                .put("result", JSONObject()
                    .put("events", JSONObject()
                        .put("limit",20).put("object", tempObject?.objectID).put("envelopeID", WebSocketFactory.getInstance().setOfEventsOne.last().envelopeID)))
            WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message2.toString())
            //createEventsRunnable()
        }
    }
}