package com.example.daniel.proyectomoviles

import android.app.Activity
import android.content.Context
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.net.NetworkInfo
import android.net.ConnectivityManager



class VerificadorInternet {


    fun hasActiveInternetConnection(context: Context): Boolean {
        if (isNetworkAvailable(context)) {
            try {
                val urlc = URL("http://www.google.com").openConnection() as HttpURLConnection
                urlc.setRequestProperty("User-Agent", "Test")
                urlc.setRequestProperty("Connection", "close")
                urlc.setConnectTimeout(1500)
                urlc.connect()
                return urlc.getResponseCode() === 200
            } catch (e: IOException) {
                Log.e("Network", "Error checking internet connection", e)
            }

        } else {
            Log.d("Network", "No network available!")
        }
        return false
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null
    }

}

