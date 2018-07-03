package com.example.daniel.proyectomoviles.http

import android.util.Log
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result

class HttpRequest {


    companion object {
        val direcccionIP = "192.168.100.48"
        val puerto = "1337"
        val uriBase = "http://$direcccionIP:$puerto"


        fun registrarCliente(cliente: String, foto: String, callback: (error: Boolean, datos: String) -> Any){
            var error = false
            var datos = ""
            val jsonBody = """
                {
                "cliente": $cliente,
                "foto": $foto
                }""".trimIndent()
            Log.i("RESPUESTA_REGISTRO", jsonBody)
            val request = "$uriBase/cliente/registrar".httpPost()
            request.header(Pair("Content-Type", "application/json"))
            request.body(jsonBody)
            request.responseString{ request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.i("HTTP_FUEL_ERROR", "${result.getException()} \n ${result.error.response}")
                        error = true
                        callback(error, datos)
                    }
                    is Result.Success -> {
                        datos = result.get()
                        callback(error, datos)
                    }
                }
            }
        }

        fun obtenerDatos(modelo: String, callback: (error: Boolean, datos: String) -> Any) {
            val request = "$uriBase/modelo".httpGet()
            var error = false
            var datos = ""

            request.responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.i("HTTP_FUEL_ERROR", "${result.getException()} \n ${result.error.response}")
                        error = true
                        callback(error, datos)
                    }
                    is Result.Success -> {
                        datos = result.get()
                        callback(error, datos)
                    }
                }
            }
        }

        fun obtenerDato(modelo: String, id: String, callback: (error: Boolean, datos: String) -> Any) {
            val request = "$uriBase/$modelo/$id".httpGet()
            var error = false
            var datos = ""

            request.responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.i("HTTP_FUEL_ERROR", "${result.getException()} \n ${result.error.response}")
                        error = true
                        callback(error, datos)
                    }
                    is Result.Success -> {
                        datos = result.get()
                        callback(error, datos)
                    }
                }
            }
        }

        fun actualizarDato(modelo: String, id: String, data: String, callback: (error: Boolean, datos: String) -> Any) {
            var error = false
            var datos = ""
            val request = "$uriBase/$modelo/$id".httpPut()
            request.header(Pair("Content-Type", "application/json"))
            request.body(data)

            request.responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.i("HTTP_FUEL_ERROR", "${result.getException()} \n ${result.error.response}")
                        error = true
                        callback(error, datos)
                    }
                    is Result.Success -> {
                        datos = result.get()
                        callback(error, datos)
                    }
                }
            }
        }

        fun insertarDato(modelo: String, data: String, callback: (error: Boolean, datos: String) -> Any) {
            var error = false
            var datos = ""
            val request = "$uriBase/$modelo".httpPost()
            request.header(Pair("Content-Type", "application/json"))
            request.responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.i("HTTP_FUEL_ERROR", "${result.getException()} \n ${result.error.response}")
                        error = true
                        callback(error, datos)
                    }
                    is Result.Success -> {
                        datos = result.get()
                        callback(error, datos)
                    }
                }
            }


        }

        fun eliminarDato(modelo: String, id: String, callback: (error: Boolean, datos: String) -> Any) {
            var error = false
            var datos = ""
            val request = "$uriBase/$modelo/$id".httpDelete()
            request.responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.i("HTTP_FUEL_ERROR", "${result.getException()} \n ${result.error.response}")
                        error = true
                        callback(error, datos)
                    }
                    is Result.Success -> {
                        datos = result.get()
                        callback(error, datos)
                    }
                }
            }

        }

    }
}