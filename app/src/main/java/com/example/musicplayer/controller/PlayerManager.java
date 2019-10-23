package com.example.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class PlayerManager {

    private MediaPlayer songPlayer;
    private Context mContext;

    public PlayerManager(Context context){
        mContext = context;
        songPlayer = new MediaPlayer();
        songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void Play(String songPath){
        try{
            songPlayer.reset();
            songPlayer.setDataSource(mContext, Uri.parse(songPath));
            songPlayer.prepare();
            songPlayer.start();

        }catch (IOException e){
            return;
        }
    }

    public void Pause(){
        songPlayer.pause();
    }

    public void Stop(){
        songPlayer.stop();
        songPlayer.reset();
    }

    public void Seek(int msec){
        songPlayer.seekTo(msec);
    }

    public void Release(){
        songPlayer.release();
        songPlayer = null;
    }
}