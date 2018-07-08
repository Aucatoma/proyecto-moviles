package com.example.daniel.proyectomoviles.entidades

import android.os.Parcel
import android.os.Parcelable

class Conductor(val id:Int,
                val nombre:String,
                val apellido:String,
                val telefono:String,
                val nombreUsuario:String,
                val contraseniaUsuario:String,
                val correoUsuario:String,
                val recorridos: List<Recorrido>? = null,
                val autos:List<Auto>? = null,
                val fotoId: Int = 0,
                val createdAt: Long,
                val updatedAt:Long) : Parcelable  {


    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(Recorrido),
            parcel.createTypedArrayList(Auto),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeString(apellido)
        parcel.writeString(telefono)
        parcel.writeString(nombreUsuario)
        parcel.writeString(contraseniaUsuario)
        parcel.writeString(correoUsuario)
        parcel.writeTypedList(recorridos)
        parcel.writeTypedList(autos)
        parcel.writeInt(fotoId as Int)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Conductor> {
        override fun createFromParcel(parcel: Parcel): Conductor {
            return Conductor(parcel)
        }

        override fun newArray(size: Int): Array<Conductor?> {
            return arrayOfNulls(size)
        }
    }
}