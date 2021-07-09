package com.aplicacion.mypet.providers

import com.aplicacion.mypet.models.Token
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
class TokenProvider() {
    private lateinit var collectionReference: CollectionReference

    init {
        collectionReference = FirebaseFirestore.getInstance().collection("Tokens")
    }

    fun create(idUser: String?) {
        if (idUser == null) {
            return
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener { s ->
            val token = Token(s)
            collectionReference.document(idUser).set(token)
        }
    }

    fun getToken(idUser: String?): Task<DocumentSnapshot?> {
        return collectionReference.document(idUser!!).get()
    }

}