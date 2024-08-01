//package com.kirara.backgroundlocation;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ServiceInfo;
//import android.location.Location;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class LocationBackgroundService extends Service {
//
//    private static final String CHANNEL_ID = "LocationServiceChannel";
//    private static final long TIMER_INTERVAL = 2 * 60 * 1000; // 15 minutes
//
//    private FusedLocationProviderClient fusedLocationClient;
//    private LocationCallback locationCallback;
//    private Timer timer;
//    private TimerTask timerTask;
//    private ApiService apiService;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        apiService = ApiService.create();
//        initLocationCallback();
//        startTimer();
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Notification notification = buildNotification();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
//        } else {
//            startForeground(1, notification);
//        }
//        return START_STICKY;
//    }
//
//    private void uploadLocationToServer(User user) {
//        Call<Void> call = apiService.update(user);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d("LocationService", "Location uploaded successfully.");
//                } else {
//                    Log.e("LocationService", "Error uploading location: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e("LocationService", "Failed to upload location.", t);
//            }
//        });
//    }
//
//
//    private void initLocationCallback() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                Location location = locationResult.getLastLocation();
//                Log.d(LocationBackgroundService.class.getName(), "lat: " + location.getLatitude() + " lon: " + location.getLongitude());
//
//                User user = new User(location.getLatitude(), location.getLongitude());
//                uploadLocationToServer(user);
//            }
//        };
//    }
//
//    private void startTimer() {
//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                LocationRequest locationRequest = LocationRequest.create()
//                        .setInterval(TIMER_INTERVAL)
//                        .setFastestInterval(TIMER_INTERVAL)
//                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//            }
//        };
//        timer.scheduleAtFixedRate(timerTask, 0, TIMER_INTERVAL);
//    }
//
//    private Notification buildNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Location Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Location Service")
//                .setContentText("Tracking location in the background")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        return builder.build();
//    }
//}


package com.kirara.backgroundlocation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationBackgroundService extends Service {

    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final long TIMER_INTERVAL = 2 * 60 * 1000; // 2 minutes
    private static final long WAKELOCK_TIMEOUT = 10 * 60 * 1000; // 10 minutes

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Timer timer;
    private TimerTask timerTask;
    private ApiService apiService;

    private Handler handler;
    private Runnable locationRequestRunnable;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = ApiService.create();
        createNotificationChannel();
        initLocationCallback();
        startForegroundServiceWithNotification();
        startLocationRequests();

        // Initialize WakeLock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire(WAKELOCK_TIMEOUT);
    }

    private void startLocationRequests() {
        handler = new Handler(Looper.getMainLooper());
        locationRequestRunnable = new Runnable() {
            @Override
            public void run() {

                LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TIMER_INTERVAL)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(TIMER_INTERVAL)
                        .setMaxUpdateDelayMillis(TIMER_INTERVAL)
                        .build();

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                handler.postDelayed(this, TIMER_INTERVAL);
            }
        };
        handler.post(locationRequestRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && locationRequestRunnable != null) {
            handler.removeCallbacks(locationRequestRunnable);
        }
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundServiceWithNotification();
        return START_STICKY;
    }



    private void uploadLocationToServer(User user) {
        Call<Void> call = apiService.update(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("LocationService", "Location uploaded successfully.");
                } else {
                    Log.e("LocationService", "Error uploading location: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LocationService", "Failed to upload location.", t);
            }
        });
    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                Log.d(LocationBackgroundService.class.getName(), "lat: " + location.getLatitude() + " lon: " + location.getLongitude());

                User user = new User(location.getLatitude(), location.getLongitude());
                uploadLocationToServer(user);
            }
        };
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startForegroundServiceWithNotification() {
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        } else {
            startForeground(1, notification);
        }
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Tracking location in the background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        return builder.build();
    }
}
