package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.MusicRecyclerAdapter;
import com.example.musicplayer.repository.AlbumRepository;
import com.example.musicplayer.repository.ArtistRepository;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private View mView;
    private RecyclerView mRecycler;
    private RecyclerView mRecyclerArtist;
    private AlbumRepository mRepository;
    private ArtistRepository mRepositoryArtist;
    private MusicRecyclerAdapter mAdapter;
    private MusicRecyclerAdapter mAdapterArtist;


    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance() {
        
        Bundle args = new Bundle();
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_category, container, false);
        mRepository = AlbumRepository.getInstance(getContext());
        mRepositoryArtist = ArtistRepository.getInstance(getContext());
        initUI();
        return mView;
    }
    private void initUI(){

        mRecycler = mView.findViewById(R.id.album_recycler_view);
        mRecyclerArtist = mView.findViewById(R.id.artist_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);

        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new MusicRecyclerAdapter(getActivity(),MusicRecyclerAdapter.ALBUM_ITEM);
        mAdapter.setList(mRepository.getAlbums());
        mRecycler.setAdapter(mAdapter);

        LinearLayoutManager layoutManagerArtist = new LinearLayoutManager(getActivity());
        layoutManagerArtist.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerArtist.setLayoutManager(layoutManagerArtist);
        mAdapterArtist = new MusicRecyclerAdapter(getActivity(),MusicRecyclerAdapter.ARTIST_ITEM);
        mAdapterArtist.setList(mRepositoryArtist.getArtists());
        mRecyclerArtist.setAdapter(mAdapterArtist);

    }

}
