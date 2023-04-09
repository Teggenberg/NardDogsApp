package com.example.narddogsinventory

import android.content.ClipData.Item
import android.content.Intent
import android.os.Binder
import android.os.Build
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
import androidx.annotation.RequiresApi

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
    private var currentUser : EntryUser? = null
    private lateinit var sales : TextView
    private lateinit var listings : TextView
    private lateinit var invested : TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //access to db
        auth = FirebaseAuth.getInstance()

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)


        //capture user data passed from previous activity for reference
        val email = currentUser?.firstName
        val displayName = currentUser?.lastName
        val userID = intent.getLongExtra("userNum", 0)
        val itemID = intent.getLongExtra("itemNum", 0)

        sales = findViewById(R.id.tvSales)
        listings = findViewById(R.id.tvInvnetory)
        invested = findViewById(R.id.tvInvested)

        listings.text = "Current Inventory : " + currentUser?.totListings.toString() + " items"
        sales.text = "Total Sales : $" + currentUser?.totSales.toString()
        invested.text = "Current Investment : $" + currentUser?.totInvested.toString()



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
                    /*val homeIntent = Intent(this, LoginActivity::class.java)
                    startActivity(homeIntent)
                    finish()*/
                    return@setOnItemSelectedListener true
                }

                //activity to add new items into inventory
                R.id.AddItem -> {
                    val mainIntent = Intent(this, ItemList::class.java)
//                    mainIntent.putExtra("itemNum", itemID)
//                    mainIntent.putExtra("userID", userID)
                    mainIntent.putExtra("currentUser", currentUser)
                    startActivity(mainIntent)
                    return@setOnItemSelectedListener true
                }

                //activity to view current/sold inventory
                R.id.Inventory -> {

                    //pass user ID so item list can be filtered  for specific user
                    val inventoryIntent = Intent(this, ViewInventory::class.java)
                    inventoryIntent.putExtra("currentUser", currentUser)
                    startActivity(inventoryIntent)
                    return@setOnItemSelectedListener true
                }
                R.id.Sales -> {
                    val salesIntent = Intent(this, Sales::class.java)
                    salesIntent.putExtra("currentUser", currentUser)
                    startActivity(salesIntent)
                    return@setOnItemSelectedListener true

                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

        findViewById<Button>(R.id.signOutButton).setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }




















    }




}