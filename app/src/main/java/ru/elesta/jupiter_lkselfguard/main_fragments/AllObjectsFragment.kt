package ru.elesta.jupiter_lkselfguard.main_fragments

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import org.jetbrains.anko.wrapContent
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.AllObjectsAndServicesActivity.Companion.SELFGUARD_SERVICE_ID
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment.Companion.tempObject
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainer
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.ObjectClass
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory

class AllObjectsFragment: Fragment() {

    lateinit var containerForObjectsMain: HorizontalScrollView
    lateinit var arrayOfViews: ArrayList<View>
    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable
    lateinit var myView: View

    var tempNum = 10000

    companion object {
        val instance = AllObjectsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v  = inflater.inflate(R.layout.all_objects_fragment, null)
        myView = v
        containerForObjectsMain = v.findViewById(R.id.containerForObjectsMain)
        arrayOfViews = ArrayList()
        cleanTable()
        getObjects()
        createRunnable()
        return v
    }


    fun createRunnable(){
        myHandler = Handler()
        myRunnable = Runnable {
            if(HomeFragment.changeFlagObject) {
                cleanTable()
                getObjects()
                HomeFragment.changeFlagObject = false
            }
            myHandler.postDelayed(myRunnable,100)
        }
        Handler().post(myRunnable)
    }

