package com.example.narddogsinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var email : EditText     //field to enter email
    private lateinit var password : EditText  //field to enter password
    private lateinit var create : Button      //button to create new account


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.etRegEmail)              //variables linked to xml objects
        password = findViewById(R.id.etRegPassword)
        create = findViewById(R.id.createAccountButton)


    }
}