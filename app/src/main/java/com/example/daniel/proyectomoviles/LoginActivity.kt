package com.example.daniel.proyectomoviles

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

    var imagePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imgBtn_face_api.setOnClickListener { v: View? ->
            tomarFoto()
        }
    }

    fun tomarFoto(){
        val imageFile = createImageFile("JPEG_", Environment.DIRECTORY_PICTURES, ".jpg")
        tomarFotoIntent(imageFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            bg{
                rotateImageFile(File(imagePath))
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


    /* Rotar la imagen del archivo y sobreescribir el mismo */
    fun rotateImageFile(file: File){

        val bitmap = fileToBitmap(file) // file a bitmap
        val exifInterface = ExifInterface(file.path) // leer los metadatos
        val rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) // leer la rotación
        val rotationInDegrees = exifToDegrees(rotation) // obtener la rotación en grados
        val rotatedBitmap = rotateBitmap(bitmap, rotationInDegrees.toFloat()) // rotar el bitmap

        var baos = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // hacer del bitmap un jpg y guardar los bytes en 'baos'

        writeFile(file, baos.toByteArray()) // guardar los bytes en el archivo

    }

    /* Leer archivo y generar un bitmap */
    fun fileToBitmap(file: File): Bitmap{
        return BitmapFactory.decodeFile(file.path)
    }

    /* Escribe/Sobreescribe un archivo con los datos que se envían */
    fun writeFile(file: File, byteArray: ByteArray){
        val outputStream = FileOutputStream(file)
        outputStream.write(byteArray)
        outputStream.flush()
        outputStream.close()
    }

    /* Rota el bitmap y devuelve el nuevo bitmap */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        if (degrees != 0.toFloat())
            matrix.preRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /* Cambia de formato Exif a grados ° */
    fun exifToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) return 90
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) return 180
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) return 270
        else return 0
    }

}
