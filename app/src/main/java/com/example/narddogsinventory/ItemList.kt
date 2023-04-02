package com.example.narddogsinventory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

//not sure why these are angry...
private lateinit var itemNum : TextView
private lateinit var itemCost : EditText
private lateinit var itemDesc : EditText
private lateinit var itemBrand : EditText

private lateinit var itemRetail : EditText
private lateinit var itemCond : AutoCompleteTextView
private lateinit var itemCat : AutoCompleteTextView
private lateinit var itemNotes : EditText
private var currentUser : EntryUser? = null

class ItemList : AppCompatActivity() {

    private lateinit var bNav : NavigationBarView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)



//      CONDITIONS
        // get reference to the string array that we just created
        val con = resources.getStringArray(R.array.conditions)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_text, con)
        // get reference to the autocomplete text view
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.etDropDownCon)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)

//      CATEGORIES
        // get reference to the string array that we just created
        val cat = resources.getStringArray(R.array.categories)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val adapter = ArrayAdapter(this, R.layout.dropdown_text, cat)
        // get reference to the autocomplete text view
        val auto = findViewById<AutoCompleteTextView>(R.id.etDropDownBox)
        // set adapter to the autocomplete tv to the arrayAdapter
        auto.setAdapter(adapter)


//      Open CAMERA

        findViewById<Button>(R.id.opCamera).setOnClickListener{

            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }

        //assign variable to textview to capture current item ID index
        itemNum = findViewById(R.id.etItemID)

        //capture user data passed from previous activity for db access and updating
        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)

        //store currentListing (item ID) into variable to assign to textview
        var itemid = currentUser?.currentListing

        //set textview to display the current item ID to be assigned to new listing
        itemNum.setText("$itemid")

        //assign values from widgets into variables for data collection of new item
        itemBrand = findViewById(R.id.etBrand)
        itemDesc = findViewById<EditText>(R.id.etItemName)
        itemCat = findViewById(R.id.etDropDownBox)
        itemCond = findViewById(R.id.etDropDownCon)
        itemCost = findViewById<EditText>(R.id.etCost)
        itemRetail = findViewById(R.id.etRetail)
        itemNotes = findViewById(R.id.etItemNotes)

        //assign bottom navigation bar to variable
        bNav = findViewById(R.id.bottomNav)

        //ensure the current activity is selected on navigation bar
        bNav.selectedItemId = R.id.AddItem

        //add new item into db when button is clicked
        //then increment user's current listing field in db, and local user's item ID
        //clear fields in widgets,and update textview with new item ID assignment
        findViewById<Button>(R.id.addButton).setOnClickListener{

            addItemToDb()
            updateCurrentTotalListing()

            val refresh = Intent(this, ItemList::class.java)
            refresh.putExtra("currentUser", currentUser)
            startActivity(refresh)
            finish()

        }

        //bottom navigation bar listener
        //pass user object to next activity have local access to data
        bNav.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.home -> {
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    homeIntent.putExtra("currentUser", currentUser)
                    startActivity(homeIntent)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.AddItem -> {
                    val addIntent = Intent(this, ItemList::class.java)
                    addIntent.putExtra("currentUser", currentUser)
                    startActivity(addIntent)
                    return@setOnItemSelectedListener true
                }
                R.id.Inventory -> {
                    val inventoryIntent = Intent(this, ViewInventory::class.java)
                    //inventoryIntent.putExtra("userID", userID)
                    inventoryIntent.putExtra("currentUser", currentUser)
                    startActivity(inventoryIntent)
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

    }

    private fun updateCurrentTotalListing() {

        //access to users database, reference document assigned to current user
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(currentUser?.email.toString())

        //access the document for the current user in database
        docRef.get().addOnSuccessListener { documentsnapshot ->

            //assign document to object for data access
            var updateCurrentListing = documentsnapshot.toObject<EntryUser>()

            //check to make sure document is not null
            if (updateCurrentListing != null) {

                //increment currentListing for next item addition
                updateCurrentListing?.currentListing = updateCurrentListing?.currentListing?.
                plus(1)

                //overwrite the fields in the database with updated values
                db.collection("Users").document(currentUser?.email.toString()).
                set(updateCurrentListing)

            }

        }

        //update currentListing for locally accessible user data to match database
        currentUser?.currentListing = currentUser?.currentListing?.plus(1)
    }

    private fun addItemToDb() {

        //capture all values from widgets to update data members for ActiveListing object
        val age = 0 //need to implement operation for age
        val brand = itemBrand.text.toString()
        val category = itemCat.text.toString()
        val cost  = itemCost.text.toString().toFloatOrNull()
        val estRetail = itemRetail.text.toString().toFloatOrNull()
        val imageURL = "coming soon" //this will include the url for the photo when complete
        val itemDesc = itemDesc.text.toString()
        val itemId = currentUser?.currentListing
        val notes = itemNotes.text.toString()
        val user = currentUser?.userID

        //access to database
        val db = FirebaseFirestore.getInstance()

        //store all values into custom class object
        val newItem = ActiveListing(age, brand, category, cost, estRetail, imageURL, itemDesc,
             itemId, notes, user)

        //add document into itemListings collection using custom class object
        db.collection("itemListings").document().set(newItem)

    }


}