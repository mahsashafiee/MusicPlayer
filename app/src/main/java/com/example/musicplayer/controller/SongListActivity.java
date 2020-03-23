package com.example.musicplayer.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.musicplayer.R;
import com.example.musicplayer.sharedPreferences.MusicPreferences;
import com.example.musicplayer.utils.PictureUtils;
import com.example.musicplayer.controller.adapter.SongRecyclerAdapter;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;


public class SongListActivity extends AppCompatActivity implements ViewHolders.CallBacks, SongRecyclerAdapter.CallBacks {

    private SongListFragment mFragment;

    private static final String EXTRA_STRING = "albumArtist";
    private static final String EXTRA_QUALIFIER = "qualifier";


    public static Intent newIntent(Context target, String albumName, Qualifier qualifier) {
        Intent intent = new Intent(target, SongListActivity.class);
        intent.putExtra(EXTRA_STRING, albumName);
        intent.putExtra(EXTRA_QUALIFIER, qualifier);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        PictureUtils.setBackgroundGradient(this, MusicPreferences.getMusicDominantColor(this));

        if (savedInstanceState == null) {
            mFragment = SongListFragment.newInstance(
                    getIntent().getStringExtra(EXTRA_STRING), (Qualifier) getIntent().getSerializableExtra(EXTRA_QUALIFIER));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }

        PictureUtils.setBackgroundGradient(this, MusicPreferences.getMusicDominantColor(this));

    }

    @Override
    public void PlaySong(Song song) {
        MusicPreferences.setLastList(this, (Qualifier) getIntent().getSerializableExtra(EXTRA_QUALIFIER), getIntent().getStringExtra(EXTRA_STRING));
        startActivity(SingleSongActivity.newIntent(SongListActivity.this, song));
        startService(PlayerService.newIntent(this, song));
    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        PictureUtils.setBackgroundGradient(this, MusicPreferences.getMusicDominantColor(this));
    }
}
