package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.annotation.RequiresApi

import com.google.android.material.navigation.NavigationBarView

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

        sales = findViewById(R.id.tvCatOne)
        listings = findViewById(R.id.tvTotalRev)
        invested = findViewById(R.id.tvCatTwo)

        listings.text = "Current Inventory : " + currentUser?.totListings.toString() + " items"
        sales.text = "Total Sales : $" + "%.2f".format(currentUser?.totSales)
        invested.text = "Current Investment : $" + "%.2f".format(currentUser?.totInvested)



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
   //                 mainIntent.putExtra("itemNum", itemID)
  //                  mainIntent.putExtra("userID", userID)
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