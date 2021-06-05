package com.aplicacion.mypet.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.aplicacion.mypet.activities.chat.ActivityChat;
import com.aplicacion.mypet.channel.NotificationHelper;
import com.aplicacion.mypet.models.Mensaje;
import com.aplicacion.mypet.utils.AppInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> data = remoteMessage.getData();

        if (!AppInfo.IN_ACTIVITY_CHAT){
            if (data.get("title") != null) {
                showNotification(data);
            }
        }
    }

    private void showNotification(String title, String body) {
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title, body);
        Random random = new Random();
        int n = random.nextInt(10000);
        notificationHelper.getManager().notify(n, builder.build());
    }


    private void showNotification(Map<String,String> data) {
        String title = data.get("title");
        String body = data.get("body");
        String usernameSender = data.get("usernameSender");
        String mensajeJSON = data.get("mensajes");
        String imagenSender = data.get("imagenSender");
        String idSender = data.get("idSender");
        String idReceiver = data.get("idReceiver");
        String idChat = data.get("idChat");
        int idNotificationChat = Integer.parseInt(data.get("idNotification"));


        Intent activityChat = new Intent(this, ActivityChat.class);
        activityChat.putExtra("idChat",idChat);
        activityChat.putExtra("idUser1",idSender);
        activityChat.putExtra("idUser2",idReceiver);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ActivityChat.class);
        stackBuilder.addNextIntent(activityChat);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);




        Gson gson = new Gson();
        Mensaje[] mensajes = gson.fromJson(mensajeJSON,Mensaje[].class);

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(imagenSender).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                                NotificationCompat.Builder builder = notificationHelper.getNotificationMensaje(mensajes,usernameSender, bitmap);
                                builder.setContentIntent(pendingIntent);
                                builder.setAutoCancel(true);
                                notificationHelper.getManager().notify(idNotificationChat,builder.build());
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                                NotificationCompat.Builder builder = notificationHelper.getNotificationMensaje(mensajes,usernameSender, null);
                                builder.setContentIntent(pendingIntent);
                                builder.setAutoCancel(true);
                                notificationHelper.getManager().notify(idNotificationChat,builder.build());

                            }
                        });
                    }
                });
    }
}
