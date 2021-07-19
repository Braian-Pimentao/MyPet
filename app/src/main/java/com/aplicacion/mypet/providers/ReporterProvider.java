package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Reporte;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReporterProvider {
    CollectionReference collectionReference;

    public ReporterProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Reportes");
    }

    public Task<Void> crearReporte(Reporte reporte){
        return  collectionReference.document().set(reporte);
    }

    public Query getReportesByPost(String idPublicacion) {
        return collectionReference.whereEqualTo("idPublicacion", idPublicacion);
    }

    public Task<Void> delete(String idReporte) {
        return collectionReference.document(idReporte).delete();
    }

}
