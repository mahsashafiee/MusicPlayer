package com.example.musicplayer.repository;

import android.content.Context;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import com.example.musicplayer.model.Album;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumRepository {
    private static AlbumRepository instance;
    private Context mContext;
    private List<Album> mAlbums;
    private MutableLiveData<List<Album>> mLiveAlbum = new MutableLiveData<>();

    private AlbumRepository(Context context){
        mContext = context;
    }

    public static AlbumRepository getInstance(Context context){
        if(instance==null)
            instance = new AlbumRepository(context);
        return instance;
    }

    public List<Album> getAlbums(){
        Collections.sort(mAlbums);
        return mAlbums;
    }

    public void findAllAlbum(){
        new Thread(this::findAlbum).start();
    }

    private void findAlbum(){
//        mAlbums = Collections.synchronizedList(new ArrayList<>());
        mAlbums = new ArrayList<>();
        mLiveAlbum.postValue(mAlbums);

        ModelCursorWrapper cursor = new ModelCursorWrapper(mContext.getContentResolver()
                .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        null, null, null, null));

        if (cursor != null && cursor.moveToFirst()) {

            try {
                do {
                    mAlbums.add(cursor.getAlbum());
                    cursor.moveToNext();

                }while (!cursor.isAfterLast());

            } finally {
                cursor.close();
            }
        }
    }

}
