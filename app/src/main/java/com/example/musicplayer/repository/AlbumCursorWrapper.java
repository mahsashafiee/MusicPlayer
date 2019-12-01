package com.example.musicplayer.repository;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.MediaStore;

import com.example.musicplayer.model.Album;

public class AlbumCursorWrapper extends CursorWrapper {
    public AlbumCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Album getAlbum(){

        String title = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
        String albumArtPath = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART));
        String  Id = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_KEY));
        String artist = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST));

        Album album = new Album(Id);
        album.setArtworkPath(albumArtPath);
        album.setTitle(title);
        album.setAlbumArtist(artist);

        return album;
    }
}
