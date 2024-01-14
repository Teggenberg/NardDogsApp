package com.izaiko.narddogsinventory

import android.os.Parcel
import android.os.Parcelable

data class ActiveListing(
    val age: Int? = null,
    val brand : String? = null,
    val category : String? = null,
    val condition : Int? = null,
    val cost : Float? = null,
    val estRetail : Float? = null,
    val imageURL : String? = null,
    val itemDesc : String? = null,
    val itemID : Long? = null,
    val notes : String? = null,
    val user : Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(age)
        parcel.writeString(brand)
        parcel.writeString(category)
        parcel.writeValue(condition)
        parcel.writeValue(cost)
        parcel.writeValue(estRetail)
        parcel.writeString(imageURL)
        parcel.writeString(itemDesc)
        parcel.writeValue(itemID)
        parcel.writeString(notes)
        parcel.writeValue(user)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActiveListing> {
        override fun createFromParcel(parcel: Parcel): ActiveListing {
            return ActiveListing(parcel)
        }

        override fun newArray(size: Int): Array<ActiveListing?> {
            return arrayOfNulls(size)
        }
    }
}