package com.aplicacion.mypet.models

class Reporte {
    var idUser: String? = null
    var idPublicacion: String? = null
    var tipoReporte: String? = null

    constructor()

    constructor(idUser: String?, idPublicacion: String?, tipoReporte: String?) {
        this.idUser = idUser
        this.idPublicacion = idPublicacion
        this.tipoReporte = tipoReporte
    }
}