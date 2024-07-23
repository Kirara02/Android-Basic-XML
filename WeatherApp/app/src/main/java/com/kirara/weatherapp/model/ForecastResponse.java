package com.kirara.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    public List<Forecast> forecastList;

    @SerializedName("city")
    public City city;

    public static class Forecast {
        @SerializedName("dt_txt")
        public String dateTime;

        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weatherList;

        @SerializedName("clouds")
        public Clouds clouds;

        @SerializedName("wind")
        public Wind wind;

        @SerializedName("visibility")
        public int visibility;

        @SerializedName("pop")
        public float pop;

        public static class Main {
            @SerializedName("temp")
            public float temperature;

            @SerializedName("feels_like")
            public float feelsLike;

            @SerializedName("pressure")
            public int pressure;

            @SerializedName("humidity")
            public int humidity;
        }

        public static class Weather {
            @SerializedName("main")
            public String main;

            @SerializedName("description")
            public String description;

            @SerializedName("icon")
            public String icon;
        }

        public static class Clouds {
            @SerializedName("all")
            public int all;
        }

        public static class Wind {
            @SerializedName("speed")
            public float speed;

            @SerializedName("deg")
            public int deg;
        }
    }

    public static class City {
        @SerializedName("name")
        public String name;

        @SerializedName("country")
        public String country;

        @SerializedName("population")
        public int population;

        @SerializedName("timezone")
        public int timezone;

        @SerializedName("sunrise")
        public long sunrise;

        @SerializedName("sunset")
        public long sunset;

        @SerializedName("coord")
        public Coord coord;

        public static class Coord {
            @SerializedName("lat")
            public float lat;

            @SerializedName("lon")
            public float lon;
        }
    }
}

