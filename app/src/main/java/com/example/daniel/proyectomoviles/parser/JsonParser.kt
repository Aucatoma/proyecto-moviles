package com.example.daniel.proyectomoviles.parser

import com.beust.klaxon.Klaxon
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Foto
import com.example.daniel.proyectomoviles.entidades.Recorrido
import com.example.daniel.proyectomoviles.entidades.TarjetaCredito

class JsonParser {

    val klaxon = Klaxon()

    fun jsonToCliente(json: String): Cliente?{
        return klaxon.parse<Cliente>(json)
    }

    fun jsonToRecorrido(json:String):Recorrido?{
        return klaxon.parse<Recorrido>(json)
    }

    fun jsonToTarjeta(json: String): TarjetaCredito?{
        return klaxon.parse<TarjetaCredito>(json)
    }

    fun clienteToJson(cliente: Cliente): String{
        return """
            {"nombre":"${cliente.nombre}",
            "apellido":"${cliente.apellido}",
            "telefono":"${cliente.telefono}",
            "nombreUsuario":"${cliente.nombreUsuario}",
            "contraseniaUsuario":"${cliente.contraseniaUsuario}",
            "correoUsuario":"${cliente.correoUsuario}"
            }""".trim().trimIndent()
    }

    fun clienteJsonUpdate(cliente: Cliente): String {
        return """{
            "nombre":"${cliente.nombre}",
            "apellido":"${cliente.apellido}",
            "telefono": "${cliente.telefono}",
            "nombreUsuario": "${cliente.nombreUsuario}",
            "correoUsuario":"${cliente.correoUsuario}"
        }""".trimIndent().trim()
    }

    fun fotoToJson(foto: Foto): String{
        return """
            {
            "datos":"${foto.datos}",
            "extension":"${foto.extension}"
            }""".trimIndent()
    }

    fun tarjetaToJson(tarjetaCredito: TarjetaCredito): String{
        return """{
            "companiaTarjeta": "${tarjetaCredito.companiaTarjeta}",
            "numeroTarjeta": "${tarjetaCredito.numeroTarjeta}",
            "codigoSeguridad": "${tarjetaCredito.codigoSeguridad}",
            "mesTarjeta":"${tarjetaCredito.mesTarjeta}",
            "anioTarjeta":"${tarjetaCredito.anioTarjeta}",
            "clienteId":"${tarjetaCredito.clienteId}"
        }""".trimIndent().trim()
    }

    fun recorridoToJson(recorrido: Recorrido):String{

        return """
            {"origenLatitud":"${recorrido.origenLatitud}",
            "origenLongitud":"${recorrido.origenLongitud}",
            "destinoLatitud":"${recorrido.destinoLatitud}",
            "destinoLongitud":"${recorrido.destinoLongitud}",
            "distanciaRecorrido":"${recorrido.distanciaRecorrido}",
            "estadoRecorrido":"${recorrido.estadoRecorrido}",
            "fechaRecorrido":"${recorrido.fechaRecorrido}",
            "valorRecorrido":"${recorrido.valorRecorrido}",
            "tarjetaCreditoId":${recorrido.tarjetaCreditoId}
            }""".trim().trimIndent()


    }
}