package com.example.daniel.proyectomoviles.utilities

import com.example.daniel.proyectomoviles.entidades.Cliente

class ValidacionCliente {

    fun validar(cliente: Cliente): Boolean{

        return  validarNombre(cliente.nombre) &&
                validarApellido(cliente.apellido) &&
                validarCorreo(cliente.correoUsuario) &&
                validarPassword(cliente.contraseniaUsuario) &&
                validarUsername(cliente.nombreUsuario) &&
                validarTelefono(cliente.telefono)

    }

    private fun validarNombre(nombre: String): Boolean{
       return !nombre.isBlank()
    }

    private fun validarApellido(apellido: String): Boolean{
        return !apellido.isBlank()
    }

    private fun validarUsername(username: String): Boolean{
        return !username.isBlank()
    }
    private fun validarPassword(password: String): Boolean{
        return !password.isBlank()
    }
    private fun validarCorreo(correo:String): Boolean{
        return !correo.isBlank()
    }
    private fun validarTelefono(telefono: String): Boolean{
        return !telefono.isBlank()
    }
}