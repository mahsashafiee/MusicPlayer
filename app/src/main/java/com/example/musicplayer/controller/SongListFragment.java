package com.example.musicplayer.controller;


import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.MusicRecyclerAdapter;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    private RecyclerView songRecycler;
    private MusicRecyclerAdapter mAdapter;
    private String mAlbumName;
    private TextView mItemCount;

    private static final String ARG_KEY = "albumName";


    public SongListFragment() {
        // Required empty public constructor
    }

    public static SongListFragment newInstance(String albumName) {
        
        Bundle args = new Bundle();
        args.putString(ARG_KEY,albumName);
        
        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAlbumName = getArguments().getString(ARG_KEY);

        PlayList.setSongList(SongRepository.getInstance(getActivity()).getAlbumSongList(mAlbumName));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        initUI(view);
        return view;
    }

    /**
     * UI Initialization
     *
     * @param view
     */
    private void initUI(View view) {
        setUpToolbar(view);
        mItemCount = view.findViewById(R.id.item_count);
        String items = getResources()
                .getQuantityString(R.plurals.item_number, PlayList.getSongList().size(), PlayList.getSongList().size());
        mItemCount.setText(items);


        mAdapter = new MusicRecyclerAdapter(getActivity(), MusicRecyclerAdapter.MUSIC_ITEM);
        mAdapter.setList(PlayList.getSongList());

        songRecycler = view.findViewById(R.id.recycler_view);

        songRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        songRecycler.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.item_count)
                    .setBackgroundResource(R.drawable.backdrop_background_v23);
        }else view.findViewById(R.id.item_count)
                .setBackgroundResource(R.drawable.backdrop_background);

    }

    /**
     * Toolbar Handler
     *
     * @param view
     */
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }
}
