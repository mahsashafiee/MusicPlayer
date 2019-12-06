package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ChangeStatusBar;
import com.example.musicplayer.controller.adapter.PagerAdapter;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class CategoryActivity extends AppCompatActivity implements ViewHolders.CallBacks,
        PlayerManager.UIController,
        CategoryFragment.ScrollHandler ,
        SongListFragment.ScrollHandler{

    private AppBarConfiguration mAppBarConfiguration;

    private static int STORAGE_PERMISSION_REQ_CODE = 1;
    private Bundle savedInstanceState;
    private Handler mHandler = new Handler();

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mIndicator;
    private int mIndicatorWidth;
    private PagerAdapter mAdapter;
    private Song mSong;
    private PlayerManager mPlayer;
    private Runnable UpdateSongTime;

    private CoordinatorLayout mCoordinatorLayout;
    private BottomAppBar mBottomAppBar;
    private CircularSeekBar mSeekBar;
    private TextView mDuration;
    private ImageButton mForward;
    private ImageButton mBackward;
    private FloatingActionButton mPlay;

    public static Intent newIntent(Context target) {
        Intent intent = new Intent(target, CategoryActivity.class);
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
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQ_CODE);

        } else RunActivity();

    }

    private void RunActivity() {

        initUI();
        ChangeStatusBar.setStatusBarGradiant(this);
        BottomAppBarListener();

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

        mBottomAppBar = findViewById(R.id.bottomAppBar);
        mSeekBar = findViewById(R.id.bottomAppBar_seekBar);
        mDuration = findViewById(R.id.bottomAppBar_duration);
        mForward = findViewById(R.id.bottomAppBar_forward);
        mBackward = findViewById(R.id.bottomAppBar_backward);
        mPlay = findViewById(R.id.bottomAppBar_playPause);
        mCoordinatorLayout = findViewById(R.id.bottomAppBar_coordinator);

        mAdapter = new PagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void BottomAppBarListener() {
        mBottomAppBar.setOnClickListener(view -> startActivity(SingleSongActivity.newIntent(CategoryActivity.this, mSong)));
        mPlay.setOnClickListener(view -> {
            mPlayer.Pause();
            if (!mPlayer.isPlaying())
                mPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
            else
                mPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        });

        mForward.setOnClickListener(view -> mPlayer.goForward());
        mBackward.setOnClickListener(view -> mPlayer.goBackward());
    }

    private void SeekBar() {
        Runnable mSeekToRun = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 130);
            }
        };
        runOnUiThread(mSeekToRun);

    }

    private void UpdateSongTime() {
        UpdateSongTime = new Runnable() {
            @Override
            public void run() {
                int sTime = mPlayer.getCurrentPosition();
                int mns = (sTime / 60000) % 60000;
                int scs = sTime % 60000 / 1000;
                String songTime = String.format("%02d:%02d", mns, scs);
                mDuration.setText(songTime);
                mHandler.postDelayed(this, 100);
            }
        };
        runOnUiThread(UpdateSongTime);
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
    public void SingleSong(Song song) {
        startActivity(SingleSongActivity.newIntent(CategoryActivity.this, song));
        mSong = song;
        PlayList.setSongList(SongRepository.getInstance(CategoryActivity.this).getSongs());
        mPlayer = PlayerManager.getPlayer(CategoryActivity.this);
        mPlayer.setUIobj(CategoryActivity.this);
        mPlayer.Play(mSong);
        mCoordinatorLayout.setVisibility(View.VISIBLE);
        SeekBar();
        UpdateSongTime();
    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_container, SongListFragment.newInstance(albumOrArtist, qualifier))
                .addToBackStack(SongListFragment.TAG)
                .commit();

    }

    @Override
    public void ViewUpdater() {
        if (this.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
            mSong = mPlayer.getCurrentSong();
            mSeekBar.setMax(mPlayer.getDuration());
        }
    }

    @Override
    public void onScrollList(boolean scrolled) {
        if (scrolled) {
            mBottomAppBar.performHide();
            mPlay.setVisibility(View.GONE);
            mSeekBar.setVisibility(View.GONE);
            mForward.setVisibility(View.GONE);
            mBackward.setVisibility(View.GONE);
            mDuration.setVisibility(View.GONE);
        }
        else {
            mBottomAppBar.getBehavior().slideUp(mBottomAppBar);
            mPlay.setVisibility(View.VISIBLE);
            mSeekBar.setVisibility(View.VISIBLE);
            mForward.setVisibility(View.VISIBLE);
            mBackward.setVisibility(View.VISIBLE);
            mDuration.setVisibility(View.VISIBLE);
        }
    }
}
