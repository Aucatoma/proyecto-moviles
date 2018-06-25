package com.example.daniel.proyectomoviles.entidades

import android.os.Parcel
import android.os.Parcelable

class Recorrido (val id:Int,
                 val origenLatitud:String,
                 val origenLongitud:String,
                 val destinoLatitud:String,
                 val destinoLongitud:String,
                 val distanciaRecorrido: Double,
                 val estadoRecorrido: Boolean,
                 val fechaRecorrido: String,
                 val valorRecorrido: Double,
                 val tarjetaCreditoId:Int,
                 val conductorId:Int,
                 val createdAt:Long,
                 val updatedAt:Long) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(origenLatitud)
        parcel.writeString(origenLongitud)
        parcel.writeString(destinoLatitud)
        parcel.writeString(destinoLongitud)
        parcel.writeDouble(distanciaRecorrido)
        parcel.writeByte(if (estadoRecorrido) 1 else 0)
        parcel.writeString(fechaRecorrido)
        parcel.writeDouble(valorRecorrido)
        parcel.writeInt(tarjetaCreditoId)
        parcel.writeInt(conductorId)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recorrido> {
        override fun createFromParcel(parcel: Parcel): Recorrido {
            return Recorrido(parcel)
        }

        override fun newArray(size: Int): Array<Recorrido?> {
            return arrayOfNulls(size)
        }
    }
}