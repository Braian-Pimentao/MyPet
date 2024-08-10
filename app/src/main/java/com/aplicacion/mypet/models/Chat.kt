package com.aplicacion.mypet.models

import java.util.*

class Chat {
    var id: String? = null
    var idUser1: String? = null
    var idUser2: String? = null
    var timestamp: Long = 0
    var idNotificacion = 0
    var ids: ArrayList<String>? = null

    constructor() {}

    constructor(id: String?, idUser1: String?, idUser2: String?, timestamp: Long, idNotificacion: Int, ids: ArrayList<String>?) {
        this.id = id
        this.idUser1 = idUser1
        this.idUser2 = idUser2
        this.timestamp = timestamp
        this.idNotificacion = idNotificacion
        this.ids = ids
    }
}