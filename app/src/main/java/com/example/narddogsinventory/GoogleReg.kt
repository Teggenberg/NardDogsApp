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


data class GetID(

    var totalItems : Int,

    var totalUsers : Int,

    var userID : Long,




) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    ) {
    }

    constructor() : this (0,0,0
            )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(totalItems)
        parcel.writeInt(totalUsers)
        parcel.writeLong(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetID> {
        override fun createFromParcel(parcel: Parcel): GetID {
            return GetID(parcel)
        }

        override fun newArray(size: Int): Array<GetID?> {
            return arrayOfNulls(size)
        }
    }

}



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
                    //addUser(email, first, last, password)  //add new user to database
                    getUserID()
                    updateGlobals()
                }
                else{
                    Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this, "credential fields incomplete", Toast.LENGTH_SHORT).show()

            }




            //addUser(email, first, last, password)

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
        //val userID = 0

        val global = FirebaseFirestore.getInstance()
        val id = global.collection("rJd01G5J8l1BTkwyo101").get().addOnCompleteListener() {





        }





        val db = FirebaseFirestore.getInstance()

        val newUser = EntryUser(0, fName, lName, password, userId)

        db.collection("Users").document(email).set(newUser)

        //db.collection("Users").document(email).update(fName, lName, password)


    }

    private fun getUserID(): Long? {
        val first = findViewById<EditText>(R.id.etFirstName).text.toString()
        val last =  findViewById<EditText>(R.id.etLastName).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        val passwordC = findViewById<EditText>(R.id.etConfirmPassword).text.toString()

        var returnID : Long? = null
        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals").document("rJd01G5J8l1BTkwyo101")

        docRef.get().addOnSuccessListener { documentsnapshot ->

            val newUser = documentsnapshot.toObject<NewID>()
            Log.i(TAG, newUser?.userID.toString())
            returnID = newUser?.userID
            addUser(email, first, last, password, returnID)

        }

        return returnID

    }

    private fun updateGlobals(){

        val global = FirebaseFirestore.getInstance()
        val docRef = global.collection("globals").document("rJd01G5J8l1BTkwyo101")

        docRef.get().addOnSuccessListener { documentsnapshot ->

            var updateID = documentsnapshot.toObject<NewID>()
            Log.i(TAG, updateID?.userID.toString())

            updateID?.userID = updateID?.userID?.plus(1)
            updateID?.userID = updateID?.totalUsers?.plus(1)


            if (updateID != null) {
                global.collection("globals").document("rJd01G5J8l1BTkwyo101")
                    .set(updateID)
            }


        }


    }
}