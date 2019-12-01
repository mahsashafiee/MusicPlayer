package com.example.musicplayer.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

public class SingleSongActivity extends AppCompatActivity{

    public static final String SONG_INTENT = "SongID";

    public static Intent newIntent(Context target, Song song) {
        Intent intent = new Intent(target, SingleSongActivity.class);
        intent.putExtra(SONG_INTENT, song);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_song);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,
                            SingleSongFragment.newInstance( getIntent().getParcelableExtra(SONG_INTENT)))
                    .commit();
        }
    }
}
