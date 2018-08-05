package com.example.daniel.proyectomoviles.adaptadores

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.example.daniel.proyectomoviles.R
import com.example.daniel.proyectomoviles.baseDeDatos.DBHandler
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaTarjetaCredito
import com.example.daniel.proyectomoviles.entidades.TarjetaCredito
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.parser.JsonParser
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener

class AdaptadorTarjetasFrag(private val myDataset: ArrayList<TarjetaCredito>, var contextApp: Context): RecyclerView.Adapter<AdaptadorTarjetasFrag.ViewHolder>() {

    val compTarDra = contextApp.resources.obtainTypedArray(R.array.drawable_tarjetas)
    val comTarArray = contextApp.resources.getStringArray(R.array.array_tarjetas)
    val jsonParser = JsonParser()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        var imgView_compania: ImageView
        var txtView_numero: TextView
        lateinit var tarjeta: TarjetaCredito

        val builder: AlertDialog.Builder = AlertDialog.Builder(contextApp)

        init {
            imgView_compania = view.findViewById(R.id.imgView_fila_card_compania)
            txtView_numero = view.findViewById(R.id.textView_fila_card_number)

            view.setOnCreateContextMenuListener(this)

        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            val menuInflater: MenuInflater = MenuInflater(contextApp)
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
            builder.setMessage(R.string.delete_data_que)
                    .setPositiveButton(R.string.log_out_yes, { dialog, which ->
                        eliminarAplicacion(tarjeta)
                        true
                    })
                    .setNegativeButton(R.string.log_out_no, { dialog, which ->
                        true
                    })
            val dialogo = builder.create()
            dialogo.show()
        }

        fun mostrarDialogo() {
            val dialog = MaterialDialog.Builder(contextApp)
                    .title(R.string.credit_card_dialog_tarjeta)
                    .customView(R.layout.credit_card_dialog, true)
                    .negativeText(R.string.credit_card_dialog_cancel)
                    .positiveText(R.string.credit_card_dialog_ok)
                    .onPositive{ dialog, which ->
                        val tarjeta = crearTarjeta(dialog.customView as View)
                        actualizarTarjeta(tarjeta)
                    }
                    .build()
            /* Generación de contenido del spinner */
            val spinner = dialog.customView!!.findViewById<Spinner>(R.id.spinner_card_dialog_compania)
            val adapter = ArrayAdapter.createFromResource(contextApp, R.array.array_tarjetas, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener {
                onItemSelected { adapterView, view, i, l ->
                    val typedArray = contextApp.resources.obtainTypedArray(R.array.drawable_tarjetas)
                    dialog.customView!!.findViewById<ImageView>(R.id.imgView_card_dialog_compania).setImageDrawable(typedArray.getDrawable(i))
                }
            }
            llenarDialog(dialog.customView as View)
            dialog.show()
        }


        fun eliminarAplicacion(tarjetaCredito: TarjetaCredito) {
            HttpRequest.eliminarDato("TarjetaCredito", tarjetaCredito.id.toString(), { error, datos ->
                if(error){
                    Toast.makeText(contextApp, contextApp.resources.getString(R.string.usuario_frag_tarjetas_fallo_del), Toast.LENGTH_LONG).show()

                }else{

                    Toast.makeText(contextApp, contextApp.resources.getString(R.string.usuario_frag_tarjetas_exito_del), Toast.LENGTH_LONG).show()
                    DBHandler.getInstance(contextApp)!!.eliminar(TablaTarjetaCredito.TABLE_NAME, tarjetaCredito.id.toString())
                    myDataset.remove(tarjetaCredito)
                    notifyDataSetChanged()

                }

            })
        }
        fun crearTarjeta(view: View): TarjetaCredito{
            val tipo = view.findViewById<Spinner>(R.id.spinner_card_dialog_compania).selectedItem as String
            val numero = view.findViewById<EditText>(R.id.editText_card_dialog_numero).text.toString()
            val codigo = view.findViewById<EditText>(R.id.editText_card_dialog_codigo).text.toString()
            val mes = view.findViewById<EditText>(R.id.editText_card_dialog_mes).text.toString().toInt()
            val anio = view.findViewById<EditText>(R.id.editText_card_dialog_anio).text.toString().toInt()

            return TarjetaCredito(
                    id = tarjeta.id,
                    companiaTarjeta = tipo,
                    numeroTarjeta = numero,
                    codigoSeguridad = codigo,
                    mesTarjeta = mes,
                    anioTarjeta = anio,
                    clienteId = tarjeta.clienteId

            )

        }

        fun actualizarTarjeta(tarjetaCredito: TarjetaCredito){
            val tarjetaJson = jsonParser.tarjetaToJson(tarjetaCredito)
            HttpRequest.actualizarDato("TarjetaCredito", tarjetaCredito.id.toString(), tarjetaJson, { error, datos ->
                if(error){
                    Toast.makeText(contextApp, contextApp.resources.getString(R.string.usuario_frag_tarjetas_fallo_upd), Toast.LENGTH_LONG).show()
                }else{
                    Log.i("ACTUALIZAR", datos)
                    Toast.makeText(contextApp, contextApp.resources.getString(R.string.usuario_frag_tarjetas_exito_upd), Toast.LENGTH_LONG).show()
                    DBHandler.getInstance(contextApp)!!.actualizar(tarjetaCredito)
                    tarjeta.numeroTarjeta = tarjetaCredito.numeroTarjeta
                    tarjeta.anioTarjeta = tarjetaCredito.anioTarjeta
                    tarjeta.mesTarjeta = tarjetaCredito.mesTarjeta
                    tarjeta.codigoSeguridad = tarjetaCredito.codigoSeguridad
                    tarjeta.companiaTarjeta = tarjetaCredito.companiaTarjeta
                    notifyDataSetChanged()

                }
            })
        }
        fun llenarDialog(view: View){
            val arrayTarjetas = contextApp.resources.getStringArray(R.array.array_tarjetas)
            arrayTarjetas.forEachIndexed{ index: Int, s: String? ->
                if(tarjeta!!.companiaTarjeta == s){
                    view.findViewById<Spinner>(R.id.spinner_card_dialog_compania).setSelection(index)
                    return@forEachIndexed
                }
            }
            view.findViewById<EditText>(R.id.editText_card_dialog_numero).append(tarjeta!!.numeroTarjeta)
            view.findViewById<EditText>(R.id.editText_card_dialog_codigo).append(tarjeta!!.codigoSeguridad)
            view.findViewById<EditText>(R.id.editText_card_dialog_mes).append(tarjeta!!.mesTarjeta.toString())
            view.findViewById<EditText>(R.id.editText_card_dialog_anio).append(tarjeta!!.anioTarjeta.toString())

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