package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSongFragment extends Fragment {


    public SingleSongFragment() {
        // Required empty public constructor
    }

    public static SingleSongFragment newInstance() {

        Bundle args = new Bundle();

        SingleSongFragment fragment = new SingleSongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_song, container, false);
    }

}
