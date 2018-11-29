package com.egci428.practicet1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {
    lateinit var dataReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

    }


    //Adding Data into Firebase under userData
    fun addUser(view: View){
        //Using userData as the child branch
        dataReference = FirebaseDatabase.getInstance().getReference("userData")

        //var peopleData = People(usernameText.text.toString(),passwordText.text.toString(),latitudeText.text.toString(),longitudeText.text.toString())
        //Toast.makeText(this@SignupActivity,peopleData.username,Toast.LENGTH_SHORT).show()
        var peopleData = People(usernameText.text.toString(),passwordText.text.toString(),"","")

        dataReference.child(peopleData.username).setValue(peopleData).addOnCompleteListener{
            Toast.makeText(applicationContext,"Save Successfully",Toast.LENGTH_SHORT).show()
        }
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference(peopleData.username)
        //myRef.setValue(peopleData)
    }

/*    fun randomCoordinates(view: View){
        val longitude = 100.324 + randomNumber()
        val latitude = 13.7946 + randomNumber()
        latitudeText.setText(latitude.toString())
        longitudeText.setText(longitude.toString())
    }*/


    fun randomNumber(): Double{
        val min: Double = -0.009
        val max: Double = 0.009
        return  (Random().nextDouble() * (max - min) + min)
    }


}
