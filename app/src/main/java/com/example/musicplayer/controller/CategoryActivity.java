package com.example.musicplayer.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.musicplayer.R;
import com.example.musicplayer.SharedPreferences.MusicPreferences;
import com.example.musicplayer.controller.adapter.ListPagerAdapter;
import com.example.musicplayer.controller.adapter.SongRecyclerAdapter;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.tabs.TabLayout;


public class CategoryActivity extends AppCompatActivity implements ViewHolders.CallBacks, ServiceConnection, SongRecyclerAdapter.CallBacks {

    public static final String EXTRA_CURRENT_PAGE = "current_page";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mIndicator;
    private ImageView mRefresh;
    private int mIndicatorWidth;
    private ListPagerAdapter mAdapter;
    private PlayBackBottomBar playBackBottomBar;

    private PlayerService mPlayer;
    boolean serviceBound = false;

    public static Intent newIntent(Context target, int currentPage) {
        Intent intent = new Intent(target, CategoryActivity.class);
        intent.putExtra(EXTRA_CURRENT_PAGE, currentPage);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        playBackBottomBar = new PlayBackBottomBar(this);

        // Bind to LocalService
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        initUI();

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

        mRefresh.setOnClickListener(view -> {
            SongRepository.getInstance(this).findAllSongs();
            AlbumRepository.getInstance(this).findAllAlbum();
            ArtistRepository.getInstance(this).findAllArtist();
        });
    }


    private void initUI() {
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mIndicator = findViewById(R.id.indicator);
        mRefresh = findViewById(R.id.refresh);

        mAdapter = new ListPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_CURRENT_PAGE,0));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public void PlaySong(Song song) {
        PlayList.setSongList(SongRepository.getInstance(CategoryActivity.this).getSongs());
        MusicPreferences.setLastList(this, Qualifier.ALLSONG, "song");
        if (serviceBound)
            startService(PlayerService.newIntent(this, song));
    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {
        startActivity(SongListActivity.newIntent(CategoryActivity.this, albumOrArtist, qualifier));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
        mPlayer = binder.getService();
        playBackBottomBar.initService(mPlayer);
        serviceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mPlayer = null;
        serviceBound = false;
    }
}