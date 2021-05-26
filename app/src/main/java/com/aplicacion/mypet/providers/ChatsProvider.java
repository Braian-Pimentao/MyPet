package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.Chat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatsProvider {
    private CollectionReference collectionReference;

    public ChatsProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat) {
        collectionReference.document(chat.getIdUser1()).collection("Users").document(chat.getIdUser2()).set(chat);
        collectionReference.document(chat.getIdUser2()).collection("Users").document(chat.getIdUser1()).set(chat);
    }


}
