package com.example.daniel.proyectomoviles.baseDeDatos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.*
import com.example.daniel.proyectomoviles.entidades.*

class DBHandler {

    companion object {
        private var dbHandler: DBHandler? = null
        private var dbOpenHelper: DBOpenHelper? = null

        fun getInstance(context: Context): DBHandler?{
            if(dbHandler == null) {
                dbHandler = DBHandler(context)
                return dbHandler
            }else{
                return dbHandler
            }
        }

    }

    private constructor (context: Context){
        dbOpenHelper = DBOpenHelper(context)
    }

    fun insertar(datos: Any){
        when(datos){
            is Cliente -> {
                val writableDatabase = dbOpenHelper!!.writableDatabase
                val cv = ContentValues().apply {
                    put(TablaCliente.COL_ID_CLIENTE, datos.id)
                    put(TablaCliente.COL_NOMBRE, datos.nombre)
                    put(TablaCliente.COL_APELLIDO, datos.apellido)
                    put(TablaCliente.COL_TELEFONO, datos.telefono)
                    put(TablaCliente.COL_NOM_USUARIO, datos.nombreUsuario)
                    put(TablaCliente.COL_CORREO_USUARIO, datos.correoUsuario)
                    put(TablaCliente.COL_JWT, datos.jwt)
                    put(TablaCliente.COL_ID_FOTO, datos.foto!!.id)
                }
                val resultado = writableDatabase.insert(TablaCliente.TABLE_NAME, null, cv)
                Log.i("RESULTADO_SQLITE", "$resultado")
                writableDatabase.close()
            }
            is Conductor -> {
                val writableDatabase = dbOpenHelper!!.writableDatabase
                val cv = ContentValues().apply {
                    put(TablaConductor.COL_ID_CONDUCTOR, datos.id)
                    put(TablaConductor.COL_NOMBRE, datos.nombre)
                    put(TablaConductor.COL_APELLIDO, datos.apellido)
                    put(TablaConductor.COL_TELEFONO, datos.telefono)
                    put(TablaConductor.COL_CORREO_USUARIO, datos.correoUsuario)
                }
                val resultado = writableDatabase.insert(TablaConductor.TABLE_NAME, null, cv)
                Log.i("RESULTADO_SQLITE", "$resultado")
                writableDatabase.close()
            }
            is Recorrido -> {
                val writableDatabase = dbOpenHelper!!.writableDatabase
                val cv = ContentValues().apply {
                    put(TablaRecorrido.COL_ID_RECORRIDO, datos.id)
                    put(TablaRecorrido.COL_ORI_LATITUD, datos.origenLatitud)
                    put(TablaRecorrido.COL_ORI_LONGITUD, datos.origenLongitud)
                    put(TablaRecorrido.COL_DES_LATITUD, datos.destinoLatitud)
                    put(TablaRecorrido.COL_DES_LONGITUD, datos.destinoLongitud)
                    put(TablaRecorrido.COL_EST_RECORRIDO, datos.estadoRecorrido)
                    put(TablaRecorrido.COL_FEC_RECORRIDO, datos.fechaRecorrido)
                    put(TablaRecorrido.COL_VAL_RECORRIDO, datos.valorRecorrido)
                    put(TablaRecorrido.COL_ID_CONDUCTOR, datos.conductor!!.id)
                    put(TablaRecorrido.COL_ID_TARJETA, datos.tarjetaCreditoId)
                }
                val resultado = writableDatabase.insert(TablaRecorrido.TABLE_NAME, null, cv)
                Log.i("RESULTADO_SQLITE", "$resultado")
                writableDatabase.close()
            }
            is TarjetaCredito -> {
                val writableDatabase = dbOpenHelper!!.writableDatabase
                val cv = ContentValues().apply {
                    put(TablaTarjetaCredito.COL_ID_TARJETA, datos.id)
                    put(TablaTarjetaCredito.COL_ANIO_TARJETA, datos.anioTarjeta)
                    put(TablaTarjetaCredito.COL_MES_TARJETA, datos.mesTarjeta)
                    put(TablaTarjetaCredito.COL_COD_SEGURIDAD, datos.codigoSeguridad)
                    put(TablaTarjetaCredito.COL_NUM_TARJETA, datos.numeroTarjeta)
                    put(TablaTarjetaCredito.COL_ID_CLIENTE, datos.clienteId)
                }
                val resultado = writableDatabase.insert(TablaTarjetaCredito.TABLE_NAME, null, cv)
                Log.i("RESULTADO_SQLITE", "$resultado")
                writableDatabase.close()
            }
            is Foto -> {
                val writableDatabase = dbOpenHelper!!.writableDatabase
                val byteArray = Base64.decode(datos.datos, Base64.NO_WRAP or Base64.URL_SAFE)
                val cv = ContentValues().apply {
                    put(TablaFoto.COL_ID_FOTO, datos.id)
                    put(TablaFoto.COL_FOTO, byteArray)
                    put(TablaFoto.COL_EXT, datos.extension)
                }
                val resultado = writableDatabase.insert(TablaFoto.TABLE_NAME, null, cv)
                Log.i("RESULTADO_SQLITE", "$resultado")
                writableDatabase.close()
            }
        }
    }

