package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Song;

public class CategoryActivity extends AppCompatActivity implements ViewHolders.CallBacks {

    private static int STORAGE_PERMISSION_REQCODE = 1;
    private Bundle savedInstanceState;

    public static Intent newIntent(Context target){
        Intent intent = new Intent(target,CategoryActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        this.savedInstanceState = savedInstanceState;

        if (ContextCompat.checkSelfPermission(CategoryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CategoryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQCODE);

        } else RunActivity();
    }

    private void RunActivity(){
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.category_container,
                            CategoryFragment.newInstance())
                    .commit();
        }

    }
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
            } else if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                RunActivity();
            }else {
                CategoryActivity.this.onBackPressed();
            }
        }
    }


    @Override
    public void SingleSong(Song song) {

    }

    @Override
    public void SongList(String albumName) {
        startActivity(SongListActivity.newIntent(CategoryActivity.this,albumName));
    }
}