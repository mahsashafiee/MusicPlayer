package com.example.musicplayer.controller;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.R;
import com.example.musicplayer.SharedPreferences.MusicPreferences;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {


    static final String PLAY_PAUSE_ACTION = "com.example.musicplayer.PLAYPAUSE";
    static final String NEXT_ACTION = "com.example.musicplayer.NEXT";
    static final String PREV_ACTION = "com.example.musicplayer.PREV";
    private static final int REQUEST_CODE = 1;
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
    private MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>();
    private MutableLiveData<Boolean> mListLoop = new MutableLiveData<>();
    private MutableLiveData<Boolean> mShuffle = new MutableLiveData<>();
    private MutableLiveData<Song> mLiveSong = new MutableLiveData<>();
    private MutableLiveData<Integer> mSongPosition;
    private int mCurrentSongIndex;
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
        mMediaPlayer.setOnCompletionListener(this);
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        mShuffle.setValue(MusicPreferences.getIsShuffle(this));
        mListLoop.setValue(MusicPreferences.getIsListLoop(this));
        mIsPlaying.setValue(false);
        singleLoop(!mListLoop.getValue());
        mSongPosition = SongRepository.getInstance(this).getSongPosition();
    }

    private void setPlayList() {
        mPlayList = new ArrayList<>(PlayList.getSongList());
        if (mShuffle.getValue())
            Collections.shuffle(mPlayList);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        if (mCurrentSongIndex == mPlayList.size() - 1 && !mListLoop.getValue()) {
            Stop();
            isStop = true;
            mLiveSong.setValue(null);
            return;
        }
        //List loop handler
        if (!mListLoop.getValue()) {
            mCurrentSongIndex++;
        } else {
            mCurrentSongIndex = (mCurrentSongIndex + 1) % mPlayList.size();
        }

        //plays the song that is referred by "mCurrentSongIndex"
        songPlayer(mPlayList.get(mCurrentSongIndex));

        mSongPosition.setValue(mCurrentSongIndex);
    }

    private void Play(Song song) {

        //check if it's first time using
        if (mSong == null)
            songPlayer(song);

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
        mIsPlaying.setValue(true);
    }

    private void songPlayer(Song song) {

        mSong = song;
        mCurrentSongIndex = mPlayList.indexOf(song);
        Play(song.getPath());
        MusicPreferences.setLastMusic(this, mSong.getSongId());

        //observe in single song fragment
        mLiveSong.setValue(song);
        mSongPosition.setValue(mCurrentSongIndex);
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
                mIsPlaying.setValue(!isPaused);
                return;
            }
            mMediaPlayer.start();
            isPaused = false;
        }
        mIsPlaying.setValue(!isPaused);
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

    public void singleLoop(boolean loop) {
        mMediaPlayer.setLooping(loop);
    }

    public void listLoop() {
        singleLoop(mListLoop.getValue());
        mListLoop.setValue(!mListLoop.getValue());
        MusicPreferences.setMusicIsListLoop(this, mListLoop.getValue());
    }

    public void shuffle() {
        mShuffle.setValue(!mShuffle.getValue());

        if (mShuffle.getValue())
            Collections.shuffle(mPlayList);
        else {
            mPlayList = PlayList.getSongList();
            mCurrentSongIndex = mPlayList.indexOf(mSong);
        }

        mSongPosition.setValue(mCurrentSongIndex);
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
    }

    public void goBackward() {
        songPlayer(mPlayList.get((mCurrentSongIndex - 1 + mPlayList.size()) % mPlayList.size()));
    }

    public boolean isShuffle() {
        return mShuffle.getValue();
    }

    public boolean isListLoop() {
        return mListLoop.getValue();
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

                else if (!mMediaPlayer.isPlaying() && pauseFocus) {
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
                        if (volume <= 1)
                            handler.postDelayed(this, 250);
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

    private Notification getNotification() {
        return new NotificationCompat
                .Builder(this, getString(R.string.notification_channel_id))
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        PENDING_INTENT_REQUEST_CODE,
                        SingleSongActivity.newIntent(this, mSong), PENDING_INTENT_FLAG))
                .build();
    }

    public MutableLiveData<Boolean> getShuffle() {
        return mShuffle;
    }

    public MutableLiveData<Boolean> getListLoop() {
        return mListLoop;
    }

    public MutableLiveData<Boolean> getIsPlaying() {
        return mIsPlaying;
    }
}
