package com.example.musicplayer.controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private String TAG = "PlayerService";
    private static final String SONG_EXTRA = "song";
    private final int SKIP_TIME = 5000;

    private MediaPlayer mMediaPlayer;
    private List<Song> mPlayList;
    private Song mSong;
    private int currentSong;
    private boolean mListLoop;
    private boolean mShuffle;
    private boolean isPaused;
    private boolean isStop;
    private AudioManager mAudioManager;
    private int newPosition;
    private final IBinder iBinder = new LocalBinder();
    private MutableLiveData<Song> mLiveSong = new MutableLiveData<>();

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

    public MutableLiveData<Song> getLiveSong() {
        return mLiveSong;
    }

    private void initMediaPlayer() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //what should happen after
        mMediaPlayer.setOnCompletionListener(this::onCompletion);
    }

    private void setPlayList() {
        mPlayList = PlayList.getSongList();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setPlayList();
        if (mMediaPlayer == null)
            initMediaPlayer();
        if (!requestAudioFocus())
            stopSelf();
        Play((Song) intent.getParcelableExtra(SONG_EXTRA));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (currentSong == mPlayList.size() - 1 && !mListLoop) {
            Stop();
            isStop = true;
            mLiveSong.setValue(null);
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
    }

    private void Play(Song song) {

        if (mSong == null)
            songPlayer(song);

        else if (!mMediaPlayer.isPlaying() && mSong.equals(song)) {
            if (isPaused)
                Pause();
            else
                songPlayer(song);
        } else if (!mSong.equals(song))
            songPlayer(song);
    }

    private void songPlayer(Song song) {

        mSong = song;
        currentSong = mPlayList.indexOf(song);
        Play(song.getPath());
        //observe in single song fragment
        mLiveSong.setValue(song);
    }

    public Song getCurrentSong() {
        return mSong;
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
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) Pause();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mMediaPlayer.isPlaying())
                    Pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            return true;
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this);
    }
}
