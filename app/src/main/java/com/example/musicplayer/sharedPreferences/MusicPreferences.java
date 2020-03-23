package com.example.musicplayer.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Qualifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicPreferences {

    private static final String LAST_MUSIC = "last_music";
    private static final String MUSIC_DOMINANT_COLOR = "music_dominant_color";
    private static final String MUSIC_IS_SHUFFLE_ON = "music_is_shuffle_on";
    private static final String MUSIC_IS_LIST_LOOP = "music_is_list_loop";
    private static final String MUSIC_IS_SINGLE_LOOP = "music_is_single_loop";
    public static final String LAST_LIST = "last_list";
    public static final String MUSIC_POSITION = "music_position";

    public static Long getLastMusic(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getLong(LAST_MUSIC, 0);
    }

    public static void setLastMusic(Context context, Long songId) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putLong(LAST_MUSIC, songId).apply();
    }

    public static Boolean getIsShuffle(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(MUSIC_IS_SHUFFLE_ON, false);
    }

    public static void setMusicIsShuffleOn(Context context, Boolean isShuffle) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MUSIC_IS_SHUFFLE_ON, isShuffle).apply();
    }

    public static Boolean getIsListLoop(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(MUSIC_IS_LIST_LOOP, false);
    }

    public static void setMusicIsListLoop(Context context, Boolean isLoop) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MUSIC_IS_LIST_LOOP, isLoop).apply();
    }

    public static Boolean getIsSingleLoop(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(MUSIC_IS_SINGLE_LOOP, false);
    }

    public static void setMusicIsSingleLoop(Context context, Boolean isLoop) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MUSIC_IS_SINGLE_LOOP, isLoop).apply();
    }

    public static int getMusicDominantColor(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(MUSIC_DOMINANT_COLOR, context.getResources().getColor(R.color.default_background));
    }

    public static void setMusicDominantColor(Context context, int dominantColor) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putInt(MUSIC_DOMINANT_COLOR, dominantColor).apply();
    }

    public static void setLastList(Context context, Qualifier qualifier, String name){
        Set<String> strings = new HashSet<>();
        strings.add(qualifier.toString());
        strings.add(name);
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putStringSet(LAST_LIST, strings).apply();
    }

    public static List getLastList(Context context) throws NullPointerException{
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return Arrays.asList(prefs.getStringSet(LAST_LIST, null).toArray());
    }

    public static void setMusicPosition(Context context, int position){
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putInt(MUSIC_POSITION, position).apply();
    }

    public static int getMusicPosition(Context context){
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(MUSIC_POSITION,0);
    }
}
