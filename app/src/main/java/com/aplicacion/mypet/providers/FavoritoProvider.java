package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Favorito;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FavoritoProvider {
    CollectionReference collectionReference;

    public FavoritoProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Favoritos");
    }

    public Task<Void> create(Favorito favorito) {
        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();
        favorito.setId(id);
        return documentReference.set(favorito);
    }

    public Query getFavoriteByPostAndUser(String idPublicacion, String idUser){
        return collectionReference.whereEqualTo("idPublicacion",idPublicacion).whereEqualTo("idUser",idUser);
    }
    public Query getFavoriteByPost(String idPublicacion) {
        return collectionReference.whereEqualTo("idPublicacion", idPublicacion);
    }

    public Query getFavoritesByUser(String idUser) {
        return collectionReference.whereEqualTo("idUser", idUser);
    }


    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }

    public Query deleteByPublicacion(String idPublicacion) {
        return collectionReference.whereEqualTo("idPublicacion", idPublicacion);
    }
}
