package com.kirara.contactapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class Settings {
    public static final String PREF_THEME = "theme";
    public static final String ARRAY_THEME_LIGHT = "lightDark";
    public static final String ARRAY_THEME_DARK = "dark";
    public static final String ARRAY_THEME_SYSTEM = "system";

    private final SharedPreferences preferences;
    public static Settings getInstance(Context context) {
        return new Settings(context);
    }
    private Settings (Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void applyTheme() {
        switch (preferences.getString(PREF_THEME, ARRAY_THEME_SYSTEM)) {
            case ARRAY_THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case ARRAY_THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case ARRAY_THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public int getTheme() {
        switch (preferences.getString(PREF_THEME, ARRAY_THEME_LIGHT)) {
            case ARRAY_THEME_LIGHT: {
                return R.style.AppTheme;
            }
            case ARRAY_THEME_DARK: {
                return R.style.AppTheme_Dark;
            }
        }

        return -1;
    }

}
