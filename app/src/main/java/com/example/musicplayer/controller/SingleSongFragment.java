package com.example.musicplayer.controller;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ID3Tags;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;

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
    private ImageView mPlayPause, mForward, mBackward, mShuffle, mRepeat;
    private MutableLiveData<Integer> mDominantColor;
    private View mView;
    private CircularSeekBar mSeekBar;
    private TextView mTitle, mArtist, mAlbum, mDuration, mRealtimeDuration;

    private Bitmap mArtwork;
    private Handler mHandler = new Handler();
    private Runnable timeRunnable;
    private Runnable seekRunnable;

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

    /**
     * FRAGMENT FACTORY
     *
     * @param song
     * @return
     */
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

    /**
     * FRAGMENT CREATION
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSong = getArguments().getParcelable(ARG_SONG);
        mDominantColor = SongRepository.getInstance(getActivity()).getDominantColor();
        liveDataObservers();
    }

    /**
     * FRAGMENT VIEW CREATION
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_single_song, container, false);
        findViews();
        initView();
        updateSongTime();
        SeekBar();
        Listener();
        return mView;
    }

    /**
     * Animation handler
     */
    private void setDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playingState = getActivity().getDrawable(R.drawable.avd_anim);
            pauseState = getActivity().getDrawable(R.drawable.avd_play_anim);
            startAnimation(mPlayer.isPlaying());
        } else {
            playingState = getActivity().getResources().getDrawable(R.drawable.ic_pause);
            pauseState = getActivity().getResources().getDrawable(R.drawable.ic_play_arrow);
        }
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

    /**
     * UI COMPONENT FIND
     */
    private void findViews() {
        mCover = mView.findViewById(R.id.album_cover);
        mSeekBar = mView.findViewById(R.id.seekBar);
        mTitle = mView.findViewById(R.id.song_title);
        mArtist = mView.findViewById(R.id.song_artist);
        mPlayPause = mView.findViewById(R.id.play_pause);
        mForward = mView.findViewById(R.id.forward);
        mBackward = mView.findViewById(R.id.backward);
        mShuffle = mView.findViewById(R.id.shuffle);
        mRepeat = mView.findViewById(R.id.looper);
        mAlbum = mView.findViewById(R.id.song_album_name);
        mDuration = mView.findViewById(R.id.song_total_duration);
        mRealtimeDuration = mView.findViewById(R.id.song_realtime_duration);
    }

    /**
     * UI COMPONENT VALUE
     */

    private void initView() {

        Artwork artwork = ID3Tags.getArtwork(mSong.getFilePath());

        if (artwork == null)
            mArtwork = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.song_placeholder);
        else
            mArtwork = BitmapFactory.decodeByteArray(artwork.getBinaryData(), 0, artwork.getBinaryData().length);

        Glide.with(mView).asDrawable()
                .placeholder(R.drawable.song_placeholder)
                .load(mArtwork)
                .into(mCover);

        PictureUtils.getDominantColor(getActivity(), mDominantColor, mArtwork);


        mDominantColor.observe(this, integer -> PictureUtils.setBackgroundGradient(getActivity(), integer));

        mTitle.setText(mSong.getTitle());
        mArtist.setText(mSong.getArtist());
        mSeekBar.setMax(mPlayer.getDuration());
        mAlbum.setText(mSong.getAlbum());
        mDuration.setText(mSong.getDuration());
        mCover.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_animation));

        setDrawable();
        startAnimation(mPlayer.isPlaying());
    }

    /**
     * LIVE DATA OBSERVER
     */
    private void liveDataObservers() {

        PlayList.getLiveSong().observe(this, song -> {
            if (song != null) {
                mSong = song;
                initView();
                updateSongTime();
                SeekBar();
            }
        });

        mPlayer.isShuffle().observe(this, isShuffle -> {
            if (isShuffle)
                mShuffle.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_shuffle_on));
            else
                mShuffle.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_shuffle_off));
        });

        mPlayer.isListLoop().observe(this, isLoop -> {
            if (isLoop)
                mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_all));
        });

        mPlayer.isSingleLoop().observe(this, isSingleLoop -> {
            if (isSingleLoop)
                mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
            else if (!mPlayer.isListLoop().getValue())
                mRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_none));
        });

        mPlayer.isPaused().observe(this, isPaused -> {
            if (!isPaused){
                mPlayPause.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_pause));
                mHandler.removeCallbacks(seekRunnable);
                mHandler.removeCallbacks(timeRunnable);
            }
            else {
                mPlayPause.setImageDrawable(pauseState);
                startAnimation(false);
                updateSongTime();
                SeekBar();
            }
        });
    }

    /**
     * SONG CURRENT DURATION
     */
    private void updateSongTime() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                int sTime = mPlayer.getCurrentPosition();
                String songTime;
                if (mPlayer.isStop())
                    sTime = 0;
                int hrs = (sTime / 3600000);
                int mns = (sTime / 60000) % 60000;
                int scs = sTime % 60000 / 1000;
                if (hrs == 0) {
                    songTime = String.format("%02d:%02d", mns, scs);
                } else
                    songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
                mRealtimeDuration.setText(songTime);
                    mHandler.postDelayed(this, 150);
            }
        };
        mHandler.post(timeRunnable);
    }

    /**
     * SEEK BAR
     */
    private void SeekBar() {
        seekRunnable = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setMax(mPlayer.getDuration());
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 140);
            }
        };

        mHandler.post(seekRunnable);
    }

    /**
     * UI LISTENER
     */
    @SuppressLint("ClickableViewAccessibility")
    private void Listener() {

        mPlayPause.setOnClickListener(view -> {
            if (mPlayer.isStop()) {
                getActivity().startService(PlayerService.newIntent(getActivity(), mSong));
                return;
            }
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

        mShuffle.setOnClickListener(view -> mPlayer.shuffle());

        mRepeat.setOnClickListener(view -> {
            if (mPlayer.isListLoop().getValue()) {
                mPlayer.listLoop();
                mPlayer.singleLoop();
            } else if (mPlayer.isSingleLoop().getValue()) {
                mPlayer.singleLoop();
            } else
                mPlayer.listLoop();
        });

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

    @Override
    public void onStop() {
        super.onStop();
        mCover.clearAnimation();
    }
}
