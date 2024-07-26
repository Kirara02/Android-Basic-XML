package com.kirara.adzanapp.worker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kirara.adzanapp.R;
import com.kirara.adzanapp.model.PrayerTimeResponse;
import com.kirara.adzanapp.receiver.AdzanReceiver;
import com.kirara.adzanapp.remote.AdzanApi;
import com.kirara.adzanapp.remote.RetrofitClient;
import com.kirara.adzanapp.util.SharedPreferencesHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchPrayerTimesWorker extends Worker {
    private static final String TAG = "FetchPrayerTimesWorker";
    private static final String CHANNEL_ID = "adzan_channel";

    public FetchPrayerTimesWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        double latitude = SharedPreferencesHelper.getLatitude(getApplicationContext());
        double longitude = SharedPreferencesHelper.getLongitude(getApplicationContext());

        if (latitude == 0.0 || longitude == 0.0) {
            return Result.failure();
        }

        fetchPrayerTimes(latitude, longitude);
        return Result.success();
    }

    private void fetchPrayerTimes(double lat, double lon) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        AdzanApi api = RetrofitClient.getClient().create(AdzanApi.class);
        Call<PrayerTimeResponse> call = api.getPrayerTimes(date, lat, lon);

        call.enqueue(new Callback<PrayerTimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<PrayerTimeResponse> call, @NonNull Response<PrayerTimeResponse> response) {
                if (response.isSuccessful()) {
                    PrayerTimeResponse.Timings times = response.body().data.timings;
                    SharedPreferencesHelper.savePrayerTimes(
                            getApplicationContext(),
                            times.fajr,
                            times.dhuhr,
                            times.asr,
                            times.maghrib,
                            times.isha
                    );
                    updateAlarms(times);
                    sendUpdateNotification(times);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PrayerTimeResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void updateAlarms(PrayerTimeResponse.Timings times) {
        Context context = getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AdzanReceiver.class);

        setAlarm(alarmManager, intent, "Subuh", times.fajr);
        setAlarm(alarmManager, intent, "Dzuhur", times.dhuhr);
        setAlarm(alarmManager, intent, "Ashar", times.asr);
        setAlarm(alarmManager, intent, "Maghrib", times.maghrib);
        setAlarm(alarmManager, intent, "Isha", times.isha);
    }

    private void setAlarm(AlarmManager alarmManager, Intent intent, String prayerName, String prayerTime) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                prayerName.hashCode(),
                intent.putExtra("prayer_name", prayerName),
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

    private void sendUpdateNotification(PrayerTimeResponse.Timings times) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Adzan Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.time) // Replace with your icon
                .setContentTitle("Adzan Times Updated")
                .setContentText("New prayer times have been updated. Fajr: " + times.fajr + ", Dhuhr: " + times.dhuhr + ", Asr: " + times.asr + ", Maghrib: " + times.maghrib + ", Isha: " + times.isha)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
