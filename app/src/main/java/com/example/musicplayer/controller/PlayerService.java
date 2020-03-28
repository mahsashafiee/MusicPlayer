package com.example.musicplayer.controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.sharedPreferences.MusicPreferences;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IBinder iBinder = new LocalBinder();

    private String TAG = "PlayerService";
    private static final String SONG_EXTRA = "song";
    private final int SKIP_TIME = 3000;
    private float volume;
    private boolean pauseFocus = false;

    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private AudioManager mAudioManager;
    private List<Song> mPlayList;
    private Song mSong;
    private int mCurrentSongIndex;
    private int newPosition;
    private MusicNotificationManager mNotificationManager;

    private MutableLiveData<Boolean> mListLoop = new MutableLiveData<>();
    private MutableLiveData<Boolean> mSingleLoop = new MutableLiveData<>();
    private MutableLiveData<Boolean> mShuffle = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPaused = new MutableLiveData<>();
    private boolean isStop = true;

    BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer.isPlaying())
                Pause();
        }
    };

    public static Intent newIntent(Context context, Song song) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(SONG_EXTRA, song);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setLiveDatas();
        initMediaPlayer();
        mNotificationManager = new MusicNotificationManager(this);
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    private void initMediaPlayer() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    public void setPlayList() {
        mPlayList = new ArrayList<>(PlayList.getSongList());
        if (mShuffle.getValue())
            Collections.shuffle(mPlayList);
    }

    public void setIndex(int songIndex) {
        setPlayList();
        mCurrentSongIndex = songIndex;
    }

    private void setLiveDatas() {

        mShuffle.setValue(MusicPreferences.getIsShuffle(this));
        if (mShuffle == null)
            mShuffle.setValue(false);
        mListLoop.setValue(MusicPreferences.getIsListLoop(this));
        if (mListLoop == null)
            mListLoop.setValue(false);
        mSingleLoop.setValue(MusicPreferences.getIsSingleLoop(this));
        if (mSingleLoop == null)
            mSingleLoop.setValue(false);
        isPaused.setValue(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setPlayList();
        if (mMediaPlayer == null)
            initMediaPlayer();
        if (!requestAudioFocus())
            stopSelf();
        if (mShuffle.getValue())
            shuffle();
        //what should happen after
        if (mCompletionListener == null) {
            mCompletionListener = this::onCompletion;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
        }
        Play((Song) intent.getParcelableExtra(SONG_EXTRA));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mNotificationManager.registerNotificationActionsReceiver(true);
        return iBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mCurrentSongIndex == mPlayList.size() - 1 && !mSingleLoop.getValue() && !mListLoop.getValue()) {
            Stop();
            isPaused.setValue(true);
            return;
        }
        //loop handler
        if (mListLoop.getValue() && !mSingleLoop.getValue())
            mCurrentSongIndex = (mCurrentSongIndex + 1) % mPlayList.size();

        else if (!mListLoop.getValue() && !mSingleLoop.getValue())
            mCurrentSongIndex++;

        //plays the song that is referred by "mCurrentSongIndex"
        songPlayer(mPlayList.get(mCurrentSongIndex));
    }

    private void Play(Song song) {

        if (song == null)
            return;

        //check if it's first time using
        if (mSong == null)
            songPlayer(song);

            //check if the current song is paused
        else if (!mMediaPlayer.isPlaying() && mSong.equals(song)) {
            if (isPaused.getValue())
                Pause();
            else
                songPlayer(song);
        }

        //check if different song has came
        else if (!mSong.equals(song))
            songPlayer(song);
    }

    private void songPlayer(Song song) {

        MediaPlay(song);
        mSong = song;
        mCurrentSongIndex = mPlayList.indexOf(song);

        MusicPreferences.setLastMusic(this, mSong.getSongId());

        //observe in single song fragment
        PlayList.getLiveSong().setValue(song);

        startForeground(1, mNotificationManager.createNotification());
    }

    private void MediaPlay(Song song) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, song.getPath());
            mMediaPlayer.prepare();
            if(mSong == null && song.getSongId().equals(MusicPreferences.getLastMusic(this)))
                Seek(MusicPreferences.getMusicPosition(this));
            mMediaPlayer.start();
            isStop = false;
            isPaused.setValue(false);
        } catch (IOException e) {
            Log.d(TAG, "Play: " + e.getMessage());
        }
    }

    public void Pause() {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPaused.setValue(true);
        } else if (!mMediaPlayer.isPlaying()) {
            if (isStop) {
                goForward();
                isPaused.setValue(false);
                return;
            }
            mMediaPlayer.start();
            isPaused.setValue(false);
        }

        mNotificationManager.updateActions();
    }

    public void Stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        isStop = true;
        mNotificationManager.updateActions();
    }

    public void Seek(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public void Release() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.release();
        mMediaPlayer = null;
        removeAudioFocus();
    }

    public void singleLoop() {
        mSingleLoop.setValue(!mSingleLoop.getValue());
        MusicPreferences.setMusicIsSingleLoop(this, mSingleLoop.getValue());
    }

    public void listLoop() {
        mListLoop.setValue(!mListLoop.getValue());
        MusicPreferences.setMusicIsListLoop(this, mListLoop.getValue());
    }

    public void shuffle() {
        mShuffle.setValue(!mShuffle.getValue());

        if (mShuffle.getValue()) {
            Collections.shuffle(mPlayList);
            mCurrentSongIndex = mPlayList.indexOf(mSong);
            mPlayList.set(mCurrentSongIndex, mPlayList.get(0));
            mPlayList.set(0, mSong);
        } else {
            mPlayList = PlayList.getSongList();
            mCurrentSongIndex = mPlayList.indexOf(mSong);
        }

        MusicPreferences.setMusicIsShuffleOn(this, mShuffle.getValue());
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
        songPlayer(mPlayList.get((mCurrentSongIndex + 1) % mPlayList.size()));
        mNotificationManager.updateNotification();
    }

    public void goBackward() {
        songPlayer(mPlayList.get((mCurrentSongIndex - 1 + mPlayList.size()) % mPlayList.size()));
        mNotificationManager.updateNotification();
    }

    public LiveData<Boolean> isShuffle() {
        return mShuffle;
    }

    public LiveData<Boolean> isListLoop() {
        return mListLoop;
    }

    public LiveData<Boolean> isSingleLoop() {
        return mSingleLoop;
    }

    public LiveData<Boolean> isPaused() {
        return isPaused;
    }

    public Boolean isStop() {
        return isStop;
    }

    public int onFastForward() {
        if (getCurrentPosition() == getDuration())
            newPosition = 0;
        newPosition = mMediaPlayer.getCurrentPosition() + SKIP_TIME;
        Seek(newPosition);
        return newPosition;
    }

    public int onFastBackward() {
        if (getCurrentPosition() == 0)
            newPosition = 0;
        newPosition = mMediaPlayer.getCurrentPosition() - SKIP_TIME;
        Seek(newPosition);
        return newPosition;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Release();
        unregisterReceiver(becomingNoisyReceiver);
        mNotificationManager.registerNotificationActionsReceiver(false);
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mMediaPlayer == null) initMediaPlayer();

                else if (!mMediaPlayer.isPlaying() && pauseFocus) {
                    volume = 0f;
                    mMediaPlayer.setVolume(volume, volume);
                    Pause();
                    pauseFocus = false;
                }

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMediaPlayer.setVolume(volume, volume);
                        volume += 0.1;
                        if (volume <= 1)
                            handler.postDelayed(this::run, 250);
                    }
                });
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Release();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mMediaPlayer.isPlaying()) {
                    pauseFocus = true;
                    Pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mMediaPlayer.isPlaying()) {
                    volume = 0.1f;
                    mMediaPlayer.setVolume(volume, volume);
                }
                break;
        }
    }

    private boolean requestAudioFocus() {
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this);
    }
}
