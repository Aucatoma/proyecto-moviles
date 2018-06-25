package com.example.daniel.proyectomoviles

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_iniciar_sesion.setOnClickListener { view: View? ->
            irAActividadMapa()
        }
    }

    private fun irAActividadMapa() {
        val intent = Intent(this, PanelActivity::class.java)
        startActivity(intent)
    }
}
