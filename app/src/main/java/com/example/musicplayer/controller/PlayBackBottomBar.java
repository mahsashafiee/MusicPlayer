package com.example.musicplayer.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.musicplayer.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class PlayBackBottomBar {

    private CoordinatorLayout mCoorLayout;
    private BottomAppBar mBottomAppBar;
    private CircularSeekBar mSeekBar;
    private TextView mDuration;
    private ImageButton mForward;
    private ImageButton mBackward;
    private FloatingActionButton mPlay;
    private Activity mActivity;
    private PlayerService mPlayer;
    private Handler mHandler = new Handler();
    private Runnable UpdateSongTime;
    private ForBackListener mForwardListener = new ForBackListener(){
        @Override
        public void run() {
            mPlayer.onFastForward();
            super.run();
        }
    };
    private ForBackListener mBackwardListener = new ForBackListener(){
        @Override
        public void run() {
            mPlayer.onFastBackward();
            super.run();
        }
    };

    public PlayBackBottomBar(Activity activity, PlayerService service) {
        mActivity = activity;
        mPlayer = service;
        initView();
        BottomAppBarListener();
        UpdateSongTime();
        SeekBar();
        mBottomAppBar.getBehavior().slideUp(mBottomAppBar);
        mCoorLayout.setVisibility(View.VISIBLE);
    }

    private void initView(){
        mBottomAppBar = mActivity.findViewById(R.id.bottomAppBar);
        mSeekBar = mActivity.findViewById(R.id.bottomAppBar_seekbar);
        mDuration = mActivity.findViewById(R.id.bottomAppBar_duration);
        mForward = mActivity.findViewById(R.id.bottomAppBar_forward);
        mBackward = mActivity.findViewById(R.id.bottomAppBar_backward);
        mPlay = mActivity.findViewById(R.id.bottomAppBar_playPause);
        mCoorLayout = mActivity.findViewById(R.id.bottomAppBar_coordinator);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void BottomAppBarListener() {
        mBottomAppBar.setOnClickListener(view -> mActivity.startActivity(SingleSongActivity.newIntent(mActivity, mPlayer.getmCurrentSongIndex())));
        mPlay.setOnClickListener(view -> {
            mPlayer.Pause();
            if (!mPlayer.isPlaying())
                mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_play_arrow_grey_600_24dp));
            else
                mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_pause_grey_600_24dp));
        });

        mForward.setOnClickListener(view -> mPlayer.goForward());
        mBackward.setOnClickListener(view -> mPlayer.goBackward());

        mForward.setOnTouchListener(mForwardListener);
        mBackward.setOnTouchListener(mBackwardListener);

        mForward.setOnLongClickListener(mForwardListener);
        mBackward.setOnLongClickListener(mBackwardListener);
    }

    private void SeekBar() {
        Runnable mSeekToRun = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setMax(mPlayer.getDuration());
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 130);
            }
        };
        mActivity.runOnUiThread(mSeekToRun);

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
        mActivity.runOnUiThread(UpdateSongTime);
    }

    public void onScrollList(boolean scrolled) {
        if (scrolled) {
            mBottomAppBar.performHide();
            setVisibility(View.GONE);
        } else {
            mBottomAppBar.getBehavior().slideUp(mBottomAppBar);
            setVisibility(View.VISIBLE);
        }
    }

    private void setVisibility(int visibility){
        mPlay.setVisibility(visibility);
        mSeekBar.setVisibility(visibility);
        mForward.setVisibility(visibility);
        mBackward.setVisibility(visibility);
        mDuration.setVisibility(visibility);
    }

    public static class ForBackListener implements View.OnLongClickListener, Runnable, View.OnTouchListener {

        private Handler mHandler = new Handler();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                mHandler.removeCallbacks(ForBackListener.this);
            return false;
        }


        @Override
        public boolean onLongClick(View view) {
            mHandler.post(this);
            return true;
        }

        @Override
        public void run() {
            mHandler.postDelayed(this, 500);
        }
    }
}
