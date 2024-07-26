package com.kirara.adzanapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.kirara.adzanapp.databinding.ActivityAdzanBinding;
import com.kirara.adzanapp.model.PrayerTimeResponse;
import com.kirara.adzanapp.receiver.AdzanReceiver;
import com.kirara.adzanapp.remote.AdzanApi;
import com.kirara.adzanapp.remote.RetrofitClient;
import com.kirara.adzanapp.util.SharedPreferencesHelper;
import com.kirara.adzanapp.worker.FetchPrayerTimesWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdzanActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 1000;
    private TextView subuh, dzuhur, ashar, magrib, isha;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adzan);

        subuh = findViewById(R.id.subuh);
        dzuhur = findViewById(R.id.dzuhur);
        ashar = findViewById(R.id.ashar);
        magrib = findViewById(R.id.magrib);
        isha = findViewById(R.id.isha);

        createNotificationChannel();

        scheduleDailyPrayerTimesFetch();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        displayPrayerTimes();
        getDeviceLocation();
    }

    private void scheduleDailyPrayerTimesFetch() {
        PeriodicWorkRequest prayerTimesFetchRequest =
                new PeriodicWorkRequest.Builder(FetchPrayerTimesWorker.class, 1, TimeUnit.DAYS)
                        .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "PrayerTimesFetchWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                prayerTimesFetchRequest
        );
    }

    private long calculateInitialDelay() {
        // Hitung delay awal agar pekerjaan berjalan tepat pada pukul 00:00
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String targetTime = "00:00";
        try {
            Date targetDate = dateFormat.parse(targetTime);
            targetDate.setDate(now.getDate());
            targetDate.setMonth(now.getMonth());
            targetDate.setYear(now.getYear());
            if (now.after(targetDate)) {
                // Tambahkan satu hari jika sekarang sudah lewat target waktu
                targetDate.setDate(targetDate.getDate() + 1);
            }
            return targetDate.getTime() - now.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void getDeviceLocation() {
        List<String> permissions = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), LOCATION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                SharedPreferencesHelper.saveLocation(this, latitude, longitude);
                fetchPrayerTimes(latitude, longitude);
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPrayerTimes() {
        String fajr = SharedPreferencesHelper.getFajr(this);
        String dhuhr = SharedPreferencesHelper.getDhuhr(this);
        String asr = SharedPreferencesHelper.getAsr(this);
        String maghrib = SharedPreferencesHelper.getMaghrib(this);
        String isya = SharedPreferencesHelper.getIsha(this);

        subuh.setText(fajr);
        dzuhur.setText(dhuhr);
        ashar.setText(asr);
        magrib.setText(maghrib);
        isha.setText(isya);
    }

    private String getDay(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        return dateFormat.format(today);
    }

    private void fetchPrayerTimes(double lat, double lon){
        String date = getDay();
        AdzanApi api = RetrofitClient.getClient().create(AdzanApi.class);
        Call<PrayerTimeResponse> call = api.getPrayerTimes(date,lat,lon);

        call.enqueue(new Callback<PrayerTimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<PrayerTimeResponse> call, @NonNull Response<PrayerTimeResponse> response) {
                if (response.isSuccessful()) {
                    PrayerTimeResponse.Timings times = response.body().data.timings;
                    SharedPreferencesHelper.savePrayerTimes(
                            AdzanActivity.this,
                            times.fajr,
                            times.dhuhr,
                            times.asr,
                            times.maghrib,
                            times.isha
                    );
                    displayPrayerTimes();
                    setAlarms(times);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PrayerTimeResponse> call, @NonNull Throwable t) {
                Log.e(AdzanActivity.class.getName(), "onFailure: "+t.getMessage());
            }
        });
    }

    private void setAlarms(PrayerTimeResponse.Timings times) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AdzanReceiver.class);

        setAlarm(alarmManager, intent, "Subuh time", times.fajr);
        setAlarm(alarmManager, intent, "Dzuhur time", times.dhuhr);
        setAlarm(alarmManager, intent, "Ashar time", times.asr);
        setAlarm(alarmManager, intent, "Maghrib time", times.maghrib);
        setAlarm(alarmManager, intent, "Isha time", times.isha);
    }

    private void setAlarm(AlarmManager alarmManager, Intent intent, String prayerTimeKey, String prayerTime) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                prayerTimeKey.hashCode(),
                intent.putExtra("prayer_name", prayerTimeKey),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long prayerTimeInMillis = convertTimeToMillis(prayerTime);
        if (prayerTimeInMillis != -1) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, prayerTimeInMillis, pendingIntent);
        }
    }

    private long convertTimeToMillis(String prayerTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date date = sdf.parse(prayerTime);
            // Set date to today
            Date today = new Date();
            date.setYear(today.getYear());
            date.setMonth(today.getMonth());
            date.setDate(today.getDate());
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                Log.d(AdzanActivity.class.getName(), "onRequestPermissionsResult: "+grantResult);
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Adzan Channel";
            String description = "Channel for Adzan notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("adzan_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
