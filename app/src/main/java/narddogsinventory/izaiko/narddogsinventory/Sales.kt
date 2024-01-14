package com.izaiko.narddogsinventory

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebSettings.TextSize
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.TextStyle

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
    private lateinit var subdialog : AlertDialog.Builder

    private var totalRev : Float = 0f

    private lateinit var bNav : NavigationBarView
    private var currentUser : EntryUser? = null

    //lists to capture the display data
    private lateinit var salesList : ArrayList<SoldListing>
    private lateinit var catList : ArrayList<SalesCategory>
    private lateinit var catListTwo : List<SalesCategory>

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
        catListTwo = arrayListOf()

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

        catOne.setOnClickListener{

            //dialog that displays sub metrics for category in textview
            createDialog(catListTwo[5])
        }

        catTwo.setOnClickListener{

            //dialog that displays sub metrics for category in textview
            createDialog(catListTwo[4])
        }

        catThree.setOnClickListener{

            //dialog that displays sub metrics for category in textview
            createDialog(catListTwo[3])
        }

        catFour.setOnClickListener{

            //dialog that displays sub metrics for category in textview
            createDialog(catListTwo[2])
        }

        catFive.setOnClickListener{

            //dialog that displays sub metrics for category in textview
            createDialog(catListTwo[1])
        }

        catSix.setOnClickListener{

            //dialog that displays sub metrics for category in textview
            createDialog(catListTwo[0])
        }

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

        allButton.isChecked = true


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

    private fun createDialog(s: SalesCategory) {

        //initialize and populate submetrics in formatted textview to include in dialog
        val subs = TextView(this)
        subs.text = submetricsInfo(s)

        subs.setTextSize(20F)

        //initialize dialog
        subdialog = AlertDialog.Builder(this)

        //declare title for dialog
        val title = s.category

        //launch dialog and set views
        subdialog.setTitle(title)
            .setView(subs)
            .setMessage("Submetric Data for $title \n")
            .setCancelable(true)
            .setNegativeButton("close"){dialogInterface, it->
                //cancel button
                //close dialog
                dialogInterface.cancel()
            }
            .show()

        //decouple textView from dialog otherwise app will crash if dialog is opened more that once
        subdialog.setOnDismissListener(DialogInterface.OnDismissListener {
            (subs.parent as ViewGroup).removeView(
                subs
            )
        })

    }

    private fun submetricsInfo(s: SalesCategory): CharSequence? {

        val sales = "$" + "%.2f".format(s.averageSale)
        val marg = s.margin?.times(100)
        val margString = "%.2f".format(marg)


        return "\t\t   Total sales:       ${s.totItems} \n\n" +
                "\t\t   Average Sale:   $sales \n\n" +
                "\t\t   Margin Rate:     $margString%"


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
                    Toast.makeText(this@Sales, "cannot contact server", Toast.LENGTH_SHORT).show()
                    return
                }

                //go through each document in query
                for(dc : DocumentChange in value?.documentChanges!!){

                    //add new item to sales list while looping through query
                    if(dc.type == DocumentChange.Type.ADDED){

                        salesList.add(dc.document.toObject(SoldListing::class.java))
                    }
                }

                //use the items in sales list to populate data in category list
                buildCategories()
                //sales.text = "Total Sales : $" + "%.2f".format(totalRev)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildCategories() {

        //loop through each item to build category data
        for(item : SoldListing in salesList){

            //loop through each category to check for match with item
            for(cat : SalesCategory in catList){

                //check to see if previous 30 day button is selected
                if(pastThirty){

                    //excludes items that were sold more than 30 days ago (currently set to 3 days
                    //for testing
                    if(calculateAge(item?.saleDate) > 30){
                        break
                    }
                }

                //check to see if previous 90 day button is selected
                if(pastNinety){

                    //excludes items that were sold more than 90 days ago (currently set to 7 days
                    //for testing
                    if(calculateAge(item?.saleDate) > 90){
                        break
                    }
                }


                //cat.totDollars!!.plus(1)

                //check to see items category against current category
                if(item.detail?.category == cat.category){

                    //capture the cost of the current item
                    val add = item.detail?.cost

                    //increment category data with data from item
                    cat.totItems += 1
                    cat.totCost += add!!
                    cat.totDollars += item.finalPrice!!

                    //exit loop
                    break
                }
            }

            //check if  90 day filter is selected
            if(pastNinety){

                //check to see if item sold more than 90 days ago
                //(7 days for testing)
                if(calculateAge(item?.saleDate) > 90){
                    //continue to next loop iteration without incrementing totRev
                    continue
                }
            }

            //check if 30 day filter is selected
            if(pastThirty){

                //check to see if item sold more than 30 days ago
                //(3 days for testing)
                if(calculateAge(item?.saleDate) > 30){
                    //continue to next loop iteration without incrementing totRev
                    continue
                }
            }

            //increment totRev by item's sold price
            totalRev += item.finalPrice!!

        }

        //populate sub metrics for category
        calculateMarginAndAverageSale()

    }

    private fun calculateMarginAndAverageSale() {

        //loop through each category in catList
        for(cat : SalesCategory in catList){

            //calculate the avg sale price and margin percentage for category
            cat.averageSale = cat.totDollars!! / cat.totItems
            cat.margin = 1 - (cat.totCost / cat.totDollars!!)
        }

        //sort list so that categories are arranged in order of sales amount
        catListTwo = catList.sortedWith(compareBy{it.totDollars})

        //populate textViews with data from sorted category list
        catSix.text = catListTwo[0].category + " : $" + "%.2f".format(catListTwo[0].totDollars)
        catFive.text = catListTwo[1].category + " : $" + "%.2f".format(catListTwo[1].totDollars)
        catFour.text = catListTwo[2].category + " : $" + "%.2f".format(catListTwo[2].totDollars)
        catThree.text = catListTwo[3].category + " : $" + "%.2f".format(catListTwo[3].totDollars)
        catTwo.text = catListTwo[4].category + " : $" + "%.2f".format(catListTwo[4].totDollars)
        catOne.text = catListTwo[5].category + " : $" + "%.2f".format(catListTwo[5].totDollars)
        sales.text = "Total Sales : $" + "%.2f".format(totalRev)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(date: String?): Int {

        //use the string passed as the date sold
        val soldOn = LocalDateTime.parse(date)

        //current date
        val current = LocalDateTime.now()

        //return the number of days between sales date and current date
        return Duration.between(soldOn,current).toDays().toInt()

    }
}