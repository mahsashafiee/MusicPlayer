package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

public class MainActivity extends AppCompatActivity implements ViewHolders.CallBacks {

    private CategoryFragment mLunchFrag;
    private static int STORAGE_PERMISSION_REQCODE = 1;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQCODE);

        } else RunActivity();

    }

    private void RunActivity() {
        if (savedInstanceState == null) {
            mLunchFrag = CategoryFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mLunchFrag)
                    .commit();
        }
    }

    @Override
    public void SingleSong(Song song) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, SingleSongFragment.newInstance(song))
                .commit();
    }

/*    @Override
    protected void onDestroy() {
        if (mLunchFrag != null)
            mLunchFrag.Release();
        super.onDestroy();
    }*/


    /**
     * Handle the permissions request response
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_REQCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RunActivity();
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
