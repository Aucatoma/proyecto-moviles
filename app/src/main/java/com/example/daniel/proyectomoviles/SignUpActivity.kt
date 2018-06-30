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
import android.widget.ProgressBar
import com.example.daniel.proyectomoviles.entidades.Cliente
import com.example.daniel.proyectomoviles.entidades.Foto
import com.example.daniel.proyectomoviles.imageHandle.ImageFileHandler
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_take_photo.setOnClickListener{ v: View? ->
            tomarFoto()
        }

        btn_sign_up.setOnClickListener { v: View? ->
            async (UI){
                val cliente: Deferred<Cliente> = bg{
                    crearCliente()
                }
                Log.i("CLIENTE_CREADO", cliente.await().nombre)
            }
        }

    }

    fun tomarFoto(){
        val imageFile = createImageFile("JPEG_", Environment.DIRECTORY_PICTURES, ".jpg")
        tomarFotoIntent(imageFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == LoginActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            async(UI){
                val imageRotated: Deferred<Boolean> = bg{
                    imageHandler.rotateImageFile(File(imagePath))
                }
                if(imageRotated.await()){
                    imageBitmap = imageHandler.fileToBitmap(File(imagePath))
                    imgView_user_photo.setImageBitmap(imageBitmap)
                }
            }
        }
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
            startActivityForResult(intent, LoginActivity.REQUEST_IMAGE_CAPTURE)
        }
    }

    fun crearCliente(): Cliente{
        val nombre = editText_signUp_name.text.toString()
        val apellido = editText_signUp_last.text.toString()
        val telefono = editText_signUp_mobile.text.toString()
        val email = editText_signUp_email.text.toString()
        val username = editText_signUp_username.text.toString()
        Log.i("CLIENTE_", "")
        val password = editText_signUp_password.text.toString()
        val foto = Foto(-1, imageHandler.bitmapToB64String(imageBitmap), "jpg")

        return Cliente(-1, nombre, apellido, telefono, username, password, email, null, foto, 0.toLong(), 0.toLong())
    }
}
