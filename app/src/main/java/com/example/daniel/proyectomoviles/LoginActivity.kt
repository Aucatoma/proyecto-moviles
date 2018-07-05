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
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.utilities.Hash
import com.example.daniel.proyectomoviles.utilities.ImageFileHandler
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

    var imagePath = ""
    val imageHandler = ImageFileHandler()
    lateinit var imageBitmap: Bitmap
    lateinit var username: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imgBtn_face_api.setOnClickListener { v: View? ->
            tomarFoto()
        }

        btn_sign_in.setOnClickListener { v: View? ->
            obtenerUserPass()
            HttpRequest.login(username, "", imageHandler.bitmapToB64String(imageBitmap), { error, datos ->
                if(error){

                }else{
                    Log.i("LOGIN_SERV_RES", datos)
                }
            })
        }
    }

    fun tomarFoto(){
        val imageFile = createImageFile("JPEG_", Environment.DIRECTORY_PICTURES, ".jpg")
        tomarFotoIntent(imageFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            async(UI){
                val imageRotated: Deferred<Boolean> = bg{
                    imageHandler.rotateImageFile(File(imagePath))
                }
                if(imageRotated.await()){
                    imageBitmap = imageHandler.fileToBitmap(File(imagePath))
                    //HttpRequest.login()
                }
            }
        }
    }


    fun createImageFile(prefix: String, directory: String, extension: String): File{
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



    fun obtenerUserPass(){
        username = editText_login_username.text.toString()
        password = Hash.stringHash("SHA-512", editText_login_password.text.toString())
    }
}
