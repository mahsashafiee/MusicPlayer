package com.example.musicplayer.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Song;


public class SongListActivity extends AppCompatActivity implements ViewHolders.CallBacks {

    private SongListFragment mFragment;
    private static final String EXTRA_STRING = "albumName";


    public static Intent newIntent(Context target , String albumName){
        Intent intent = new Intent(target,SongListActivity.class);
        intent.putExtra(EXTRA_STRING,albumName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mFragment = SongListFragment.newInstance(getIntent().getStringExtra(EXTRA_STRING));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }

    }

    @Override
    public void SingleSong(Song song) {
        startActivity(SingleSongActivity.newIntent(SongListActivity.this, song));
    }

    @Override
    public void SongList(String albumName) {

    }
}
