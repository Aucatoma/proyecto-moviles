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
                    put(TablaRecorrido.COL_DIST_RECORRIDO,datos.distanciaRecorrido)
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
                    put(TablaTarjetaCredito.COL_COM_TARJETA, datos.companiaTarjeta)
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
                    "${TablaCliente.TABLE_NAME}" -> resultado = obtenerCliente(this)
                    "${TablaTarjetaCredito.TABLE_NAME}" -> resultado = obtenerTarjeta(this)
                    "${TablaConductor.TABLE_NAME}" -> resultado = obtenerConductor(this)
                    "${TablaFoto.TABLE_NAME}" -> resultado = obtenerFoto(this)
                }
            }
        }
        return resultado
    }

    private fun obtenerConductor(cursor: Cursor): Conductor? {
        var conductor:Conductor? = null
        with(cursor){
            val id = getInt(getColumnIndexOrThrow("${TablaConductor.COL_ID_CONDUCTOR}"))
            val nombre = getString(getColumnIndexOrThrow("${TablaConductor.COL_NOMBRE}"))
            val apellido = getString(getColumnIndexOrThrow("${TablaConductor.COL_APELLIDO}"))
            val telefono = getString(getColumnIndexOrThrow("${TablaConductor.COL_TELEFONO}"))
            conductor = Conductor(id = id,
                    nombre = nombre,
                    apellido = apellido,
                    telefono = telefono)
        }


        return conductor

    }


    private fun obtenerFoto(cursor: Cursor): Foto?{
        var foto: Foto? = null
        with(cursor){
            val id = getInt(getColumnIndexOrThrow("${TablaFoto.COL_ID_FOTO}"))
            val datos = getBlob(getColumnIndexOrThrow("${TablaFoto.COL_FOTO}"))
            val ext = getString(getColumnIndexOrThrow("${TablaFoto.COL_EXT}"))
            foto = Foto(id = id, datos = Base64.encodeToString(datos, Base64.URL_SAFE or Base64.NO_WRAP), extension = ext)
        }
        return foto
    }

    private fun obtenerTarjeta(cursor: Cursor): TarjetaCredito? {

        var tarjetaCredito: TarjetaCredito? = null
        with(cursor){
            val id = getInt(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_ID_TARJETA}"))
            val companiaTarjeta = getString(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_COM_TARJETA}"))
            val numeroTarjeta = getString(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_NUM_TARJETA}"))
            val codigoSeguridad = getInt(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_COD_SEGURIDAD}"))
            val mesTarjeta = getInt(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_MES_TARJETA}"))
            val anioTarjeta = getInt(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_ANIO_TARJETA}"))
            val clienteId = getInt(getColumnIndexOrThrow("${TablaTarjetaCredito.COL_ID_CLIENTE}"))
            tarjetaCredito = TarjetaCredito(id = id,
                    companiaTarjeta = companiaTarjeta,
                    numeroTarjeta = numeroTarjeta,
                    codigoSeguridad = codigoSeguridad.toString(),
                    mesTarjeta = mesTarjeta,
                    anioTarjeta = anioTarjeta,
                    clienteId = clienteId)
        }


        return tarjetaCredito


    }

    private fun obtenerRecorrido(cursor: Cursor): Recorrido? {

        var recorrido: Recorrido? = null
        with(cursor){
            val id = getInt(getColumnIndexOrThrow("${TablaRecorrido.COL_ID_RECORRIDO}"))
            val origenLatitud = getDouble(getColumnIndexOrThrow("${TablaRecorrido.COL_ORI_LATITUD}"))
            val origenLongitud = getDouble(getColumnIndexOrThrow("${TablaRecorrido.COL_ORI_LONGITUD}"))
            val detinoLatitud = getDouble(getColumnIndexOrThrow("${TablaRecorrido.COL_DES_LATITUD}"))
            val destinoLongitud = getDouble(getColumnIndexOrThrow("${TablaRecorrido.COL_DES_LONGITUD}"))
            val distanciaRecorrido = getDouble(getColumnIndexOrThrow("${TablaRecorrido.COL_DIST_RECORRIDO}"))
            val estadoRecorrido = getString(getColumnIndexOrThrow("${TablaRecorrido.COL_EST_RECORRIDO}"))
            val fechaRecorrido = getString(getColumnIndexOrThrow("${TablaRecorrido.COL_FEC_RECORRIDO}"))
            val valorRecorrido = getDouble(getColumnIndexOrThrow("${TablaRecorrido.COL_VAL_RECORRIDO}"))
            val tarjetaCreditoId = getInt(getColumnIndexOrThrow("${TablaRecorrido.COL_ID_TARJETA}"))
            val conductor = getInt(getColumnIndexOrThrow("${TablaRecorrido.COL_ID_CONDUCTOR}"))

            recorrido = Recorrido(id = id,
                    origenLatitud = origenLatitud,
                    origenLongitud = origenLongitud,
                    destinoLatitud = detinoLatitud,
                    destinoLongitud = destinoLongitud,
                    distanciaRecorrido = distanciaRecorrido,
                    estadoRecorrido = estadoRecorrido,
                    fechaRecorrido = fechaRecorrido,
                    valorRecorrido = valorRecorrido,
                    tarjetaCreditoId = tarjetaCreditoId,
                    conductor = Conductor(conductor,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            null,
                            0))
        }

        return recorrido

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
                "${TablaTarjetaCredito.TABLE_NAME}" ->
                    do {
                        entidad.add(obtenerTarjeta(cursor)!!)

                    } while (cursor.moveToNext())
                "${TablaRecorrido.TABLE_NAME}" ->
                        do{
                            entidad.add(obtenerRecorrido(cursor)!!)

                        }while(cursor.moveToNext())

            }


        }
        cursor.close()
        dbReadable.close()


        return entidad
    }


    fun actualizar(datos: Any): Boolean{
        val dbWritable = dbOpenHelper!!.writableDatabase

        when(datos){
            is TarjetaCredito -> {
                val values = ContentValues().apply {
                    put(TablaTarjetaCredito.COL_COM_TARJETA, datos.companiaTarjeta)
                    put(TablaTarjetaCredito.COL_NUM_TARJETA, datos.numeroTarjeta)
                    put(TablaTarjetaCredito.COL_MES_TARJETA, datos.mesTarjeta)
                    put(TablaTarjetaCredito.COL_ANIO_TARJETA, datos.anioTarjeta)
                    put(TablaTarjetaCredito.COL_COD_SEGURIDAD, datos.codigoSeguridad)
                }
                val selection = "${TablaTarjetaCredito.COL_ID_TARJETA} LIKE ?"
                val selectionArgs = arrayOf("${datos.id}")
                val count = dbWritable.update(
                        TablaTarjetaCredito.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                )
                dbWritable.close()
                Log.i("RESULTADO_SQLITE", count.toString())
                if(count >= 0)
                    return@actualizar true
            }
            is Cliente -> {
                val values = ContentValues().apply {
                    put(TablaCliente.COL_NOMBRE, datos.nombre)
                    put(TablaCliente.COL_APELLIDO, datos.apellido)
                    put(TablaCliente.COL_NOM_USUARIO, datos.nombreUsuario)
                    put(TablaCliente.COL_CORREO_USUARIO, datos.correoUsuario)
                    put(TablaCliente.COL_TELEFONO, datos.telefono)

                }
                val selection = "${TablaCliente.COL_ID_CLIENTE} LIKE ?"
                val selectionArgs = arrayOf("${datos.id}")
                val count = dbWritable.update(
                        TablaCliente.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                )
                dbWritable.close()
                Log.i("RESULTADO_SQLITE", count.toString())
                if(count >= 0)
                    return@actualizar true
            }
            is Recorrido -> {

                val values = ContentValues().apply {
                    put(TablaRecorrido.COL_EST_RECORRIDO, datos.estadoRecorrido)

                }
                val selection = "${TablaRecorrido.COL_ID_RECORRIDO} LIKE ?"
                val selectionArgs = arrayOf("${datos.id}")
                val count = dbWritable.update(
                        TablaRecorrido.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                )
                dbWritable.close()
                Log.i("RESULTADO_SQLITE", count.toString())
                if(count >= 0)
                    return@actualizar true

            }
            is Foto -> {
                val byteArray = Base64.decode(datos.datos, Base64.NO_WRAP or Base64.URL_SAFE)
                val values = ContentValues().apply {
                    put(TablaFoto.COL_FOTO, byteArray)
                    put(TablaFoto.COL_EXT, datos.extension)
                }
                val selection = "${TablaFoto.COL_ID_FOTO} LIKE ?"
                val selectionArgs = arrayOf("${datos.id}")
                val count = dbWritable.update(
                        TablaFoto.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                )
                Log.i("RESULTADO_SQLITE", count.toString())
                dbWritable.close()
                if(count >= 0)
                    return@actualizar true
            }
            else -> return@actualizar false
        }
        dbWritable.close()
        return false
    }

    fun eliminar_log_out(): Boolean{
        val dbWritable = dbOpenHelper!!.writableDatabase
        dbWritable.execSQL("DELETE FROM ${TablaCliente.TABLE_NAME}")
        dbWritable.execSQL("DELETE FROM ${TablaConductor.TABLE_NAME}")
        dbWritable.execSQL("DELETE FROM ${TablaFoto.TABLE_NAME}")
        dbWritable.execSQL("DELETE FROM ${TablaTarjetaCredito.TABLE_NAME}")
        dbWritable.execSQL("DELETE FROM ${TablaRecorrido.TABLE_NAME}")
        dbWritable.close()
        return true
    }

    fun eliminar(tabla: String, id: String): Boolean{
        val dbWritable = dbOpenHelper!!.writableDatabase
        when(tabla){
            TablaTarjetaCredito.TABLE_NAME -> {
                Log.i("ELIMINAR", id)
                val selection = "${TablaTarjetaCredito.COL_ID_TARJETA} LIKE ?"
                val selectionArgs = arrayOf(id)
                val deletedRows = dbWritable.delete(tabla, selection, selectionArgs)
                Log.i("RESULTADO_SQLITE", deletedRows.toString())
                dbWritable.close()
                if(deletedRows >= 0)
                    return@eliminar true

            }
            TablaFoto.TABLE_NAME -> {}
            TablaCliente.TABLE_NAME -> { }
            TablaRecorrido.TABLE_NAME -> {
                Log.i("ELIMINAR", id)
                val selection = "${TablaRecorrido.COL_ID_RECORRIDO} LIKE ?"
                val selectionArgs = arrayOf(id)
                val deletedRows = dbWritable.delete(tabla, selection, selectionArgs)
                Log.i("RESULTADO_SQLITE", deletedRows.toString())
                dbWritable.close()
                if(deletedRows >= 0)
                    return@eliminar true

            }
        }
        return false
    }

}