    fun obtenerUno(tabla: String, condiciones: Array<Pair<String, String>>? = null): Any?{
        val dbReadable = dbOpenHelper!!.readableDatabase

        var selection: String? = null
        var selectionArgs: Array<String>? = null
        condiciones?.let{
            selectionArgs = arrayOf("")
            selection = ""
            condiciones.forEachIndexed { index, pair ->
                selection += "${pair.first} = ?"
                selectionArgs!![index] = pair.second
                if(index  != condiciones.size - 1)
                    selection += " AND"
            }
        }

        val cursor = dbReadable.query(
                tabla,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        )
        var resultado: Any? = null
        with(cursor){
            while(moveToNext()){
                when(tabla.toUpperCase()){
                    "$TablaCliente.TABLE_NAME" -> resultado = obtenerCliente(this)
                    "$TablaTarjetaCredito.TABLE_NAME" -> resultado = obtenerTarjeta(this)
                }
            }
        }
        return resultado
    }

    private fun obtenerTarjeta(cursor: Cursor): TarjetaCredito? {

        var tarjetaCredito: TarjetaCredito? = null
        with(cursor){
            val id = getInt(getColumnIndexOrThrow("$TablaTarjetaCredito.COL_ID_TARJETA"))
            val numeroTarjeta = getString(getColumnIndexOrThrow("$TablaTarjetaCredito.COL_NUM_TARJETA"))
            val codigoSeguridad = getInt(getColumnIndexOrThrow("$TablaTarjetaCredito.COL_COD_SEGURIDAD"))
            val mesTarjeta = getInt(getColumnIndexOrThrow("$TablaTarjetaCredito.COL_MES_TARJETA"))
            val anioTarjeta = getInt(getColumnIndexOrThrow("$TablaTarjetaCredito.COL_ANIO_TARJETA"))
            val clienteId = getInt(getColumnIndexOrThrow("$TablaTarjetaCredito.COL_ID_CLIENTE"))
            tarjetaCredito = TarjetaCredito(id = id,
                    numeroTarjeta = numeroTarjeta,
                    codigoSeguridad = codigoSeguridad.toString(),
                    mesTarjeta = mesTarjeta,
                    anioTarjeta = anioTarjeta,
                    clienteId = clienteId)
        }


        return tarjetaCredito


    }


    private fun obtenerCliente(cursor: Cursor): Cliente?{
        var cliente: Cliente? = null
        with(cursor){
            val id = getInt(getColumnIndexOrThrow("${TablaCliente.COL_ID_CLIENTE}"))
            val nombre = getString(getColumnIndexOrThrow("${TablaCliente.COL_NOMBRE}"))
            val apellido = getString(getColumnIndexOrThrow("${TablaCliente.COL_APELLIDO}"))
            val nombreUsuario = getString(getColumnIndexOrThrow("${TablaCliente.COL_NOM_USUARIO}"))
            val correoUsuario = getString(getColumnIndexOrThrow("${TablaCliente.COL_CORREO_USUARIO}"))
            val jwt = getString(getColumnIndexOrThrow("${TablaCliente.COL_JWT}"))
            val telefono = getString(getColumnIndexOrThrow("${TablaCliente.COL_TELEFONO}"))
            val idFoto = getInt(getColumnIndexOrThrow("${TablaCliente.COL_ID_FOTO}"))
            cliente = Cliente(
                    id = id,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono,
                    nombreUsuario = nombreUsuario,
                    correoUsuario = correoUsuario,
                    jwt = jwt,
                    foto = Foto(id = idFoto))
        }
        return cliente
    }

    fun obtenerDatos(tabla:String, condiciones: Array<Pair<String, String>>? = null) : ArrayList<Any>{

        val dbReadable = dbOpenHelper!!.readableDatabase
        val entidad : ArrayList<Any> = ArrayList()

        var selection: String? = null
        var selectionArgs: Array<String>? = null
        condiciones?.let{
            selectionArgs = arrayOf("")
            selection = ""
            condiciones.forEachIndexed { index, pair ->
                selection += "${pair.first} = ?"
                selectionArgs!![index] = pair.second
                if(index  != condiciones.size - 1)
                    selection += " AND"
            }
        }

        val cursor = dbReadable.query(tabla, null, selection, selectionArgs, null, null, null)

        if (cursor.moveToFirst()) {
            when(tabla.toUpperCase()){
                "$TablaTarjetaCredito.TABLE_NAME" ->
                    do {
                        entidad.add(obtenerTarjeta(cursor)!!)

                    } while (cursor.moveToNext())
            }


        }
        cursor.close()
        dbReadable.close()


        return entidad





    }


    fun actualizar(){

    }

    fun eliminar(tabla: String, selectionArgs: ArrayList<Pair<String, Any>>){

    }

}