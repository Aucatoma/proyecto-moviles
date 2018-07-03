package com.example.daniel.proyectomoviles

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.parser.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST = 1
    }
    val jsonParser = JsonParser()
    val verificadorIntennet = VerificadorInternet()
    val permisos = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA)

    var cliente: Cliente? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HttpRequest.obtenerDato("Cliente", "2",{ error, datos ->
            /* Este bloque de cód*/
            if(error){

            }else{
                Log.i("CLIENTE_1", datos)
                //cliente = jsonParser.jsonToCliente(datos) as Cliente
               // Log.i("CLIENTE_1", "$cliente")
            }
        })

        Log.i("CLIENTE_1", "PRIMERO")
        Log.i("#_PERMISOS", "${permisos.size}")
        val permisosApedir = checkPermissions(permisos)
        Log.i("#_PERMISOS", "${permisosApedir.size} - ${permisosApedir.last()}")
        if(permisosApedir.isNotEmpty()){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST )
        }

        btn_iniciar_sesion.setOnClickListener { view: View? ->
            //launchLoginActivity()
            irAActividadMapa()
        }



        btn_registrar.setOnClickListener { v: View? ->
            launchSignUpActivity()
        }
    }


    private fun launchSignUpActivity(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun launchLoginActivity(){
        val intent = Intent(this, LoginActivity:: class.java)
        startActivity(intent)
    }

    /* Devuelve los permisos que se deben pedir */
    private fun checkPermissions(permissions: Array<String>): List<String>{
       return permissions.dropWhile { permissions: String ->
            ActivityCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED
       }
    }

    private fun irAActividadMapa() {


        async {

            val hayConexion= verificadorIntennet.hasActiveInternetConnection(applicationContext)

            uiThread {

                if(hayConexion){
                    val intent = Intent(applicationContext, PanelActivity::class.java)
                    startActivity(intent)
                }else{
                    val toast = Toast.makeText(applicationContext, "No tiene conexión a internet", Toast.LENGTH_SHORT)
                    toast.show()
                }



            }
        }



    }
}
