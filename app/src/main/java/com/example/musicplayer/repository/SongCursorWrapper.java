package com.example.musicplayer.repository;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musicplayer.Utils.ID3Tags;
import com.example.musicplayer.model.Song;

public class SongCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public SongCursorWrapper(Cursor cursor) {
        super(cursor);
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
