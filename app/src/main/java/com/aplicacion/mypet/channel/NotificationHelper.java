package com.aplicacion.mypet.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.models.Mensaje;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID = "com.aplicacion.mypet";
    private static final String CHANNEL_NAME = "MyPet";

    private NotificationManager manager;

    public NotificationHelper(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotificationCompat(String title, String body) {
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.drawable.ic_hueso_notificacion)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder getNotificationMensaje(Mensaje[] mensajes, String usernameSender, Bitmap bitmap) {
        Person person = null;
        if (bitmap!= null) {
            person = new Person.Builder()
                    .setName(usernameSender)
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .build();
        } else {
            person = new Person.Builder()
                    .setName(usernameSender)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(),R.drawable.ic_person_2))
                    .build();
        }

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person);


        for (Mensaje m : mensajes) {
            NotificationCompat.MessagingStyle.Message message2 =
                    new NotificationCompat.MessagingStyle.Message(
                            m.getMensaje(),
                            m.getTimestamp(),
                            person);
            messagingStyle.addMessage(message2);
        }
        return new  NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_hueso_notificacion)
                .setStyle(messagingStyle);
    }
}
