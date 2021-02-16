package ru.elesta.jupiter_lkselfguard.EventsFragment

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import org.jetbrains.anko.sdk27.coroutines.onScrollChange
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.helpers.PreloaderClass
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.helpers.TimeConverter
import ru.elesta.jupiter_lkselfguard.dataClasses.EventClass
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.util.*

@RequiresApi(Build.VERSION_CODES.M)
class EventsFragment : Fragment(), View.OnScrollChangeListener {


    companion object{
        var addMainEvent = false
    }

    var showEventsFragment = false


    lateinit var mainTableOfEvents: TableLayout

    private var localeSetOfMainEvents = ArrayList<EventClass>()

    lateinit var eventsMainConstraintLayout: ConstraintLayout
    lateinit var scrollViewMainEvents: ScrollView
    lateinit var viewOfEvents: View
    lateinit var myRunnable: Runnable
    lateinit var myHandler: Handler

    lateinit var myRunnable2: Runnable
    lateinit var myHandler2: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_events, null)
        showEventsFragment = true
        viewOfEvents = v
        eventsMainConstraintLayout = v.findViewById(R.id.eventsMainConstraintLayout)
        mainTableOfEvents = v.findViewById(R.id.mainTableOfEvents)
        scrollViewMainEvents = v.findViewById(R.id.scrollViewMainEvents)
        scrollViewMainEvents.setOnScrollChangeListener(this)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRunnable()
        createRunnable2()
    }

    fun createRunnable(){
        if(!PreloaderClass.instance.flag) {
            PreloaderClass.instance.startPreloader(requireContext(), eventsMainConstraintLayout)
        }
        myHandler = Handler()
        myRunnable = Runnable{
            if(LKActivity.loadingOfEventsFinished){
                if(showEventsFragment) {
                    localeSetOfMainEvents.addAll(WebSocketFactory.getInstance().setOfEvents)
                    cleanTable()
                    getMainEvents()
                    PreloaderClass.instance.stopPreloader(eventsMainConstraintLayout)
                    myHandler.removeCallbacks(myRunnable)
                }
            }
            else {
                myHandler.postDelayed(myRunnable, 100)
            }
        }
        myHandler.post(myRunnable)
    }

    fun createRunnable2(){
        myHandler2 = Handler()
        myRunnable2 = Runnable{
            if(addMainEvent){
                if(showEventsFragment) {
                    localeSetOfMainEvents.clear()
                    localeSetOfMainEvents.addAll(WebSocketFactory.getInstance().setOfEvents)
                    addMainEvent = false
                    cleanTable()
                    getMainEvents()
                    myHandler2.removeCallbacks(myRunnable2)
                }
            }
            myHandler2.postDelayed(myRunnable2, 100)
        }
        myHandler2.post(myRunnable2)
    }

    fun cleanTable(){
        mainTableOfEvents.removeAllViewsInLayout()
    }

    fun getMainEvents(){
        var k = 0
        val display: Display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        for (i in 0..localeSetOfMainEvents.size) {
            if(i < localeSetOfMainEvents.size) {
                val myTimeInMillis = Calendar.getInstance().timeInMillis
                val dayInMillis = 86400000
                val timeIntervalInMillis = myTimeInMillis - localeSetOfMainEvents[i].receiveTime.toLong()
                val timeIntervalInMillisToStartDay = myTimeInMillis % dayInMillis
                val countOfDays = ((timeIntervalInMillis - timeIntervalInMillisToStartDay + 86400000) / 86400000).toInt()

                val tableRow = TableRow(activity)
                tableRow.setOnClickListener{
                    Log.v("QWEQW","FFFF")
                }
                val lp = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                lp.topMargin = 5
                lp.bottomMargin = 5
                tableRow.layoutParams = lp
                tableRow.setPadding(5, 5, 5, 5)
                val shared = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
                val darkTheme = shared.getBoolean("dark", true)
                if(darkTheme) {
                    tableRow.setBackgroundResource(R.drawable.roundcornersectionsdarktheme)
                }
                else{
                    tableRow.setBackgroundResource(R.drawable.roundcornerssectionslighttheme)
                }

                val innerConstraintLayout = ConstraintLayout(requireContext())
                val innerConstraintLayoutLP = TableRow.LayoutParams((mainTableOfEvents.width * 0.99).toInt(), (0.04924760601 * height).toInt())
                innerConstraintLayout.layoutParams = innerConstraintLayoutLP


                val imageOfGuard = ImageView(activity)
                if (localeSetOfMainEvents[i].bgColor.equals("#858585")) {
                    imageOfGuard.setImageResource(R.drawable.ic_lock_not_guard)
                    imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                if (localeSetOfMainEvents[i].bgColor.equals("#4ea03e")) {
                    imageOfGuard.setImageResource(R.drawable.ic_lock_guard)
                    imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                if (localeSetOfMainEvents[i].bgColor.equals("#e0995e")) {
                    imageOfGuard.setImageResource(R.drawable.ic_fault_event)
                    imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                if (localeSetOfMainEvents[i].bgColor.equals("#e0675e")) {
                    imageOfGuard.setImageResource(R.drawable.ic_bell_alarm)
                    imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                if (localeSetOfMainEvents[i].bgColor.equals("#ffffff") || localeSetOfMainEvents[i].bgColor.equals("#FFFFFF")) {
                    imageOfGuard.setImageResource(R.drawable.ic_info_blue_icon)
                    imageOfGuard.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                val lp1 = TableRow.LayoutParams(0, 0)
                imageOfGuard.layoutParams = lp1
                imageOfGuard.setPadding(15, 15, 15, 15)
                imageOfGuard.setBackgroundResource(R.drawable.roundcornersgraybuttonondevicescreen)

                val textOfMessage = TextView(activity)
                var sensor = "шлейф " + localeSetOfMainEvents[i].sensor
                var razdel = ", раздел " + localeSetOfMainEvents[i].section + ", "
                if (localeSetOfMainEvents[i].section == "-1") {
                    razdel = ""
                    sensor = ""
                }
                var pribor = ", Прибор " + localeSetOfMainEvents[i].deviceNumber
                if (localeSetOfMainEvents[i].deviceNumber == "прибор не найден") {
                    pribor = ""
                }
                for (j in WebSocketFactory.getInstance().setOfDevices) {
                    if (localeSetOfMainEvents[i].deviceID == j.deviceID) {
                        pribor = j.type
                    }
                }
                var objText = ""
                val obj = localeSetOfMainEvents[i].objectID
                for (j in WebSocketFactory.getInstance().setOfObjects) {
                    if (j.objectID == obj) {
                        objText = j.customName
                    }
                }
                textOfMessage.text = localeSetOfMainEvents[i].message + " " + localeSetOfMainEvents[i].info + ", объект " + objText + " " + pribor + razdel + sensor
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
                var dateForLabel = "${TimeConverter().convertDateForLongTime(localeSetOfMainEvents[i].receiveTime)} "
                if (countOfDays == 0) {
                    dateForLabel = "Сегодня "
                }
                if (countOfDays == 1) {
                    dateForLabel = "Вчера "
                }

                val receiveTime = TextView(activity)
                receiveTime.text = dateForLabel + TimeConverter().convertTime(localeSetOfMainEvents[i].receiveTime)
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
                receiveTime.setPadding(10, 0, 10, 0)
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


                mainTableOfEvents.addView(tableRow, k)
            }
            k++
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showEventsFragment = false
        myHandler.removeCallbacks(myRunnable)
        myHandler2.removeCallbacks(myRunnable2)
    }

    override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        //Log.e("Compare Events", "${scrollViewMainEvents.scrollY} || ${scrollViewMainEvents.getChildAt(0).height - scrollViewMainEvents.height}")
        if(scrollViewMainEvents.scrollY == (scrollViewMainEvents.getChildAt(0).height - scrollViewMainEvents.height)){
            LKActivity.loadingOfEventsFinished = false
            localeSetOfMainEvents.clear()
            val message2 = JSONObject()
                .put("action","get")
                .put("result", JSONObject()
                    .put("events", JSONObject()
                        .put("limit",20).put("object", -1).put("envelopeID", WebSocketFactory.getInstance().setOfEvents.last().envelopeID)))
            WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message2.toString())
            createRunnable()
        }
    }
}
