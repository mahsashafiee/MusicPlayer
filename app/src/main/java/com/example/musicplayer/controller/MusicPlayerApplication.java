package com.example.musicplayer.controller;

import android.app.Application;

import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.SongRepository;

public class MusicPlayerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SongRepository.getInstance(this).findAllSongs();
        AlbumRepository.getInstance(this).findAllAlbum();
        ArtistRepository.getInstance(this).findAllArtist();
    }
}
