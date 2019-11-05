package com.example.musicplayer.model;

public class Artist implements Comparable<Artist> {

    private String mName;
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

    public Artist(long id, String name){
        this.mId = id;
        this.mName = name;
    }

    @Override
    public int compareTo(Artist artist) {
        return this.getName().compareTo(artist.getName());
    }
}
