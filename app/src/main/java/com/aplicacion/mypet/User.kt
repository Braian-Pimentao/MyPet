package com.aplicacion.mypet

import java.util.*

class User {
    var id: String? = null
    var email: String? = null
    var username: String? = null
    var ubicacion: ArrayList<Double>? = null
    var urlPerfil: String? = null
    var ocultarUbicacion = false
    var online = false
    var ultimaConexion: Long = 0


    constructor(id: String?, email: String?, username: String?, ubicacion: ArrayList<Double>?, urlPerfil: String?, ocultarUbicacion: Boolean, online: Boolean, ultimaConexion: Long) {
        this.id = id
        this.email = email
        this.username = username
        this.ubicacion = ubicacion
        this.urlPerfil = urlPerfil
        this.ocultarUbicacion = ocultarUbicacion
        this.online = online
        this.ultimaConexion = ultimaConexion
    }
    constructor()


}