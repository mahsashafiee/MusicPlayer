package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Qualifier;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends NavigationFragment {


    @Override
    LinearLayoutManager getRecyclerLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    Qualifier getSongQualifier() {
        return Qualifier.ARTIST;
    }

    public ArtistFragment() {
        // Required empty public constructor
    }

}
