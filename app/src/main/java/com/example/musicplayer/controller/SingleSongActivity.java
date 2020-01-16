package com.example.musicplayer.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

public class SingleSongActivity extends AppCompatActivity implements ServiceConnection {

    private static final String SONG_INTENT = "SongID";
    private PlayerService mPlayer;
    private boolean mServiceConction;
    private SingleSongFragment mFragment;
    private Song mSong;
    private Bundle mSavedInstanceState;

    public static Intent newIntent(Context target, Song song) {
        Intent intent = new Intent(target, SingleSongActivity.class);
        intent.putExtra(SONG_INTENT, song);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);
        mSavedInstanceState = savedInstanceState;
        mSong = getIntent().getParcelableExtra(SONG_INTENT);
        Intent intent = new Intent(this, PlayerService.class);
        startService(PlayerService.newIntent(this, mSong));
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
        mPlayer = binder.getService();
        mServiceConction = true;
        mFragment = SingleSongFragment.newInstance(mSong);
        mFragment.setPlayer(mPlayer);

        if (mSavedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mFragment)
                    .commit();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mServiceConction = false;
        mPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }
}
