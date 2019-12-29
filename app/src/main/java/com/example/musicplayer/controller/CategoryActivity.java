package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ChangeStatusBar;
import com.example.musicplayer.controller.adapter.PagerAdapter;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;


public class CategoryActivity extends AppCompatActivity implements
        ViewHolders.CallBacks,
        ServiceConnection,
        CategoryFragment.RecyclerScroller {

    private static int STORAGE_PERMISSION_REQ_CODE = 1;

    private AppBarConfiguration mAppBarConfiguration;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mIndicator;
    private int mIndicatorWidth;
    private PagerAdapter mAdapter;
    private PlayBackBottomBar playBackBottomBar;

    private PlayerService mPlayer;
    boolean serviceBound = false;

    public static Intent newIntent(Context target) {
        Intent intent = new Intent(target, CategoryActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(CategoryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CategoryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQ_CODE);

        } else RunActivity();

    }

    private void RunActivity() {

        SongRepository.getInstance(this).findAllSongs();
        AlbumRepository.getInstance(this).findAllAlbum();
        ArtistRepository.getInstance(this).findAllArtist();

        setContentView(R.layout.activity_category);

        initUI();

        ChangeStatusBar.setStatusBarGradiant(this);

        mTabLayout.post(() -> {
            mIndicatorWidth = mTabLayout.getWidth() / mTabLayout.getTabCount();

            FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
            indicatorParams.width = mIndicatorWidth;
            mIndicator.setLayoutParams(indicatorParams);
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();

                //Multiply positionOffset with indicatorWidth to get translation

                float translationOffset = (positionOffset + position) * mIndicatorWidth;
                params.leftMargin = (int) translationOffset;
                mIndicator.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initUI() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_photos, R.id.nav_collections, R.id.nav_favorite)
                .setDrawerLayout(drawer)
                .build();
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mIndicator = findViewById(R.id.indicator);

        mAdapter = new PagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
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
                CategoryActivity.this.onBackPressed();
            }
        }
    }


    @Override
    public void PlaySong(Song song) {
        PlayList.setSongList(SongRepository.getInstance(CategoryActivity.this).getSongs());
        if (serviceBound)
            startService(PlayerService.newIntent(this, song));
        playBackBottomBar = new PlayBackBottomBar(this, mPlayer);
    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, SongListFragment.newInstance(albumOrArtist, qualifier))
                .addToBackStack(SongListFragment.TAG)
                .commit();
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
