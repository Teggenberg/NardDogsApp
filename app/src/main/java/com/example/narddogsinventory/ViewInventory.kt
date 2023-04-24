package com.example.narddogsinventory

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject


class ViewInventory : AppCompatActivity(), ItemAdapter.OnItemClickListener {
    private lateinit var db : FirebaseFirestore
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var itemAdapter : ItemAdapter
    private lateinit var itemList : ArrayList<ActiveListing>
    private lateinit var filteredList : ArrayList<ActiveListing>
    private  var userID : Long = 0
    private var currentUser : EntryUser? = null

    var filtered = false
    private lateinit var bNav : NavigationBarView
    private lateinit var dialogSearchButton : Button
    private lateinit var dialogSearchBuilder : AlertDialog.Builder
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_inventory)


        //search editText for dialog box
        val etSearch = EditText(this)
        //set to numeric entry
        etSearch.inputType = InputType.TYPE_CLASS_NUMBER

        //assign button to open dialog
        dialogSearchButton = findViewById(R.id.buttonItemSearch)
        //initialize reference for dialog window
        dialogSearchBuilder = AlertDialog.Builder(this)



        //listener for search button that launches dialog
        dialogSearchButton.setOnClickListener{

            //launch dialog and set views
            dialogSearchBuilder.setTitle("Item Search")
                .setView(etSearch)
                .setMessage("search by item Number")
                .setCancelable(true)
                .setPositiveButton("search"){dialogInterface,it->
                    //search button
                    //check edit text to make sure user input exists
                    if(!etSearch.text.isNullOrEmpty()){
                        //search database with item number entered by user
                        searchItem(etSearch.text.toString().toLong())
                    }

                }
                .setNegativeButton("cancel"){dialogInterface, it->
                    //cancel button
                    //close dialog
                    dialogInterface.cancel()
                }
                .show()
        }

        //decouple editText from dialog otherwise app will crash if dialog is opened more that once
        dialogSearchBuilder.setOnDismissListener(DialogInterface.OnDismissListener {
            (etSearch.parent as ViewGroup).removeView(
                etSearch
            )
        })


        //      CATEGORIES
        // get reference to the string array that we just created
        val category = resources.getStringArray(R.array.categories)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val catAdapter = ArrayAdapter(this, R.layout.dropdown_text, category)
        // get reference to the autocomplete text view
        val catDropDown = findViewById<AutoCompleteTextView>(R.id.itDropDownCat)
        // set adapter to the autocomplete tv to the arrayAdapter
        catDropDown.setAdapter(catAdapter)

        //initialize current user from previous activity
        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)

        //initialize recycler view and set attributes
        inventoryRecyclerView = findViewById(R.id.inventoryList)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(this)
        inventoryRecyclerView.setHasFixedSize(true)

        //initialize lists to populate recyclerview
        itemList = arrayListOf()
        filteredList = arrayListOf()
        filteredList.clear()

        //set adapter for the list and recyclerview
        itemAdapter = ItemAdapter(itemList, this)
        inventoryRecyclerView.adapter = itemAdapter
        eventChangeListener()

        //initialize and assign navBar
        bNav = findViewById(R.id.bottomNav)
        bNav.selectedItemId = R.id.Inventory


        //listener for category filter
        catDropDown.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->

                //capture current selection in dropdown as string
                val catFilter = catDropDown.text.toString()

                //no filter is 'all' is selected
                if (catFilter == "--ALL--") {
                    //boolean for recyclerview onClick
                    filtered = false
                    //refactor recyclerView with no filter
                    itemAdapter = ItemAdapter(itemList, this@ViewInventory)
                    inventoryRecyclerView.adapter = itemAdapter

                } else {
                    //boolean for recyclerView onClick
                    filtered = true
                    //clear current filtered list
                    filteredList.clear()
                    //apply filter to current list
                    filterCategory(catFilter)
                    //refactor recyclerView using filtered list
                    itemAdapter = ItemAdapter(filteredList, this@ViewInventory)
                    itemAdapter.notifyDataSetChanged()
                    inventoryRecyclerView.adapter = itemAdapter
                }
            }

        //navBar listener for app navigation
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
                    inventoryIntent.putExtra("userID", userID)
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


//      Inventory CATEGORIES
        // get reference to the string array that we just created
        val cat = resources.getStringArray(R.array.invCategories)
        // create an array adapter and pass the required parameter
        // pass the context, drop down layout , and array.
        val adapter = ArrayAdapter(this, R.layout.dropdown_text, cat)
        // get reference to the autocomplete text view
        val auto = findViewById<AutoCompleteTextView>(R.id.itDropDownCat)
        // set adapter to the autocomplete tv to the arrayAdapter
        auto.setAdapter(adapter)

        val switchActiveSold = findViewById<Switch>(R.id.switch_active_sold)
        val textViewActiveSold = findViewById<TextView>(R.id.text_view_active_sold)

        switchActiveSold.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textViewActiveSold.text = "Sold"
            } else {
                textViewActiveSold.text = "Active"
            }
        }

    }

    private fun filterCategory(catFilter: String) {

        for(item in itemList){

            if(item?.category == catFilter){
                filteredList.add(item)
            }
        }

    }

    private fun searchItem(itemID : Long) {

        val docID = currentUser?.email + itemID.toString()

        val db = FirebaseFirestore.getInstance()
        //val docRef = db.collection("itemListings").document(docID)

        db.collection("itemListings").document(docID).get().addOnCompleteListener(){
                task ->

                if(task.isSuccessful){

                    val userItem = task.result

                    if(userItem.exists()){

                        val viewItem = userItem.toObject<ActiveListing>()

                        val intent = Intent(this, ViewItem::class.java)
                        intent.putExtra("currentItem", viewItem)
                        intent.putExtra("currentUser", currentUser)
                        startActivity(intent)


                    }
                    else{
                        Toast.makeText(this, "item not found", Toast.LENGTH_SHORT).show()
                    }

                }
                else{

                    Toast.makeText(this, "Unable to contact server", Toast.LENGTH_SHORT)
                        .show()

                }

        }

    }

    override fun onItemClick(position: Int) {

        if(!filtered){searchItem(itemList[position].itemID!!)}
        else{searchItem(filteredList[position].itemID!!)}
    }

    private fun eventChangeListener() {

        //whereEqualTo("user", 1000000000).

        db = FirebaseFirestore.getInstance()
        db.collection("itemListings").whereEqualTo("user", currentUser?.userID)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {

                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {

                        if (dc.type == DocumentChange.Type.ADDED) {

                            itemList.add(dc.document.toObject(ActiveListing::class.java))

                        }
                    }

                    itemAdapter.notifyDataSetChanged()

                }


            })

        //Toast.makeText( this, "total items ${itemList.size}", Toast.LENGTH_LONG).show()


    }


}