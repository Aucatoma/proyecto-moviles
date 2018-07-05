package com.example.daniel.proyectomoviles.entidades

import android.os.Parcel
import android.os.Parcelable
import com.beust.klaxon.Json

class TarjetaCredito (val id:Int,
                      val numeroTarjeta:String,
                      val codigoSeguridad:String,
                      val mesTarjeta:Int,
                      val anioTarjeta:Int,
                      @Json(ignored = false) val recorridos:List<Recorrido>? = null,
                      val clienteId:Int,
                      val createdAt:Long,
                      val updatedAt:Long) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createTypedArrayList(Recorrido),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(numeroTarjeta)
        parcel.writeString(codigoSeguridad)
        parcel.writeInt(mesTarjeta)
        parcel.writeInt(anioTarjeta)
        parcel.writeTypedList(recorridos)
        parcel.writeInt(clienteId)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {

        return this.numeroTarjeta

    }

    companion object CREATOR : Parcelable.Creator<TarjetaCredito> {
        override fun createFromParcel(parcel: Parcel): TarjetaCredito {
            return TarjetaCredito(parcel)
        }

        override fun newArray(size: Int): Array<TarjetaCredito?> {
            return arrayOfNulls(size)
        }
    }
}