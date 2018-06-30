package com.example.daniel.proyectomoviles.entidades

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class Cliente (val id:Int,
               val nombre:String,
               val apellido:String,
               val telefono:String,
               val nombreUsuario:String,
               val contraseniaUsuario:String,
               val correoUsuario:String,
               val tarjetasDeCredito: List<TarjetaCredito>?,
               val foto: Foto,
               val createdAt: Long,
               val updatedAt:Long) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(TarjetaCredito),
            parcel.readParcelable(Foto.javaClass.classLoader),
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
        parcel.writeTypedList(tarjetasDeCredito)
        parcel.writeParcelable(foto, flags)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cliente> {
        override fun createFromParcel(parcel: Parcel): Cliente {
            return Cliente(parcel)
        }

        override fun newArray(size: Int): Array<Cliente?> {
            return arrayOfNulls(size)
        }
    }

}