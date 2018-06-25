package com.example.daniel.proyectomoviles


import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback {

    //Para usar FusedLocationProviderClient se debe agregar la siguiente dependencia en el build.gradle a nivel de aplicacion:
    // implementation 'com.google.android.gms:play-services-location:15.0.1'
    //Para mas informacion ir a: https://developer.android.com/trainning/location/retrieve-current
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var mMap: GoogleMap
    lateinit var marcador: Marker
    val coordenadas = ArrayList<String>()
    val ZOOM_LEVEL = 17f



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        establecerPosicionUsuario()

        mMap.setOnMapClickListener { latLng: LatLng? ->
            aniadirMarcador(latLng)
        }
    }

    private fun aniadirMarcador(posicion: LatLng?) {
        if(posicion!=null){
            marcador.remove()
            coordenadas.clear()
            marcador = mMap.addMarker(MarkerOptions().position(posicion))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,mMap.cameraPosition.zoom))

            coordenadas.add(posicion.latitude.toString())
            coordenadas.add(posicion.longitude.toString())
        }
    }

    private fun establecerPosicionUsuario() {
        //En esta linea se pone this.requireActivity() porque el metodo recibe una actividad, no un fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        //En esta linea se pone this.requiereContext para obtener le contexto del fragmento
        if(ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                if(location!=null){
                    val posicionDelUsuario = LatLng(location.latitude, location.longitude)
                    marcador = mMap.addMarker(MarkerOptions().position(posicionDelUsuario).title("Usted esta aquí"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionDelUsuario,ZOOM_LEVEL))
                    marcador.showInfoWindow()

                    coordenadas.add(location.latitude.toString())
                    coordenadas.add(location.longitude.toString())
                }

            }
        }
    }


}
