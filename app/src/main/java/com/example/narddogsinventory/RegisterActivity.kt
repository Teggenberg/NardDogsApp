package com.example.narddogsinventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


private const val TAG = "RegisterActivity"
class RegisterActivity : AppCompatActivity() {

    private lateinit var email : EditText     //field to enter email
    //private lateinit var password : EditText  //field to enter password
    private lateinit var create : Button      //button to create new account


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val returnMain = findViewById<Button>(R.id.button_reg_return)

        returnMain.setOnClickListener{

            val rtnMain = Intent(this, MainActivity::class.java)
            startActivity(rtnMain)
        }


        email = findViewById(R.id.etRegEmail)              //variables linked to xml objects
        //password = findViewById(R.id.etRegPassword)
        create = findViewById(R.id.createAccountButton)

        findViewById<TextView>(R.id.tvBack).setOnClickListener{

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.createAccountButton).setOnClickListener{

            //capture values in widgets for user account data
            val first = findViewById<EditText>(R.id.etRegFirstN).text.toString()
            val last =  findViewById<EditText>(R.id.etRegLastN).text.toString()
            val password = findViewById<EditText>(R.id.etRegPassword).text.toString()
            val passwordC = findViewById<EditText>(R.id.etRegPasswordC).text.toString()
            val email = findViewById<EditText>(R.id.etRegEmail).text.toString()

            //check to make sure all fields are filled in
            if(fieldsComplete(first, last, password, passwordC, email)){

                if(passwordMatch(password, passwordC)){

                    //if fields are complete, and passwords match, create usr account
                    newUser(first, last, password,  email)
                }
                else{

                    //toast for mismatch passwords
                    Toast.makeText(this, "Passwords do not match",Toast.LENGTH_LONG).show()
                }

            }
            else{

                //toast for incomplete  fields
                Toast.makeText(this, "Credentials Not Complete",Toast.LENGTH_LONG).show()
            }

        }




    }

    private fun newUser(first : String, last : String, password : String, email: String) {

        //database access, pass in username entered to check against existing accounts
        val db = FirebaseFirestore.getInstance()
        val emailCheck = db.collection("Users").document("$email")


        emailCheck.get().addOnCompleteListener{
                task ->
            //access to db successful
            if(task.isSuccessful){

                //store result in variable for reference
                val user = task.result

                //check variable to see if it already exists in db
                if (user.exists()){

                    //toast to let user know username is taken
                    Log.d("TAG", "User found")
                    Toast.makeText(this, "Username Unavailable", Toast.LENGTH_LONG).show()

                }
                else{

                    //create new user account
                    generateUserID()
                }
            }
            else{
                //no access to db
                Log.d("TAG", "User not found")
            }
        }
    }

    private fun updateGlobals() {
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

    private fun addUser(first: String, last: String, password: String, email: String, userId : Long?) : EntryUser {
        //instantiate firestore access
        val db = FirebaseFirestore.getInstance()

        //custom object to hold data being written to db
        val newUser = EntryUser(1000000, email, first, last, password, 0F, 0,0F, userId)

        //pass object into collection to create new doc in user collection
        db.collection("Users").document(email).set(newUser)

        return newUser

    }

    private fun generateUserID(){

        //read in values in editTexts for user data fields
        val first = findViewById<EditText>(R.id.etRegFirstN).text.toString()
        val last =  findViewById<EditText>(R.id.etRegLastN).text.toString()
        val password = findViewById<EditText>(R.id.etRegPassword).text.toString()
        val email = findViewById<EditText>(R.id.etRegEmail).text.toString()

        //variable to hold userID accessed from globals table
        var returnID : Long? = null


        //instantiate db access, and target specific document
        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals").document("rJd01G5J8l1BTkwyo101")


        //document retrieved from globals to access user ID for new user
        docRef.get().addOnSuccessListener { documentsnapshot ->

            val newUser = documentsnapshot.toObject<NewID>() //document captured into object
            Log.i(TAG, newUser?.userID.toString()) //logkat to verify proper function
            returnID = newUser?.userID //assign userID to variable

            //call addUser with all data passed for new account
            val currentUser = addUser( first, last, password, email, returnID)

            //update global db with current user count, and new user ID for assignment of next account
            updateGlobals()

            //move new user to homepage, pass the user data for local access to data
            val intentR = Intent(this, LoginActivity::class.java)
            intentR.putExtra("currentUser", currentUser)
            startActivity(intentR)
            finish()


        }

    }

    private fun passwordMatch(password: String, passwordC: String): Boolean {

        if(password == passwordC){ //check string in each et to make sure they are equal
            return true
        }
        return false
    }

    private fun fieldsComplete(email: String, first: String, last: String, password: String, passwordC: String): Boolean {

        //check each et to make sure there are no empty fields
        if(first.isNullOrEmpty() || last.isNullOrEmpty() || password.isNullOrEmpty() || passwordC.isNullOrEmpty() || email.isNullOrEmpty()){
            return false
        }
        return true
    }
}