package com.example.musicplayer.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongRepository {

    private List<Song> mSongs;
    private List<Song> mBasedSongs;
    private Context mContext;
    private ManageActivity mSplashActivity;
    private static SongRepository instance;
    private String TAG = "Album exception";
    private MutableLiveData<List<Song>> mLiveSong = new MutableLiveData<>();
    private MutableLiveData<Integer> mDominantColor = new MutableLiveData<>();

    private SongRepository(Context context) {
        mContext = context;
        mSplashActivity = (ManageActivity) context;
        mDominantColor.setValue(context.getResources().getColor(R.color.default_background));
    }

    public static SongRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SongRepository(context);
        }
        return instance;
    }

    public List<Song> getSongs() {
        Collections.sort(mSongs);
        return mSongs;
    }

    public List<Song> getAlbumSongList(String albumName) {
        getAlbumSongs(albumName);
        Collections.sort(mBasedSongs);
        return mBasedSongs;
    }

    public List<Song> getArtistSongList(String artistSong) {
        getArtistSongs(artistSong);
        Collections.sort(mBasedSongs);
        return mBasedSongs;
    }

    public MutableLiveData<List<Song>> getLiveSong(){
        return mLiveSong;
    }

    public void findAllSongs(){
        new Thread(this::findSongs).start();
    }

    private void findSongs() {
        mSongs = new ArrayList<>();
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ModelCursorWrapper songWrapper = new ModelCursorWrapper(musicResolver.query(musicUri, null, null, null, null));
        if (songWrapper != null && songWrapper.moveToFirst()) {
            try {

                while (!songWrapper.isAfterLast()) {
                    long id = songWrapper.getLong(songWrapper.getColumnIndex(MediaStore.Audio.Media._ID));
                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                    Song song = songWrapper.getSong(contentUri);

                    mSongs.add(song);
                    mLiveSong.postValue(getSongs());
                    songWrapper.moveToNext();
                }

            } catch (Exception e) {
                /*Log.e("MusicPlayer", Objects.requireNonNull(e.getMessage()));*/
            } finally {
                songWrapper.close();
                mSplashActivity.startCategory();
            }
        }
    }

    public Song findSongById(Long id){
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media._ID + "= ?";
        String [] selectionArg = new String[]{String.valueOf(id)};
        ModelCursorWrapper songWrapper = new ModelCursorWrapper(musicResolver.query(musicUri, null, selection, selectionArg, null));
        if (songWrapper != null && songWrapper.moveToFirst()) {
            try {
                return songWrapper.getSong(ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id));
            } catch (Exception e) {
                /*Log.e("MusicPlayer", Objects.requireNonNull(e.getMessage()));*/
            } finally {
                songWrapper.close();
            }
        }
        return null;
    }

    private void getAlbumSongs(String albumName) {
        mBasedSongs = new ArrayList<>();

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ModelCursorWrapper songWrapper = new ModelCursorWrapper(musicResolver.query(musicUri,
                null, MediaStore.Audio.Media.ALBUM + " = ? ", new String[]{albumName}, null));

        if (songWrapper != null && songWrapper.moveToFirst()) {
            try {

                while (!songWrapper.isAfterLast()) {
                    long id = songWrapper.getLong(songWrapper.getColumnIndex(MediaStore.Audio.Media._ID));
                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                    Song song = songWrapper.getSong(contentUri);

                    mBasedSongs.add(song);
                    songWrapper.moveToNext();
                }

            } catch (Exception e) {
                Log.d(TAG, "getAlbumSongs: " + e.getMessage());
            } finally {
                songWrapper.close();
            }
        }
    }

    private void getArtistSongs(String artistName) {
        mBasedSongs = new ArrayList<>();

        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ModelCursorWrapper songWrapper = new ModelCursorWrapper(musicResolver.query(musicUri,
                null, MediaStore.Audio.Media.ARTIST + " = ? ", new String[]{artistName}, null));

        if (songWrapper != null && songWrapper.moveToFirst()) {
            try {

                while (!songWrapper.isAfterLast()) {
                    long id = songWrapper.getLong(songWrapper.getColumnIndex(MediaStore.Audio.Media._ID));
                    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                    Song song = songWrapper.getSong(contentUri);

                    mBasedSongs.add(song);
                    songWrapper.moveToNext();
                }

            } catch (Exception e) {
                Log.d(TAG, "getArtistSongs: " + e.getMessage());
            } finally {
                songWrapper.close();
            }
        }
    }

    public MutableLiveData<Integer> getDominantColor() {
        return mDominantColor;
    }

    public interface ManageActivity{
        void startCategory();
    }
}
