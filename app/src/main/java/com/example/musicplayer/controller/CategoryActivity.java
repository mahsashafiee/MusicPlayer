package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
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
import com.google.android.material.tabs.TabLayout;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class CategoryActivity extends AppCompatActivity implements ViewHolders.CallBacks, PlayerManager.UIController, CategoryFragment.ScrollHandler {

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

    private CoordinatorLayout mCoorLayout;
    private BottomAppBar mBottomAppBar;
    private CircularSeekBar mSeekbar;
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
        BottomAppBarListener();

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
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mIndicator = findViewById(R.id.indicator);

        mBottomAppBar = findViewById(R.id.bottomAppBar);
        mSeekbar = findViewById(R.id.bottomAppBar_seekbar);
        mDuration = findViewById(R.id.bottomAppBar_duration);
        mForward = findViewById(R.id.bottomAppBar_forward);
        mBackward = findViewById(R.id.bottomAppBar_backward);
        mPlay = findViewById(R.id.bottomAppBar_playPause);
        mCoorLayout = findViewById(R.id.bottomAppBar_coordinator);

        mAdapter = new PagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void BottomAppBarListener() {
        mBottomAppBar.setOnClickListener(view -> startActivity(SingleSongActivity.newIntent(CategoryActivity.this, mSong)));
        mPlay.setOnClickListener(view -> {
            mPlayer.Pause();
            if (!mPlayer.isPlaying())
                mPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_grey_600_24dp));
            else
                mPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_grey_600_24dp));
        });

        mForward.setOnClickListener(view -> mPlayer.goForward());
        mBackward.setOnClickListener(view -> mPlayer.goBackward());
    }

    private void SeekBar() {
        Runnable mSeekToRun = new Runnable() {
            @Override
            public void run() {
                mSeekbar.setProgress(mPlayer.getCurrentPosition());
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
    public void PlaySong(Song song) {
        mSong = song;
        PlayList.setSongList(SongRepository.getInstance(CategoryActivity.this).getSongs());
        mPlayer = PlayerManager.getPlayer(CategoryActivity.this);
        mPlayer.setUIobj(CategoryActivity.this);
        mPlayer.Play(mSong);
        mCoorLayout.setVisibility(View.VISIBLE);
        SeekBar();
        UpdateSongTime();
    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {
        startActivity(SongListActivity.newIntent(CategoryActivity.this, albumOrArtist, qualifier));
    }

    @Override
    public void ViewUpdater() {
        if (this.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
            mSong = mPlayer.getCurrentSong();
            mSeekbar.setMax(mPlayer.getDuration());
        }
    }

    @Override
    public void onScrollList(boolean scrolled) {
        if (scrolled) {
            mBottomAppBar.performHide();
            mPlay.setVisibility(View.GONE);
            mSeekbar.setVisibility(View.GONE);
            mForward.setVisibility(View.GONE);
            mBackward.setVisibility(View.GONE);
            mDuration.setVisibility(View.GONE);
        }
        else {
            mBottomAppBar.getBehavior().slideUp(mBottomAppBar);
            mPlay.setVisibility(View.VISIBLE);
            mSeekbar.setVisibility(View.VISIBLE);
            mForward.setVisibility(View.VISIBLE);
            mBackward.setVisibility(View.VISIBLE);
            mDuration.setVisibility(View.VISIBLE);
        }
    }
}
