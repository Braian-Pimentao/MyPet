package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Reporte;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReporterProvider {
    CollectionReference collectionReference;

    public ReporterProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Reportes");
    }

    public Task<Void> crearReporte(Reporte reporte){
        return  collectionReference.document().set(reporte);
    }

}
