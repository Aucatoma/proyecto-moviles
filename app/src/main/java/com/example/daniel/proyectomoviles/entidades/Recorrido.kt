package com.example.daniel.proyectomoviles.entidades

import android.os.Parcel
import android.os.Parcelable

class Recorrido (val id:Int,
                 val origenLatitud:Double,
                 val origenLongitud:Double,
                 val destinoLatitud:Double,
                 val destinoLongitud:Double,
                 val distanciaRecorrido: Double,
                 val estadoRecorrido: String,
                 val fechaRecorrido: String,
                 val valorRecorrido: Double,
                 val tarjetaCreditoId:Int,
                 val conductor:Conductor? = null,
                 val createdAt:Long,
                 val updatedAt:Long) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readParcelable(Conductor.javaClass.classLoader),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeDouble(origenLatitud)
        parcel.writeDouble(origenLongitud)
        parcel.writeDouble(destinoLatitud)
        parcel.writeDouble(destinoLongitud)
        parcel.writeDouble(distanciaRecorrido)
        parcel.writeString(estadoRecorrido)
        parcel.writeString(fechaRecorrido)
        parcel.writeDouble(valorRecorrido)
        parcel.writeInt(tarjetaCreditoId)
        parcel.writeParcelable(conductor, flags)
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