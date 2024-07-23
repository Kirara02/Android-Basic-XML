package com.kirara.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.kirara.weatherapp.model.ForecastResponse;
import com.kirara.weatherapp.model.WeatherResponse;
import com.kirara.weatherapp.remote.RetrofitClient;
import com.kirara.weatherapp.remote.WeatherApi;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1000;
    private MaterialTextView textViewCity;
    private MaterialTextView textViewTemperature;
    private MaterialTextView textViewDescription;
    private MaterialTextView textViewHumidity;
    private MaterialTextView textViewPressure;
    private MaterialTextView textViewWindSpeed;
    private MaterialTextView textViewVisibility;
    private MaterialTextView textViewSunrise;
    private MaterialTextView textViewSunset;
    private MaterialTextView textViewForecast;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCity = findViewById(R.id.textViewCity);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewHumidity = findViewById(R.id.textViewHumidity);
        textViewPressure = findViewById(R.id.textViewPressure);
        textViewWindSpeed = findViewById(R.id.textViewWindSpeed);
        textViewVisibility = findViewById(R.id.textViewVisibility);
        textViewSunrise = findViewById(R.id.textViewSunrise);
        textViewSunset = findViewById(R.id.textViewSunset);
        MaterialButton buttonRefresh = findViewById(R.id.buttonRefresh);
        textViewForecast = findViewById(R.id.textViewForecast);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        buttonRefresh.setOnClickListener(v -> getDeviceLocation());

        getDeviceLocation();
    }

    private void getDeviceLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                getCurrentWeather(location.getLatitude(), location.getLongitude());
                getWeatherForecast(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentWeather(double lat, double lon) {
        WeatherApi weatherApi = RetrofitClient.getClient(Constant.BASE_URL).create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(Constant.API_KEY,lat, lon);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String cityName = weatherResponse.name;
                    String country = weatherResponse.sys.country;
                    double temperatureKelvin = weatherResponse.main.temp;
                    double temperatureCelsius = temperatureKelvin - 273.15;
                    String description = weatherResponse.weatherList.get(0).description;

                    // Format sunrise and sunset times
                    String sunrise = formatTime(weatherResponse.sys.sunrise);
                    String sunset = formatTime(weatherResponse.sys.sunset);

                    textViewCity.setText(String.format("%s, %s", cityName, country));
                    textViewTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", temperatureCelsius));
                    textViewDescription.setText(description);
                    textViewHumidity.setText(String.format(Locale.getDefault(), "Humidity: %d%%", weatherResponse.main.humidity));
                    textViewPressure.setText(String.format(Locale.getDefault(), "Pressure: %d hPa", weatherResponse.main.pressure));
                    textViewWindSpeed.setText(String.format(Locale.getDefault(), "Wind Speed: %.1f m/s", weatherResponse.wind.speed));
                    textViewVisibility.setText(String.format(Locale.getDefault(), "Visibility: %d m", weatherResponse.visibility));
                    textViewSunrise.setText(String.format(Locale.getDefault(), "Sunrise: %s", sunrise));
                    textViewSunset.setText(String.format(Locale.getDefault(), "Sunset: %s", sunset));
                } else {
                    Toast.makeText(MainActivity.this, "Failed to get weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call,@NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherForecast(double lat, double lon) {
        WeatherApi weatherApi = RetrofitClient.getClient(Constant.BASE_URL).create(WeatherApi.class);
        Call<ForecastResponse> call = weatherApi.getForecastWeather(Constant.API_KEY, lat, lon);
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForecastResponse> call, @NonNull Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    ForecastResponse forecastResponse = response.body();
//                    StringBuilder forecastStringBuilder = new StringBuilder();
//
//                    for (ForecastResponse.Forecast forecast : forecastResponse.forecastList) {
//                        String dateTime = forecast.dateTime;
//                        double temperatureKelvin = forecast.main.temperature;
//                        double temperatureCelsius = temperatureKelvin - 273.15;
//                        String description = forecast.weatherList.get(0).description;
//
//                        forecastStringBuilder.append(String.format(Locale.getDefault(), "%s: %.1f°C, %s\n", dateTime, temperatureCelsius, description));
//                    }

                    ForecastResponse forecastResponse = response.body();
                    Map<String, StringBuilder> forecastMap = new HashMap<>();

                    for (ForecastResponse.Forecast forecast : forecastResponse.forecastList) {
                        String dateTime = forecast.dateTime;
                        String date = dateTime.split(" ")[0];
                        String time = dateTime.split(" ")[1];
                        double temperatureKelvin = forecast.main.temperature;
                        double temperatureCelsius = temperatureKelvin - 273.15;
                        String description = forecast.weatherList.get(0).description;

                        if (!forecastMap.containsKey(date)) {
                            forecastMap.put(date, new StringBuilder());
                        }

                        forecastMap.get(date).append(String.format(Locale.getDefault(), "%s: %.1f°C, %s\n", time, temperatureCelsius, description));
                    }

                    StringBuilder forecastStringBuilder = new StringBuilder();
                    for (Map.Entry<String, StringBuilder> entry : forecastMap.entrySet()) {
                        forecastStringBuilder.append(String.format(Locale.getDefault(), "%s\n%s", entry.getKey(), entry.getValue().toString()));
                    }

                    textViewForecast.setText(forecastStringBuilder.toString());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to get forecast data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to get forecast data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(long timestamp) {
        // Convert timestamp to readable time format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp * 1000));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem switchItem = menu.findItem(R.id.action_switch_theme);
        SwitchCompat switchCompat = (SwitchCompat) switchItem.getActionView();

        if (switchCompat != null) {
            switchCompat.setChecked(isNightModeEnabled());


            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveThemeState(true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveThemeState(false);
                }
            });
        }

        return true;
    }

    private void saveThemeState(boolean isNightMode) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.THEME_KEY, isNightMode);
        editor.apply();
    }

    private boolean isNightModeEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constant.THEME_KEY, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}