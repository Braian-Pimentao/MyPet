package com.aplicacion.mypet.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit getClient(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        System.out.println("-------------------------------------------------------------SendNotification22222222222222222");
        return retrofit;
    }
}
