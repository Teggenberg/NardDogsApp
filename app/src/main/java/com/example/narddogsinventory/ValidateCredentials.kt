package com.example.narddogsinventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

//variables to be used for user validation
private lateinit var user : String
private lateinit var password : String


//this is the activity for existing users to log in to the app
class ValidateCredentials : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_credentials)


        //login button reference
        val logger = findViewById<Button>(R.id.LoginButton)

        //action when log in button is clicked
        logger.setOnClickListener{

            //assign variables with user's input from editTexts
            user = findViewById<EditText>(R.id.etUsername).text.toString()
            password = findViewById<EditText>(R.id.etLoginPassword).text.toString()

            //check to make sure no editText is empty
            if(fieldsComplete(user, password)){
                //compare user input to db for verification of account
                checkCredentials(user, password)
            }
            else{
                //toast that appears if either editText is left empty
                Toast.makeText(this, "credentials incomplete"
                    , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkCredentials(user: String, password: String) {

        //access to the users db
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(user)

        //check user db against the credentials entered
        docRef.get().addOnCompleteListener{
                task ->
            //ensures db access is successful
            if(task.isSuccessful){

                //store task result into variable
                val user = task.result

                //username credential matches existing user in db
                if (user.exists()){

                    //logcat for debugging path
                    Log.d("TAG", "User found")

                    //capture user document and store into object for reference
                    val existingUser = user.toObject<EntryUser>()

                    //check entered password against db password for user
                    if(existingUser?.password == password) {

                        //matching username and password allow successful login intent
                        val successfulLog = Intent(this, LoginActivity::class.java)

                        //user data that is sent into next activity for reference
                        successfulLog.putExtra("email", existingUser.firstName)
                        successfulLog.putExtra("name", existingUser.lastName)
                        successfulLog.putExtra("userNum", existingUser?.userID)
                        successfulLog.putExtra("itemNum", existingUser?.currentListing)
                        startActivity(successfulLog)
                        finish()
                    }
                    else {

                        //toast for password mismatch
                        Toast.makeText(this, "username or password incorrect "
                            , Toast.LENGTH_LONG)
                            .show()
                    }

                }
                else{

                    //toast for username mismatch
                    Toast.makeText(this, "username or password incorrect"
                        ,Toast.LENGTH_LONG)
                        .show()
                }
            }
            else{
                Log.d("TAG", "User not found")

                //error accessing db
                Toast.makeText(this, "error contacting server"
                    , Toast.LENGTH_LONG)
                    .show()

            }

        }

    }

    private fun fieldsComplete(user : String, password : String) : Boolean {

        //checks editTexts to make sure user input exists
        if(user.isNullOrEmpty() || password.isNullOrEmpty() ){
            return false
        }
        return true
    }
}