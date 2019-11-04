package com.example.musicplayer.repository;

import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayList {

    private static List<Song> songList = new ArrayList<>();

    public static List<Song> getSongList() {
        return songList;
    }

    public static void setSongList(List<Song> songList) {
        PlayList.songList = songList;
    }
}
