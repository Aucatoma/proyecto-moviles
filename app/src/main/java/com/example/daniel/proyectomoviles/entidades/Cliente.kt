package com.example.daniel.proyectomoviles.entidades

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class Cliente (val id:Int,
               val nombre:String,
               val apellido:String,
               val telefono:String,
               val nombreUsuario:String,
               val contraseniaUsuario:String = "",
               val correoUsuario:String,
               val tarjetasDeCredito: List<TarjetaCredito>? = null,
               val foto: Foto? = null,
               val jwt: String = "",
               val createdAt: Long = 0,
               val updatedAt:Long = 0) : Parcelable {


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
            parcel.readString(),
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
        parcel.writeString(jwt)
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

    override fun toString(): String {
        return """
            $id
            $nombre
            $apellido
            $telefono
            $nombreUsuario
            $contraseniaUsuario
            $createdAt
            $updatedAt
            ${foto!!.id}
            ${foto!!.extension}
            ${tarjetasDeCredito!!.size}
            ${tarjetasDeCredito[0].recorridos!![0].conductor!!.nombre}
            """
    }

}