package com.example.musicplayer.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayList {

    private static List<Song> songList = new ArrayList<>();

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
}
