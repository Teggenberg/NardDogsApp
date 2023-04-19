package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.*
import java.time.Duration
import java.time.LocalDateTime

class Sales : AppCompatActivity() {

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

    private lateinit var salesList : ArrayList<SoldListing>
    private lateinit var catList : ArrayList<SalesCategory>



    //@RequiresApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)

        catList = arrayListOf()
        salesList = arrayListOf()

        catList.add(SalesCategory("Electronics", 0, 0f, 0f, 0f, 0f))
        catList.add(SalesCategory("Apparel",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Media",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Furniture/Appliances",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Collectibles",0,0f,0f,0f,0f))
        catList.add(SalesCategory("Other",0,0f,0f,0f,0f))

        buildLists()



        bNav = findViewById(R.id.bottomNav)

        bNav.selectedItemId = R.id.Sales

        sales = findViewById(R.id.tvTotalRev)
        catOne = findViewById(R.id.tvCatOne)
        catTwo = findViewById(R.id.tvCatTwo)
        catThree = findViewById(R.id.tvCatThree)
        catFour = findViewById(R.id.tvCatFour)
        catFive = findViewById(R.id.tvCatFive)
        catSix = findViewById(R.id.tvCatSix)




//        sales.text = "Total Revenue : $totalRev"
//        catOne.text = catList[0].category + " : " + catList[0].totDollars.toString()
//        catTwo.text = catList[1].category + " : " + catList[1].totDollars.toString()
//        catThree.text = catList[2].category + " : " + catList[2].totDollars.toString()
//        catFour.text = catList[3].category + " : " + catList[3].totDollars.toString()
//        catFive.text = catList[4].category + " : " + catList[4].totDollars.toString()
//        catSix.text = catList[5].category + " : " + catList[5].totDollars.toString()





        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")
        val current = LocalDateTime.now()

        val days = Duration.between(origin,current).toDays().toString()






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

        db = FirebaseFirestore.getInstance()
        db.collection("soldListings").whereEqualTo("user", currentUser?.userID).
        addSnapshotListener(object : EventListener<QuerySnapshot>{
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
                        Log.e("Document :", dc.toString() )

                        salesList.add(dc.document.toObject(SoldListing::class.java))


                    }
                }

                buildCategories()
                sales.text = "Total Sales : $" + "%.2f".format(totalRev)
//                catOne.text = catList[0].category + " : " + catList[0].totDollars.toString()
//                catTwo.text = catList[1].category + " : " + catList[1].totDollars.toString()
//                catThree.text = catList[2].category + " : " + catList[2].totDollars.toString()
//                catFour.text = catList[3].category + " : " + catList[3].totDollars.toString()
//                catFive.text = catList[4].category + " : " + catList[4].totDollars.toString()
//                catSix.text = catList[5].category + " : " + catList[5].totDollars.toString()
                Log.e("Category :", catList[0].totDollars.toString() )



            }


        })

        Toast.makeText( this, "total items ${salesList.size}", Toast.LENGTH_LONG).show()

        //buildCategories()
    }

    private fun buildCategories() {

//        catList.add(SalesCategory("Electronics", 0, 0f, 0f, 0f, 0f))
//        catList.add(SalesCategory("Apparel",0,0f,0f,0f,0f))
//        catList.add(SalesCategory("Media",0,0f,0f,0f,0f))
//        catList.add(SalesCategory("Furniture/Appliances",0,0f,0f,0f,0f))
//        catList.add(SalesCategory("Collectibles",0,0f,0f,0f,0f))
//        catList.add(SalesCategory("Other",0,0f,0f,0f,0f))



        for(item : SoldListing in salesList){
            Log.e("Item :", item.toString() )

            for(cat : SalesCategory in catList){

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
}