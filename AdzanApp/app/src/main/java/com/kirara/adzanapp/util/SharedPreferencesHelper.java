package com.kirara.adzanapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "adzan_prefs";
    private static final String KEY_FAJR = "fajr";
    private static final String KEY_DHUHR = "dhuhr";
    private static final String KEY_ASR = "asr";
    private static final String KEY_MAGHRIB = "maghrib";
    private static final String KEY_ISHA = "isha";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public static void savePrayerTimes(Context context, String fajr, String dhuhr, String asr, String maghrib, String isha) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FAJR, fajr);
        editor.putString(KEY_DHUHR, dhuhr);
        editor.putString(KEY_ASR, asr);
        editor.putString(KEY_MAGHRIB, maghrib);
        editor.putString(KEY_ISHA, isha);
        editor.apply();
    }

    public static void saveLocation(Context context, double latitude, double longitude) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_LATITUDE, (float) latitude);
        editor.putFloat(KEY_LONGITUDE, (float) longitude);
        editor.apply();
    }

    public static double getLatitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_LATITUDE, 0.0f);
    }

    public static double getLongitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_LONGITUDE, 0.0f);
    }

    public static String getFajr(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_FAJR, "00:00");
    }

    public static String getDhuhr(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_DHUHR, "00:00");
    }

    public static String getAsr(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ASR, "00:00");
    }

    public static String getMaghrib(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_MAGHRIB, "00:00");
    }

    public static String getIsha(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ISHA, "00:00");
    }
}
