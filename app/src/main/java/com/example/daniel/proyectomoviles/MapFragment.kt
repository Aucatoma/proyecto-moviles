package com.example.daniel.proyectomoviles


import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.support.v4.uiThread
import java.net.URL


class MapFragment : Fragment(), OnMapReadyCallback {

    //Para usar FusedLocationProviderClient se debe agregar la siguiente dependencia en el build.gradle a nivel de aplicacion:
    // implementation 'com.google.android.gms:play-services-location:15.0.1'
    //Para mas informacion ir a: https://developer.android.com/trainning/location/retrieve-current
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var mMap: GoogleMap
    private lateinit var busqueda:EditText

    lateinit var marcadorOrigen: Marker
    lateinit var marcadorDestino: Marker

    val coordenadasOrigen = ArrayList<String>()
    val coordenadasDestino = ArrayList<String>()


    val ZOOM_LEVEL = 17f

    //Declaramos un objeto bounds para que toda la ruta que se dibuje quepa en la pantalla
    val LatLongB = LatLngBounds.Builder()



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

        //Cada vez que agregue un marcador tambien vamos a dibujar la ruta
        mMap.setOnMapClickListener { latLng: LatLng? ->
            aniadirMarcador(latLng)
        }
    }

    private fun aniadirMarcador(posicion: LatLng?) {

        limpiarMapa()

        if(posicion!=null){

            coordenadasDestino.clear()
            marcadorDestino = mMap.addMarker(MarkerOptions().position(posicion))


            coordenadasDestino.add(posicion.latitude.toString())
            coordenadasDestino.add(posicion.longitude.toString())

            dibujarRuta()

        }
    }

    private fun limpiarMapa() {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(LatLng(coordenadasOrigen[0].toDouble(), coordenadasOrigen[1].toDouble())))
    }

    private fun dibujarRuta() {

        //Creacion de un objeto polyLine options

        val options = PolylineOptions()
        options.color(R.color.colorRuta)
        options.width(8f)

        //este metodo nos permite construir la URL que usaremos para hacer la llamada a la API diretions
        val url= construirURL()
        Log.i("URL",url)

        async {
            // here you put the potentially blocking code
            val result = URL(url).readText()

            uiThread {
                // this will execute in the main thread, after the async call is done

                //Aqui haremos el parsing usando Klaxon. Para ello hacemos esto:

                //Declaramos el Parser
                val parser: Parser = Parser()

                //Pasamos nuestra cadena a un StringBuilder
                val stringBuilder: StringBuilder = StringBuilder(result)

                //Y luego pasamos esto al parser
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                //Ahora tenemos nuestro objeto JSON, y necesitamos encontrar los puntos para formar la ruta.
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>

                val polypts = points.flatMap {
                    decodePoly(it.obj("polyline")?.string("points")!!)
                }

                //Se añade el origen a nuestro PolyLineOptions y a nuestro objeto bounds
                options.add(LatLng(coordenadasOrigen[0].toDouble(),coordenadasOrigen[1].toDouble()))
                LatLongB.include(LatLng(coordenadasOrigen[0].toDouble(),coordenadasOrigen[1].toDouble()))

                //Se añade nuestro conjunto de puntos desde el origen al destino a nuestro PolyLine y nuetro bounds
                for (point in polypts){

                    options.add(point)
                    LatLongB.include(point)

                }

                //Y finalmente se añade el destino a nuestro Polyline y a nuestro bounds
                options.add(LatLng(coordenadasDestino[0].toDouble(),coordenadasDestino[1].toDouble()))
                LatLongB.include(LatLng(coordenadasDestino[0].toDouble(),coordenadasDestino[1].toDouble()))

                mMap.addPolyline(options)

                //Se construye nuestro bounds
                val bounds = LatLongB.build()

                //Centramos la ruta en la pantalla usando nuestro bounds construido
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))

                //Calculamos distancia entre los dos puntos
                calcularDistancia(coordenadasOrigen,coordenadasDestino)

            }
        }

    }

    //Metodo para decodificar el string que nos llega de google maps y obtener los puntos de ruta
    //Fuente: https://github.com/irenenaya/Kotlin-Practice/blob/master/MapsRouteActivity.kt

    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly

    }

    private fun construirURL() : String {

        //Usaremos las coordenadas origen y destino

        //Empezamos a contruir nuestra URL:
        //Construirmos el siguiente string: origin=latitud,longitud
        val origen = "origin="+coordenadasOrigen[0]+","+coordenadasOrigen[1]
        val destino = "destination="+coordenadasDestino[0]+","+coordenadasDestino[1]
        val sensor = "sensor=false"
        val parametros = "$origen&$destino&$sensor"

        //Reornamos la url construida
        return "https://maps.googleapis.com/maps/api/directions/json?$parametros"

    }

    private fun establecerPosicionUsuario() {
        //En esta linea se pone this.requireActivity() porque el metodo recibe una actividad, no un fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        //En esta linea se pone this.requiereContext para obtener le contexto del fragmento
        if(ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                if(location!=null){
                    val posicionDelUsuario = LatLng(location.latitude, location.longitude)
                    marcadorOrigen = mMap.addMarker(MarkerOptions().position(posicionDelUsuario).title("Usted esta aquí"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionDelUsuario,ZOOM_LEVEL))
                    marcadorOrigen.showInfoWindow()

                    marcadorDestino = mMap.addMarker(MarkerOptions().position(posicionDelUsuario))

                    coordenadasDestino.add(location.latitude.toString())
                    coordenadasDestino.add(location.longitude.toString())

                    coordenadasOrigen.add(location.latitude.toString())
                    coordenadasOrigen.add(location.longitude.toString())
                }

            }
        }
    }

    private fun calcularDistancia(coordenadasOrigen: ArrayList<String>, coordenadasDestino: ArrayList<String>){

        val origen = LatLng(coordenadasOrigen[0].toDouble(), coordenadasOrigen[1].toDouble())
        val destino = LatLng(coordenadasDestino[0].toDouble(), coordenadasDestino[1].toDouble())

        val distancia = SphericalUtil.computeDistanceBetween(origen,destino)

        val distanciaString = formatNumber(distancia)

       // val toast = Toast.makeText(this.requireContext(), "LA DISTANCIA ES: "+distanciaString, Toast.LENGTH_SHORT)
       // toast.show()


    }

    private fun formatNumber(distance: Double): String {
        var distance = distance
        var unit = "m"
        if (distance < 1) {
            distance *= 1000.0
            unit = "mm"
        } else if (distance > 1000) {
            distance /= 1000.0
            unit = "km"
        }

        return String.format("%4.3f%s", distance, unit)
    }




}
