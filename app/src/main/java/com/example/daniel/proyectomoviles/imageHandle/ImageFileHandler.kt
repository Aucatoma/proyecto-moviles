package com.example.daniel.proyectomoviles.imageHandle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageFileHandler {

    val flags = Base64.URL_SAFE or Base64.NO_WRAP

    /* Rotar la imagen del archivo y sobreescribir el mismo */
    fun rotateImageFile(file: File): Boolean{
        val bitmap = fileToBitmap(file) // file a bitmap
        val exifInterface = ExifInterface(file.path) // leer los metadatos
        val rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) // leer la rotación
        val rotationInDegrees = exifToDegrees(rotation) // obtener la rotación en grados
        val rotatedBitmap = rotateBitmap(bitmap, rotationInDegrees.toFloat()) // rotar el bitmap

        var baos = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos) // hacer del bitmap un jpg y guardar los bytes en 'baos'

        writeFile(file, baos.toByteArray()) // guardar los bytes en el archivo
        return true
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


    fun bitmapToB64String(bitmap: Bitmap): String {
        var byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), flags)
    }

    fun base64ToBitmap(base64: String): Bitmap {
        val imageDecoded = Base64.decode(base64, flags)
        return BitmapFactory.decodeByteArray(imageDecoded, 0, imageDecoded.size)
    }
}