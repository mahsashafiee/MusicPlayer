package com.example.musicplayer.controller;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;


public class ForBackListener implements View.OnLongClickListener, Runnable, View.OnTouchListener {

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
