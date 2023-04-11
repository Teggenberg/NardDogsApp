package com.example.narddogsinventory

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime

class ViewItem : AppCompatActivity() {

    private lateinit var itemBrand: TextView
    private lateinit var itemDesc : TextView
    private lateinit var itemCategory : TextView
    private lateinit var itemCondition : TextView
    private lateinit var itemCost : TextView
    private lateinit var itemAge : TextView
    private lateinit var itemNotes : TextView
    private lateinit var itemId : TextView

    private  var currentItem : ActiveListing? = null
    private var currentUser : EntryUser? = null
    private lateinit var back : Button

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)

        currentUser = intent.getParcelableExtra("currentUser", EntryUser::class.java)
        currentItem = intent.getParcelableExtra("currentItem", ActiveListing::class.java)

        back = findViewById(R.id.buttonReturn)
        itemBrand = findViewById(R.id.tvItemBrand)
        itemDesc = findViewById(R.id.tvItemDesc)
        itemCategory = findViewById(R.id.tvItemCat)
        itemCondition = findViewById(R.id.tvItemCondition)
        itemCost = findViewById(R.id.tvItemCost)
        itemAge = findViewById(R.id.tvAge)
        itemNotes = findViewById(R.id.tvItemNotes)
        itemId = findViewById(R.id.tvItemID)

        itemDesc.text = currentItem?.itemDesc.toString()
        itemBrand.text = currentItem?.brand.toString()
        itemCategory.text = currentItem?.category.toString()
        itemCondition.text = decipherCondition(currentItem?.condition)
        itemCost.text = currentItem?.cost.toString()
        itemAge.text = calculateAge(currentItem?.age)
        itemNotes.text = currentItem?.notes.toString()
        itemId.text = currentItem?.itemID.toString()


        back.setOnClickListener{

            val intent = Intent(this, ViewInventory::class.java)
            intent.putExtra("currentUser", currentUser)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(age: Int?): CharSequence? {

        //use 01-01-23 as constant origin point
        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")

        //current date captured when item is added
        val current = LocalDateTime.now()

        //return the number of days between origin and current date
        val compare = Duration.between(origin,current).toDays().toInt()

        return (compare - age!!).toString()

    }


    private fun decipherCondition(condition: Int?): CharSequence? {

        val rating : CharSequence?
        when(condition){

            1 -> rating = "Roughed Up"
            2 -> rating = "Fair"
            3 -> rating = "Good"
            4 -> rating = "Great"
            5 -> rating = "Excellent"
            else -> rating = " "
        }

        return rating

    }


}