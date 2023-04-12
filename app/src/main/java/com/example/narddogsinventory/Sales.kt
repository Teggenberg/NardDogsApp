package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationBarView
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class Sales : AppCompatActivity() {

    private lateinit var sales : TextView
    private lateinit var inventory : TextView
    private lateinit var invested : TextView
    private lateinit var bNav : NavigationBarView
    private var currentUser : EntryUser? = null
    //@RequiresApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)

        sales = findViewById(R.id.tvSales)
        inventory = findViewById(R.id.tvInvnetory)
        invested = findViewById(R.id.tvInvested)
        bNav = findViewById(R.id.bottomNav)

        bNav.selectedItemId = R.id.Sales





        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")
        val current = LocalDateTime.now()

        val days = Duration.between(origin,current).toDays().toString()

        findViewById<TextView>(R.id.etDate).text = days

        sales.text = "Total Sales: " + currentUser?.totSales.toString()
        invested.text =  "Total Invested: " + currentUser?.totInvested.toString()
        inventory.text = "Total Inventory: " + currentUser?.totListings + " items"


        bNav.setOnItemSelectedListener {

            when(it.itemId) {

                //home/dashboard
                R.id.home -> {
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    homeIntent.putExtra("currentUser", currentUser)
                    startActivity(homeIntent)
                    finish()
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
    }
}