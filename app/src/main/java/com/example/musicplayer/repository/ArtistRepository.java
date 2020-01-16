package com.example.musicplayer.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Artist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistRepository {

    private Context mContext;
    private List<Artist> mArtists;
    private static ArtistRepository mInstance;
    private MutableLiveData<List<Artist>> mLiveArtist = new MutableLiveData<>();

    private ArtistRepository(Context context) {
        mContext = context;
    }

    public static ArtistRepository getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ArtistRepository(context);
        return mInstance;
    }


    public List<Artist> getArtists() {
        Collections.sort(mArtists);
        return mArtists;
    }

    public MutableLiveData<List<Artist>> getLiveArtist() {
        return mLiveArtist;
    }

    public void findAllArtist() {
        new Thread(this::findArtist).start();
    }

    private void findArtist() {
        mArtists = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Audio.Artists.ARTIST_KEY, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            try {
                int id = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST_KEY);
                int name = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
                int numberOfSongs = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
                do {
                    Artist artist = new Artist(cursor.getLong(id), cursor.getString(name));
                    artist.setNumberOfSongs(cursor.getString(numberOfSongs));
                    mArtists.add(artist);
                    cursor.moveToNext();

                } while (!cursor.isAfterLast());

            } finally {
                mLiveArtist.postValue(getArtists());
                cursor.close();
            }
        }
    }
}
