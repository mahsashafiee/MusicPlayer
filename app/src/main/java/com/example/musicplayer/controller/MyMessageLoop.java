package com.example.musicplayer.controller;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.SongRepository;

public class MyMessageLoop extends HandlerThread {

    private static final int ALL_SONGS_WHAT = 0;
    private static final int ALBUM_WHAT = 1;
    private static final int ARTIST_WHAT = 2;
    private static String name = "MyMessageLoop";

    private Handler mHandler;
    private Handler mResponseHandler;
    private SongRepository mSongRepository;
    private AlbumRepository mAlbumRepository;
    private ArtistRepository mArtistRepository;

    public MyMessageLoop(Context context) {
        super(name);
        mSongRepository = SongRepository.getInstance(context);
        mAlbumRepository = AlbumRepository.getInstance(context);
        mArtistRepository = ArtistRepository.getInstance(context);

    }

    public void setResponseHandler(Handler handler){
        mResponseHandler = handler;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case ALBUM_WHAT:
                        mAlbumRepository.findAlbum();
                        break;
                    case ALL_SONGS_WHAT:
                        mSongRepository.findSongs();
                        break;
                    case ARTIST_WHAT:
                        mArtistRepository.findArtist();
                        break;
                }
            }
        };
    }


    public void queueMessage(Qualifier qualifier) {

        mHandler.obtainMessage(QualifierToInt(qualifier)).sendToTarget();
    }

    private int QualifierToInt(Qualifier qualifier) {
        if (qualifier.equals(Qualifier.ALBUM))
            return ALBUM_WHAT;
        else if (qualifier.equals(Qualifier.ALLSONG))
            return ALL_SONGS_WHAT;
        return ARTIST_WHAT;
    }
}
