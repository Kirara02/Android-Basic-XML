package com.kirara.adzanapp.remote;

import com.kirara.adzanapp.model.PrayerTimeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdzanApi {
    @GET("timings/{date}")
    Call<PrayerTimeResponse>  getPrayerTimes(@Path("date") String date,
                                             @Query("latitude") double lat,
                                             @Query("longitude") double lon);
}
