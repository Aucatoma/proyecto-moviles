package com.example.daniel.proyectomoviles.adaptadores

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.example.daniel.proyectomoviles.R
import com.example.daniel.proyectomoviles.entidades.TarjetaCredito

class AdaptadorTarjetasFrag(private val myDataset: ArrayList<TarjetaCredito>, var context: Context): RecyclerView.Adapter<AdaptadorTarjetasFrag.ViewHolder>() {

    val compTarDra = context.resources.obtainTypedArray(R.array.drawable_tarjetas)
    val comTarArray = context.resources.getStringArray(R.array.array_tarjetas)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        var imgView_compania: ImageView
        var txtView_numero: TextView
        lateinit var tarjeta: TarjetaCredito

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        init {
            imgView_compania = view.findViewById(R.id.imgView_fila_card_compania)
            txtView_numero = view.findViewById(R.id.textView_fila_card_number)

            view.setOnCreateContextMenuListener(this)

        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            val menuInflater: MenuInflater = MenuInflater(context)
            menuInflater.inflate(R.menu.context_menu, menu)

            menu?.findItem(R.id.editar)?.setOnMenuItemClickListener {
                mostrarDialogo()
                true
            }

            menu?.findItem(R.id.eliminar)?.setOnMenuItemClickListener {
                crearDialogo()
                true
            }
        }


        fun crearDialogo() {
            builder.setMessage("¿Eliminar datos?")
                    .setPositiveButton("Sí", { dialog, which ->
                        eliminarAplicacion(tarjeta)
                        true
                    })
                    .setNegativeButton("No", { dialog, which ->
                        true
                    })
            val dialogo = builder.create()
            dialogo.show()
        }

        fun mostrarDialogo() {

        }


        fun eliminarAplicacion(tarjetaCredito: TarjetaCredito) {

        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarjetaActual = myDataset[position]
        holder.txtView_numero.text = tarjetaActual.numeroTarjeta.replaceRange(0, 12, "XXXXXXXXXXXX")
        comTarArray.forEachIndexed{ index: Int, s: String? ->
            if(s == tarjetaActual.companiaTarjeta)
                holder.imgView_compania.setImageDrawable(compTarDra.getDrawable(index))
        }
        holder.tarjeta = tarjetaActual
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.fila_tarjeta_frag, parent, false)

        return ViewHolder(itemView)
    }

}