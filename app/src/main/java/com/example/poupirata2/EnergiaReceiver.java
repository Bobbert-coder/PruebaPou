package com.example.poupirata2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class EnergiaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "canal_energia")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Energía al máximo ⚡")
                        .setContentText("Tu mascota ya terminó de dormir.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(10, builder.build());
    }
}