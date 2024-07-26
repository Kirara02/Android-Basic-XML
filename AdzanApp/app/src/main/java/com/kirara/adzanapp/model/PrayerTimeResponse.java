package com.kirara.adzanapp.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PrayerTimeResponse {
    public Data data;

    public static class Data {
        public Timings timings;

    }

    public static class Timings {
        @SerializedName("Fajr")
        public String fajr;
        @SerializedName("Dhuhr")
        public String dhuhr;
        @SerializedName("Asr")
        public String asr;
        @SerializedName("Maghrib")
        public String maghrib;
        @SerializedName("Isha")
        public String isha;
    }
}
