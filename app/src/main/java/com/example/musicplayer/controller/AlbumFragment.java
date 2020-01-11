package com.example.musicplayer.controller;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.musicplayer.model.Qualifier;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends NavigationFragment {


    @Override
    LinearLayoutManager getRecyclerLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    Qualifier getSongQualifier() {
        return Qualifier.ALBUM;
    }

    public AlbumFragment() {
        // Required empty public constructor
    }


}
