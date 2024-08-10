package com.aplicacion.mypet.models

class SliderItem {
    var urlImagen: String? = null
    var timestamp: Long? = null

    constructor()

    constructor(urlImagen: String?, timestamp: Long?) {
        this.urlImagen = urlImagen
        this.timestamp = timestamp
    }
}