package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.*
import java.time.Duration
import java.time.LocalDateTime

class Sales : AppCompatActivity() {

    //references to textViews for data display
    private lateinit var sales : TextView
    private lateinit var catOne : TextView
    private lateinit var catTwo : TextView
    private lateinit var catThree : TextView
    private lateinit var catFour : TextView
    private lateinit var catFive : TextView
    private lateinit var catSix : TextView
    private lateinit var db : FirebaseFirestore

    private var totalRev : Float = 0f

    private lateinit var bNav : NavigationBarView
    private var currentUser : EntryUser? = null

    //lists to capture the display data
    private lateinit var salesList : ArrayList<SoldListing>
    private lateinit var catList : ArrayList<SalesCategory>

    //to toggle filters for dates. 'all time' default as true for no date filtering
    private var allTime = true
    private var pastThirty = false
    private var pastNinety = false



    //@RequiresApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)


        //initialize user from previous activity
        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)

        //initialize lists
        catList = arrayListOf()
        salesList = arrayListOf()

        //populate lists defaults to all time, no filters
        buildLists()


        //assign bottom nav, set item to current activity
        bNav = findViewById(R.id.bottomNav)
        bNav.selectedItemId = R.id.Sales

        //assign textView references
        sales = findViewById(R.id.tvTotalRev)
        catOne = findViewById(R.id.tvCatOne)
        catTwo = findViewById(R.id.tvCatTwo)
        catThree = findViewById(R.id.tvCatThree)
        catFour = findViewById(R.id.tvCatFour)
        catFive = findViewById(R.id.tvCatFive)
        catSix = findViewById(R.id.tvCatSix)

        //listener for user click on nav bar
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


        // ALL Time, Month, and 90 days radio button code
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val allButton = findViewById<RadioButton>(R.id.All_Time_button)
        val monthButton = findViewById<RadioButton>(R.id.month_button)
        val yearButton = findViewById<RadioButton>(R.id.three_Months_button)


        //listener for date filter buttons
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.All_Time_button -> {

                    //reassign filters for proper date data
                    allTime = true
                    pastNinety = false
                    pastThirty = false
                    //clear current data for fresh start
                    catList.clear()
                    salesList.clear()
                    totalRev = 0f
                    //rebuild lists with current filer selection
                    buildLists()
                }
                R.id.month_button -> {

                    //reassign filters for proper date data
                    allTime = false
                    pastNinety = false
                    pastThirty = true
                    //clear current data for fresh start
                    catList.clear()
                    salesList.clear()
                    totalRev = 0f
                    //rebuild lists with current filer selection
                    buildLists()
                }
                R.id.three_Months_button -> {

                    //reassign filters for proper date data
                    allTime = false
                    pastNinety = true
                    pastThirty = false
                    //clear current data for fresh start
                    catList.clear()
                    salesList.clear()
                    totalRev = 0f
                    //rebuild lists with current filer selection
                    buildLists()
                }
            }
        }

    }

    private fun buildLists() {

        //populate category list with default categories, all values initialized to 0
        catList.add(SalesCategory("Electronics", 0, 0f, 0f, 0f, 0f))
        catList.add(SalesCategory("Apparel",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Media",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Furniture/Appliances",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Collectibles",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Other",0,0f,0f,0f,0f))

        //reference and access to database
        db = FirebaseFirestore.getInstance()
        db.collection("soldListings").whereEqualTo("user", currentUser?.userID).
        addSnapshotListener(object : EventListener<QuerySnapshot>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {

                //if no connection to db is established
                if(error != null){
                    Log.e("Firestore Error", error.message.toString() )
                    Toast.makeText(this, "cannot contact server", Toast.LENGTH_SHORT).show()
                    return
                }

                //go through each document in query
                for(dc : DocumentChange in value?.documentChanges!!){

                    //add new item to sales list while looping through query
                    if(dc.type == DocumentChange.Type.ADDED){
                        Log.e("Document :", dc.toString() )
                        salesList.add(dc.document.toObject(SoldListing::class.java))
                    }
                }

                //use the items in sales list to populate data in category list
                buildCategories()
                sales.text = "Total Sales : $" + "%.2f".format(totalRev)

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildCategories() {






        for(item : SoldListing in salesList){
            Log.e("Item :", item.toString() )

            for(cat : SalesCategory in catList){

                if(pastThirty){

                    if(calculateAge(item?.saleDate) > 3){
                        break
                    }
                    Toast.makeText(this, calculateAge(item.saleDate).toString(), Toast.LENGTH_SHORT).show()
                }

                if(pastNinety){

                    if(calculateAge(item?.saleDate) > 7){
                        break
                    }
                    Toast.makeText(this, calculateAge(item.saleDate).toString(), Toast.LENGTH_SHORT).show()

                }


                cat.totDollars!!.plus(1)

                if(item.detail?.category == cat.category){
                    Log.e("Cat :", item.detail?.category.toString() )
                    Log.e("cost :", item.detail?.cost.toString() )

                    val add = item.detail?.cost

                    Log.e("var :", add.toString() )

                    cat.totItems += 1
                    cat.totCost += add!!
                    cat.totDollars += item.finalPrice!!
                    Log.e("cat cost :", cat.totCost.toString() )

                }

            }


            if(pastNinety){

                if(calculateAge(item?.saleDate) > 7){
                    continue
                }
                Toast.makeText(this, calculateAge(item.saleDate).toString(), Toast.LENGTH_SHORT).show()

            }

            if(pastThirty){

                if(calculateAge(item?.saleDate) > 3){
                    continue
                }
                Toast.makeText(this, calculateAge(item.saleDate).toString(), Toast.LENGTH_SHORT).show()
            }

            totalRev += item.finalPrice!!

        }

        calculateMarginAndAverageSale()


    }

    private fun calculateMarginAndAverageSale() {

        for(cat : SalesCategory in catList){

            cat.averageSale = cat.totDollars!! / cat.totItems
            cat.margin = 1 - (cat.totCost / cat.totDollars!!)
        }

        var catListTwo = catList.sortedWith(compareBy{it.totDollars})

        catSix.text = catListTwo[0].category + " : $" + "%.2f".format(catListTwo[0].totDollars)
        catFive.text = catListTwo[1].category + " : $" + "%.2f".format(catListTwo[1].totDollars)
        catFour.text = catListTwo[2].category + " : $" + "%.2f".format(catListTwo[2].totDollars)
        catThree.text = catListTwo[3].category + " : $" + "%.2f".format(catListTwo[3].totDollars)
        catTwo.text = catListTwo[4].category + " : $" + "%.2f".format(catListTwo[4].totDollars)
        catOne.text = catListTwo[5].category + " : $" + "%.2f".format(catListTwo[5].totDollars)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(date: String?): Int {

        //use 01-01-23 as constant origin point
        val soldOn = LocalDateTime.parse(date)

        //current date captured when item is added
        val current = LocalDateTime.now()

        //return the number of days between origin and current date
        return Duration.between(soldOn,current).toDays().toInt()



    }
}