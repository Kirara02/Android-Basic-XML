package com.kirara.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.kirara.weatherapp.model.WeatherResponse;
import com.kirara.weatherapp.remote.RetrofitClient;
import com.kirara.weatherapp.remote.WeatherApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final long API_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(10); // 10 minutes
    private TextView place;
    private TextView clock;
    private TextView desc;
    private TextView temp;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView sunrise;
    private TextView sunset;
    private TextView wind;
    private TextView pressure;
    private TextView humidity;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;
    private final Handler handler = new Handler();
    private final Runnable updateClockRunnable = new Runnable() {
        @Override
        public void run() {
            updateClock();
            handler.postDelayed(this, 1000); // Update every second
        }
    };
    private final Runnable apiUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            getDeviceLocation();
            handler.postDelayed(this, API_UPDATE_INTERVAL); // Update every 10 minutes
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        progressBar = findViewById(R.id.loader);
        place = findViewById(R.id.address);
        clock = findViewById(R.id.updated_at);
        desc = findViewById(R.id.status);
        temp = findViewById(R.id.temp);
        minTemp = findViewById(R.id.temp_min);
        maxTemp = findViewById(R.id.temp_max);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        wind = findViewById(R.id.wind);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        handler.post(updateClockRunnable);
        handler.post(apiUpdateRunnable);
        showProgressBar(true);
        getDeviceLocation();

    }

    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        clock.setText(currentTime);
    }

    private void getDeviceLocation() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            showProgressBar(false);
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                getCurrentWeather(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(WeatherActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentWeather(double lat, double lon) {
        WeatherApi weatherApi = RetrofitClient.getClient(Constant.BASE_URL).create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(Constant.API_KEY,lat, lon);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                showProgressBar(false);
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String cityName = weatherResponse.name;
                    String country = weatherResponse.sys.country;
                    double temperatureKelvin = weatherResponse.main.temp;
                    double temperatureCelsius = temperatureKelvin - 273.15;
                    String description = weatherResponse.weatherList.get(0).description;
                    double minTemperatureCelsius = weatherResponse.main.tempMin - 273.15;
                    double maxTemperatureCelsius = weatherResponse.main.tempMax - 273.15;


                    // Format sunrise and sunset times
                    String strSunrise = formatTime(weatherResponse.sys.sunrise);
                    String strSunset = formatTime(weatherResponse.sys.sunset);

                    place.setText(String.format("%s, %s", cityName, country));
                    temp.setText(String.format(Locale.getDefault(), "%.1f°C", temperatureCelsius));
                    maxTemp.setText(String.format(Locale.getDefault(), "%.1f°C", maxTemperatureCelsius));
                    minTemp.setText(String.format(Locale.getDefault(), "%.1f°C", minTemperatureCelsius));
                    desc.setText(description);
                    humidity.setText(String.format(Locale.getDefault(), "Humidity: %d%%", weatherResponse.main.humidity));
                    pressure.setText(String.format(Locale.getDefault(), "Pressure: %d hPa", weatherResponse.main.pressure));
                    wind.setText(String.format(Locale.getDefault(), "Wind Speed: %.1f m/s", weatherResponse.wind.speed));
                    sunrise.setText(String.format(Locale.getDefault(), "Sunrise: %s", strSunrise));
                    sunset.setText(String.format(Locale.getDefault(), "Sunset: %s", strSunset));
                } else {
                    Toast.makeText(WeatherActivity.this, "Failed to get weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call,@NonNull Throwable t) {
                Toast.makeText(WeatherActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(long timestamp) {
        // Convert timestamp to readable time format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp * 1000));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateClockRunnable);
        handler.removeCallbacks(apiUpdateRunnable);
    }
}