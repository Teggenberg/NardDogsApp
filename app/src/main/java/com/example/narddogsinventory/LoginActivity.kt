package com.example.narddogsinventory

import android.content.ClipData.Item
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.provider.MediaStore
import android.view.inputmethod.InputBinding

import android.util.Log

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

//this activity is poorly named, it is the landing page upon logging in (retrospect is 20/20)
class LoginActivity : AppCompatActivity() {

    //db reference
    private lateinit var auth : FirebaseAuth

    //navigation bar reference
    private lateinit var bNav: NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //access to db
        auth = FirebaseAuth.getInstance()


        //capture user data passed from previous activity for reference
        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")
        val userID = intent.getLongExtra("userNum", 0)
        val itemID = intent.getLongExtra("itemNum", 0)


        //write out user data to text views
        findViewById<TextView>(R.id.textView).text = email + "\n" + displayName

        //link reference to nav menu
        bNav = findViewById(R.id.bottomNav)

        //ensure that the selected item in the navbar matches current activity
        bNav.selectedItemId = R.id.home

        //switch between activities when different items in navbar are clicked
        bNav.setOnItemSelectedListener {

            when(it.itemId) {

                //home/dashboard
                R.id.home -> {
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    return@setOnItemSelectedListener true
                }

                //activity to add new items into inventory
                R.id.AddItem -> {
                    val mainIntent = Intent(this, ItemList::class.java)
                    mainIntent.putExtra("itemNum", itemID)
                    startActivity(mainIntent)
                    return@setOnItemSelectedListener true
                }

                //activity to view current/sold inventory
                R.id.Inventory -> {

                    //pass user ID so item list can be filtered  for specific user
                    val inventoryIntent = Intent(this, ViewInventory::class.java)
                    inventoryIntent.putExtra("userID", userID)
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