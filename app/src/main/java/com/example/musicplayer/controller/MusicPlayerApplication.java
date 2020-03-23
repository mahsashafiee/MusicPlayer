package com.example.musicplayer.controller;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayer.R;

public class MusicPlayerApplication extends Application {

    private ServiceConnection mPlayerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        bindService(new Intent(this, PlayerService.class), mPlayerConnection, Context.BIND_AUTO_CREATE);
        CreateNotificationChannel();
    }

    private void CreateNotificationChannel() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (notificationManagerCompat.getNotificationChannel(getString(R.string.notification_channel_id)) == null) {
            NotificationChannel notificationChannel;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(getString(R.string.notification_channel_id),
                        getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_LOW);

                notificationChannel.setDescription(getString(R.string.app_name));

                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setShowBadge(false);

                notificationManagerCompat.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unbindService(mPlayerConnection);
    }
}