package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.PagerAdapter;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.navigation.NavigationView;


public class NavigationActivity extends AppCompatActivity implements ViewHolders.CallBacks, ServiceConnection, NavigationFragment.RecyclerScroller {

    private static int STORAGE_PERMISSION_REQ_CODE = 1;
    private AppBarConfiguration mAppBarConfiguration;

    private PlayBackBottomBar playBackBottomBar;

    private PlayerService mPlayer;
    boolean serviceBound = false;

    public static Intent newIntent(Context target) {
        Intent intent = new Intent(target, NavigationActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(NavigationActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NavigationActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQ_CODE);

        } else RunActivity();

    }

    private void RunActivity() {

        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_all_song, R.id.nav_albums, R.id.nav_artists)
                .setDrawerLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SongRepository.getInstance(this).findAllSongs();
        AlbumRepository.getInstance(this).findAllAlbum();
        ArtistRepository.getInstance(this).findAllArtist();


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

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
                NavigationActivity.this.onBackPressed();
            }
        }
    }


    @Override
    public void PlaySong(Song song) {
        PlayList.setSongList(SongRepository.getInstance(NavigationActivity.this).getSongs());
        if (serviceBound)
            startService(PlayerService.newIntent(this, song));
        playBackBottomBar = new PlayBackBottomBar(this, mPlayer);
    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {
        startActivity(SongListActivity.newIntent(NavigationActivity.this, albumOrArtist, qualifier));
    }

    @Override
    public void onScrollList(boolean scrolled) {
        if (playBackBottomBar != null)
            playBackBottomBar.onScrollList(scrolled);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
        mPlayer = binder.getService();
        serviceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mPlayer = null;
        serviceBound = false;
    }
}
