package com.example.musicplayer;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.musicplayer.repository.SongRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


/**
 * A simple {@link Fragment} subclass.
 */
public class LauncherFragment extends Fragment {

    private BottomSheetBehavior sheetBehavior;
    private boolean backdropShown;
    private Integer openIcon, closeIcon;
    private RecyclerView songRecycler;
    private SongRepository mRepository;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_luncher, container, false);

        mRepository = SongRepository.getInstance(getActivity());

        initUI(view);

        return view;
    }

    private void initUI(View view) {
        setUpToolbar(view);

        LinearLayout contentLayout = view.findViewById(R.id.contentLayout);

        sheetBehavior = BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        songRecycler = view.findViewById(R.id.recycler_view);
        RecyclerInit();


    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(v -> {
            toggleFilters();
            updateIcon(v);
        });
    }

    private void toggleFilters(){
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            backdropShown = !backdropShown;
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else {
            backdropShown = !backdropShown;
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void updateIcon(View view) {
        if (openIcon != null && closeIcon != null) {
            if (!(view instanceof ImageView)) {
                throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
            }
            if (backdropShown) {
                ((ImageView) view).setImageResource(closeIcon);
            } else {
                ((ImageView) view).setImageResource(openIcon);
            }
        }
    }

    private void RecyclerInit(){
        songRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        songRecycler.setAdapter(new MusicRecyclerAdapter(mRepository.getSongList(),getActivity()));
    }

}
