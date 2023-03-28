package com.example.narddogsinventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class ViewInventory : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var itemAdapter : ItemAdapter
    private lateinit var itemList : ArrayList<ActiveListing>
    private  var userID : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_inventory)

        userID = intent.getLongExtra("userID", 0)
        Log.d("ViewInventory", userID.toString())

        inventoryRecyclerView = findViewById(R.id.inventoryList)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(this)
        inventoryRecyclerView.setHasFixedSize(true)

        itemList = arrayListOf()

        itemAdapter = ItemAdapter(itemList)

        inventoryRecyclerView.adapter = itemAdapter

        eventChangeListener()
    }

    private fun eventChangeListener() {

        //whereEqualTo("user", 1000000000).

        db = FirebaseFirestore.getInstance()
        db.collection("itemListings").whereEqualTo("user", 1000000000).
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

                            itemList.add(dc.document.toObject(ActiveListing::class.java))

                        }
                    }

                    itemAdapter.notifyDataSetChanged()

                }


            })
//        db.collection("itemListings")
//            .whereEqualTo("user", 1000000004)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    itemList.add(document.toObject(ActiveListing::class.java))
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Firestore Error", exception.message.toString())
//            }
//            //itemAdapter.notifyDataSetChanged()
//        }


    }


}