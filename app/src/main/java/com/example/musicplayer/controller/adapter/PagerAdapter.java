package com.example.musicplayer.controller.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.CategoryFragment;
import com.example.musicplayer.model.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();




    public PagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;

        addTabs(CategoryFragment.newInstance(Qualifier.ALLSONG), mContext.getString(R.string.category_all));
        addTabs(CategoryFragment.newInstance(Qualifier.ALBUM), mContext.getString(R.string.category_album));
        addTabs(CategoryFragment.newInstance(Qualifier.ARTIST), mContext.getString(R.string.category_artist));
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

    private void addTabs(CategoryFragment fragment, String title){
        mFragments.add(fragment);
        mTitles.add(title);
    }
}
