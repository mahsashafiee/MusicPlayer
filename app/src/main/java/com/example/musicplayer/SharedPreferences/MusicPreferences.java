package com.example.musicplayer.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicplayer.R;

public class MusicPreferences {

    private static final String LAST_MUSIC = "last_music";
    private static final String MUSIC_DOMINANT_COLOR = "music_dominant_color";

    public static Long getLastMusic(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getLong(LAST_MUSIC, 0);
    }

    public static int getMusicDominantColor(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(MUSIC_DOMINANT_COLOR, context.getResources().getColor(R.color.default_background));
    }

    public static void setMusicDominantColor(Context context, int dominantColor) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putInt(MUSIC_DOMINANT_COLOR, dominantColor).apply();
    }

    public static void setLastMusic(Context context, Long songId) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putLong(LAST_MUSIC, songId).apply();
    }
}
