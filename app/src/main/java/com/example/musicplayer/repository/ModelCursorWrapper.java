package com.example.musicplayer.repository;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musicplayer.Utils.ID3Tags;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Song;

public class ModelCursorWrapper extends CursorWrapper {
    public ModelCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Album getAlbum(){

        String title = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
        String albumArtPath = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART));
        String  Id = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_KEY));
        String artist = getString(getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST));
        String totalSong= getString(getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS));

        Album album = new Album(Id);
        album.setArtworkPath(albumArtPath);
        album.setTitle(title);
        album.setNumberOfSongs(totalSong);
        album.setAlbumArtist(artist);

        return album;
    }

    public Song getSong(Uri path){

        long id = getLong(getColumnIndex(MediaStore.Audio.Media._ID));
        String  FilePath = getString(getColumnIndex(MediaStore.Audio.Media.DATA));
        String Title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE));
        String Artist =getString(getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String Album = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM));
        int Duration = getInt(getColumnIndex(MediaStore.Audio.Media.DURATION));

        Song song = new Song(path,id);
        song.setTitle(Title);
        song.setAlbum(Album);
        song.setArtist(Artist);
        song.setDuration(Duration);
        song.setLyrics(ID3Tags.getLyrics(FilePath));
        song.setFilePath(FilePath);

        return song;
    }
}
