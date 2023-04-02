package com.example.narddogsinventory

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var itemNum : TextView
private lateinit var itemCost : EditText
private lateinit var itemDesc : EditText
private lateinit var itemBrand : EditText
private lateinit var itemRetail : EditText
private lateinit var itemCond : AutoCompleteTextView
private lateinit var itemCat : AutoCompleteTextView
private lateinit var itemNotes : EditText
class ItemList : AppCompatActivity() {

    private lateinit var bNav : NavigationBarView

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

        var itemid = intent.getLongExtra("itemNum", 0)
        itemNum = findViewById(R.id.etItemID)

        val userID = intent.getLongExtra("userID", 0)

        itemNum.setText("$itemid")

        itemBrand = findViewById(R.id.etBrand)
        itemDesc = findViewById<EditText>(R.id.etItemName)
        itemCat = findViewById(R.id.etDropDownBox)
        itemCond = findViewById(R.id.etDropDownCon)
        itemCost = findViewById<EditText>(R.id.etCost)
        itemRetail = findViewById(R.id.etRetail)
        itemNotes = findViewById(R.id.etItemNotes)






        bNav = findViewById(R.id.bottomNav)

        bNav.selectedItemId = R.id.AddItem

        findViewById<Button>(R.id.addButton).setOnClickListener{

            addItemToDb()

        }

        bNav.setOnItemSelectedListener {

            when(it.itemId) {

                R.id.home -> {
                    val homeIntent = Intent(this, LoginActivity::class.java)
                    startActivity(homeIntent)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.AddItem -> {
                    val mainIntent = Intent(this, ItemList::class.java)
                    startActivity(mainIntent)
                    return@setOnItemSelectedListener true
                }
                R.id.Inventory -> {
                    val inventoryIntent = Intent(this, ViewInventory::class.java)
                    //inventoryIntent.putExtra("userID", userID)
                    startActivity(inventoryIntent)
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }

    }

    private fun addItemToDb() {

        val age = 0
        val brand = itemBrand.text.toString()
        val category = itemCat.text.toString()
        val cost  = itemCost.text.toString().toFloatOrNull()
        val estRetail = itemRetail.text.toString().toFloatOrNull()
        val imageURL = "coming soon"
        val itemDesc = itemDesc.text.toString()
        val itemID = intent.getLongExtra("itemNum", 0)
        val notes = itemNotes.text.toString()
        val user = intent.getLongExtra("userID", 0)


        val db = FirebaseFirestore.getInstance()


        val newItem = ActiveListing(age, brand, category, cost, estRetail, imageURL, itemDesc,
             itemID, notes, user)

        db.collection("itemListings").document().set(newItem)

    }


}