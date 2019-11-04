package com.example.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;

import androidx.fragment.app.Fragment;

import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PlayerManager {

    private MediaPlayer songPlayer;
    private Context mContext;
    private List<Song> mPlayList;
    private int currentSong;
    private Song mSong;
    private boolean mListLoop;
    private boolean mShuffle;
    private static PlayerManager Instance;
    private updateUI update;

    public PlayerManager(Context context , Fragment fragment) {
        mPlayList = PlayList.getSongList();
        mContext = context;
        songPlayer = new MediaPlayer();
        songPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        update = (updateUI) fragment;
    }

    public static PlayerManager getPlayer(Context context , Fragment fragment) {
        if (Instance == null) {
            Instance = new PlayerManager(context,fragment);
        }
        return Instance;
    }

    public void setPlayList(List<Song> songs){
        mPlayList = songs;
    }


    public void Play(Song song) {

        if(mPlayList.get(currentSong).equals(song) && songPlayer.isPlaying())
            return;
        currentSong = mPlayList.indexOf(song);

        //clicked song will be played
        Play(song.getPath());

        //what should happen after that
        OnCompletionListener();

    }

    private void OnCompletionListener (){
        songPlayer.setOnCompletionListener(mediaPlayer -> {
            if (currentSong == mPlayList.size() - 1 && !mShuffle) {
                Stop();
                return;
            }

            //Shuffle List handler
            if (mShuffle) {
                Collections.shuffle(mPlayList);
            } else {
                mPlayList = PlayList.getSongList();
            }

            //List loop handler
            if (!mListLoop) {
                currentSong++;
            } else { currentSong = (currentSong + 1) % mPlayList.size(); }

            //plays the song that is referred by "currentSong"
            Play(mPlayList.get(currentSong).getPath());
        });
    }

    public Song getCurrentSong() {
        mSong = mPlayList.get(currentSong);
        return mSong;
    }

    private void Play(Uri songPath) {
        try {
            songPlayer.reset();
            songPlayer.setDataSource(mContext, songPath);
            songPlayer.prepare();
            songPlayer.start();
            update.Update();

        } catch (IOException e) {
            return;
        }
    }

    public void Pause() {
        if (songPlayer.isPlaying()) {
            songPlayer.pause();
        }
        else
            songPlayer.start();
    }

    public void Stop() {
        songPlayer.stop();
        songPlayer.reset();
        update.Handler();
    }

    public void Seek(int msec) {
        songPlayer.seekTo(msec);
    }

    public void Release() {
        if (songPlayer == null)
            return;
        songPlayer.release();
        songPlayer = null;
    }

    public void SingleLoop(boolean loop) {
        songPlayer.setLooping(loop);
    }

    public void ListLoop(boolean loop) {
        mListLoop = loop;
    }

    public void Shuffle(boolean shuffle) {
        mShuffle = shuffle;
    }

    public int getCurrentPosition() {
        return songPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return songPlayer.getDuration();
    }

    public boolean isPlaying() {
        return songPlayer.isPlaying();
    }

    public void goForward(){
        int after = (currentSong + 1) % mPlayList.size();
        Play(mPlayList.get(after));
    }
    public void goBackward(){
        int before = (currentSong - 1 + mPlayList.size()) % mPlayList.size();
        Play(mPlayList.get(before));
    }

    interface updateUI{
        void Update();
        void Handler();
    }

}
