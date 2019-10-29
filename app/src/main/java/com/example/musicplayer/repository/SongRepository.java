package com.example.musicplayer.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
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
    private static SongRepository instance;

    private SongRepository(Context context) {
        mContext = context;
        setAlbumInfo();
        findSongs();
    }

    public static SongRepository getInstance(Context context) {
        if(instance == null) {
            instance = new SongRepository(context);
        }
        return instance;
    }

    public List<Song> getSongList() {
        Collections.sort(mSongs);
        return mSongs;
    }


    private void findSongs() {

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        SongCursorWrapper songWrapper = new SongCursorWrapper(musicResolver.query(musicUri, null, null, null, null));

        if (songWrapper != null && songWrapper.moveToFirst()) {
            try {

                while (!songWrapper.isAfterLast()){
                    long id = songWrapper.getLong(songWrapper.getColumnIndex(MediaStore.Audio.Media._ID));
                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                    Song song = songWrapper.getSong(contentUri);
                    song.setArtworkPath(AlbumsInfo.get(song.getAlbum()));

                    mSongs.add(song);
                    songWrapper.moveToNext();
                }

            }catch (Exception e){
                return;
            }
            finally {
                songWrapper.close();
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
