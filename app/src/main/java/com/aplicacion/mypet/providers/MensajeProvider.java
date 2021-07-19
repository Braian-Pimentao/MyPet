package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Mensaje;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MensajeProvider {
    private CollectionReference collectionReference;

    public MensajeProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Mensajes");
    }

    public Task<Void> create(Mensaje mensaje) {
        DocumentReference documentReference = collectionReference.document();
        mensaje.setId(documentReference.getId());
        return documentReference.set(mensaje);
    }

    public Query getMensajesByChat(String idChat) {
        return  collectionReference.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public Query getMensajeByChatAndSender(String idChat, String idSender) {
        return  collectionReference.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).whereEqualTo("visto", false);
    }

    public Query getMensajesNoLeidosUsuario(String idReceiver) {
        return collectionReference.whereEqualTo("idReceiver", idReceiver).whereEqualTo("visto", false);
    }

    public Query getTresUltimosMensajesByChatAndSender(String idChat, String idSender) {
        return  collectionReference.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).
                whereEqualTo("visto", false).limit(3).orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getLastMensaje(String idChat) {
        return  collectionReference.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public Task<Void> updateVisto(String idDocument, boolean estado) {
        Map<String,Object> map = new HashMap<>();
        map.put("visto",estado);
        return  collectionReference.document(idDocument).update(map);
    }

    public Task<Void> deleteMensaje(String idMensaje) {
        return collectionReference.document(idMensaje).delete();
    }
}
