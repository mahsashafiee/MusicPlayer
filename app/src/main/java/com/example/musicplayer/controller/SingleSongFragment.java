package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Song;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSongFragment extends Fragment {

    private static String ARG_SONG = "song";
    private Song mSong;
    private CircleImageView mCover;
    private View mView;


    public SingleSongFragment() {
        // Required empty public constructor
    }

    public static SingleSongFragment newInstance(Song song) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_SONG, song);

        SingleSongFragment fragment = new SingleSongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSong = (Song) getArguments().getSerializable(ARG_SONG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_single_song, container, false);
        initView();
        return mView;
    }
    private void initView(){
        mCover = mView.findViewById(R.id.album_cover);
        mCover.setImageBitmap(PictureUtils.getScaledBitmap(mSong.getArtworkPath(),mCover));
    }



}
