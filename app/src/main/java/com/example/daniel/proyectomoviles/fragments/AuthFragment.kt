package com.example.daniel.proyectomoviles.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.daniel.proyectomoviles.R
import com.example.daniel.proyectomoviles.utilities.ImageFileHandler
import kotlinx.android.synthetic.main.fragment_auth_fragment.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AuthFragment : Fragment() {

    var passwordHiding = true
    var idVisibilityOffRes: Int = 0
    var idVisibilityOnRes: Int = 0
    var imagePath = ""

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idVisibilityOffRes = resources.getIdentifier("@drawable/ic_baseline_visibility_off_24px", "drawable", activity!!.packageName)
        idVisibilityOnRes = resources.getIdentifier("@drawable/ic_baseline_visibility_24px", "drawable", activity!!.packageName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgBtn_password_visibility.setOnClickListener { v: View? ->
            if(passwordHiding){
                imgBtn_password_visibility.setImageResource(idVisibilityOnRes)
                editText_frag_auth_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordHiding = false
            }
            else {
                imgBtn_password_visibility.setImageResource(idVisibilityOffRes)
                editText_frag_auth_password.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordHiding = true
            }
        }

        btn_facial_recognition.setOnClickListener{
            tomarFoto(this.activity!!.baseContext)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            async(UI){
                val imageRotated: Deferred<Boolean> = bg{
                    ImageFileHandler.rotateImageFile(File(imagePath))
                }
                if(imageRotated.await()){
                    ImageFileHandler.fileToBitmap(File(imagePath))
                }
            }
        }
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
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

}
