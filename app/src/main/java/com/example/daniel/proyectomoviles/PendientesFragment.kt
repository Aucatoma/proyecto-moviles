package com.example.daniel.proyectomoviles


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.daniel.proyectomoviles.adaptadores.AdaptadorPendientes
import com.example.daniel.proyectomoviles.entidades.Recorrido
import java.util.*

class PendientesFragment : Fragment() {

    lateinit var recyclerRecorrido: RecyclerView
    lateinit var listaRecorridos: ArrayList<Recorrido>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

            return inflater.inflate(R.layout.fragment_pendientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        listaRecorridos = ArrayList()

        recyclerRecorrido = view.findViewById(R.id.recycler_view_pendientes)
        recyclerRecorrido.layoutManager = LinearLayoutManager(requireContext())

        llenarRecorridos()

        val adaptador = AdaptadorPendientes(listaRecorridos,requireContext())
        recyclerRecorrido.adapter = adaptador


    }

    private fun llenarRecorridos() {


        //Aqui se consultaria todos los recorridos de ese clientes cuyo estado sea false porque significa que esta pendiente

        listaRecorridos.add(Recorrido(-1, "-155.55896892", "-155.55896892",
                "-17.83844136",
                "-155.55896892",
                6.123,
                false,
                Date().toString(),
                6.123,
                1,
                1,
                0.toLong(),
                0.toLong()

        ))


    }


}
