package com.example.musicplayer.controller;

import android.app.Application;

import com.example.musicplayer.model.Qualifier;

import android.os.Handler;

public class MusicPlayerApplication extends Application {
    private MyMessageLoop mMessageLoop;
    private Handler mainHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        mMessageLoop = new MyMessageLoop(this);
        mMessageLoop.start();
        mMessageLoop.getLooper();
        mMessageLoop.setResponseHandler(mainHandler);

        mMessageLoop.queueMessage(Qualifier.ALLSONG);
        mMessageLoop.queueMessage(Qualifier.ALBUM);
        mMessageLoop.queueMessage(Qualifier.ARTIST);

    }
}
