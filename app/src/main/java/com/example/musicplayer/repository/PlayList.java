package com.example.musicplayer.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayList {
    public PlayList() {
        mArtistURLs.setValue(new ArrayList<>());
    }

    private static List<Song> songList;

    private static MutableLiveData<List<String>> mArtistURLs = new MutableLiveData<>();

    private static MutableLiveData<Song> mSong = new MutableLiveData<>();

    public static List<Song> getSongList() {
        return songList;
    }

    public static void setSongList(List<Song> songList) {
        PlayList.songList = songList;
    }

    public static MutableLiveData<Song> getLiveSong(){
        return mSong;
    }

    public static MutableLiveData<List<String>> getArtistURLs() {
        return mArtistURLs;
    }
}
