package com.example.musicplayer.controller;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.MusicRecyclerAdapter;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;
import com.example.musicplayer.repository.SongRepository;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class NavigationFragment extends Fragment {

    private MusicRecyclerAdapter mAdapter;
    private RecyclerScroller mActivity;
    private RecyclerView mRecyclerView;

    abstract LinearLayoutManager getRecyclerLayoutManager();

    abstract Qualifier getSongQualifier();

    public NavigationFragment() {
        // Required empty public constructor
    }

    public interface RecyclerScroller {
        void onScrollList(boolean scrolled);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (RecyclerScroller) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_navigation, container, false);

        setupRecyclerView(root);
        repositoryObserver();

        return root;
    }

    private void setupRecyclerView(View root) {

        mRecyclerView = root.findViewById(R.id.category_recyclerView);
        mAdapter = new MusicRecyclerAdapter(getActivity(), getSongQualifier());

        if (isAdded()) {

            mRecyclerView.setLayoutManager(getRecyclerLayoutManager());
            mRecyclerView.setAdapter(mAdapter);
        }


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1))
                    mActivity.onScrollList(true);
                else mActivity.onScrollList(false);

            }
        });
    }

    private void repositoryObserver() {

        if (getSongQualifier().equals(Qualifier.ALLSONG))
            SongRepository.getInstance(getContext()).getLiveSong().observe(this, Song ->
                    mAdapter.setList(Song));

        else if (getSongQualifier().equals(Qualifier.ALBUM))
            AlbumRepository.getInstance(getContext()).getLiveAlbum().observe(this, albums ->
                    mAdapter.setList(albums));
        else
            ArtistRepository.getInstance(getContext()).getLiveArtist().observe(this, artists ->
                    mAdapter.setList(artists));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
