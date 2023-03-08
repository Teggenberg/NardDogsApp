package com.example.narddogsinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoogleReg : AppCompatActivity() {

    private lateinit var emailTV : TextView
    private lateinit var auth : FirebaseAuth
    private lateinit var register : Button
    private lateinit var email : String

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




            addUser(email, first, last, password)

        }

    }

    private fun addUser(email: String, fName: String, lName: String, password: String) {

        val newUser : MutableMap <String, Any> = HashMap()
        newUser["firstName"] = fName
        newUser["lastName"] = lName
        newUser["userId"] = password

        val db1 = FirebaseFirestore.getInstance()

        db1.collection("Users").document(email).set(newUser)

        //db.collection("Users").document(email).update(fName, lName, password)


    }
}