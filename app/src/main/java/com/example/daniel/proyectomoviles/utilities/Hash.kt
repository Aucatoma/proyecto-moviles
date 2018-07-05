package com.example.daniel.proyectomoviles.utilities

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Hash {
    companion object {


        lateinit var digest: MessageDigest

        fun stringHash(algorithm: String, string: String): String {
            var stringBuffer = StringBuilder()
            /* Hash */
            try {
                var messageDigest = byteArrayOf()
                digest = MessageDigest.getInstance(algorithm)
                digest.update(string.toByteArray(charset("UTF-8")))
                messageDigest = digest.digest()

                /* Hash String */
                var h = ""
                messageDigest.forEach { byte: Byte ->
                    h = Integer.toHexString(0xFF and byte.toInt())
                    while(h.length < 2)
                        h = "0" + h
                    stringBuffer.append(h)
                }
                return stringBuffer.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                return ""
            }
            return stringBuffer.toString()
        }
    }
}