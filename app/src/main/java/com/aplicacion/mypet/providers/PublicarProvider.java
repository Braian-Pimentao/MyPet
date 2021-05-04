package com.aplicacion.mypet.providers;



import com.aplicacion.mypet.models.Publicacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PublicarProvider {
    CollectionReference collectionReference;

    public PublicarProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Publicaciones");
    }

    public Task<Void> save(Publicacion publicacion) {
        return collectionReference.document().set(publicacion);
    }
}
