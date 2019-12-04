package com.example.musicplayer.controller;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.MusicRecyclerAdapter;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    public static final String TAG = "SongListFragment";

    private RecyclerView songRecycler;
    private MusicRecyclerAdapter mAdapter;
    private String mAlbumArtist;
    private TextView mItemCount;
    private ScrollHandler mCallbacks;

    private static final String ARG_KEY = "albumArtist";
    private static final String ARG_QUALIFIER = "qualifier";


    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (ScrollHandler) context;
    }

    public static SongListFragment newInstance(String albumName, Qualifier qualifier) {

        Bundle args = new Bundle();
        args.putString(ARG_KEY, albumName);
        args.putSerializable(ARG_QUALIFIER, qualifier);

        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAlbumArtist = getArguments().getString(ARG_KEY);

        Qualifier qualifier = (Qualifier) getArguments().getSerializable(ARG_QUALIFIER);

        if (qualifier.equals(Qualifier.ALBUM))
            PlayList.setSongList(SongRepository.getInstance(getActivity()).getAlbumSongList(mAlbumArtist));
        else
            PlayList.setSongList(SongRepository.getInstance(getActivity()).getArtistSongList(mAlbumArtist));

    }

    public interface ScrollHandler {
        void onScrollList(boolean scrolled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        initUI(view);
        return view;
    }

    /**
     * ChangeStatusBar Initialization
     *
     * @param view
     */
    private void initUI(View view) {
        setUpToolbar(view);
        mItemCount = view.findViewById(R.id.item_count);
        songRecycler = view.findViewById(R.id.recycler_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.item_count)
                    .setBackgroundResource(R.drawable.backdrop_background_v23);
        } else view.findViewById(R.id.item_count)
                .setBackgroundResource(R.drawable.backdrop_background);

        String items = getResources()
                .getQuantityString(R.plurals.item_number, PlayList.getSongList().size(), PlayList.getSongList().size());
        mItemCount.setText(items);

        mAdapter = new MusicRecyclerAdapter(getActivity(), Qualifier.ALLSONG);
        mAdapter.setList(PlayList.getSongList());
        songRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        songRecycler.setAdapter(mAdapter);

        songRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1))
                    mCallbacks.onScrollList(true);
                else mCallbacks.onScrollList(false);
            }
        });

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

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