    fun cleanTable(){
        myView.findViewById<LinearLayout>(R.id.linearLayoutInContainerForObjectsMain).removeAllViews()
    }
    fun getObjects(){
        var k = 0
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val height: Int = size.y
        if(WebSocketFactory.getInstance().commonSetOfObjects.isEmpty()) {
            Toast.makeText(activity, "Объектов нет", Toast.LENGTH_SHORT).show()
        }
        if(!WebSocketFactory.getInstance().commonSetOfObjects.isEmpty()) {
            val sortedSetOfObjects = ArrayList<ObjectClass>()
            for (i in WebSocketFactory.getInstance().commonSetOfObjects) {
                if(i.alarmStatus == "Alarm"){
                    sortedSetOfObjects.add(i)
                }
            }
            for(i in WebSocketFactory.getInstance().commonSetOfObjects) {
                if(i.faultStatus == "Fault"){
                    if(i.alarmStatus == "Alarm"){
                        continue
                    }
                    sortedSetOfObjects.add(i)
                }
            }
            for (i in WebSocketFactory.getInstance().commonSetOfObjects) {
                if(i.guardStatus == "Guard"){
                    if(i.alarmStatus == "Alarm"){
                        continue
                    }
                    if(i.faultStatus == "Fault"){
                        continue
                    }
                    sortedSetOfObjects.add(i)
                }
            }
            for(i in WebSocketFactory.getInstance().commonSetOfObjects) {
                if(i.guardStatus == "Partial"){
                    if(i.alarmStatus == "Alarm"){
                        continue
                    }
                    if(i.faultStatus == "Fault"){
                        continue
                    }
                    sortedSetOfObjects.add(i)
                }
            }
            for(i in WebSocketFactory.getInstance().commonSetOfObjects) {
                if(i.guardStatus == "NotGuard"){
                    if(i.alarmStatus == "Alarm"){
                        continue
                    }
                    if(i.faultStatus == "Fault"){
                        continue
                    }
                    sortedSetOfObjects.add(i)
                }
            }
            for(i in WebSocketFactory.getInstance().commonSetOfObjects) {
                if(i.guardStatus == "Unknown" && i.alarmStatus == "Unknown" && i.faultStatus == "Unknown"){
                    sortedSetOfObjects.add(i)
                }
            }
            for (i in sortedSetOfObjects) {
                val viewOfObject = ConstraintLayout(requireContext())
                val lp = LinearLayout.LayoutParams((height * 0.18604651162).toInt(),(height * 0.18604651162).toInt())
                lp.setMargins(20, 50,20,50)
                lp.bottomMargin = lp.topMargin
                viewOfObject.layoutParams = lp
                val nameOfObject = TextView(activity)
                val lp2 = LinearLayout.LayoutParams(wrapContent, wrapContent)
                lp2.setMargins(10, 10,10,10)
                nameOfObject.layoutParams = lp2
                nameOfObject.textSize = (height * 0.0083754432).toFloat()
                if(k == tempNum){
                    viewOfObject.scaleX = 1.2F
                    viewOfObject.scaleY = 1.2F
                }

                arrayOfViews.add(k,viewOfObject)

                val statusOfObject = TextView(activity)
                val lp1 = LinearLayout.LayoutParams(wrapContent, wrapContent)
                lp1.setMargins(10, 10,10,10)
                statusOfObject.layoutParams = lp1
                statusOfObject.textSize = (height * 0.00670035456).toFloat()

                val imageStatusOfObject = ImageView(activity)
                val imageStatusOfObjectLP = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                imageStatusOfObject.layoutParams = imageStatusOfObjectLP
                imageStatusOfObject.alpha = 1F
                //imageStatusOfObject.setBackgroundColor(Color.BLACK)

                viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_not_guard)
                nameOfObject.text = i.customName
                if(i.customName == ""){
                    nameOfObject.text = i.name
                }

                if(i.guardStatus.equals("NotGuard")) {
                    viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_not_guard)
                    statusOfObject.text = "Снят"
                    imageStatusOfObject.setImageResource(R.drawable.ic_unlock_for_object_icon)
                }
                if(i.guardStatus.equals("Guard") || i.guardStatus.equals("Partial")) {
                    viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_guard)
                    imageStatusOfObject.setImageResource(R.drawable.ic_lock_for_object_icon)
                    if(i.guardStatus.equals("Guard")) {
                        statusOfObject.text = "На охране"
                    }
                    if(i.guardStatus.equals("Partial")) {
                        statusOfObject.text = "Частично взят"
                    }
                }
                if(i.faultStatus.equals("Fault")) {
                    viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_fault)
                    statusOfObject.text = "Неисправность"
                    imageStatusOfObject.setImageResource(R.drawable.ic_info_for_object_icon)
                }
                if(i.alarmStatus.equals("Alarm")) {
                    viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_alarm)
                    statusOfObject.text = "Тревога"
                    imageStatusOfObject.setImageResource(R.drawable.ic_alarm_for_object_icon)
                }

                val alphaView = ConstraintLayout(requireContext())
                val alphaViewLP = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                alphaView.layoutParams = alphaViewLP
                alphaView.setBackgroundColor(Color.WHITE)
                alphaView.alpha = 0.63F
                alphaView.setBackgroundResource(R.drawable.rounded_alpha_view)
                alphaView.addView(imageStatusOfObject)

                viewOfObject.setOnClickListener{

                    for(b in arrayOfViews){
                        if(b == it){
                            tempNum = arrayOfViews.indexOf(b)
                            it.scaleX = 1.2F
                            it.scaleY = 1.2F
                        }
                        else{
                            b.scaleX = 1.0F
                            b.scaleY = 1.0F
                        }
                    }
                    viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_not_guard_big)
                    if(i.guardStatus.equals("NotGuard")) {
                        viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_not_guard_big)
                        statusOfObject.text = "Снят"
                    }
                    if(i.guardStatus.equals("Guard") || i.guardStatus.equals("Partial")) {
                        viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_guard_big)
                        if(i.guardStatus.equals("Guard")) {
                            statusOfObject.text = "На охране"
                        }
                        if(i.guardStatus.equals("Partial")) {
                            statusOfObject.text = "Частично взят"
                        }
                    }
                    if(i.faultStatus.equals("Fault")) {
                        viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_fault_big)
                        statusOfObject.text = "Неисправность"
                    }
                    if(i.alarmStatus.equals("Alarm")) {
                        viewOfObject.setBackgroundResource(R.drawable.round_corners_objects_alarm_big)
                        statusOfObject.text = "Тревога"
                    }
                    tempObject = i
                    val message = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject().put("sections", tempObject!!.objectID.toInt()))
                    WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message.toString())
                    WebSocketFactory.getInstance().setOfEventsOne.clear()
                    val message1 = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject()
                            .put("events", JSONObject()
                                .put("limit",15)
                                .put("object", tempObject!!.objectID.toInt())
                                .put("envelopeID",-1)))
                    WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message1.toString())
                    HomeFragmentSections.showRemoveAlarmButton = null
                    var checkSelfguardSocketNumber = ""
                    for (g in WebSocketFactory.getInstance().licenseContract.services){
                        if(g.serviceID == SELFGUARD_SERVICE_ID){
                            checkSelfguardSocketNumber = g.id
                        }
                    }
                    if(i.alarmStatus.equals("Alarm") && i.socketNumber == checkSelfguardSocketNumber) {
                        HomeFragmentSections.showRemoveAlarmButton = true
                    }
                    if(i.alarmStatus.equals("NotAlarm")){
                        HomeFragmentSections.showRemoveAlarmButton = false
                    }
                    if(i.typeID == "11"){
                        HomeFragmentSections.itIsAlarmButtonType = true
                    }
                    if(i.typeID != "11"){
                        HomeFragmentSections.itIsAlarmButtonType = false
                    }
                    if(i.guardStatus == "Unknown"){
                        HomeFragmentSections.guardStatusIsUnknown = true
                    }
                    activity?.findViewById<FrameLayout>(R.id.frameForObjectMain)?.removeAllViewsInLayout()
                    val homeFragCont = HomeFragmentContainer()
                    val bundle = Bundle()
                    bundle.putString("objectFragment", "common")
                    homeFragCont.arguments = bundle
                    val childFragmentManager = childFragmentManager.beginTransaction()
                    childFragmentManager.replace(R.id.frameForObjectMain, homeFragCont)
                    childFragmentManager.addToBackStack(null)
                    childFragmentManager.commit()
                }
                myView.findViewById<LinearLayout>(R.id.linearLayoutInContainerForObjectsMain).addView(viewOfObject)
                viewOfObject.addView(nameOfObject)
                viewOfObject.addView(statusOfObject)
                viewOfObject.addView(alphaView)
                nameOfObject.id = View.generateViewId()
                statusOfObject.id = View.generateViewId()
                viewOfObject.id = View.generateViewId()
                alphaView.id = View.generateViewId()
                imageStatusOfObject.id = View.generateViewId()
                k++
                val constraintSet = ConstraintSet()
                constraintSet.clone(viewOfObject)
                constraintSet.connect(alphaView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect(alphaView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.connect(nameOfObject.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 20)
                constraintSet.connect(nameOfObject.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 20)
                constraintSet.connect(statusOfObject.id, ConstraintSet.LEFT, nameOfObject.id, ConstraintSet.LEFT, 0)
                constraintSet.connect(statusOfObject.id, ConstraintSet.TOP, nameOfObject.id, ConstraintSet.BOTTOM, 5)
                constraintSet.applyTo(viewOfObject)
                alphaView.layoutParams.height = viewOfObject.layoutParams.height / 2
                alphaView.layoutParams.width = viewOfObject.layoutParams.height / 2
                val innerConstraintSet = ConstraintSet()
                innerConstraintSet.clone(alphaView)
                innerConstraintSet.centerHorizontally(imageStatusOfObject.id, ConstraintSet.PARENT_ID)
                innerConstraintSet.centerVertically(imageStatusOfObject.id, ConstraintSet.PARENT_ID)
                innerConstraintSet.applyTo(alphaView)
                imageStatusOfObject.layoutParams.height = (alphaView.layoutParams.height * 0.8).toInt()
                imageStatusOfObject.layoutParams.width = (alphaView.layoutParams.width * 0.8).toInt()
            }
        }
    }
}