package com.example.musicplayer.model;

import android.net.Uri;

import java.io.Serializable;

public class Song implements Comparable<Song> , Serializable {

    private Long mSongId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mDuration;
    private Uri mPath;
    private String mArtworkPath;
    private String mLyrics;
    private String mFilePath;

    public Song(Uri path , Long id) {
        mPath = path;
        mSongId = id;
    }

    public Long getSongId() {
        return mSongId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getDuration() {
        return mDuration;
    }

    public Uri getPath() {
        return mPath;
    }

    public String getArtworkPath() {
        return mArtworkPath;
    }

    public String getLyrics(){ return mLyrics; }

    public String getFilePath(){ return mFilePath; }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }

    public void setPath(Uri Path) { this.mPath = Path; }

    public void setArtist(String Artist) {
        this.mArtist = Artist;
    }

    public void setAlbum(String Album) {
        this.mAlbum = Album;
    }

    public void setDuration(int Duration) {
        int hrs = (Duration / 3600000);
        int mns = (Duration / 60000) % 60000;
        int scs = Duration % 60000 / 1000;
        if (hrs == 0) {
            this.mDuration = String.format("%02d:%02d", mns, scs);
        } else
            this.mDuration = String.format("%02d:%02d:%02d", hrs, mns, scs);
    }

    public void setArtworkPath(String  Artwork) {
        this.mArtworkPath = Artwork;
    }

    public void setLyrics(String lyrics) {this.mLyrics = lyrics; }

    public void setFilePath(String FilePath) {this.mFilePath = FilePath; }


    @Override
    public int compareTo(Song song) {
        return this.getTitle().compareTo( song.getTitle());
    }
}
