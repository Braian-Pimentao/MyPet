package com.aplicacion.mypet.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aplicacion.mypet.channel.NotificationHelper;
import com.aplicacion.mypet.models.Mensaje;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> data = remoteMessage.getData();
        if (data.get("title") != null) {
            showNotification(data);
        }
    }

    private void showNotification(Map<String,String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String mensajeJSON = data.get("mensajes");
        int idNotificationChat = Integer.parseInt(data.get("idNotification"));

        Gson gson = new Gson();
        Mensaje[] mensajes = gson.fromJson(mensajeJSON,Mensaje[].class);


        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationMensaje(mensajes);
        notificationHelper.getManager().notify(idNotificationChat,builder.build());
    }
}
