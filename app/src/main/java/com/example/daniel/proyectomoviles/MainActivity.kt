package com.example.daniel.proyectomoviles

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    val verificadorIntennet = VerificadorInternet()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_iniciar_sesion.setOnClickListener { view: View? ->
            launchLoginActivity()
            //irAActividadMapa()
        }
    }

    private fun launchLoginActivity(){
        val intent = Intent(this, LoginActivity:: class.java)
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
