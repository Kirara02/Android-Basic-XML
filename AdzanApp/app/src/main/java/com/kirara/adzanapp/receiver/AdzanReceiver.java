package com.kirara.adzanapp.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.kirara.adzanapp.R;

public class AdzanReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "adzan_channel")
                .setSmallIcon(R.drawable.time)
                .setContentTitle("Adzan Reminder")
                .setContentText("It's time for " + prayerName + " prayer!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
