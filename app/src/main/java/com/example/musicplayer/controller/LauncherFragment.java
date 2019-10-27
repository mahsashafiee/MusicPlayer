package com.example.musicplayer.controller;


import android.os.AsyncTask;
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

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LauncherFragment extends Fragment {

    private BottomSheetBehavior sheetBehavior;
    private boolean backdropShown;
    private Integer openIcon, closeIcon;
    private RecyclerView songRecycler;
    private SongRepository mRepository;
    private SwipeRefreshLayout mRefreshLayout;
    private List<Song> mSongList;
    private MusicRecyclerAdapter mAdapter;


    public LauncherFragment() {
        // Required empty public constructor
    }

    public static LauncherFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LauncherFragment fragment = new LauncherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        openIcon = R.drawable.ic_queue_music_black_36dp;
        closeIcon = R.drawable.ic_clear_black_36dp;

        mRepository = SongRepository.getInstance(getActivity());
        MusicPlayerList musicPlayerList = new MusicPlayerList();
        musicPlayerList.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_luncher, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View view) {
        setUpToolbar(view);
        mAdapter = new MusicRecyclerAdapter(mSongList,getActivity(), MusicRecyclerAdapter.MUSIC_ITEM);
        songRecycler = view.findViewById(R.id.recycler_view);
        songRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        songRecycler.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_grid)
                    .setBackgroundResource(R.drawable.backdrop_background_v23);
        }else view.findViewById(R.id.product_grid)
                .setBackgroundResource(R.drawable.backdrop_background);

    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.ic_queue_music_black_36dp), // Menu open icon
                getContext().getResources().getDrawable(R.drawable.ic_clear_black_36dp))); // Menu close icon
    }

    /**
     * Background Thread
     */
    private class MusicPlayerList extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            mSongList = mRepository.getSongList();
            return null;
        }
    }

    /**
     * MediaPlayer Resource Release
     */

    public void Release(){
        mAdapter.Releaser();
    }
}
