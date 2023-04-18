package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.*
import java.time.Duration
import java.time.LocalDateTime

class Sales : AppCompatActivity() {

    private lateinit var sales : TextView
    private lateinit var inventory : TextView
    private lateinit var invested : TextView
    private lateinit var bNav : NavigationBarView
    private var currentUser : EntryUser? = null

    private lateinit var salesList : ArrayList<SoldListing>
    private lateinit var catList : ArrayList<SalesCategory>

    //@RequiresApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)

        sales = findViewById(R.id.tvCatOne)
        inventory = findViewById(R.id.tvTotalRev)
        invested = findViewById(R.id.tvCatTwo)
        bNav = findViewById(R.id.bottomNav)

        bNav.selectedItemId = R.id.Sales

        buildLists()





        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")
        val current = LocalDateTime.now()

        val days = Duration.between(origin,current).toDays().toString()

        findViewById<TextView>(R.id.tvCatThree).text = days

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

    private fun buildLists() {

        val db = FirebaseFirestore.getInstance()
        db.collection("soldListings").whereEqualTo("user", currentUser?.userID).
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {

                if(error != null){
                    Log.e("Firestore Error", error.message.toString() )
                    return
                }

                for(dc : DocumentChange in value?.documentChanges!!){

                    if(dc.type == DocumentChange.Type.ADDED){

                        salesList.add(dc.document.toObject(SoldListing::class.java))

                    }
                }

                //itemAdapter.notifyDataSetChanged()

            }


        })

        buildCategories()
    }

    private fun buildCategories() {

        catList.add(SalesCategory("Electronics"))
        catList.add(SalesCategory("Apparel"))
        catList.add(SalesCategory("Media"))
        catList.add(SalesCategory("Furniture/Appliances"))
        catList.add(SalesCategory("Collectibles"))
        catList.add(SalesCategory("Other"))

        for(item in salesList){

            for(cat in catList){
                
                if(item.detail?.category == cat.category){
                    cat.totDollars?.plus(item.finalPrice!!)
                    cat.totItems?.plus(1)
                    cat.totCost?.plus(item.detail?.cost!!)
                }
            }

        }
    }
}