package com.example.musicplayer.controller.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.AlbumFragment;
import com.example.musicplayer.controller.AllSongFragment;
import com.example.musicplayer.controller.ArtistFragment;
import com.example.musicplayer.controller.CategoryFragment;
import com.example.musicplayer.controller.NavigationFragment;
import com.example.musicplayer.model.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<NavigationFragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();


    public PagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;

        addTabs(new AllSongFragment(), mContext.getString(R.string.category_all));
        addTabs(new AlbumFragment(), mContext.getString(R.string.category_album));
        addTabs(new ArtistFragment(), mContext.getString(R.string.category_artist));
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    private void addTabs(NavigationFragment fragment, String title) {
        mFragments.add(fragment);
        mTitles.add(title);
    }
}
