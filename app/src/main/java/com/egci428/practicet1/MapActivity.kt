package com.egci428.practicet1

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_map.*
import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.Random


class MapActivity : AppCompatActivity(),OnMapReadyCallback {

    private var monsterNumber = 4

    private var locationManager: LocationManager ?= null
    private var listener : LocationListener ?= null

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private var currentLatitude : Double ?= null
    private var currentLongtitude : Double ?= null
    private var monsterCount = 0
    private var totalDamage: Int = 0
    private var monsterName: String ?= null
    private var monsterDamage: Int ?= null
    private var monsterKilled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //monsterList = mutableListOf()
        //val mapFragment = supportFragmentManager .findFragmentById(R.id.map) as SupportMapFragment? ?: SupportMapFragment.newInstance()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        listener = object: LocationListener{
            override fun onLocationChanged (location: Location){
                currentLatitude = location.latitude
                currentLongtitude = location.longitude
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }
        }

        requestLocationService()

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            10 -> requestLocationService()
            else -> {}
        }
    }

    fun requestLocationService(){

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET),10)
                return
            }
        }
        locationManager!!.requestLocationUpdates("gps",3,3f,listener)
        //Checking whether is there GPS readings
        gpsBtn.setOnClickListener{
            if (currentLongtitude.toString()=="null")
            {
                longitudeEditview.setText("No Location Reading")
                latitudeEditview.setText("No Location Reading")
            }
            else{
                longitudeEditview.setText(currentLongtitude.toString())
                latitudeEditview.setText(currentLatitude.toString())
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap)
    }



    private fun setupMap(googleMap: GoogleMap){
        mMap = googleMap


        val mahidol = LatLng(13.794631,100.323536)
        //mMap.addMarker(MarkerOptions().position(mahidol).title("Mahidol University"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mahidol,15f))

        //Monster Respawn
        saveBtn.setOnClickListener {
            if (latitudeEditview.text.toString() == "No Location Reading"){
                Toast.makeText(this,"No GPS Coordinate collected",Toast.LENGTH_SHORT).show()
            }
            else{

                val currentLocation = LatLng(latitudeEditview.text.toString().toDouble(), longitudeEditview.text.toString().toDouble())
                //mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))

                if (monsterCount==0){
                    monsterCount = monsterNumber
                    for (i in 1..monsterNumber){
                        var monsterMarker = LatLng(latitudeEditview.text.toString().toDouble()+randomNumber(),longitudeEditview.text.toString().toDouble()+randomNumber())
                        mMap.addMarker(MarkerOptions().position(monsterMarker).title("Monster "+ i).icon(BitmapDescriptorFactory.fromResource(R.drawable.question)))
                    }
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15f))

            }
        }

        //Click on Individual Marker for next intent
        mMap.setOnMarkerClickListener {
            Marker->
            monsterKilled = false
            val intent = Intent(this@MapActivity,ShakeActivity::class.java)
            intent.putExtra("monsterName",Marker.title.toString())
            intent.putExtra("currentLatitude",latitudeEditview.text.toString().toDouble())
            intent.putExtra("currentLongtitude",longitudeEditview.text.toString().toDouble())
            intent.putExtra("markerLatitude",Marker.position.latitude)
            intent.putExtra("markerLongtitude",Marker.position.longitude)
            //Remove Marker
            Marker.remove()
            monsterCount -= 1
            startActivityForResult(intent,1)
            true

        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==1){
            if(resultCode == RESULT_OK){
                //messageList = data!!.getStringExtra("editTextValue")
                monsterName = data!!.getStringExtra("monsterName")
                monsterDamage = data!!.getStringExtra("monsterDamage").toInt()
                totalDamage = totalDamage + monsterDamage!!
                scoreView.setText(totalDamage.toString())
                Toast.makeText(this,monsterName+ " KILLED!" ,Toast.LENGTH_SHORT).show()

            }
        }
    }
    //Generate random coordinate from origing
    fun randomNumber(): Double{
        val min: Double = -0.009
        val max: Double = 0.009
        return  (Random().nextDouble() * (max - min) + min)
    }
}