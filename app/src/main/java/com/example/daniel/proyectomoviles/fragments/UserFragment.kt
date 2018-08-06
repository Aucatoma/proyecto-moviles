package com.example.daniel.proyectomoviles.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.beust.klaxon.Json

import com.example.daniel.proyectomoviles.R
import com.example.daniel.proyectomoviles.adaptadores.AdaptadorTarjetasFrag
import com.example.daniel.proyectomoviles.baseDeDatos.DBHandler
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaCliente
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaFoto
import com.example.daniel.proyectomoviles.baseDeDatos.esquemaBase.TablaTarjetaCredito
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Foto
import com.example.daniel.proyectomoviles.entidades.TarjetaCredito
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.interfaces.OnImageChanged
import com.example.daniel.proyectomoviles.parser.JsonParser
import com.example.daniel.proyectomoviles.utilities.ImageFileHandler
import kotlinx.android.synthetic.main.fragment_auth_fragment.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.image
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener
import org.jetbrains.anko.support.v4.act
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var cliente: Cliente
    lateinit var tarjetas: ArrayList<TarjetaCredito>
    lateinit var foto: Foto
    var imagePath = ""
    val jsonParser = JsonParser()
    var mensajeFallo: Int = 0
    var mensajeExito: Int = 0
    lateinit var mListener: OnImageChanged

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cliente = DBHandler.getInstance(activity!!.baseContext)!!.obtenerUno(TablaCliente.TABLE_NAME) as Cliente
        tarjetas = DBHandler.getInstance(activity!!.baseContext)!!.obtenerDatos(TablaTarjetaCredito.TABLE_NAME) as ArrayList<TarjetaCredito>
        foto = DBHandler.getInstance(activity!!.baseContext)!!.obtenerUno(TablaFoto.TABLE_NAME) as Foto
        mensajeExito = resources.getIdentifier("@string/usuario_frag_tarjetas_exito", "string", activity!!.packageName)
        mensajeFallo = resources.getIdentifier("@string/usuario_frag_tarjetas_fallo", "string", activity!!.packageName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewManager = LinearLayoutManager(activity!!.baseContext)
        viewAdapter = AdaptadorTarjetasFrag(tarjetas, activity!!)

        recyclerView = recycler_usu_frag_cards.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        pgrBar_frag_user_photo.visibility = View.GONE
        pgrBar_frag_user_save.visibility = View.GONE

        llenarVista()
        blockUI()
        btn_usu_frag_save.visibility = View.GONE
        imgView_usu_frag_take.visibility = View.GONE

        btn_usu_frag_add_card.setOnClickListener {
            val dialog = MaterialDialog.Builder(activity!!)
                    .title(R.string.credit_card_dialog_tarjeta)
                    .customView(R.layout.credit_card_dialog, true)
                    .negativeText(R.string.credit_card_dialog_cancel)
                    .positiveText(R.string.credit_card_dialog_ok)
                    .onPositive{ dialog, which ->
                        val tarjeta = crearTarjeta(dialog.customView as View)
                        guardarTarjeta(tarjeta)
                    }
                    .build()
            /* Generación de contenido del spinner */
            val spinner = dialog.customView!!.findViewById<Spinner>(R.id.spinner_card_dialog_compania)
            val adapter = ArrayAdapter.createFromResource(activity!!.baseContext, R.array.array_tarjetas, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener {
                onItemSelected { adapterView, view, i, l ->
                    val typedArray = resources.obtainTypedArray(R.array.drawable_tarjetas)
                    dialog.customView!!.findViewById<ImageView>(R.id.imgView_card_dialog_compania).setImageDrawable(typedArray.getDrawable(i))
                }
            }
            dialog.show()
        }

        imgView_usu_frag_take.setOnClickListener {
            tomarFoto(activity!!.baseContext)
        }

        btn_usu_frag_edit.setOnClickListener{
            Log.i("USUA_FRAG", "CLIC EDIT")
            btn_usu_frag_edit.visibility = View.GONE
            btn_usu_frag_save.visibility = View.VISIBLE
            imgView_usu_frag_take.visibility = View.VISIBLE
            enableUI()
        }

        btn_usu_frag_save.setOnClickListener {
            Log.i("USUA_FRAG", "CLIC SAVE")
            btn_usu_frag_save.visibility = View.GONE
            btn_usu_frag_edit.visibility = View.VISIBLE
            imgView_usu_frag_take.visibility = View.GONE
            blockUI()
            Toast.makeText(activity!!.baseContext, R.string.update_state, Toast.LENGTH_SHORT).show()
            pgrBar_frag_user_save.visibility = View.VISIBLE
            val cliente = Cliente(
                    id = this.cliente.id,
                    nombre = editText__usu_frag_name.text.toString(),
                    apellido = editText_usu_frag_last.text.toString(),
                    correoUsuario = editText_usu_frag_email.text.toString(),
                    nombreUsuario = editText_usu_frag_username.text.toString(),
                    telefono = editText_usu_frag_mobile.text.toString()
            )

            val clienteJsonUpdate = jsonParser.clienteJsonUpdate(cliente)
            Log.i("CLIENTE A ACTUALIZAR", clienteJsonUpdate)

            bg{
            HttpRequest.actualizarDato("cliente", "${cliente.id}", clienteJsonUpdate, {
                error, datos ->
                if(error){
                    Toast.makeText(activity!!.baseContext, R.string.update_cliente_error, Toast.LENGTH_LONG).show()
                    Log.i("Entró?", "jajaja1")
                    pgrBar_frag_user_save.visibility = View.GONE
                    true
                }else{
                    if(DBHandler.getInstance(activity!!.baseContext)!!.actualizar(cliente)){
                        if(!this.imagePath.equals("")) {
                            val foto = Foto(
                                    id = this.foto.id,
                                    datos = ImageFileHandler.bitmapToB64String(ImageFileHandler.fileToBitmap(File(this.imagePath))),
                                    extension = "jpg"
                            )
                            val fotoJson = jsonParser.fotoToJson(foto)
                            HttpRequest.actualizarDato("foto", "${foto.id}", fotoJson, {
                                error, datos ->
                                if(error){
                                    Toast.makeText(activity!!.baseContext, R.string.update_cliente_error_img, Toast.LENGTH_LONG).show()
                                    pgrBar_frag_user_save.visibility = View.GONE
                                }else{
                                    if(DBHandler.getInstance(activity!!.baseContext)!!.actualizar(foto)){
                                        Toast.makeText(activity!!.baseContext, R.string.update_cliente_success, Toast.LENGTH_LONG).show()
                                        mListener.imageChanged("${foto.id}")
                                        Log.i("FOTO A ACTUALIZAR", fotoJson)
                                    }
                                    pgrBar_frag_user_save.visibility = View.GONE
                                }
                            })
                        }else{
                            pgrBar_frag_user_save.visibility = View.GONE
                            Toast.makeText(activity!!.baseContext, R.string.update_cliente_success, Toast.LENGTH_LONG).show()
                        }
                    }
                    true
                }
            })
        }
        }
    }

    fun crearTarjeta(view: View): TarjetaCredito{
        val tipo = view.findViewById<Spinner>(R.id.spinner_card_dialog_compania).selectedItem as String
        val numero = view.findViewById<EditText>(R.id.editText_card_dialog_numero).text.toString()
        val codigo = view.findViewById<EditText>(R.id.editText_card_dialog_codigo).text.toString()
        val mes = view.findViewById<EditText>(R.id.editText_card_dialog_mes).text.toString().toInt()
        val anio = view.findViewById<EditText>(R.id.editText_card_dialog_anio).text.toString().toInt()

        return TarjetaCredito(
                companiaTarjeta = tipo,
                numeroTarjeta = numero,
                codigoSeguridad = codigo,
                mesTarjeta = mes,
                anioTarjeta = anio,
                clienteId = cliente.id

        )

    }

    fun guardarTarjeta(tarjetaCredito: TarjetaCredito){
        val tarjetaJson = jsonParser.tarjetaToJson(tarjetaCredito)
        HttpRequest.insertarDato("TarjetaCredito", tarjetaJson, { error, datos ->
            if(error){
                Toast.makeText(activity!!, resources.getString(R.string.usuario_frag_tarjetas_fallo), Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(activity!!, resources.getString(R.string.usuario_frag_tarjetas_exito), Toast.LENGTH_LONG).show()
                val tarjetaInsertada = jsonParser.jsonToTarjeta(datos)
                DBHandler.getInstance(activity!!)!!.insertar(tarjetaInsertada as TarjetaCredito)
                tarjetas.add(tarjetaInsertada)
                viewAdapter.notifyDataSetChanged()

            }
        })
    }


    fun llenarVista(){
        val image = ImageFileHandler.base64ToBitmap(foto.datos)
        val imageBitmap = Bitmap.createScaledBitmap(image, 123, 150, false)
        imgView_usu_frag_foto.setImageBitmap(imageBitmap)
        editText__usu_frag_name.append(cliente.nombre)
        editText_usu_frag_last.append(cliente.apellido)
        editText_usu_frag_username.append(cliente.nombreUsuario)
        editText_usu_frag_email.append(cliente.correoUsuario)
        editText_usu_frag_mobile.append(cliente.telefono)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try{
            mListener = context as OnImageChanged
        }catch(e: ClassCastException){
            throw ClassCastException(context.toString() + "must implement OnNextArrowClickedListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == AuthFragment.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            pgrBar_frag_user_photo.visibility = View.VISIBLE
            async(UI){
                val imageRotated: Deferred<Bitmap> = bg{
                    ImageFileHandler.bitmapFromFileRotation(File(imagePath))
                }
                afterRotation(imageRotated.await())
            }
        }
    }

    fun afterRotation(bitmap: Bitmap){
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, imgView_usu_frag_foto.width, imgView_usu_frag_foto.height, false)
        imgView_usu_frag_foto.setImageBitmap(scaledBitmap)
        pgrBar_frag_user_photo.visibility = View.GONE
    }

    fun tomarFoto(context: Context){
        val imageFile = createImageFile("JPEG_", Environment.DIRECTORY_PICTURES, ".jpg")
        tomarFotoIntent(imageFile, context)
    }

    fun createImageFile(prefix: String, directory: String, extension: String): File {
        val timestamp = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
        val filename = prefix + timestamp + "_"
        val storageDir = activity!!.getExternalFilesDir(directory)
        return File.createTempFile(filename, extension, storageDir)
    }

    fun blockUI(){
        editText_usu_frag_mobile.isEnabled = false
        editText_usu_frag_username.isEnabled = false
        editText_usu_frag_email.isEnabled = false
        editText_usu_frag_last.isEnabled = false
        editText__usu_frag_name.isEnabled = false
    }

    fun enableUI(){
        editText_usu_frag_mobile.isEnabled = true
        editText_usu_frag_username.isEnabled = true
        editText_usu_frag_email.isEnabled = true
        editText_usu_frag_last.isEnabled = true
        editText__usu_frag_name.isEnabled = true
    }

    fun tomarFotoIntent(file: File, context: Context){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imagePath = file.absolutePath
        val photoUri: Uri = FileProvider.getUriForFile(
                context,
                "com.example.daniel.proyectomoviles.fileprovider",
                file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        if(intent.resolveActivity(activity!!.packageManager) != null){
            startActivityForResult(intent, AuthFragment.REQUEST_IMAGE_CAPTURE)
        }
    }
}
