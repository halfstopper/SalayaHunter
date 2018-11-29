package com.egci428.practicet1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_shake.*


class ShakeActivity  : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private var step : Int = 0
    private var currentLatitude : Double = 0.0
    private var currentLongtitude : Double = 0.0
    private var markerLatitude : Double = 0.0
    private var markerLongtitude : Double = 0.0
    private var monsterName : String = ""
    private var distanceBetween: Int = 0
    private var monsterKilled: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)
        killBtn.visibility = View.INVISIBLE


        //Get from MapActivity Intent
        val bundle = intent.extras

        monsterName = bundle.getString("monsterName")
        currentLatitude = bundle.getDouble("currentLatitude")
        currentLongtitude = bundle.getDouble("currentLongtitude")
        markerLatitude = bundle.getDouble("markerLatitude")
        markerLongtitude = bundle.getDouble("markerLongtitude")

        //Image Selection for MOnster
        if (monsterName == "Monster 1"){
            imageView.setImageResource(R.drawable.monster1)
        }
        if (monsterName == "Monster 2"){
            imageView.setImageResource(R.drawable.monster2)
        }

        if (monsterName == "Monster 3"){
            imageView.setImageResource(R.drawable.monster3)
        }

        if (monsterName == "Monster 4"){
            imageView.setImageResource(R.drawable.monster4)
        }

        distanceBetween = distance(markerLatitude,markerLongtitude,currentLatitude,currentLongtitude)


        monsternameTextview.setText(monsterName)
        distanceTextview.setText(distanceBetween.toString())
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()


    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            getAccelerometer(event)
        }
    }

    private fun getAccelerometer(event: SensorEvent){
        val values = event.values
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val accel = (x*x+y*y+z*z)/(SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH)

        val actualTime = System.currentTimeMillis()

        if (accel >= 1.3){
            if ((actualTime - lastUpdate)<200){
                return
            }
            lastUpdate = actualTime

            if (step > distanceBetween-1){
                monsterKilled = true
                killBtn.visibility = View.VISIBLE
                shakeTextView.visibility = View.INVISIBLE
                textView.visibility = View.INVISIBLE

            }
            else{
                step += accel.toInt()
                shakeTextView.setText(step.toString())
            }
        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    override fun onResume(){
        super.onResume()
        sensorManager!!.registerListener(this,sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)

    }


    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"Monster not Killed",Toast.LENGTH_SHORT).show()

    }
    //Throwing back variable to MapActivity
    fun killMonster(view:View){
        intent.putExtra("monsterDamage",distanceBetween.toString())
        intent.putExtra("monsterName",monsterName)
        intent.putExtra("monsterKilled",monsterKilled)
        setResult(RESULT_OK, intent)
        finish()
        true

    }

    //Calculate Distance from 2 LatLong Coordinates
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60.0 * 1.1515
        //Convert to Metre
        dist = dist * 1000
        dist = dist /10
        return dist.toInt()
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}
