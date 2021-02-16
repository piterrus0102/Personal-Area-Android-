package ru.elesta.jupiter_lkselfguard.HomeFragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import org.jetbrains.anko.wrapContent
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.guardStatusIsUnknown
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.itIsAlarmButtonType
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragmentContainerObject.HomeFragmentSections.Companion.showRemoveAlarmButton
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.ObjectClass
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.lang.Thread.sleep


class HomeFragment : Fragment(), View.OnClickListener {

    lateinit var myView: View
    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable
    lateinit var createNewObjectButtonMain: Button
    lateinit var newNameTextMain: EditText
    lateinit var newCustomNameTextMain: EditText
    lateinit var newCountryTextMain: EditText
    lateinit var newCityTextMain: EditText
    lateinit var newStreetTextMain: EditText
    lateinit var newHouseTextMain: EditText
    lateinit var newBuildingTextMain: EditText
    lateinit var newFlatTextMain: EditText

    lateinit var containerForObjectsHome: HorizontalScrollView


    var tempNum = 10000

    lateinit var arrayOfViews: ArrayList<View>

    companion object{
        var changeFlagObject = false
        var changeFlagSection = false
        var tempObject: ObjectClass? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, null)
        myView = v
        createNewObjectButtonMain = v.findViewById(R.id.createNewObjectButtonMain)
        createNewObjectButtonMain.setOnClickListener(this)
        newNameTextMain = v.findViewById(R.id.newNameTextMain)
        newCustomNameTextMain = v.findViewById(R.id.newCustomNameTextMain)
        newCountryTextMain = v.findViewById(R.id.newCountryTextMain)
        newCityTextMain = v.findViewById(R.id.newCityTextMain)
        newStreetTextMain = v.findViewById(R.id.newStreetTextMain)
        newHouseTextMain = v.findViewById(R.id.newHouseTextMain)
        newBuildingTextMain = v.findViewById(R.id.newBuildingTextMain)
        newFlatTextMain = v.findViewById(R.id.newFlatTextMain)
        containerForObjectsHome = v.findViewById(R.id.containerForObjectsHome)
        arrayOfViews = ArrayList()
        cleanTable()
        getObjects()
        createRunnable()
        return v
    }



    fun createRunnable(){
        myHandler = Handler()
        myRunnable = Runnable {
            if(changeFlagObject) {
                    cleanTable()
                    getObjects()
                changeFlagObject = false
            }
            myHandler.postDelayed(myRunnable,2000)
        }
        Handler().post(myRunnable)
    }

    fun cleanTable(){
        myView.findViewById<LinearLayout>(R.id.linearLayoutInContainerForObjectsHome).removeAllViews()
    }
    fun getObjects(){
        var k = 0
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        if(!WebSocketFactory.getInstance().setOfObjects.isEmpty()) {
            val sortedSetOfObjects = ArrayList<ObjectClass>()
            for (i in WebSocketFactory.getInstance().setOfObjects) {
                if(i.alarmStatus == "Alarm"){
                    sortedSetOfObjects.add(i)
                }
            }
            for(i in WebSocketFactory.getInstance().setOfObjects) {
                if(i.faultStatus == "Fault"){
                    if(i.alarmStatus == "Alarm"){
                        continue
                    }
                    sortedSetOfObjects.add(i)
                }
            }
            for (i in WebSocketFactory.getInstance().setOfObjects) {
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
            for(i in WebSocketFactory.getInstance().setOfObjects) {
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
            for(i in WebSocketFactory.getInstance().setOfObjects) {
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
            for(i in WebSocketFactory.getInstance().setOfObjects) {
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
                        .put("result", JSONObject().put("sections", i.objectID.toInt()))
                    WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message.toString())
                    WebSocketFactory.getInstance().setOfEventsOne.clear()
                    val message1 = JSONObject()
                        .put("action","get")
                        .put("result", JSONObject()
                            .put("events",JSONObject()
                                .put("limit",15)
                                .put("object",i.objectID.toInt())
                                .put("envelopeID",-1)))
                    WebSocketFactory.getInstance().mapSockets[tempObject?.socketNumber]?.send(message1.toString())
                    if(i.alarmStatus.equals("Alarm")) {
                        showRemoveAlarmButton = true
                    }
                    if(i.alarmStatus.equals("NotAlarm")){
                        showRemoveAlarmButton = false
                    }
                    if(i.typeID == "11"){
                        itIsAlarmButtonType = true
                    }
                    if(i.typeID != "11"){
                        itIsAlarmButtonType = false
                    }
                    if(i.guardStatus == "Unknown"){
                        guardStatusIsUnknown = true
                    }
                    activity?.findViewById<FrameLayout>(R.id.frameForObject)?.removeAllViewsInLayout()
                    val homeFragCont = HomeFragmentContainer()
                    val bundle = Bundle()
                    bundle.putString("objectFragment", "selfguard")
                    homeFragCont.arguments = bundle
                    val childFragmentManager = childFragmentManager.beginTransaction()
                    childFragmentManager.add(R.id.frameForObject, homeFragCont)
                    childFragmentManager.addToBackStack(null)
                    childFragmentManager.commit()
                }
                myView.findViewById<LinearLayout>(R.id.linearLayoutInContainerForObjectsHome).addView(viewOfObject)
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
        val viewOfAddObject = LinearLayout(activity)
        val lp = LinearLayout.LayoutParams((height * 0.18604651162).toInt(),(height * 0.18604651162).toInt())
        lp.setMargins(20, 50,20,50)
        viewOfAddObject.layoutParams = lp
        viewOfAddObject.orientation = VERTICAL
        val nameOfObject = TextView(activity)
        val lp2 = LinearLayout.LayoutParams(wrapContent, wrapContent)
        lp2.setMargins(10, 10,10,10)
        nameOfObject.layoutParams = lp2
        nameOfObject.textSize = (height * 0.0083754432).toFloat()
        nameOfObject.text = "Добавить объект"
        viewOfAddObject.setBackgroundResource(R.drawable.round_corners_objects_not_guard)

        arrayOfViews.add(k,viewOfAddObject)

        myView.findViewById<LinearLayout>(R.id.linearLayoutInContainerForObjectsHome).addView(viewOfAddObject)
        viewOfAddObject.addView(nameOfObject)
        viewOfAddObject.setOnClickListener{
            val mBuilder = AlertDialog.Builder(context!!)
            mBuilder.setTitle("Вы уверены что хотите создать новый объект?")
            mBuilder.setCancelable(false)
            mBuilder.setPositiveButton("Да", DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                for(b in arrayOfViews){
                    if(b == it){
                        it.scaleX = 1.2F
                        it.scaleY = 1.2F
                    }
                    else{
                        b.scaleX = 1.0F
                        b.scaleY = 1.0F
                    }
                }
                myView.findViewById<FrameLayout>(R.id.frameForObject).visibility = View.GONE
                myView.findViewById<HorizontalScrollView>(R.id.containerForObjectsHome).visibility = View.GONE
                myView.findViewById<ScrollView>(R.id.createNewObjectScrollViewMain).visibility = View.VISIBLE
            })
            mBuilder.setNegativeButton("Нет", DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            })
            val mDialog = mBuilder.create()
            mDialog.show()
        }
        //k = 0
    }

    override fun onClick(v: View?) {
        val message = JSONObject()
            .put("action","create")
            .put("result", JSONObject()
                .put("object",JSONObject()
                    .put("customName",newCustomNameTextMain.text.toString())
                    .put("name", newNameTextMain.text.toString())
                    .put("country",newCountryTextMain.text.toString())
                    .put("city", newCityTextMain.text.toString())
                    .put("street", newStreetTextMain.text.toString())
                    .put("house",newHouseTextMain.text.toString())
                    .put("building",newBuildingTextMain.text.toString())
                    .put("flat",newFlatTextMain.text.toString())))
        WebSocketFactory.getInstance().mapSockets.get(LKActivity.mainSocketString)?.send(message.toString())
        myView.findViewById<FrameLayout>(R.id.frameForObject).visibility = View.VISIBLE
        myView.findViewById<HorizontalScrollView>(R.id.containerForObjectsHome).visibility = View.VISIBLE
        myView.findViewById<ScrollView>(R.id.createNewObjectScrollViewMain).visibility = View.GONE
        newCustomNameTextMain.text.clear()
        newNameTextMain.text.clear()
        newCountryTextMain.text.clear()
        newCityTextMain.text.clear()
        newStreetTextMain.text.clear()
        newHouseTextMain.text.clear()
        newBuildingTextMain.text.clear()
        newFlatTextMain.text.clear()
        sleep(500)
        cleanTable()
        getObjects()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myHandler.removeCallbacks(myRunnable)
        Log.v("HomeFragment", "onDestroyView")
        Log.v("---", "-----------------------------------------")
    }
}

