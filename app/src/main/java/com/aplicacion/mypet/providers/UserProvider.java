package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserProvider {
    private CollectionReference collectionReference;

    public UserProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return collectionReference.document(id).get();
    }

    public Task<Void> create(User user){
        return collectionReference.document(user.getId()).set(user);
    }



    public Task<Void> update(User user){
        Map<String, Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("ubicacion",user.getUbicacion());
        map.put("urlPerfil",user.getUrlPerfil());
        map.put("ocultarUbicacion",user.getOcultarUbicacion());
        return collectionReference.document(user.getId()).update(map);
    }

    public DocumentReference getUserRealTime(String id) {
        return collectionReference.document(id);
    }

    public Task<Void> updateOnline(String idUser, boolean estado){
        Map<String, Object> map = new HashMap<>();
        map.put("online",estado);
        map.put("ultimaConexion",new Date().getTime());

        return collectionReference.document(idUser).update(map);
    }
}


