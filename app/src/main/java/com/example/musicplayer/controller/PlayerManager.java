package com.example.musicplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;

import com.example.musicplayer.model.Song;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PlayerManager {

    private MediaPlayer songPlayer;
    private Context mContext;
    private List<Song> mPlayList;
    private int currentSong;
    private boolean mListLoop;
    private boolean mShuffle;

    public PlayerManager(Context context) {
        mContext = context;
        songPlayer = new MediaPlayer();
        songPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void Play(List<Song> songs, int position) {

        mPlayList = songs;
        currentSong = position;

        //clicked song will be played
        Play(songs.get(position).getPath());

        //what should happen after that
        songPlayer.setOnCompletionListener(mediaPlayer -> {
            if (currentSong == mPlayList.size()-1) {
                Stop();
                return;
            }

            //Shuffle List handler
            if (mShuffle) {
                Collections.shuffle(mPlayList);
            } else {
                mPlayList = songs;
            }

            //List loop handler
            if (!mListLoop) {
                currentSong++;
            } else {
                currentSong = (currentSong + 1) % songs.size();
            }

            //plays the song that is referred by "currentSong"
            Play(mPlayList.get(currentSong).getPath());
        });

    }

    public void Play(Uri songPath) {
        try {
            songPlayer.reset();
            songPlayer.setDataSource(mContext, songPath);
            songPlayer.prepare();
            songPlayer.start();

        } catch (IOException e) {
            return;
        }
    }

    public void Pause() {
        if (songPlayer.isPlaying())
            songPlayer.pause();
        else
            songPlayer.start();
    }

    public void Stop() {
        songPlayer.stop();
        songPlayer.reset();
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
}
