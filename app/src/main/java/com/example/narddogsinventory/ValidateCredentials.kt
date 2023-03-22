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


private lateinit var user : String
private lateinit var password : String

class ValidateCredentials : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_credentials)



        val logger = findViewById<Button>(R.id.LoginButton)

        logger.setOnClickListener{

            user = findViewById<EditText>(R.id.etUsername).text.toString()
            password = findViewById<EditText>(R.id.etLoginPassword).text.toString()

            if(fieldsComplete(user, password)){
                checkCredentials(user, password)
            }
            else{
                Toast.makeText(this, "credentials incomplete", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkCredentials(user: String, password: String) {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(user)

        docRef.get().addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                val user = task.result
                if (user.exists()){
                    Log.d("TAG", "User found")
                    val existingUser = user.toObject<EntryUser>()

                    if(existingUser?.password == password) {

                        val successfulLog = Intent(this, LoginActivity::class.java)
                        startActivity(successfulLog)
                        finish()
                    }
                    else {
                        Toast.makeText(this, "username or password incorrect (password)", Toast.LENGTH_LONG)
                            .show()
                    }



                }
                else{

                    Toast.makeText(this, "username or password incorrect(user not found)", Toast.LENGTH_LONG)
                        .show()
                }
            }
            else{
                Log.d("TAG", "User not found")
                Toast.makeText(this, "username or password incorrect", Toast.LENGTH_LONG)
                    .show()


            }

        }






    }

    private fun fieldsComplete(user : String, password : String) : Boolean {
        if(user.isNullOrEmpty() || password.isNullOrEmpty() ){
            return false
        }
        return true
    }
}