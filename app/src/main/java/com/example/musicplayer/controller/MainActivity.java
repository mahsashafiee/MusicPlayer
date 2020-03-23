package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.musicplayer.R;
import com.example.musicplayer.sharedPreferences.MusicPreferences;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;

import java.util.List;

import static com.example.musicplayer.utils.Utils.setWindowFlag;

public class MainActivity extends AppCompatActivity implements SongRepository.ManageActivity {

    private static int STORAGE_PERMISSION_REQ_CODE = 1;
    private int page = 0;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQ_CODE);
        } else
            RunActivity();

    }

    private void RunActivity() {
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.parent_layout);

        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        AnimationDrawable animDrawable = (AnimationDrawable) view.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(2000);
        animDrawable.start();


        SongRepository.getInstance(this).findAllSongs();
        AlbumRepository.getInstance(this).findAllAlbum();
        ArtistRepository.getInstance(this).findAllArtist();

        try {
            List strings = MusicPreferences.getLastList(this);
            if (strings != null && strings.size() != 0) {
                if (strings.contains(Qualifier.ALBUM.toString())) {
                    page = 1;
                    int index;
                    if (strings.indexOf(Qualifier.ALBUM.toString()) == 0)
                        index = 1;
                    else
                        index = 0;
                    PlayList.setSongList(SongRepository.getInstance(this).getAlbumSongList(strings.get(index).toString()));
                }
                if (strings.contains(Qualifier.ARTIST.toString())) {
                    page = 2;
                    int index;
                    if (strings.indexOf(Qualifier.ALBUM.toString()) == 0)
                        index = 1;
                    else
                        index = 0;
                    PlayList.setSongList(SongRepository.getInstance(this).getArtistSongList(strings.get(index).toString()));
                }
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "RunActivity: ");
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
        if (requestCode == STORAGE_PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RunActivity();
            } else if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                RunActivity();
            } else {
                MainActivity.this.onBackPressed();
            }
        }
    }

    @Override
    public void startCategory() {
        startActivity(CategoryActivity.newIntent(this, page));
        finish();
    }
}
