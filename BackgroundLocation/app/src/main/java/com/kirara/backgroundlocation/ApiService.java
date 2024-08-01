package com.kirara.backgroundlocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface ApiService {
    @POST("api/location/1")
    Call<Void> update(@Body User user);

    static ApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.19:8080/") // Ganti dengan URL backend Anda
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }
}
