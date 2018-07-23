package com.example.daniel.proyectomoviles

import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat


import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.daniel.proyectomoviles.baseDeDatos.DBHandler
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaCliente
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaFoto
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Foto
import com.example.daniel.proyectomoviles.fragments.UserFragment
import com.example.daniel.proyectomoviles.utilities.ImageFileHandler
import kotlinx.android.synthetic.main.activity_panel.*
import kotlinx.android.synthetic.main.app_bar_panel.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

import org.jetbrains.anko.coroutines.experimental.bg


class PanelActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var nav_foto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)
        setSupportActionBar(toolbar)

        val cliente = DBHandler.getInstance(this)!!.obtenerUno(TablaCliente.TABLE_NAME) as Cliente
        val foto = DBHandler.getInstance(this)!!.obtenerUno(TablaFoto.TABLE_NAME, arrayOf(Pair(TablaFoto.COL_ID_FOTO, cliente.foto!!.id.toString()))) as Foto

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        nav_view.setNavigationItemSelectedListener(this)

        val view = nav_view.getHeaderView(0)
        val nav_user = view.findViewById<TextView>(R.id.textView_lateral_name)
        val nav_email = view.findViewById<TextView>(R.id.textView_lateral_mail)
        nav_foto = view.findViewById<ImageView>(R.id.imgView_lateral_nav)

        nav_user.text = "${cliente.nombre} ${cliente.apellido}"
        nav_email.text = "${cliente.correoUsuario}"

        async(UI){
            val bitmap: Deferred<Bitmap> = bg {
                ImageFileHandler.base64ToBitmap(foto.datos)
            }
            afterConversion(bitmap.await())
        }

        val contactUsFragment = MapFragment()
        val manager:android.support.v4.app.FragmentManager = supportFragmentManager

        manager.beginTransaction().replace(R.id.mainLayout,contactUsFragment).commit()
    }


    fun afterConversion(bitmap: Bitmap){
        nav_foto.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.panel, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_cuenta -> {
                val userFragment = UserFragment()
                val manager = supportFragmentManager
                val transactionManager = manager.beginTransaction()
                transactionManager.replace(R.id.mainLayout, userFragment)
                transactionManager.commit()
            }

            R.id.nav_mapa -> {
                val mapFragment = MapFragment()
                val manager:android.support.v4.app.FragmentManager = supportFragmentManager
                manager.beginTransaction().replace(R.id.mainLayout,mapFragment).commit()

            }
            R.id.nav_pendientes -> {
                val pendientesFragment = PendientesFragment()
                val manager = supportFragmentManager
                val transactionManager = manager.beginTransaction()
                transactionManager.replace(R.id.mainLayout, pendientesFragment)
                transactionManager.commit()
            }
            R.id.nav_historial -> {
                val historialFragment = HistorialFragment()
                val manager = supportFragmentManager
                val transactionManager = manager.beginTransaction()
                transactionManager.replace(R.id.mainLayout, historialFragment)
                transactionManager.commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
