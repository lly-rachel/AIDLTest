package com.example.aidltest

import android.os.Parcel
import android.os.Parcelable


open class Book(var bookName: String?, var BookId: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookName)
        parcel.writeInt(BookId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}