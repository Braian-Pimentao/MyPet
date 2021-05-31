package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatsProvider {
    private CollectionReference collectionReference;

    public ChatsProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat) {
        collectionReference.document(chat.getIdUser1() + chat.getIdUser2()).set(chat);
    }

    public Query getAll(String id) {
        return collectionReference.whereArrayContains("ids",id);
    }

    public Task<Void> deleteChat(String idChat) {
        return collectionReference.document(idChat).delete();
    }

    public Query getChatByUser1AndUser2(String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1+idUser2);
        ids.add(idUser2+idUser1);
        return collectionReference.whereIn("id",ids);
    }


}
