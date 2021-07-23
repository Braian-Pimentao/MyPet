package com.aplicacion.mypet.providers;



import android.util.Log;

import com.aplicacion.mypet.models.Publicacion;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublicacionProvider {
    CollectionReference collectionReference;
    GeoFirestore geoFirestore;
    UserProvider userProvider;
    GeoPoint geoPoint;

    public PublicacionProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Publicaciones");
        geoFirestore = new GeoFirestore(collectionReference);
        userProvider = new UserProvider();
    }

    public Task<Void> save(Publicacion publicacion) {
        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();
        publicacion.setId(id);
        userProvider.getUser(publicacion.getIdUser())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       if (documentSnapshot.contains("ubicacion")) {
                           ArrayList<Double> ubicacionRecogida = (ArrayList<Double>) documentSnapshot.get("ubicacion");
                           if (ubicacionRecogida != null) {
                               geoPoint = new GeoPoint(ubicacionRecogida.get(0), ubicacionRecogida.get(1));
                               geoFirestore.setLocation(id, geoPoint);
                               geoFirestore.getLocation(id, new GeoFirestore.LocationCallback() {
                                   @Override
                                   public void onComplete(@Nullable GeoPoint geoPoint, @Nullable Exception e) {
                                       if (e == null && geoPoint != null){
                                           Log.d("Localizacion", "The location for this document is" + geoPoint);
                                       }
                                   }
                               });
                           }
                       }
                   }
               });

        return documentReference.set(publicacion);
    }

    public GeoQuery getIDPostByUbication(ArrayList<Double> ubicacion, float value) {

        GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(ubicacion.get(0),ubicacion.get(1)),value);
        return geoQuery;
    }

    public void updateUbicacion(String id, GeoPoint geoPoint) {
        geoFirestore.setLocation(id,geoPoint);
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

    public Query getPublicacionByUbication(ArrayList<String> publicaciones) {
        return collectionReference.whereIn("id",publicaciones).orderBy("fechaPublicacion",Query.Direction.DESCENDING);
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
