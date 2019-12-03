package com.example.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PlayerManager {

    private String TAG = "PlayerManager";

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private List<Song> mPlayList;
    private Song mSong;
    private int currentSong;
    private boolean mListLoop;
    private boolean mShuffle;
    private boolean isPaused;
    private boolean isStop;
    private UIController mUIObj;
    private static PlayerManager Instance;

    private PlayerManager(Context context) {
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

    public void setUIobj(Object view) {
        mUIObj = (UIController) view;
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

        mUIObj.ViewUpdater();
    }

    private void OnCompletionListener() {
        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (currentSong == mPlayList.size() - 1 && !mListLoop) {
                Stop();
                isStop = true;
                mUIObj.ViewUpdater();
                return;
            }

            //List loop handler
            if (!mListLoop) {
                currentSong++;
            } else {
                currentSong = (currentSong + 1) % mPlayList.size();
            }

            //plays the song that is referred by "currentSong"
            songPlayer(mPlayList.get(currentSong));

            mUIObj.ViewUpdater();
        });
    }

    public Song getCurrentSong() {
        return mSong;
    }

    private void Play(Uri songPath) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, songPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            isStop = false;
        } catch (IOException e) {
            Log.d(TAG, "Play: "+e.getMessage());
        }
    }

    public void Pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPaused = true;
        } else if (!mMediaPlayer.isPlaying()) {
            if (isStop) {
                goForward();
                isPaused = false;
                return;
            }
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

    public void ListLoop() {
        mListLoop = !mListLoop;
    }

    public void Shuffle() {
        mShuffle = !mShuffle;

        if (mShuffle)
            Collections.shuffle(mPlayList);
        else
            mPlayList = PlayList.getSongList();

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
        songPlayer(mPlayList.get((currentSong + 1) % mPlayList.size()));
    }

    public void goBackward() {
        songPlayer(mPlayList.get((currentSong - 1 + mPlayList.size()) % mPlayList.size()));
    }

    public boolean isShuffle() {
        return mShuffle;
    }

    public boolean isListLoop() {
        return mListLoop;
    }

    interface UIController {
        void ViewUpdater();
    }

}
