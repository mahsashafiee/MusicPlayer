package com.example.musicplayer.controller;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ID3Tags;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Song;

import org.jaudiotagger.tag.datatype.Artwork;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSongFragment extends Fragment {

    private static String ARG_SONG = "song";
    private Song mSong;
    private PlayerService mPlayer;

    private CircleImageView mCover;
    private ImageView mPlayPause;
    private ImageView mForward;
    private ImageView mBackward;
    private ImageView mShuffle;
    private ImageView mRepeat;
    private View mView;
    private CircularSeekBar mSeekBar;
    private TextView mTitle;
    private TextView mArtist;

    private Bitmap mArtwork;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private Drawable playingState;
    private Drawable pauseState;

    private PlayBackBottomBar.ForBackListener mForwardListener = new PlayBackBottomBar.ForBackListener() {
        @Override
        public void run() {
            mPlayer.onFastForward();
            super.run();
        }
    };
    private PlayBackBottomBar.ForBackListener mBackwardListener = new PlayBackBottomBar.ForBackListener() {
        @Override
        public void run() {
            mPlayer.onFastBackward();
            super.run();
        }
    };


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

    public void setPlayer(PlayerService mPlayer) {
        this.mPlayer = mPlayer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSong = getArguments().getParcelable(ARG_SONG);

        mPlayer.getLiveSong().observe(this, song -> {
            if (song == null) {

                mHandler.removeCallbacks(mRunnable);
                mPlayPause.setImageDrawable(pauseState);
                if (mPlayer.isPlaying())
                    mPlayPause.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause));
                else
                    mPlayPause.setImageDrawable(pauseState);

                startAnimation(false);
            } else {
                mSong = song;
                initView();
                SeekBar();
            }
        });
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

    /**
     * Animation handler
     */
    private void setDrawable() {

        playingState = getActivity().getResources().getDrawable(R.drawable.ic_pause);
        pauseState = getActivity().getResources().getDrawable(R.drawable.ic_play_arrow);

    }

    private void startAnimation(boolean isPlaying) {
        if (pauseState instanceof Animatable && playingState instanceof Animatable) {
            if (isPlaying) {
                ((Animatable) playingState).start();
            } else
                ((Animatable) pauseState).start();
        } else if (pauseState instanceof Animatable)
            ((Animatable) pauseState).start();
    }

    private void findViews() {

        mCover = mView.findViewById(R.id.album_cover);
        mSeekBar = mView.findViewById(R.id.seekBar);
        mTitle = mView.findViewById(R.id.song_title);
        mArtist = mView.findViewById(R.id.song_artist);
        mPlayPause = mView.findViewById(R.id.play_pause);
        mForward = mView.findViewById(R.id.forward);
        mBackward = mView.findViewById(R.id.backward);
        mShuffle = mView.findViewById(R.id.shuffle);
        mRepeat = mView.findViewById(R.id.repeat);

    }

    private void initView() {

        Artwork artwork = ID3Tags.getArtwork(mSong.getFilePath());

        if (artwork == null)
            mArtwork = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.music_placeholder);
        else {
            try {
                mArtwork = BitmapFactory.decodeByteArray(artwork.getBinaryData(), 0, artwork.getBinaryData().length);
            }catch (OutOfMemoryError error){
                Log.d("Single song fragment", "initView: "+error.getMessage());
            }
        }

        Glide.with(mView).asDrawable()
                .placeholder(R.drawable.music_placeholder)
                .load(mArtwork)
                .into(mCover);

        mTitle.setText(mSong.getTitle());
        mTitle.setSelected(true);
        mArtist.setText(mSong.getArtist());
        mSeekBar.setMax(mPlayer.getDuration());

        setDrawable();

        if (mPlayer.isPlaying())
            mPlayPause.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause));
        else
            mPlayPause.setImageDrawable(pauseState);

        startAnimation(mPlayer.isPlaying());
    }

    private void SeekBar() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setMax(mPlayer.getDuration());
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 130);
            }
        };

        mHandler.post(mRunnable);
    }

    private void Listener() {

        mPlayPause.setOnClickListener(view -> {
            mPlayer.Pause();
            if (!mPlayer.isPlaying())
                mPlayPause.setImageDrawable(pauseState);
            else
                mPlayPause.setImageDrawable(playingState);
            startAnimation(mPlayer.isPlaying());
        });
        mForward.setOnClickListener(view -> mPlayer.goForward());
        mBackward.setOnClickListener(view -> mPlayer.goBackward());

        mForward.setOnTouchListener(mForwardListener);
        mBackward.setOnTouchListener(mBackwardListener);

        mForward.setOnLongClickListener(mForwardListener);
        mBackward.setOnLongClickListener(mBackwardListener);

        mShuffle.setOnClickListener(view -> mPlayer.Shuffle());

        mRepeat.setOnClickListener(view -> mPlayer.ListLoop());

        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress,
                                          boolean fromUser) {
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
}
