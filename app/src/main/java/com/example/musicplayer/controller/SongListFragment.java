package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.SongRecyclerAdapter;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    private RecyclerView songRecycler;
    private SongRecyclerAdapter mAdapter;
    private String mAlbumArtist;
    private TextView mListTitle;

    private static final String ARG_KEY = "albumArtist";
    private static final String ARG_QUALIFIER = "mQualifier";
    private Qualifier mQualifier;


    public SongListFragment() {
        // Required empty public constructor
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

        mQualifier = (Qualifier) getArguments().getSerializable(ARG_QUALIFIER);

        if (mQualifier.equals(Qualifier.ALBUM))
            PlayList.setSongList(SongRepository.getInstance(getActivity()).getAlbumSongList(mAlbumArtist));

        else
            PlayList.setSongList(SongRepository.getInstance(getActivity()).getArtistSongList(mAlbumArtist));


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
        songRecycler = view.findViewById(R.id.recycler_view);
        mListTitle = view.findViewById(R.id.list_title);

        if (mQualifier.equals(Qualifier.ALBUM)) {
            mListTitle.setText(PlayList.getSongList().get(0).getAlbum());
        } else {
            mListTitle.setText(PlayList.getSongList().get(0).getArtist());
        }

        mAdapter = new SongRecyclerAdapter(getActivity());
        mAdapter.setSongs(PlayList.getSongList());
        songRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        songRecycler.setAdapter(mAdapter);

        /*songRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:

                        //System.out.println("The RecyclerView is not scrolling");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //System.out.println("Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //System.out.println("Scroll Settling");
                        break;
                }
            }
        });*/
    }
}
