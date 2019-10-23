package com.example.musicplayer.repository;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.provider.MediaStore;
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

        String Title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE));
        String Artist =getString(getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String Album = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM));
        String Duration = getString(getColumnIndex(MediaStore.Audio.Media.DURATION));

        Song song = new Song(path);
        song.setTitle(Title);
        song.setAlbum(Album);
        song.setArtist(Artist);
        song.setDuration(Integer.parseInt(Duration));

        return song;


    }

}
