package com.example.daniel.proyectomoviles.entidades

import android.os.Parcel
import android.os.Parcelable

class Foto (val id:Int,
            var datos:String = "",
            val extension:String = "",
            val createdAt: Long = 0,
            val updatedAt: Long = 0) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(datos)
        parcel.writeString(extension)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Foto> {
        override fun createFromParcel(parcel: Parcel): Foto {
            return Foto(parcel)
        }

        override fun newArray(size: Int): Array<Foto?> {
            return arrayOfNulls(size)
        }
    }
}