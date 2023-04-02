package com.example.narddogsinventory

import android.content.Intent
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

    //references for editTexts and button
    private lateinit var auth : FirebaseAuth
    private lateinit var register : Button
    private lateinit var email : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_reg)

        auth = FirebaseAuth.getInstance()

        email = intent.getStringExtra("email").toString()     //display email in tv pulled form Google
        register = findViewById<Button>(R.id.registerGoogleButton)  //button to register new account

        findViewById<TextView>(R.id.tvEmail).text = "$email"        // set tv to google address

        //when Register button is clicked
        register.setOnClickListener{
            val first = findViewById<EditText>(R.id.etFirstName).text.toString()
            val last =  findViewById<EditText>(R.id.etLastName).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            val passwordC = findViewById<EditText>(R.id.etConfirmPassword).text.toString()

            if(fieldsComplete(first, last, password, passwordC)){  //validate that all fields are filled

                if(password == passwordC){   //validate that passwords both match

                    generateUserID()  //create new account, user ID assigned from globals collection
                    updateGlobals()   //totalUsers and UserID index updated in globals collection
                    val homeScreen = Intent(this, LoginActivity::class.java)
                    startActivity(homeScreen)
                    finish()
                }
                else{
                    //password fields do not match
                    Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                //empty fields remain
                Toast.makeText(this, "credential fields incomplete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun passwordMatch(password: String, passwordC: String): Boolean {

        if(password == passwordC){ //check string in each et to make sure they are equal
            return true
        }
        return false
    }

    private fun fieldsComplete(first: String, last: String, password: String, passwordC: String): Boolean {

        //check each et to make sure there are no empty fields
        if(first.isNullOrEmpty() || last.isNullOrEmpty() || password.isNullOrEmpty() || passwordC.isNullOrEmpty()){
            return false
        }
        return true
    }


    private fun addUser(email: String, fName: String, lName: String,
                        password: String, userId : Long?) : EntryUser {

        //instantiate firestore access
        val db = FirebaseFirestore.getInstance()

        //custom object to hold data being written to db
        val newUser = EntryUser(0, email, fName, lName, password, userId)

        //pass object into collection to create new doc in user collection
        db.collection("Users").document(email).set(newUser)

        return newUser

    }

    private fun generateUserID(){

        //read in values in editTexts for user data fields
        val first = findViewById<EditText>(R.id.etFirstName).text.toString()
        val last =  findViewById<EditText>(R.id.etLastName).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()

        //variable to hold userID accessed from globals table
        var returnID : Long? = null


        //instantiate db access, and target specific document
        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals")
            .document("rJd01G5J8l1BTkwyo101")


        //document retrieved from globals to access user ID for new user
        docRef.get().addOnSuccessListener { documentsnapshot ->

            val newUser = documentsnapshot.toObject<NewID>() //document captured into object
            Log.i(TAG, newUser?.userID.toString()) //logkat to verify proper function
            returnID = newUser?.userID //assign userID to variable

            //call addUser with all data passed for new account
            addUser(email, first, last, password, returnID)
        }

    }

    private fun updateGlobals(){

        //db access to global document
        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals").document("rJd01G5J8l1BTkwyo101")

        docRef.get().addOnSuccessListener { documentsnapshot ->

            //assign document to object for data access
            val updateID = documentsnapshot.toObject<NewID>()
            Log.i(TAG, updateID?.userID.toString())

            //check to make sure document is not null
            if (updateID != null) {

                //increment global userID for next assignment
                updateID?.userID = updateID?.userID?.plus(1)

                //check to see if total users is null, increment value
                if(updateID?.totalUsers != null) {
                    updateID.totalUsers = updateID.totalUsers?.plus(1)
                }

                //if null assign 1 as value
                else{
                    updateID?.userID = 1}

                //update document with new values
                global.collection("globals").document("rJd01G5J8l1BTkwyo101")
                    .set(updateID)
            }

        }
        
    }
}