package com.example.narddogsinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


private const val TAG = "GoogleReg"




class GoogleReg : AppCompatActivity() {

    private lateinit var emailTV : TextView
    private lateinit var auth : FirebaseAuth
    private lateinit var register : Button
    private lateinit var email : String
    //private  var uselessList : ArrayList<NewID> = TODO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_reg)

        auth = FirebaseAuth.getInstance()

        email = intent.getStringExtra("email").toString()
        register = findViewById<Button>(R.id.registerGoogleButton)

        findViewById<TextView>(R.id.tvEmail).text = "$email"

        register.setOnClickListener{
            val first = findViewById<EditText>(R.id.etFirstName).text.toString()
            val last =  findViewById<EditText>(R.id.etLastName).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            val passwordC = findViewById<EditText>(R.id.etConfirmPassword).text.toString()

            if(fieldsComplete(first, last, password, passwordC)){  //validate that all fields are filled

                if(password == passwordC){   //validate that passwords both match
                    Toast.makeText(this, "Hello", Toast.LENGTH_SHORT)
                    generateUserID()
                    updateGlobals()
                }
                else{
                    Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "credential fields incomplete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun passwordMatch(password: String, passwordC: String): Boolean {

        if(password == passwordC){
            return true
        }
        return false
    }

    private fun fieldsComplete(first: String, last: String, password: String, passwordC: String): Boolean {

        if(first.isNullOrEmpty() || last.isNullOrEmpty() || password.isNullOrEmpty() || passwordC.isNullOrEmpty()){
            return false
        }
        return true
    }


    private fun addUser(email: String, fName: String, lName: String, password: String, userId : Long?) {


        val db = FirebaseFirestore.getInstance()

        val newUser = EntryUser(0, fName, lName, password, userId)

        db.collection("Users").document(email).set(newUser)



    }

    private fun generateUserID(){
        val first = findViewById<EditText>(R.id.etFirstName).text.toString()
        val last =  findViewById<EditText>(R.id.etLastName).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()


        var returnID : Long? = null
        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals").document("rJd01G5J8l1BTkwyo101")

        docRef.get().addOnSuccessListener { documentsnapshot ->

            val newUser = documentsnapshot.toObject<NewID>()
            Log.i(TAG, newUser?.userID.toString())
            returnID = newUser?.userID
            addUser(email, first, last, password, returnID)

        }



    }

    private fun updateGlobals(){

        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals").document("rJd01G5J8l1BTkwyo101")

        docRef.get().addOnSuccessListener { documentsnapshot ->

            val updateID = documentsnapshot.toObject<NewID>()
            Log.i(TAG, updateID?.userID.toString())




            if (updateID != null) {
                updateID?.userID = updateID?.userID?.plus(1)

                if(updateID?.totalUsers != null) {
                    updateID.totalUsers = updateID.totalUsers?.plus(1)
                }

                else{
                    updateID?.userID = 1}

                global.collection("globals").document("rJd01G5J8l1BTkwyo101")
                    .set(updateID)
            }


        }


    }
}