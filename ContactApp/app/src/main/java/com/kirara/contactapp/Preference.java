package com.kirara.contactapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;

public class Preference extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if(key.equals(Settings.PREF_THEME)) {
            String theme = sharedPreferences.getString(key, Settings.ARRAY_THEME_LIGHT);
            Settings.getInstance(this).applyTheme();
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}