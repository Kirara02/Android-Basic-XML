package com.kirara.weatherapp.remote;

import com.kirara.weatherapp.model.ForecastResponse;
import com.kirara.weatherapp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(@Query("appid") String apiKey,
                                            @Query("lat") double lat,
                                            @Query("lon") double lon);

    @GET("forecast")
    Call<ForecastResponse> getForecastWeather(@Query("appid") String apiKey,
                                              @Query("lat") double lat,
                                              @Query("lon") double lon);
}
