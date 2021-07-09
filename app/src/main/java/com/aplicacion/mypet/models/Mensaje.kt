package com.aplicacion.mypet.models

class Mensaje {
    var id: String? = null
    var idSender: String? = null
    var idReceiver: String? = null
    var idChat: String? = null
    var mensaje: String? = null
    var timestamp: Long = 0
    var visto = false

    constructor()

    constructor(id: String?, idSender: String?, idReceiver: String?, idChat: String?, mensaje: String, timestamp: Long, visto: Boolean) {
        this.id = id
        this.idSender = idSender
        this.idReceiver = idReceiver
        this.idChat = idChat
        this.mensaje = mensaje
        this.timestamp = timestamp
        this.visto = visto
    }
}