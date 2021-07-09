package com.aplicacion.mypet.models

class Favorito {
    var id: String? = null
    var idPublicacion: String? = null
    var idUser: String? = null
    var timestamp: Long = 0

    constructor()

    constructor(id: String?, idPublicacion: String?, idUser: String?, timestamp: Long) {
        this.id = id
        this.idPublicacion = idPublicacion
        this.idUser = idUser
        this.timestamp = timestamp
    }
}