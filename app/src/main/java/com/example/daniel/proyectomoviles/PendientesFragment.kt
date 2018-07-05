package com.example.daniel.proyectomoviles
import android.graphics.Canvas
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

                irADetallePendienteFragment()
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


    private fun irADetallePendienteFragment() {

    }

    private fun enviarPendienteAHistorial() {

    }


    private fun llenarRecorridos() {

        //Aqui se consultaria todos los recorridos del cliente y lo meteria en la listaRecorridos


        //Supongamos que el resultado de los recorridos son los objetos de abajo
        listaRecorridos.add(Recorrido(-1, "-155.55896892", "-155.55896892",
                "-0.2033062",
                "-78.49077559999999",
                10.123,
                false,
                "06/08/1996",
                6.123,
                1,
                1,
                0,
                0))

        listaRecorridos.add(Recorrido(-1, "-0.2033062", "-78.49077559999999",
                "-0.2033062",
                "-78.49077559999999",
                6.123,
                false,
                "06/08/1996",
                6.123,
                1,
                1,
                0,
                0))

        listaRecorridos.add(Recorrido(-1, "-0.2033062", "-78.49077559999999",
                "-0.2033062",
                "-78.49077559999999",
                6.123,
                false,
                "06/08/1996",
                6.123,
                1,
                1,
                0,
                0))



        //Ahora, para enviar al RV de pendientes, solo se escogen aquellos cuyo estado sea false,
        //ya que eso significa que están pendientes aún

        //Para hacer esto, primero recorremos el arreglo de los recorridos
        listaRecorridos.forEach { recorrido:Recorrido ->

            //Y añadimos solo los pendientes
            if(!recorrido.estadoRecorrido){
                listaPendientes.add(recorrido)
            }

        }







    }


}
