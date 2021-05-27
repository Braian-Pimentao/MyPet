package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Mensaje;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MensajeProvider {
    private CollectionReference collectionReference;

    public MensajeProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Mensajes");
    }

    public Task<Void> create(Mensaje mensaje) {
        DocumentReference documentReference = collectionReference.document();
        mensaje.setId(documentReference.getId());
        return collectionReference.document().set(mensaje);
    }
}
