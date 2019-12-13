package com.example.musicplayer.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MusicPreferences {

    private static final String LAST_MUSIC = "last_music";

    public static Long getLastMusic(Context context){
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return prefs.getLong(LAST_MUSIC,0);
    }

    public static void setLastMusic(Context context,Long songId){
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        prefs.edit().putLong(LAST_MUSIC,songId).apply();
    }
}
