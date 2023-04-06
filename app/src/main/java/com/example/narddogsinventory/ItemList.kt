package com.example.narddogsinventory

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputBinding
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import com.example.narddogsinventory.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.Reference
import java.text.SimpleDateFormat
import java.util.*


//not sure why these are angry...
private lateinit var itemNum : TextView
private lateinit var itemCost : EditText
private lateinit var itemDesc : EditText
private lateinit var itemBrand : EditText

private lateinit var itemRetail : EditText
private lateinit var itemCond : AutoCompleteTextView
private lateinit var itemCat : AutoCompleteTextView
private lateinit var itemNotes : EditText

var imageReference = Firebase.storage.reference

private var currentUser : EntryUser? = null

class ItemList : AppCompatActivity() {

    private lateinit var bNav : NavigationBarView
    private lateinit var oButton: Button
    private  lateinit var binding: ActivityMainBinding
    private var currentFile: Uri? = null
    val REQUEST_TAKE_PHOTO = 1
    lateinit var photoPath: String
    private lateinit var captureButton: Button
    private lateinit var imageView: ImageView
    val REQUEST_CODE = 200
    private val REQUEST_CAMERA_PERMISSION = 1
    private val REQUEST_IMAGE_CAPTURE = 2




    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        val btIndent = findViewById<EditText>(R.id.etCost)


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


        captureButton = findViewById(R.id.opCamera)
//        imageView = findViewById(R.id.imageView)

        captureButton.setOnClickListener {
//
            takePicture()
//
        }

        //findViewById<Button>(R.id.opCamera).setOnClickListener{

//            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivity(intent)
       // }

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

        //Upload Image code

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        binding.imageView.setOnClickListener{
//            Intent(Intent.ACTION_GET_CONTENT).also {
//                it.type="image/*"
//                imageLauncher.launch(it)
//            }
//        }


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

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            saveImageToFile(imageBitmap)
        }
    }


    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )

        // Log the file path
        Log.d("createImageFile", "Created image file at: ${image.absolutePath}")

        // Return the file
        return image
    }


    private fun saveImageToFile(bitmap: Bitmap) {
        val file = createImageFile()
        val outputStream: OutputStream? = FileOutputStream(file)

        Log.d("saveImageToFile", "Saving image to file: ${file.absolutePath}")

        outputStream?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
            it.close()

            // Add the image to the device's gallery
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, file.name)
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by camera")
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase().hashCode())
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.name.toLowerCase())

        }
    }



    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if (result.resultCode== RESULT_OK){
            result?.data?.data?.let{
                currentFile = it
                binding.imageView.setImageURI(it)
            }
        }
        else{
            Toast.makeText(this,"Canceled",Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(filename:String){
        try {
            currentFile?.let {
                imageReference.child("").putFile(it).addOnSuccessListener {
                    Toast.makeText(this,"Uploaded Successfully", Toast.LENGTH_SHORT)
                }.addOnFailureListener{
                    Toast.makeText(this,"Failed to upload", Toast.LENGTH_SHORT)
                }
            }
        }catch (e:java.lang.Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }

    }

}