package com.aplicacion.mypet.providers;



import com.aplicacion.mypet.models.Publicacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PublicacionProvider {
    CollectionReference collectionReference;

    public PublicacionProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Publicaciones");
    }

    public Task<Void> save(Publicacion publicacion) {
        return collectionReference.document().set(publicacion);
    }

    public Query getAll() {
        return collectionReference.orderBy("nombre", Query.Direction.DESCENDING);
    }
}
