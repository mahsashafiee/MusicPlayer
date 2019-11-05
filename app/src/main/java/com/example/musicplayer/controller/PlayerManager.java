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

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private List<Song> mPlayList;
    private int currentSong;
    private Song mSong;
    private boolean mListLoop;
    private boolean mShuffle;
    private static PlayerManager Instance;
    private updateUI update;
    private boolean isPaused;

    public PlayerManager(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //what should happen after
        OnCompletionListener();
        setPlayList();
    }

    public static PlayerManager getPlayer(Context context) {
        if (Instance == null) {
            Instance = new PlayerManager(context);
        }
        return Instance;
    }

    public void setUIobj(Fragment fragment){
        update = (updateUI) fragment;
    }

    public void setPlayList(List<Song> songs) {
        mPlayList = songs;
    }

    private void setPlayList() {
        mPlayList = PlayList.getSongList();
    }


    public void Play(Song song) {

        if (!mMediaPlayer.isPlaying() && mPlayList.get(currentSong).equals(song)) {
            if (isPaused)
                Pause();
            else
                songPlayer(song);
        } else if (!mPlayList.get(currentSong).equals(song)) {

            if (mPlayList.equals(PlayList.getSongList()))
                songPlayer(song);
            else {
                setPlayList();
                songPlayer(song);
            }
        }
    }

    private void songPlayer(Song song) {

        mSong = song;

        currentSong = mPlayList.indexOf(song);

        //clicked song will be played
        Play(song.getPath());

        update.Update();
    }

    private void OnCompletionListener() {
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (currentSong == mPlayList.size() - 1 && !mListLoop) {
                Stop();
                update.Handler();
                update.Update();
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
            } else {
                currentSong = (currentSong + 1) % mPlayList.size();
            }

            mSong = mPlayList.get(currentSong);

            //plays the song that is referred by "currentSong"
            Play(mSong.getPath());

            update.Update();
        });
    }

    public Song getCurrentSong() {
//        mSong = mPlayList.get(currentSong);
        return mSong;
    }

    private void Play(Uri songPath) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, songPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            return;
        }
    }

    public void Pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPaused = true;
        } else {
            mMediaPlayer.start();
            isPaused = false;
        }
    }

    public void Stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public void Seek(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void Release() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void SingleLoop(boolean loop) {
        mMediaPlayer.setLooping(loop);
    }

    public void ListLoop(boolean loop) {
        mListLoop = loop;
    }

    public void Shuffle(boolean shuffle) {
        mShuffle = shuffle;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void goForward() {
        int after = (currentSong + 1) % mPlayList.size();
        Play(mPlayList.get(after));
    }

    public void goBackward() {
        int before = (currentSong - 1 + mPlayList.size()) % mPlayList.size();
        Play(mPlayList.get(before));
    }

    public boolean isShuffle() {
        return mShuffle;
    }

    public boolean isListLoop() {
        return mListLoop;
    }

    interface updateUI {
        void Update();
        void Handler();
    }

}
