package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Favorito;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FavoritoProvider {
    CollectionReference collectionReference;

    public FavoritoProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Favoritos");
    }

    public Task<Void> create(Favorito favorito) {
        return collectionReference.document().set(favorito);
    }
}
