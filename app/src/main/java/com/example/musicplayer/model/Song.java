package com.example.musicplayer.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


public class Song implements Comparable<Song> , Parcelable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return mSongId.equals(song.mSongId) &&
                mFilePath.equals(song.mFilePath);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mSongId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mArtist);
        dest.writeString(this.mAlbum);
        dest.writeString(this.mDuration);
        dest.writeString(this.mArtworkPath);
        dest.writeParcelable(this.mPath, flags);
        dest.writeString(this.mLyrics);
        dest.writeString(this.mFilePath);
    }

    protected Song(Parcel in) {
        this.mSongId = (Long) in.readValue(Long.class.getClassLoader());
        this.mTitle = in.readString();
        this.mArtist = in.readString();
        this.mAlbum = in.readString();
        this.mDuration = in.readString();
        this.mArtworkPath = in.readString();
        this.mPath = in.readParcelable(Uri.class.getClassLoader());
        this.mLyrics = in.readString();
        this.mFilePath = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
