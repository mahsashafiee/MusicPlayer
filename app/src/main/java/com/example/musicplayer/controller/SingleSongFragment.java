package com.example.musicplayer.controller;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Song;

import java.util.concurrent.TimeUnit;

import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSongFragment extends Fragment implements PlayerManager.updateUI {

    private static String ARG_SONG = "song";
    private Song mSong;
    private PlayerManager mPlayer;
    private Handler mHandler = new Handler();
    private Runnable mSeekToRun;

    private ImageView mCover;
    private ImageView mPlayPause;
    private ImageView mForward;
    private ImageView mBackward;
    private View mView;
    private CircularSeekBar mSeekBar;
    private TextView mTitle;
    private TextView mArtist;
    private TextView mTime;
    private Runnable UpdateSongTime;


    public SingleSongFragment() {
        // Required empty public constructor
    }

    public static SingleSongFragment newInstance(Song song) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_SONG, song);

        SingleSongFragment fragment = new SingleSongFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSong = getArguments().getParcelable(ARG_SONG);
        mPlayer = PlayerManager.getPlayer(getContext(), SingleSongFragment.this);
        mPlayer.Play(mSong);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_single_song, container, false);
        findViews();
        initView();
        SeekBar();
        Listener();
        return mView;
    }

    private void findViews() {

        mCover = mView.findViewById(R.id.album_cover);
        mSeekBar = mView.findViewById(R.id.seekBar);
        mTitle = mView.findViewById(R.id.song_title);
        mArtist = mView.findViewById(R.id.song_artist);
        mPlayPause = mView.findViewById(R.id.play_pause);
        mForward = mView.findViewById(R.id.forward);
        mBackward = mView.findViewById(R.id.backward);

    }

    private void initView() {

        if (mSong.getArtworkPath() != null) {
            Glide.with(mView).asDrawable().load(mSong.getArtworkPath()).into(PictureUtils.getTarget(mCover));
        }
        mTitle.setText(mSong.getTitle());
        mArtist.setText(mSong.getArtist());
        mSeekBar.setMax(mPlayer.getDuration());
    }

    private void SeekBar() {
        mSeekToRun = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 150);
            }
        };
        getActivity().runOnUiThread(mSeekToRun);

    }

    private void Listener() {

        mPlayPause.setOnClickListener(view -> {
            mPlayer.Pause();
        });
        mForward.setOnClickListener(view -> mPlayer.goForward());
        mBackward.setOnClickListener(view -> mPlayer.goBackward());

        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.Seek(((int) progress));
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });
    }

    private void UpdateSongTime() {
        UpdateSongTime = new Runnable() {
            @Override
            public void run() {
                int sTime = mPlayer.getCurrentPosition();
                mTime.setText(String.format("%d h, %d min, %d sec", 0, TimeUnit.MILLISECONDS.toMinutes(sTime),
                        TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                mHandler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    public void Update() {
        if (isAdded()) {
            mSong = mPlayer.getCurrentSong();
            initView();
        }
    }

    @Override
    public void Handler() {
        if (isAdded()) {
            mHandler.removeCallbacks(mSeekToRun);
//            mHandler.removeCallbacks(UpdateSongTime);
        }
    }
}
