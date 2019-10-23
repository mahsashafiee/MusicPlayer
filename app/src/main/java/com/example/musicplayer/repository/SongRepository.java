package com.example.musicplayer.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SongRepository {

    private List<Song> mSongs = new ArrayList<>();
    private HashMap<String, String> AlbumsInfo = new HashMap<>();
    private Context mContext;
    private static SongRepository instance = new SongRepository();

    private SongRepository() {
    }

    public static SongRepository getInstance(Context context) {
        instance.mContext = context;
        instance.setAlbumInfo();
        instance.findSongs();
        return instance;
    }

    public List<Song> getSongList() {
        Collections.sort(mSongs);
        return mSongs;
    }


    private void findSongs() {

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {

            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int TitleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int ArtistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int AlbumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int DurationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            try {
                do {
                    long id = musicCursor.getLong(idColumn);
                    String title = musicCursor.getString(TitleColumn);
                    String artist = musicCursor.getString(ArtistColumn);
                    String album = musicCursor.getString(AlbumColumn);
                    int duration = musicCursor.getInt(DurationColumn);

                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                    Song song = new Song(contentUri);

                    song.setTitle(title);
                    song.setArtist(artist);
                    song.setAlbum(album);
                    song.setDuration(duration);
                    /*song.setArtwork(BitmapFactory.decodeFile(AlbumsInfo.get(album)));*/

                    mSongs.add(song);

                } while (musicCursor.moveToNext());

            } finally {
                musicCursor.close();
            }
        }
    }

    private void setAlbumInfo() {
        Cursor cursor = mContext.getContentResolver()
                .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.AlbumColumns.ALBUM , MediaStore.Audio.AlbumColumns.ALBUM_ART},
                        null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            int Album = cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM);
            int AlbumArt = cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART);

            try {
                do {
                    String album = cursor.getString(Album);
                    String albumArtPath = cursor.getString(AlbumArt);
                    AlbumsInfo.put(album, albumArtPath);
                    cursor.moveToNext();

                }while (!cursor.isAfterLast());

            } finally {
                cursor.close();
            }
        }
    }

}
