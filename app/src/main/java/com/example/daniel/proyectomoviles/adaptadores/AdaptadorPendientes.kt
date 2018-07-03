package com.example.daniel.proyectomoviles.adaptadores

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.daniel.proyectomoviles.R
import com.example.daniel.proyectomoviles.entidades.Recorrido

class AdaptadorPendientes (internal var recorridos: ArrayList<Recorrido>, internal var ctx: Context) : RecyclerView.Adapter<AdaptadorPendientes.ViewHolderPendientes>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPendientes {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pendiente,null,false)
        return ViewHolderPendientes(view)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolderPendientes, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    inner class ViewHolderPendientes(itemView: View): RecyclerView.ViewHolder(itemView) {

    }


}