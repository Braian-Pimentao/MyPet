package com.aplicacion.mypet.providers;



import com.aplicacion.mypet.models.Publicacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        return collectionReference.orderBy("fechaPublicacion", Query.Direction.DESCENDING);
    }

    public Query getPostByUser(String id) {
        return collectionReference.whereEqualTo("idUser",id);
    }

    public Task<DocumentSnapshot> getPostById(String id) {
        return  collectionReference.document(id).get();
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }
}
