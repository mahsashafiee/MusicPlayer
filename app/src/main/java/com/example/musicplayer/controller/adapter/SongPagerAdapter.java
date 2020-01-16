package com.example.musicplayer.controller.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.musicplayer.controller.SingleSongFragment;
import com.example.musicplayer.model.Song;

import java.util.List;

public class SongPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<Song> mSongs;

    public SongPagerAdapter(@NonNull FragmentManager fm, Context context, List<Song> songs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        mSongs = songs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return SingleSongFragment.newInstance(mSongs.get(position));
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }
}
