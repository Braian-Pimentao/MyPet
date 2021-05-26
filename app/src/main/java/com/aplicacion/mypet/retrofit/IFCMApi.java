package com.aplicacion.mypet.retrofit;

import com.aplicacion.mypet.models.FCMBody;
import com.aplicacion.mypet.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAwu4pqbs:APA91bEJhFT1pK_j60Cj1IRy6i99nkH2saPDNgyfaRZuL7PHNlHvJ9g6NkrDgEudxJ0Qe7FK4Mdo0IQKd92S4008iUIAjI1mdAcrvWm_sIFKLcs5K-Iq4NXrOUedIfJOWs4l2hWVeWJD"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
