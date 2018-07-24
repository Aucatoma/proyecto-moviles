package com.example.daniel.proyectomoviles


import android.content.Intent
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.example.daniel.proyectomoviles.adaptadores.AdaptadorHistorial
import com.example.daniel.proyectomoviles.baseDeDatos.DBHandler
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaConductor
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaRecorrido
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaTarjetaCredito
import com.example.daniel.proyectomoviles.entidades.Conductor
import com.example.daniel.proyectomoviles.entidades.Recorrido
import com.example.daniel.proyectomoviles.entidades.TarjetaCredito
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.swipeUtilities.SwipeController
import com.example.daniel.proyectomoviles.swipeUtilities.SwipeControllerActions


class HistorialFragment : Fragment() {

    private lateinit var recyclerRecorrido: RecyclerView
    lateinit var listaRecorridos: ArrayList<Recorrido>

    lateinit var conductor: Conductor
    lateinit var tarjetaCredito: TarjetaCredito


    lateinit var swipeController : SwipeController


    //-------------DATOS PARA EMAIL-----------------//
    var origenParaEmail = ""
    var destinoParaEmail = ""
    var costoRecorridoParaEmail = ""
    var distanciaRecorridoParaEmail = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_pendientes, container, false)

        listaRecorridos = ArrayList()
        setUpRecyclerView(view)

        return view
    }

    private fun setUpRecyclerView(view: View) {
        recyclerRecorrido = view.findViewById(R.id.recycler_view_pendientes)
        recyclerRecorrido.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        llenarRecorridos()

        val adaptador = AdaptadorHistorial(listaRecorridos,requireContext())
        recyclerRecorrido.adapter = adaptador

        setUpSwipeOnRecyclerView(adaptador)
    }

    private fun setUpSwipeOnRecyclerView(adaptador: AdaptadorHistorial) {

        swipeController = SwipeController(requireContext(), object : SwipeControllerActions(){

            override fun onRightClicked(position: Int) {

                eliminarRecorrido(listaRecorridos[position], position, adaptador)
                /*adaptador.recorridos.removeAt(position)
                adaptador.notifyItemRemoved(position)
                adaptador.notifyItemRangeChanged(position,adaptador.itemCount)*/
            }

            override fun onLeftClicked(position: Int) {
                mostrarDetalle(listaRecorridos[position])
            }
        },"H")

        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(recyclerRecorrido)



        recyclerRecorrido.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })

    }

    private fun eliminarRecorrido(recorrido: Recorrido, position: Int, adaptador: AdaptadorHistorial) {

        HttpRequest.eliminarDato("Recorrido","${recorrido.id}",{error, datos ->

            if(error){
                Toast.makeText(requireContext(),"Error", Toast.LENGTH_SHORT).show()
            }else{
                Log.i("RESPUESTA_REGISTRO", datos)
                DBHandler.getInstance(activity!!)!!.eliminar(TablaRecorrido.TABLE_NAME,"${recorrido.id}")
                adaptador.recorridos.removeAt(position)
                adaptador.notifyItemRemoved(position)
                adaptador.notifyItemRangeChanged(position,adaptador.itemCount)

            }

        })



    }

    private fun mostrarDetalle(recorrido: Recorrido) {
        val materialDialog = MaterialDialog.Builder(requireContext())
                .onAny{ dialog, which ->

                    if(which.name=="POSITIVE"){compartirRecorrido()}
                    else if (which.name=="NEGATIVE"){ }

                }
                .title(requireContext().getString(R.string.cabecera_detalle_recorrido))
                .customView(R.layout.alert_dialog_detalle_pendiente,true)
                .positiveText(requireContext().getString(R.string.boton_compartir))
                .negativeText("OK")
                .show()

        if(materialDialog!=null){

            val view = materialDialog.customView
            llenarDetalle(view, recorrido)
        }
    }

    private fun compartirRecorrido() {

        val address = arrayOf("","")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL,address)
        intent.putExtra(Intent.EXTRA_SUBJECT,"")
        intent.putExtra(Intent.EXTRA_TEXT,"Origen:   ${origenParaEmail}\n" +
                                                "Destino:   ${destinoParaEmail}\n"+
                                                "Costo:     ${costoRecorridoParaEmail}\n"+
                                                "Distancia: ${distanciaRecorridoParaEmail}\n")
        startActivity(intent)

    }

    private fun llenarDetalle(view: View?, recorrido: Recorrido) {

        val localizador = Geocoder(requireContext())

        val direccionOrigen : List<Address>
        val direccionDestino : List<Address>

        direccionOrigen = localizador.getFromLocation(recorrido.origenLatitud, recorrido.origenLongitud,1)
        direccionDestino = localizador.getFromLocation(recorrido.destinoLatitud, recorrido.destinoLongitud,1)

        if(view!=null){

            val txtOrigen: TextView = view.findViewById(R.id.txt_origen_input)
            val txtDestino: TextView = view.findViewById(R.id.txt_destino_input)
            val txtDistancia: TextView = view.findViewById(R.id.txt_distancia_input)
            val txtFecha: TextView = view.findViewById(R.id.txt_fecha_input)
            val txtCoductor: TextView = view.findViewById(R.id.txt_conductor_input)
            val txtMetodoPago: TextView = view.findViewById(R.id.txt_metodo_pago_input)
            val txtCostoViaje: TextView = view.findViewById(R.id.txt_costo_viaje)

            txtOrigen.text = direccionOrigen[0].getAddressLine(0)
            txtDestino.text = direccionDestino[0].getAddressLine(0)
            txtDistancia.text = String.format("%.2f",recorrido.distanciaRecorrido)+" m"
            txtFecha.text = recorrido.fechaRecorrido
            txtCostoViaje.text = String.format("%.3f",recorrido.valorRecorrido)

            //------------SE ALMACENAN LOS DATOS PARA COMPARTIR EN EMAIL-------------------//
            origenParaEmail = direccionOrigen[0].getAddressLine(0)
            destinoParaEmail = direccionDestino[0].getAddressLine(0)
            distanciaRecorridoParaEmail = String.format("%.2f",recorrido.distanciaRecorrido)+" m"
            costoRecorridoParaEmail = String.format("%.3f",recorrido.valorRecorrido)


            if(recorrido.conductor!=null){
                txtCoductor.text = obtenerConductor(recorrido.conductor.id)
            }

            txtMetodoPago.text = obtenerTarjetaCredito(recorrido.tarjetaCreditoId)


        }

    }

    private fun obtenerTarjetaCredito(id: Int): String {

        tarjetaCredito = DBHandler.getInstance(requireContext())!!.obtenerUno(TablaTarjetaCredito.TABLE_NAME, arrayOf(Pair(TablaTarjetaCredito.COL_ID_TARJETA, "${id}"))) as TarjetaCredito
        return tarjetaCredito.numeroTarjeta

        return ""
    }



    private fun obtenerConductor(id: Int): String {

        conductor = DBHandler.getInstance(requireContext())!!.obtenerUno(TablaConductor.TABLE_NAME, arrayOf(Pair(TablaConductor.COL_ID_CONDUCTOR,"${id}"))) as Conductor
        return "${conductor.nombre} ${conductor.apellido}"

        return ""
    }

    private fun llenarRecorridos() {

       listaRecorridos = DBHandler.getInstance(requireContext())!!.obtenerDatos(TablaRecorrido.TABLE_NAME, arrayOf(Pair(TablaRecorrido.COL_EST_RECORRIDO, "F"))) as ArrayList<Recorrido>

    }


}
