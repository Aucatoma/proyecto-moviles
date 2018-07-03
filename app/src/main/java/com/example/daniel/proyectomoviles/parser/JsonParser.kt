package com.example.daniel.proyectomoviles.parser

import com.beust.klaxon.Klaxon
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Foto
import com.example.daniel.proyectomoviles.entidades.Recorrido

class JsonParser {

    val klaxon = Klaxon()

    fun jsonToCliente(json: String): Cliente?{
        return klaxon.parse<Cliente>(json)
    }

    fun clienteToJson(cliente: Cliente): String{
        return """
            {
            "nombre":"${cliente.nombre}",
            "apellido":"${cliente.apellido}",
            "telefono":"${cliente.telefono}",
            "nombreUsuario":"${cliente.nombreUsuario}",
            "contraseniaUsuario":${cliente.contraseniaUsuario},
            "correoUsuario":"${cliente.correoUsuario}"
            }""".trimIndent()
    }



    fun fotoToJson(foto: Foto): String{
        return """
            {
            "datos":"${foto.datos}",
            "extension":"${foto.extension}"
            }""".trimIndent()
    }

    fun recorridoToJson(recorrido: Recorrido):String{

        return """
            {
            "origenLatitud":"${recorrido.origenLatitud}",
            "origenLongitud":"${recorrido.origenLongitud}",
            "destinoLatitud":"${recorrido.destinoLatitud}",
            "destinoLongitud":"${recorrido.destinoLongitud}",
            "distanciaRecorrido":"${recorrido.distanciaRecorrido}",
            "estadoRecorrido":"${recorrido.estadoRecorrido}",
            "fechaRecorrido":"${recorrido.fechaRecorrido}",
            "valorRecorrido":"${recorrido.valorRecorrido}",
            "tarjetaCreditoId":"${recorrido.tarjetaCreditoId}",
            "conductorId":"${recorrido.conductorId}",
            "createdAt":"${recorrido.createdAt}",
            "updatedAt":"${recorrido.updatedAt}",
            }""".trimIndent()


    }
}