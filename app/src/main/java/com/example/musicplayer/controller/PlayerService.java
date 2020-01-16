package com.example.musicplayer.controller;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.R;
import com.example.musicplayer.SharedPreferences.MusicPreferences;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IBinder iBinder = new LocalBinder();

    private static int PENDING_INTENT_REQUEST_CODE = 0;
    private static int PENDING_INTENT_FLAG = 0;
    private String TAG = "PlayerService";
    private static final String SONG_EXTRA = "song";
    private final int SKIP_TIME = 5000;
    private float volume;
    private boolean pauseFocus = false;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private List<Song> mPlayList;
    private Song mSong;
    private MutableLiveData<Song> mLiveSong = new MutableLiveData<>();
    private int mCurrentSongIndex;
    private boolean mListLoop;
    private boolean mShuffle;
    private boolean isPaused;
    private boolean isStop = true;
    private int newPosition;

    BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Pause();
            mLiveSong.setValue(null);
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
        initMediaPlayer();
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public LiveData<Song> getLiveSong() {
        return mLiveSong;
    }

    private void initMediaPlayer() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //what should happen after
        mMediaPlayer.setOnCompletionListener(this::onCompletion);
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    private void setPlayList() {
        mPlayList = PlayList.getSongList();
        if (mShuffle)
            Collections.shuffle(mPlayList);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setPlayList();
        if (mMediaPlayer == null)
            initMediaPlayer();
        if (!requestAudioFocus())
            stopSelf();
        Play((Song) intent.getParcelableExtra(SONG_EXTRA));
        startForeground(1, getNotification());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mCurrentSongIndex == mPlayList.size() - 1 && !mListLoop) {
            Stop();
            isStop = true;
            mLiveSong.setValue(null);
            return;
        }
        //List loop handler
        if (!mListLoop) {
            mCurrentSongIndex++;
        } else {
            mCurrentSongIndex = (mCurrentSongIndex + 1) % mPlayList.size();
        }

        //plays the song that is referred by "mCurrentSongIndex"
        songPlayer(mPlayList.get(mCurrentSongIndex));
    }

    private void Play(Song song) {

        //check if it's first time using
        if (mSong == null)
            return;

        //check if the current song is paused
        else if (!mMediaPlayer.isPlaying() && mSong.equals(song)) {
            if (isPaused)
                Pause();
            else
                songPlayer(song);
        }

        //check if different song has came
        else if (!mSong.equals(song))
            songPlayer(song);

        MusicPreferences.setLastMusic(this, song.getSongId());
    }

    private void songPlayer(Song song) {

        mSong = song;
        mCurrentSongIndex = mPlayList.indexOf(song);
        Play(song.getPath());
        MusicPreferences.setLastMusic(this,mSong.getSongId());

        //observe in single song fragment
        mLiveSong.setValue(song);
    }

    private void Play(Uri songPath) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, songPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            isStop = false;
        } catch (IOException e) {
            Log.d(TAG, "Play: " + e.getMessage());
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
        removeAudioFocus();
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
        else {
            mPlayList = PlayList.getSongList();
            mCurrentSongIndex = mPlayList.indexOf(mSong);
        }

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
    }

    public void goBackward() {
        songPlayer(mPlayList.get((mCurrentSongIndex - 1 + mPlayList.size()) % mPlayList.size()));
    }

    public boolean isShuffle() {
        return mShuffle;
    }

    public boolean isListLoop() {
        return mListLoop;
    }

    public boolean isStop() {
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
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mMediaPlayer == null) initMediaPlayer();

                else if (!mMediaPlayer.isPlaying() && pauseFocus){
                    volume = 0f;
                    Pause();
                    pauseFocus = false;
                }

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMediaPlayer.setVolume(volume, volume);
                        volume += 0.1;
                        if(volume <= 1)
                            handler.postDelayed(this::run,250);
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

    private Notification getNotification(){
        return new NotificationCompat
                .Builder(this, getString(R.string.notification_channel_id))
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        PENDING_INTENT_REQUEST_CODE,
                        SingleSongActivity.newIntent(this, mSong), PENDING_INTENT_FLAG))
                .build();
    }
}
