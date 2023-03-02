package com.example.narddogsinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var register : Button
    private lateinit var login : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.registerButton)
        login = findViewById(R.id.loginButton)

        register.setOnClickListener {
            val intentR = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intentR)
            finish()
        }

        login.setOnClickListener{
            val intentL = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intentL)
            finish()
        }


    }
}