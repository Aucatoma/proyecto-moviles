package com.example.daniel.proyectomoviles


import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.example.daniel.proyectomoviles.baseDeDatos.DBHandler
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaCliente
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaTarjetaCredito
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Recorrido
import com.example.daniel.proyectomoviles.entidades.TarjetaCredito
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.parser.JsonParser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.alert_dialog_solicitar_taxi.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MapFragment : Fragment(), OnMapReadyCallback {

    val dateFormat = SimpleDateFormat("dd-MM-yyyy")
    //Para usar FusedLocationProviderClient se debe agregar la siguiente dependencia en el build.gradle a nivel de aplicacion:
    // implementation 'com.google.android.gms:play-services-location:15.0.1'
    //Para mas informacion ir a: https://developer.android.com/trainning/location/retrieve-current
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap
    private lateinit var busqueda:EditText

    lateinit var marcadorOrigen: Marker
    lateinit var marcadorDestino: Marker

    //---------DATOS QUE SE ALMCACENAN EN LA ABASE--------//

    val coordenadasOrigen = ArrayList<String>()
    val coordenadasDestino = ArrayList<String>()
    var distancia = 0.0
    var valorRecorrido = 0.0

    lateinit var recorridoCreado: Deferred<Boolean>
    lateinit var recorrido:Recorrido
    var recorridoJson = ""
    private val jsonParser = JsonParser()
    var tarjetasCredito:List<TarjetaCredito>? = ArrayList()

    //-----------------------------------------------------//


    //Variable que guarda la tarjeta seleccionada en el spinner al momento de seleccionar la tarjeta en el pago
    lateinit var tarjetaSeleccionada:TarjetaCredito


    var distanciaString = ""
    var direccionOrigen= ""
    var direccioDestino=""

    //Informacion de la direccion actual de usuario
    var provincia:String =""
    var ciudad:String=""
    var pais:String=""
    var featureName:String=""

    val verificadorIntenet = VerificadorInternet()
    var hayConexion = true

    val ZOOM_LEVEL = 15f

    //Declaramos un objeto bounds para que toda la ruta que se dibuje quepa en la pantalla
    val LatLongB = LatLngBounds.Builder()

    lateinit var inputMethodManager : InputMethodManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val cliente = DBHandler.getInstance(requireContext())!!.obtenerUno(TablaCliente.TABLE_NAME) as Cliente?

        if(cliente==null){
            Log.i("MI-CLIENTE","Cliente nulo")
        }else{
            Log.i("MI-CLIENTE", cliente.nombre)
        }



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        busqueda = view.findViewById(R.id.texto_busqueda) as EditText
        inputMethodManager = this.requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager



        val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment?
        mapFragment?.getMapAsync(this)




    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        establecerPosicionUsuario()

        val txtBusqueda: TextView? = view?.findViewById(R.id.texto_busqueda)

        btn_buscar_direccion.setOnClickListener { view: View? ->

            if(!txtBusqueda?.text.isNullOrBlank()){

                async {
                    //Se comprueba si hay conexion a internet
                    hayConexion= verificadorIntenet.hasActiveInternetConnection(requireContext())

                    uiThread {
                        if(hayConexion){
                            //Se usa para que al momento de que haga click en el boton, el teclado se esconda
                            inputMethodManager.hideSoftInputFromWindow(view?.windowToken,0)
                            buscarLocacion()

                        }else{
                            val toast = Toast.makeText(requireContext(), R.string.conexion_internet, Toast.LENGTH_SHORT)
                            toast.show()
                        }
                    }
                }
            }else{
                val toast = Toast.makeText(requireContext(), R.string.texto_busqueda_vacia, Toast.LENGTH_SHORT)
                toast.show()
            }

        }

        btn_solicitar_taxi.setOnClickListener { view: View? ->

            if(!txtBusqueda?.text.isNullOrBlank()){

                val materialDialog = MaterialDialog.Builder(requireContext())

                        //Se crean los listeners para los botones del Dialog
                        .onAny { dialog, which ->

                            if(which.name=="POSITIVE"){registrarRecorrido()}
                            else if (which.name=="NEGATIVE"){ }

                        }
                        .title(R.string.cabecera_dialog)
                        .customView(R.layout.alert_dialog_solicitar_taxi,true)
                        .positiveText(R.string.btn_aceptar_carrera)
                        .negativeText(R.string.btn_cancelar_carrera)
                        .show()

                //Si no es null, se llena de AlertDialog
                if(materialDialog!=null){
                    //Es importante tomar la customView!!
                    val view = materialDialog.customView as View
                    llenarAlertDialog(view)
                }

            }else{
                val toast = Toast.makeText(requireContext(), R.string.texto_busqueda_vacia, Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun consultarTarjetasCredito() {

        tarjetasCredito = DBHandler.getInstance(requireContext())!!.obtenerDatos(TablaTarjetaCredito.TABLE_NAME) as List<TarjetaCredito>


    }

    private fun inicializarSpinner(customView: View) {

        val spinner:Spinner = customView.findViewById(R.id.spinner_tarjetas)

        val opcionesTarjetasSpinner:ArrayList<Any>
        opcionesTarjetasSpinner = ArrayList()

        if(tarjetasCredito?.size==0){

            opcionesTarjetasSpinner.add(resources.getString(R.string.no_tiene_tarjetas))

        }else{
            tarjetasCredito?.forEach { tarjetasCredito:TarjetaCredito ->
                opcionesTarjetasSpinner.add(tarjetasCredito)
            }
        }

        val adaptadorSpinner = ArrayAdapter<Any>(requireContext(),android.R.layout.simple_spinner_dropdown_item,opcionesTarjetasSpinner)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adaptadorSpinner

        val typedArray = resources.obtainTypedArray(R.array.drawable_tarjetas)

        spinner.onItemSelectedListener{
            onItemSelected { adapterView, view, i, l ->
                tarjetaSeleccionada = opcionesTarjetasSpinner[i] as TarjetaCredito
                when {
                    tarjetaSeleccionada.companiaTarjeta == "Visa" -> customView.findViewById<ImageView>(R.id.img_tarjeta_credito).setImageResource(R.drawable.ic_icons8_visa)
                    tarjetaSeleccionada.companiaTarjeta == "Mastercard" -> customView.findViewById<ImageView>(R.id.img_tarjeta_credito).setImageResource(R.drawable.ic_mastercard_logo)
                    tarjetaSeleccionada.companiaTarjeta == "American Express" -> customView.findViewById<ImageView>(R.id.img_tarjeta_credito).setImageResource(R.drawable.ic_icons8_american_express)

                }
            }
        }
    }

    private fun registrarRecorrido() {

        kotlinx.coroutines.experimental.async(UI) {
            recorridoCreado = bg {
                crearRecorrido()
            }
            if(recorridoCreado.await()){

                bg {HttpRequest.insertarDato("Recorrido",recorridoJson,{ error, datos ->

                    if(error){
                        Toast.makeText(requireContext(),"Error", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.i("RESPUESTA_REGISTRO", datos)
                        val recorridoRespuesta = jsonParser.jsonToRecorrido(datos)

                        if(recorridoRespuesta?.conductor!=null && recorridoRespuesta.tarjetaCreditoId!=null){
                            DBHandler.getInstance(requireContext())!!.insertar(recorridoRespuesta.conductor)
                            DBHandler.getInstance(requireContext())!!.insertar(recorridoRespuesta!!)
                            //Se inicia el nuevo Fragment de los RV de Pendientes
                            val pendientesFragment = PendientesFragment()
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.mainLayout, pendientesFragment).addToBackStack(null).commit()
                        }



                    }

                })

                }

            }
        }


    }

    private fun crearRecorrido(): Boolean {

        recorrido = Recorrido(-1,
                coordenadasOrigen[0].toDouble(),
                coordenadasOrigen[1].toDouble(),
                coordenadasDestino[0].toDouble(),
                coordenadasDestino[1].toDouble(),
                distancia,
                "P",
                dateFormat.format(Date()),
                valorRecorrido,
                tarjetaSeleccionada.id,
                null,
                0.toLong(),
                0.toLong()

                )
        recorridoJson = jsonParser.recorridoToJson(recorrido)
        return true
    }


    private fun llenarAlertDialog(view: View?) {

        if(view!=null){

            //Llenamos nuestro AlertDialog
            var campoOrigen:TextView = view.findViewById(R.id.txt_origen_input)
            var campoDestino:TextView = view.findViewById(R.id.txt_destino_input)
            var campoDistancia:TextView = view.findViewById(R.id.txt_distancia_input)
            var campoCosto: TextView = view.findViewById(R.id.txt_costo_viaje)

            campoOrigen.text=direccionOrigen
            campoDestino.text=direccioDestino
            campoDistancia.text=distanciaString

            //Consultamos las tarjetas de credito que tenga el usuario
            consultarTarjetasCredito()
            inicializarSpinner(view)

            calcularCostoRecorrido()

           campoCosto.text = "$"+String.format("%.2f",valorRecorrido)


        }

    }

    private fun calcularCostoRecorrido() {

        Log.i("COSTO",distancia.toString())

        valorRecorrido = (distancia/1000) * 1.15

    }


    private fun buscarLocacion() {
        val textoBusqueda = busqueda.text.toString()
        val localizador = Geocoder(requireContext())
        var direcciones : List<Address>
        direcciones = ArrayList()

        try {

            //Se añaden datos extras a la busqueda para que sea mas precisa. Se añade ciudad, pais y provicia desde donde el usuario hace la busqueda
            direcciones = localizador.getFromLocationName("$textoBusqueda, $ciudad, $pais, $provincia", 1)
            Log.i("Search","El usuario busco esto: "+textoBusqueda)

        }catch (e:IOException){
            Log.i("Search","Error: "+e.message)
        }

        //Si se encontro la dirección...
        if(direcciones.isNotEmpty()){

            var direccionEncontrada = direcciones[0]
            Log.i("Search","Se encontro direccion!: "+direccionEncontrada.toString())

            //...se añade el marcador
            aniadirMarcador(LatLng(direccionEncontrada.latitude,direccionEncontrada.longitude))

            //direccionDestino es lo que se va a mostrar en el AlertDialog en el campo 'Destino'
            direccioDestino = "$textoBusqueda, $ciudad"

        }else{
            Log.i("Search","No se hallo nada")
        }
    }

    private fun aniadirMarcador(posicion: LatLng?) {

        limpiarMapa()

        if(posicion!=null){

            coordenadasDestino.clear()

            marcadorDestino = mMap.addMarker(MarkerOptions()
                    .position(posicion)
                    .title(requireContext().getString(R.string.marcador_destino))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
            marcadorDestino.showInfoWindow()


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

                if(routes!!.size!=0){
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
                }

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

        //Retornamos la url construida
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

                    marcadorOrigen = mMap.addMarker(MarkerOptions().position(posicionDelUsuario).title(requireContext().getString(R.string.marcador_orgien)))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionDelUsuario,ZOOM_LEVEL))
                    marcadorOrigen.showInfoWindow()

                    marcadorDestino = mMap.addMarker(MarkerOptions().position(posicionDelUsuario))

                    //Se inicializa tanto origen como destino con la posicion inicial del usuario

                    coordenadasDestino.add(location.latitude.toString())
                    coordenadasDestino.add(location.longitude.toString())

                    coordenadasOrigen.add(location.latitude.toString())
                    coordenadasOrigen.add(location.longitude.toString())

                    val localizador = Geocoder(requireContext())


                    //Se toman los datos de la direccion del usuario, necesarios para realizar una busqueda mas precisa de su destino
                    var direccionUsuario:List<Address>
                    direccionUsuario = ArrayList()
                    direccionUsuario = localizador.getFromLocation(coordenadasOrigen[0].toDouble(), coordenadasOrigen[1].toDouble(),1)

                    featureName=direccionUsuario[0].featureName
                    ciudad=direccionUsuario[0].locality.toString()
                    pais=direccionUsuario[0].countryName
                    provincia=direccionUsuario[0].adminArea

                    direccionOrigen = "$featureName, $ciudad"


                }

            }
        }
    }

    private fun calcularDistancia(coordenadasOrigen: ArrayList<String>, coordenadasDestino: ArrayList<String>){

        val origen = LatLng(coordenadasOrigen[0].toDouble(), coordenadasOrigen[1].toDouble())
        val destino = LatLng(coordenadasDestino[0].toDouble(), coordenadasDestino[1].toDouble())

        distancia = SphericalUtil.computeDistanceBetween(origen,destino)

        distanciaString = formatNumber(distancia)

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
