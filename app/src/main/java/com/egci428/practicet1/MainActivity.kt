package com.egci428.practicet1

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    //Get API URL
    val url = "https://practicet1-d97cc.firebaseio.com/userData/"
    var uname: String? = null
    var pname: String? = null
    var userprofile: People? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun signUp( view :View){
        val intent = Intent(this,SignupActivity::class.java)
        startActivity(intent)
    }

    fun cancel (view: View){
        usernameText.getText().clear()
        passwordText.getText().clear()
    }

    fun signIn (view: View){
        uname = usernameText.text.toString()
        pname = passwordText.text.toString()

        if (!uname.isNullOrEmpty() && !pname.isNullOrEmpty()) {
            var asyncTask = object : AsyncTask<String, String, String>() {

                override fun onPreExecute() {
                    Toast.makeText(this@MainActivity, "Please wait...", Toast.LENGTH_SHORT).show()
                }
                //With the help of HTTP Helper Class, to obtain the API call
                override fun doInBackground(vararg p0: String?): String {
                    val helper = HTTPHelper()
                    Log.d("Uname", url + uname + ".json")
                    println (helper.getHTTPData(url + uname + ".json"))
                    return helper.getHTTPData(url + uname + ".json")

                }

                override fun onPostExecute(result: String?) {
                    if (result != "null") {

                        userprofile = Gson().fromJson(result, People::class.java)

                        if (userprofile!!.password == pname) {
                            Toast.makeText(this@MainActivity, "CORRECT!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@MainActivity, MapActivity::class.java)

                            applicationContext.startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "Username or Password is not matched", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@MainActivity, "Username or Password is not matched", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            asyncTask.execute()
        }
    }
}
