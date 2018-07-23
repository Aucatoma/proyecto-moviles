package com.example.daniel.proyectomoviles.baseDeDatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.*

class DBOpenHelper(context: Context): SQLiteOpenHelper(context, Database.DB_NAME, null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {

        val createTabCli = """
            CREATE TABLE ${TablaCliente.TABLE_NAME}(
                ${TablaCliente.COL_ID_CLIENTE} INTEGER PRIMARY KEY NOT NULL,
                ${TablaCliente.COL_NOMBRE} VARCHAR(50) NOT NULL,
                ${TablaCliente.COL_APELLIDO} VARCHAR(50) NOT NULL,
                ${TablaCliente.COL_NOM_USUARIO} VARCHAR(50) NOT NULL,
                ${TablaCliente.COL_CORREO_USUARIO} VARCHAR(75) NOT NULL,
                ${TablaCliente.COL_TELEFONO} VARCHAR(15) NOT NULL,
                ${TablaCliente.COL_ID_FOTO} INTEGER NOT NULL,
                ${TablaCliente.COL_JWT} VARCHAR(4000) NOT NULL,
                FOREIGN KEY(${TablaCliente.COL_ID_FOTO}) REFERENCES ${TablaFoto.TABLE_NAME}(${TablaFoto.COL_ID_FOTO})
                )""".trimIndent().trim()
        val createTabCon = """
            CREATE TABLE ${TablaConductor.TABLE_NAME}(
                ${TablaConductor.COL_ID_CONDUCTOR} INTEGER PRIMARY KEY NOT NULL,
                ${TablaConductor.COL_NOMBRE} VARCHAR(50) NOT NULL,
                ${TablaConductor.COL_APELLIDO} VARCHAR(50) NOT NULL,
                ${TablaConductor.COL_CORREO_USUARIO} VARCHAR(75) NOT NULL,
                ${TablaConductor.COL_TELEFONO} VARCHAR(15) NOT NULL
                )""".trimIndent().trim()
        val createTabRec = """
            CREATE TABLE ${TablaRecorrido.TABLE_NAME} (
                ${TablaRecorrido.COL_ID_RECORRIDO} INTEGER PRIMARY KEY NOT NULL,
                ${TablaRecorrido.COL_ORI_LATITUD} REAL NOT NULL,
                ${TablaRecorrido.COL_ORI_LONGITUD} REAL NOT NULL,
                ${TablaRecorrido.COL_DES_LATITUD} REAL NOT NULL,
                ${TablaRecorrido.COL_DES_LONGITUD} REAL NOT NULL,
                ${TablaRecorrido.COL_DIST_RECORRIDO} REAL NOT NULL,
                ${TablaRecorrido.COL_EST_RECORRIDO} VARCHAR(1) NOT NULL,
                ${TablaRecorrido.COL_FEC_RECORRIDO} VARCHAR(10) NOT NULL,
                ${TablaRecorrido.COL_VAL_RECORRIDO} REAL NOT NULL,
                ${TablaRecorrido.COL_ID_CONDUCTOR} INTEGER NOT NULL,
                ${TablaRecorrido.COL_ID_TARJETA} INTEGER NOT NULL,
                FOREIGN KEY (${TablaRecorrido.COL_ID_TARJETA}) REFERENCES ${TablaTarjetaCredito.TABLE_NAME}(${TablaTarjetaCredito.COL_ID_TARJETA}),
                FOREIGN KEY (${TablaRecorrido.COL_ID_CONDUCTOR}) REFERENCES ${TablaConductor.TABLE_NAME}(${TablaConductor.COL_ID_CONDUCTOR})
                )""".trimIndent().trim()
        val createTabTar = """
            CREATE TABLE ${TablaTarjetaCredito.TABLE_NAME}(
                ${TablaTarjetaCredito.COL_ID_TARJETA} INTEGER PRIMARY KEY NOT NULL,
                ${TablaTarjetaCredito.COL_COM_TARJETA} VARCHAR(15) NOT NULL,
                ${TablaTarjetaCredito.COL_NUM_TARJETA} VARCHAR(20) NOT NULL,
                ${TablaTarjetaCredito.COL_ANIO_TARJETA} INTEGER NOT NULL,
                ${TablaTarjetaCredito.COL_MES_TARJETA} INTEGER NOT NULL,
                ${TablaTarjetaCredito.COL_COD_SEGURIDAD} INTEGER NOT NULL,
                ${TablaTarjetaCredito.COL_ID_CLIENTE} INTEGER NOT NULL,
                FOREIGN KEY(${TablaTarjetaCredito.COL_ID_CLIENTE}) REFERENCES ${TablaCliente.TABLE_NAME}(${TablaCliente.COL_ID_CLIENTE})
            )""".trimIndent().trim()

        val createTabFoto = """
            CREATE TABLE ${TablaFoto.TABLE_NAME}(
                ${TablaFoto.COL_ID_FOTO} INTEGER PRIMARY KEY NOT NULL,
                ${TablaFoto.COL_FOTO} BLOB NOT NULL,
                ${TablaFoto.COL_EXT} VARCHAR(10) NOT NULL
            )""".trimIndent().trim()
        db?.let {
            db.execSQL(createTabFoto)
            db.execSQL(createTabCli)
            db.execSQL(createTabCon)
            db.execSQL(createTabTar)
            db.execSQL(createTabRec)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }



}