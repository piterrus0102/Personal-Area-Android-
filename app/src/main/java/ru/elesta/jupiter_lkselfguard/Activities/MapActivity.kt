package ru.elesta.jupiter_lkselfguard.Activities

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BitmapCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.HomeFragment.HomeFragment
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.dataClasses.ObjectClass
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import java.io.IOException
import java.io.InputStream
import java.lang.Thread.sleep
import java.net.MalformedURLException
import java.net.URL


class MapActivity : AppCompatActivity() {

    lateinit var mapView: MapView
    lateinit var tempObject: ObjectClass

    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    var positionOfStaticObject: String? = null

    var doubleArray = ArrayList<Double>()
    var mainArray = ArrayList<ArrayList<Double>>()

    lateinit var mapObject: PlacemarkMapObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_map)
        var latitude = 0.0
        var longitude = 0.0
        mapView = findViewById(R.id.mapview)
        val extraObjectId = this.intent.extras?.get("tempobject")
        for(i in WebSocketFactory.getInstance().commonSetOfObjects){
            if(i.objectID == extraObjectId){
                tempObject = i
            }
        }
        for(i in WebSocketFactory.getInstance().listOfCoordinates){
            Log.e("Coord", "${i.lat} ${i.lng} ${i.time}")
        }
        val country = tempObject.country
        val city = tempObject.city
        val street = tempObject.street
        val house = tempObject.house
        val building = tempObject.building
        val address = arrayOf(country, city, street, house, building)
        var addressString = ""
        if(tempObject.typeID != "11") {
            for (i in address.indices) {
                if (address[i] == "") {
                    continue
                }
                addressString += address[i]
                addressString += "+"
            }
            if(addressString != "") {
                if (addressString.last() == '+') {
                    addressString = addressString.substring(0, addressString.length - 1)
                }
            }
            val str = "http://geocode-maps.yandex.ru/1.x/?apikey=d8de04c5-b1c5-4b10-a146-9dff90d2ed9b&format=json&geocode=$addressString"
            getAddress(str)
        }


        latitude = tempObject.latitude
        longitude = tempObject.longitude
        if(tempObject.typeID != "11"){
           if(tempObject.city != "" || tempObject.street != ""){
               var i = 0
               while(positionOfStaticObject == null && i < 25){
                   i++
                   sleep(200)
               }
               val g = positionOfStaticObject!!.split(" ")
               latitude = g[1].toDouble()
               longitude = g[0].toDouble()
           }
        }
        if((latitude != -1.0 && longitude != -1.0) && (latitude != 0.0 && longitude != 0.0)) {
            doubleArray.add(latitude)
            doubleArray.add(longitude)
            val tempArray = ArrayList<Double>();
            tempArray.addAll(doubleArray)
            mainArray.add(tempArray)
            doubleArray.clear()
            mapView.map.move(
                CameraPosition(Point(latitude, longitude), 15.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0F),
                null
            )
            val view = View(this).apply {
                background = this.resources.getDrawable(R.drawable.ic_mapicon)
            }
            mapObject = mapView.mapWindow.map.mapObjects.addPlacemark(Point(latitude, longitude), ViewProvider(view))
        }
        else{
            Toast.makeText(this, "Координаты для данного объекта не указаны", Toast.LENGTH_LONG).show()
            onBackPressed()
        }

        myHandler = Handler()
        myRunnable = Runnable {
            latitude = tempObject.latitude
            longitude = tempObject.longitude
            if(!((latitude == -1.0 && longitude == -1.0) || (latitude == 0.0 && longitude == 0.0)) && tempObject.alarmStatus == "Alarm") {
                doubleArray.add(latitude)
                doubleArray.add(longitude)
                //Toast.makeText(this, "latitude $latitude longitude $longitude", Toast.LENGTH_SHORT).show()
                val tempArray = ArrayList<Double>();
                tempArray.addAll(doubleArray)
                mainArray.add(tempArray)
                doubleArray.clear()
                mapView.mapWindow.map.mapObjects.remove(mapObject)
                mapView.map.move(
                    CameraPosition(Point(tempObject.latitude, tempObject.longitude), 20.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 0F),
                    null
                )
                val view = View(this).apply {
                    background = this.resources.getDrawable(R.drawable.ic_mapicon)
                }
                mapObject = mapView.mapWindow.map.mapObjects.addPlacemark(Point(latitude, longitude), ViewProvider(view))
                val k = ArrayList<Point>()
                k.add(Point(mainArray[mainArray.size - 2][0], mainArray[mainArray.size - 2][1]))
                k.add(Point(mainArray[mainArray.size - 1][0], mainArray[mainArray.size - 1][1]))
                val polyline = Polyline(k)
                mapView.mapWindow.map.mapObjects.addPolyline(polyline)
            }
            myHandler.postDelayed(myRunnable, 5000)
        }
        myHandler.postDelayed(myRunnable, 5000)



    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        myHandler.removeCallbacks(myRunnable)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    fun getAddress(string: String){
        try {
            val url = URL(string)
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder().url(url).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.v("registration", "OnFailure")
                    if(!isNetworkAvailableAndConnected()) {
                        doAsync {
                            uiThread {
                                Toast.makeText(this@MapActivity, "Проверьте интернет-соединение", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    return
                }

                override fun onResponse(call: Call?, response: Response) {
                    if(response.code() != 200){
                        doAsync {
                            uiThread {
                                Toast.makeText(this@MapActivity, "У объекта не задан адрес", Toast.LENGTH_LONG).show()
                            }
                        }
                        return
                    }
                    if (response.code() == 200) {
                        val json = response.body()?.string()
                        Log.v("JSON", json)
                        val jsonObj = JSONObject(json!!)
                        val r = jsonObj.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember").getJSONObject(0).getJSONObject("GeoObject").getJSONObject("Point").getString("pos")
                        positionOfStaticObject = r
                        Log.v("COORDS HORRAY!", r)
                    }
                }
            })
        }catch(e: MalformedURLException){
            Log.e("URLERROR", e.message.toString())
            doAsync {
                uiThread {
                    Toast.makeText(this@MapActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        catch (e: NullPointerException) {
            Log.v("AUTHORIZATION", "IsNull")
            e.printStackTrace()
            doAsync {
                uiThread {
                    Toast.makeText(this@MapActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
        catch (e: RuntimeException) {
            Log.v("AUTHORIZATION", "runtime!")
            doAsync {
                uiThread {
                    Toast.makeText(this@MapActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
            return
        }
    }

    fun isNetworkAvailableAndConnected(): Boolean {
        val runtime = Runtime.getRuntime()
        try {

            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return (exitValue == 0)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}
