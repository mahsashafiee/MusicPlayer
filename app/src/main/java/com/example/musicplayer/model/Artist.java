package com.example.musicplayer.model;

public class Artist implements Comparable<Artist> {

    private String mName;

    private String mNumberOfSongs;

    private long mId;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public long getId() {
        return mId;
    }

    public String getNumberOfSongs() {
        return mNumberOfSongs;
    }

    public void setNumberOfSongs(String numberOfSongs) {
        mNumberOfSongs = numberOfSongs;
    }

    public void setId(long id) {
        mId = id;
    }

    public Artist(long id, String name){
        this.mId = id;
        this.mName = name;
    }

    @Override
    public int compareTo(Artist artist) {
        return this.getName().compareTo(artist.getName());
    }
}
