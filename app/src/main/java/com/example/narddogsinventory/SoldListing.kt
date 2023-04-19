package com.example.narddogsinventory

import android.os.Parcel
import android.os.Parcelable

data class SoldListing (

val detail : ActiveListing? = null,
val saleDate : String? = null,
val finalPrice : Float? = null,
val user : Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ActiveListing::class.java.classLoader),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(detail, flags)
        parcel.writeString(saleDate)
        parcel.writeValue(finalPrice)
        parcel.writeValue(user)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SoldListing> {
        override fun createFromParcel(parcel: Parcel): SoldListing {
            return SoldListing(parcel)
        }

        override fun newArray(size: Int): Array<SoldListing?> {
            return arrayOfNulls(size)
        }
    }
}
