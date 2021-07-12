package com.aplicacion.mypet.providers;



import com.aplicacion.mypet.models.Publicacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublicacionProvider {
    CollectionReference collectionReference;

    public PublicacionProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Publicaciones");
    }

    public Task<Void> save(Publicacion publicacion) {
        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();
        publicacion.setId(id);
        return documentReference.set(publicacion);
    }

    public Query getAll() {
        return collectionReference.orderBy("fechaPublicacion", Query.Direction.DESCENDING);
    }

    public Query getPublicacionByCategoryAndTimesTamp(String tipo) {
        return collectionReference.whereEqualTo("tipo",tipo).orderBy("fechaPublicacion", Query.Direction.DESCENDING);
    }

    public Query getPublicacionByRaza(String raza) {
        return collectionReference.orderBy("raza").startAt(raza).endAt(raza+'\uf8ff');
    }

    public Query getPublicacionByFavoriteUser(ArrayList<String> publicaciones) {
        return collectionReference.whereIn("id",publicaciones);
    }



    public Query getPostByUser(String id) {
        return collectionReference.whereEqualTo("idUser",id);
    }

    public Task<DocumentSnapshot> getPostById(String id) {
        return  collectionReference.document(id).get();
    }

    public Task<Void> update(Publicacion publicacion) {
        Map<String,Object> map = new HashMap<>();
        map.put("nombre", publicacion.getNombre());
        map.put("edad", publicacion.getEdad());
        map.put("raza", publicacion.getRaza());
        map.put("sexo", publicacion.getSexo());
        map.put("tipo", publicacion.getTipo());
        map.put("descripcion", publicacion.getDescripcion());
        map.put("fechaPublicacion", publicacion.getFechaPublicacion());
        map.put("imagenes", publicacion.getImagenes());
        return collectionReference.document(publicacion.getId()).update(map);

    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }
}
