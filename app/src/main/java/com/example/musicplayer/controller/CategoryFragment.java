package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    public static final String SONG_QUALIFIER = "song_qualifier";
    private View mView;
    private RecyclerView mRecyclerView;
    private AlbumRepository mAlbumRepository;
    private Qualifier mQualifier;
    private ArtistRepository mArtistRepository;
    private SongRepository mSongRepository;
    private MusicRecyclerAdapter mAdapter;


    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(Qualifier qualifier) {
        
        Bundle args = new Bundle();
        args.putSerializable(SONG_QUALIFIER, qualifier);

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQualifier = (Qualifier) getArguments().getSerializable(SONG_QUALIFIER);
        mAlbumRepository = AlbumRepository.getInstance(getContext());
        mArtistRepository = ArtistRepository.getInstance(getContext());
        mSongRepository = SongRepository.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_category, container, false);

        setupRecyclerView();

        return mView;
    }

    private void setupRecyclerView(){

        mRecyclerView = mView.findViewById(R.id.category_recyclerView);
        mAdapter = new MusicRecyclerAdapter(getActivity(), mQualifier);

        if (isAdded()){
            if (mQualifier.equals(Qualifier.ALLSONG)) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mAdapter.setList(mSongRepository.getSongs());

            }

            else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                if (mQualifier.equals(Qualifier.ALBUM))
                    mAdapter.setList(mAlbumRepository.getAlbums());
                else
                    mAdapter.setList(mArtistRepository.getArtists());
            }

            mRecyclerView.setAdapter(mAdapter);

        }
    }

}
