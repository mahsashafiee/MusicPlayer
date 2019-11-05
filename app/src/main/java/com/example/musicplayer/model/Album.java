package com.example.musicplayer.model;


public class Album implements Comparable<Album> {

    private String  mId;
    private String mTitle = "Unknown";
    private String mArtworkPath;
    private String mAlbumArtist;
    private String mReleaseDate;

    public Album (String id){
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtworkPath() {
        return mArtworkPath;
    }

    public String getAlbumArtist() {
        return mAlbumArtist;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }

    public void setArtworkPath(String ArtworkPath) {
        this.mArtworkPath = ArtworkPath;
    }

    public void setAlbumArtist(String AlbumArtist) {
        this.mAlbumArtist = AlbumArtist;
    }

    public void setReleaseDate(String ReleaseDate) {
        this.mReleaseDate = ReleaseDate;
    }

    @Override
    public int compareTo(Album album) { return this.getTitle().compareTo(album.getTitle()); }
}
