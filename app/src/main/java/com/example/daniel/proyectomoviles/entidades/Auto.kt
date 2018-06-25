package com.example.daniel.proyectomoviles.entidades

import android.os.Parcel
import android.os.Parcelable

class Auto (val id:Int,
            val marcaAuto:String,
            val modeloAuto:String,
            val placaAuto:String,
            val conductorId:Int,
            val createdAt:Long,
            val updatedAt:Long) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(marcaAuto)
        parcel.writeString(modeloAuto)
        parcel.writeString(placaAuto)
        parcel.writeInt(conductorId)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Auto> {
        override fun createFromParcel(parcel: Parcel): Auto {
            return Auto(parcel)
        }

        override fun newArray(size: Int): Array<Auto?> {
            return arrayOfNulls(size)
        }
    }
}