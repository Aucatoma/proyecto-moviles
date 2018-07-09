package com.example.daniel.proyectomoviles

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Foto
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.utilities.ImageFileHandler
import com.example.daniel.proyectomoviles.parser.JsonParser
import com.example.daniel.proyectomoviles.utilities.Hash
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.textView_sign_up_feed
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {

    var imagePath = ""
    lateinit var imageBitmap: Bitmap
    val imageHandler = ImageFileHandler()
    private val jsonParser = JsonParser()
    var clienteJson = ""
    var fotoJson = ""
    var cliente: Cliente? = null

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        progressBar_sign_up.visibility = View.GONE
        btn_take_photo.setOnClickListener{ v: View? ->
            tomarFoto()
        }

        btn_sign_up.setOnClickListener { v: View? ->

            async(UI){
                val clienteCreado: Deferred<Boolean> = bg{
                    crearCliente()
                }
                if(clienteCreado.await()){
                    bg{HttpRequest.registrarCliente(clienteJson, fotoJson, { error, datos ->
                        if(error){
                            textView_sign_up_feed.text = resources.getString(R.string.sign_up_error_feedback)
                        }else{
                            Toast.makeText(baseContext, resources.getString(R.string.signed_up), Toast.LENGTH_LONG).show()
                            Log.i("RESPUESTA_REGISTRO", datos)
                            finish()
                        }
                    })
                    }
                }
            }
        }
    }

    fun tomarFoto(){
        val imageFile = createImageFile("JPEG_", Environment.DIRECTORY_PICTURES, ".jpg")
        tomarFotoIntent(imageFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            progressBar_sign_up.visibility = View.VISIBLE
            async(UI){
                val imageRotated: Deferred<Bitmap> = bg{
                    ImageFileHandler.bitmapFromFileRotation(File(imagePath))
                }
                afterImageRotationCompleted(imageRotated.await())
            }
        }
    }

    fun afterImageRotationCompleted(bitmap: Bitmap){
        imageBitmap = bitmap
        progressBar_sign_up.visibility = View.GONE
        imgView_user_photo.setImageBitmap(imageBitmap)
    }


    fun createImageFile(prefix: String, directory: String, extension: String): File {
        val timestamp = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
        val filename = prefix + timestamp + "_"
        val storageDir = getExternalFilesDir(directory)
        return File.createTempFile(filename, extension, storageDir)
    }


    fun tomarFotoIntent(file: File){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imagePath = file.absolutePath
        val photoUri: Uri = FileProvider.getUriForFile(
                this,
                "com.example.daniel.proyectomoviles.fileprovider",
                file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun crearCliente(): Boolean{

        val nombre = editText_signUp_name.text.toString()
        val apellido = editText_signUp_last.text.toString()
        val telefono = editText_signUp_mobile.text.toString()
        val email = editText_signUp_email.text.toString()
        val username = editText_signUp_username.text.toString()
        val password = Hash.stringHash("SHA-512", editText_signUp_password.text.toString())
        val foto = Foto(-1, ImageFileHandler.bitmapToB64String(imageBitmap), "jpg", 0, 0)

        cliente =  Cliente(-1, nombre, apellido, telefono, username, password, email, null, foto, "",0.toLong(), 0.toLong())
        fotoJson = jsonParser.fotoToJson(cliente!!.foto as Foto)
        clienteJson = jsonParser.clienteToJson(cliente!!)

        return true
    }
}
