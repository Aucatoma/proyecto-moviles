package com.example.daniel.proyectomoviles
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.daniel.proyectomoviles.adaptadores.AdaptadorPendientes
import com.example.daniel.proyectomoviles.entidades.Recorrido
import com.example.daniel.proyectomoviles.swipeUtilities.SwipeController
import kotlin.collections.ArrayList
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.daniel.proyectomoviles.swipeUtilities.SwipeControllerActions

class PendientesFragment : Fragment() {

    lateinit var recyclerRecorrido: RecyclerView
    lateinit var listaRecorridos: ArrayList<Recorrido>
    lateinit var listaPendientes: ArrayList<Recorrido>

    lateinit var swipeController : SwipeController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view:View = inflater.inflate(R.layout.fragment_pendientes, container, false)

        listaRecorridos = ArrayList()
        listaPendientes = ArrayList()



        setUpRecyclerView(view)

        return view
    }

    private fun setUpRecyclerView(view: View) {

        recyclerRecorrido = view.findViewById(R.id.recycler_view_pendientes)
        recyclerRecorrido.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        llenarRecorridos()

        val adaptador = AdaptadorPendientes(listaPendientes,requireContext())
        recyclerRecorrido.adapter = adaptador

        setUpSwipeOnRecyclerView(adaptador)
    }

    private fun setUpSwipeOnRecyclerView(adaptador: AdaptadorPendientes) {

        swipeController = SwipeController(requireContext(), object : SwipeControllerActions(){

            override fun onRightClicked(position: Int) {


                enviarPendienteAHistorial()

                adaptador.recorridos.removeAt(position)
                adaptador.notifyItemRemoved(position)
                adaptador.notifyItemRangeChanged(position,adaptador.itemCount)
            }

            override fun onLeftClicked(position: Int) {

                mostrarDetallePendiente(listaPendientes[position])
            }
        })

        var itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(recyclerRecorrido)

        recyclerRecorrido.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })

    }


    private fun mostrarDetallePendiente(recorrido: Recorrido) {

        val materialDialog = MaterialDialog.Builder(requireContext())
                .title("Detalle")
                .customView(R.layout.alert_dialog_detalle_pendiente,true)
                .positiveText("OK")
                .show()

        if(materialDialog!=null){

            //Es importante tomar la customView!!
            val view = materialDialog.customView
            llenarDetalle(view, recorrido)
        }

    }

    private fun llenarDetalle(view: View?, recorrido: Recorrido) {

        val localizador = Geocoder(requireContext())

        var direccionOrigen : List<Address>
        var direccionDestino : List<Address>

        direccionOrigen = ArrayList()
        direccionDestino = ArrayList()

        direccionOrigen = localizador.getFromLocation(recorrido.origenLatitud, recorrido.origenLongitud,1)
        direccionDestino = localizador.getFromLocation(recorrido.destinoLatitud, recorrido.destinoLongitud,1)

        if(view!=null){

            var txtOrigen:TextView = view.findViewById(R.id.txt_origen_input)
            var txtDestino:TextView = view.findViewById(R.id.txt_destino_input)
            var txtDistancia:TextView = view.findViewById(R.id.txt_distancia_input)
            var txtFecha:TextView = view.findViewById(R.id.txt_fecha_input)
            var txtCoductor:TextView = view.findViewById(R.id.txt_conductor_input)
            var txtMetodoPago:TextView = view.findViewById(R.id.txt_metodo_pago_input)
            var txtCostoViaje:TextView = view.findViewById(R.id.txt_costo_viaje)

            txtOrigen.text = direccionOrigen[0].getAddressLine(0)
            txtDestino.text = direccionDestino[0].getAddressLine(0)
            txtDistancia.text = String.format("%.2f",recorrido.distanciaRecorrido)
            txtFecha.text = recorrido.fechaRecorrido
            txtCoductor.text = "To do"
            txtMetodoPago.text = "To do"
            txtCostoViaje.text = String.format("%.3f",recorrido.valorRecorrido)

        }


    }

    private fun enviarPendienteAHistorial() {

    }


    private fun llenarRecorridos() {

        //Aqui se consultaria todos los recorridos del cliente y lo meteria en la listaRecorridos








        //Ahora, para enviar al RV de pendientes, solo se escogen aquellos cuyo estado sea false,
        //ya que eso significa que están pendientes aún

        //Para hacer esto, primero recorremos el arreglo de los recorridos
        listaRecorridos.forEach { recorrido:Recorrido ->

            //Y añadimos solo los pendientes
            if(recorrido.estadoRecorrido.equals("P")){
                listaPendientes.add(recorrido)
            }

        }

    }


}
