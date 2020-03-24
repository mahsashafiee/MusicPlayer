package com.example.musicplayer.controller;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.utils.ID3Tags;
import com.example.musicplayer.utils.Utils;

import org.jaudiotagger.tag.datatype.Artwork;

/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession. This is required so that the music service
 * don't get killed during playback.
 */

class MusicNotificationManager {

    private static final int REQUEST_CODE = 100;

    private static final String ACTION_PLAY_PAUSE = "com.example.android.musicplayer.play_pause";
    private static final String ACTION_NEXT = "com.example.android.musicplayer.next";
    private static final String ACTION_PREV = "com.example.android.musicplayer.prev";

    private PlayerService mService;
    private NotificationManagerCompat mManagerCompat;
    private NotificationActionReceiver mNotificationActionReceiver;
    private Notification mNotification;

    MusicNotificationManager(@NonNull final PlayerService service) {
        mService = service;
        mManagerCompat = NotificationManagerCompat.from(service);
    }

    private PendingIntent playerAction(String action) {

        Intent pauseIntent = new Intent();
        pauseIntent.setPackage(mService.getPackageName());
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(mService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    private NotificationCompat.Action notificationAction(String action) {
        int icon;
        String label;
        switch (action) {
            default:
            case ACTION_PREV:
                icon = R.drawable.ic_skip_previous_black_24dp;
                label = mService.getString(R.string.label_previous);
                break;
            case ACTION_PLAY_PAUSE:
                icon = mService.isPlaying() ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp;
                label = mService.isPlaying() ? mService.getString(R.string.label_pause) : mService.getString(R.string.label_play);
                break;
            case ACTION_NEXT:
                icon = R.drawable.ic_skip_next_black_24dp;
                label = mService.getString(R.string.label_next);
                break;
        }
        return new NotificationCompat.Action.Builder(icon, label, playerAction(action)).build();
    }

    Notification createNotification() {
        Song song = PlayList.getLiveSong().getValue();
        Intent openPlayerIntent = SingleSongActivity.newIntent(mService, song);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mService, REQUEST_CODE,
                openPlayerIntent, 0);

        String artist = song.getArtist();
        String songTitle = song.getTitle();

        Spanned spanned = Utils.buildSpanned(mService.getString(R.string.playing_song, artist, songTitle));

        mNotification = new NotificationCompat.Builder(mService, mService.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.music_notification)
                .setContentTitle(spanned)
                .setContentText(song.getAlbum())
                .setLargeIcon(getLargeIcon(song))
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(ACTION_PREV))
                .addAction(notificationAction(ACTION_PLAY_PAUSE))
                .addAction(notificationAction(ACTION_NEXT))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                .build();

        mManagerCompat.notify(1, mNotification);

        return mNotification;
    }

    void updateNotification() {
        mManagerCompat.notify(1, mNotification);
    }

    private Bitmap getLargeIcon(Song song) {
        Artwork artwork = ID3Tags.getArtwork(song.getFilePath());
        Bitmap bitmap;

        if (artwork == null)
            bitmap = BitmapFactory.decodeResource(mService.getResources(), R.drawable.song_placeholder);
        else
            bitmap = BitmapFactory.decodeByteArray(artwork.getBinaryData(), 0, artwork.getBinaryData().length);
        return bitmap;
    }

    private void registerActionsReceiver() {
        mNotificationActionReceiver = new NotificationActionReceiver();
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MusicNotificationManager.ACTION_PREV);
        intentFilter.addAction(MusicNotificationManager.ACTION_PLAY_PAUSE);
        intentFilter.addAction(MusicNotificationManager.ACTION_NEXT);

        mService.registerReceiver(mNotificationActionReceiver, intentFilter);
    }

    private void unregisterActionsReceiver() {
        if (mService != null && mNotificationActionReceiver != null) {
            try {
                mService.unregisterReceiver(mNotificationActionReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    void registerNotificationActionsReceiver(boolean isReceiver) {

        if (isReceiver) {
            registerActionsReceiver();
        } else {
            unregisterActionsReceiver();
        }
    }

    private class NotificationActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null)
                switch (action) {
                    case ACTION_PLAY_PAUSE:
                        if (mService.isPlaying()) mService.Pause();
                        break;
                    case ACTION_NEXT:
                        mService.goForward();
                        break;
                    case ACTION_PREV:
                        mService.goBackward();
                        break;
                }
        }
    }

}
