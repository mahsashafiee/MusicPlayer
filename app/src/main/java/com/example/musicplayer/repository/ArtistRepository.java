package com.example.musicplayer.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.musicplayer.model.Artist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistRepository {

    private Context mContext;
    private List<Artist> mArtists ;
    private static ArtistRepository mInstance;

    private ArtistRepository(Context context){
        mContext = context;
    }

    public static ArtistRepository getInstance(Context context){
        if(mInstance == null)
            mInstance = new ArtistRepository(context);
        return mInstance;
    }


    public List<Artist> getArtists(){
        if(mArtists==null)
            findArtist();
        Collections.sort(mArtists);
        return mArtists;
    }

    public void findArtist(){
        mArtists = Collections.synchronizedList(new ArrayList<>());
        Cursor cursor =mContext.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
                ,new String[]{MediaStore.Audio.Artists.ARTIST_KEY, MediaStore.Audio.Artists.ARTIST},null,null,null);

        if (cursor != null && cursor.moveToFirst()) {

            try {
                int id = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST_KEY);
                int name = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
                do {
                    Artist artist = new Artist(cursor.getLong(id),cursor.getString(name));
                    mArtists.add(artist);
                    cursor.moveToNext();

                }while (!cursor.isAfterLast());

            } finally {
                cursor.close();
            }
        }
    }
}
