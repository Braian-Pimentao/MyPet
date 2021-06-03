package com.aplicacion.mypet.providers;

import com.aplicacion.mypet.models.FCMBody;
import com.aplicacion.mypet.models.FCMResponse;
import com.aplicacion.mypet.retrofit.IFCMApi;
import com.aplicacion.mypet.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {
    private final String URL = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        System.out.println("-------------------------------------------------------------SendNotification");
        return RetrofitClient.getClient(URL).create(IFCMApi.class).send(body);
    }
}
