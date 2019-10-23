package com.example.musicplayer.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.UUID;

public class Song implements Comparable<Song> , Serializable {

    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mDuration;
    private Uri mPath;
    private String mArtworkPath;
    private UUID mSongId;

    public Song(Uri path) {
        mPath = path;
        mSongId = UUID.randomUUID();
    }

    public UUID getSongId() {
        return mSongId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }

    public String getArtist() {
        return mArtist;
    }

    public Uri getPath() {
        return mPath;
    }

    public void setPath(Uri Path) {
        this.mPath = Path;
    }

    public void setArtist(String Artist) {
        this.mArtist = Artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String Album) {
        this.mAlbum = Album;
    }

    public String getDuration() {
        return mDuration;
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

    public String getArtworkPath() {
        return mArtworkPath;
    }

    public void setArtworkPath(String  Artwork) {
        this.mArtworkPath = Artwork;
    }


    @Override
    public int compareTo(Song song) {
        return this.getTitle().compareTo( song.getTitle());
    }
}
