package com.example.narddogsinventory

import android.os.Parcel
import android.os.Parcelable

data class EntryUser(
    var currentListing : Long? = null,
    val email : String? = null,
    val firstName : String? = null,
    val lastName : String? = null,
    val password : String? = null,
    val totInvested : Float? = null,
    val totListings : Int? = null,
    val totSales : Float? = null,
    val userID : Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(currentListing)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(password)
        parcel.writeValue(totInvested)
        parcel.writeValue(totListings)
        parcel.writeValue(totSales)
        parcel.writeValue(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EntryUser> {
        override fun createFromParcel(parcel: Parcel): EntryUser {
            return EntryUser(parcel)
        }

        override fun newArray(size: Int): Array<EntryUser?> {
            return arrayOfNulls(size)
        }
    }

}
//) : Parcelable {
//
//    constructor(parcel: Parcel) : this(
//        parcel.readValue(Long::class.java.classLoader) as? Long,
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readValue(Long::class.java.classLoader) as? Long
//    ) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeValue(currentListing)
//        parcel.writeString(email)
//        parcel.writeString(firstName)
//        parcel.writeString(lastName)
//        parcel.writeString(password)
//        parcel.writeValue(userID)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<EntryUser> {
//        override fun createFromParcel(parcel: Parcel): EntryUser {
//            return EntryUser(parcel)
//        }
//
//        override fun newArray(size: Int): Array<EntryUser?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
