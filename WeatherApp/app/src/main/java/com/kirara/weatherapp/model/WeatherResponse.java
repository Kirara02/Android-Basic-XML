package com.kirara.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("coord")
    public Coord coord;

    @SerializedName("weather")
    public List<Weather> weatherList;

    @SerializedName("main")
    public Main main;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("visibility")
    public int visibility;

    @SerializedName("sys")
    public Sys sys;

    @SerializedName("name")
    public String name;

    public static class Coord {
        @SerializedName("lon")
        public double lon;

        @SerializedName("lat")
        public double lat;
    }

    public static class Main {
        @SerializedName("temp")
        public float temp;

        @SerializedName("feels_like")
        public float feelsLike;

        @SerializedName("pressure")
        public int pressure;

        @SerializedName("humidity")
        public int humidity;

        @SerializedName("temp_min")
        public float tempMin;

        @SerializedName("temp_max")
        public float tempMax;

        @SerializedName("sea_level")
        public int seaLevel;

        @SerializedName("grnd_level")
        public int grndLevel;
    }

    public static class Weather {
        @SerializedName("main")
        public String main;

        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;
    }

    public static class Wind {
        @SerializedName("speed")
        public float speed;

        @SerializedName("deg")
        public int deg;

        @SerializedName("gust")
        public float gust;
    }

    public static class Sys {
        @SerializedName("country")
        public String country;

        @SerializedName("sunrise")
        public long sunrise;

        @SerializedName("sunset")
        public long sunset;
    }
}
