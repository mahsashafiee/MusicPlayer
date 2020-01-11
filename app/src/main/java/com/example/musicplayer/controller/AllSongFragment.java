package com.example.musicplayer.controller;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.model.Qualifier;
/**
 * A simple {@link Fragment} subclass.
 */
public class AllSongFragment extends NavigationFragment {

    @Override
    LinearLayoutManager getRecyclerLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    Qualifier getSongQualifier() {
        return Qualifier.ALLSONG;
    }

    public AllSongFragment() {
        // Required empty public constructor
    }

}
