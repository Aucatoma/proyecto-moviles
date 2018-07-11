package com.example.daniel.proyectomoviles

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.example.daniel.proyectomoviles.baseDeDatos.DBHandler
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaCliente
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.fragments.AuthFragment
import com.example.daniel.proyectomoviles.fragments.LoginFragment
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.interfaces.OnNextArrowClickedListener
import com.example.daniel.proyectomoviles.parser.JsonParser
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity(), OnNextArrowClickedListener {

    companion object {
        val PERMISSIONS_REQUEST = 1

    }
    val verificadorIntennet = VerificadorInternet()
    val permisos = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA)

    val fragmentManager = supportFragmentManager

    override fun onNextClicked(username: String) {
        val fragAuth = AuthFragment()
        val bundle = Bundle()
        bundle.putString("USERNAME", username)
        fragAuth.arguments = bundle
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
                R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit)
        fragmentTransaction.replace(R.id.rel_main, fragAuth)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //irActividadPanel()
        //finish()

        val cliente = DBHandler.getInstance(this.baseContext)!!.obtenerUno(TablaCliente.TABLE_NAME) as Cliente?
        if(cliente != null){
            Log.i("CLIENTE", cliente.nombre)
            //irActividadPanel()
            //finish()
        }



        val fragmentoLogin = LoginFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
                R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit)
        fragmentTransaction.replace(R.id.rel_main, fragmentoLogin)
        fragmentTransaction.commit()

        val permisosApedir = checkPermissions(permisos)

        if(permisosApedir.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permisosApedir.toTypedArray(), PERMISSIONS_REQUEST )
        }

    }


    /* Devuelve los permisos que se deben pedir */
    private fun checkPermissions(permissions: Array<String>): List<String>{
       return permissions.dropWhile { permissions: String ->
            ActivityCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED
       }
    }

    private fun irActividadPanel(){
        val intent = Intent(this, PanelActivity::class.java)
        startActivity(intent)
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
