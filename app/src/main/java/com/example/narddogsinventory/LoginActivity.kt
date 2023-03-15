package com.example.narddogsinventory

import android.content.ClipData.Item
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputBinding
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import android.view.MenuItem

import com.google.android.material.navigation.NavigationBarMenu
import com.google.android.material.navigation.NavigationBarView
import androidx.appcompat.widget.ButtonBarLayout

import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.android.material.snackbar.BaseTransientBottomBar

import java.nio.BufferUnderflowException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var bNav: NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        bNav = findViewById(R.id.bottomNav)

        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")

        findViewById<TextView>(R.id.textView).text = email + "\n" + displayName


        bNav = findViewById(R.id.bottomNav)

        bNav.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.home -> {
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    return@setOnItemSelectedListener true


                }
                R.id.AddItem -> {
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    return@setOnItemSelectedListener true

                }
                R.id.Inventory -> {
                    val inventoryIntent = Intent(this, RegisterActivity::class.java)
                    startActivity(inventoryIntent)
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }




















    }




}