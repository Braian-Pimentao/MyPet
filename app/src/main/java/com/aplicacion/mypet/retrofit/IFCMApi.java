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
            "Authorization:key=AAAAY9TPn64:APA91bEs5HlxXYrNP2XrHuODGrFeK0hb_Dmst74J7Ni84dGN9TCsIvvg88A4XD7J0FQYavtlPFiL14R85w7sWx6iKWk97Vrc1BcdusTNNdMGfSdnDtX-MipnEySoFP1ZddoDR_uOlUVS"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
