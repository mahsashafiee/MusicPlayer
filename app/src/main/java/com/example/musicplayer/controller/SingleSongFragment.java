package com.example.musicplayer.controller;


import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
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
    private ImageView mShuffle;
    private ImageView mRepeat;
    private View mView;
    private CircularSeekBar mSeekBar;
    private TextView mTitle;
    private TextView mArtist;
    private TextView mTime;
    private Runnable UpdateSongTime;

    private Drawable playingState;
    private Drawable pauseState;


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
        mPlayer = PlayerManager.getPlayer(getContext());
        mPlayer.setUIobj(SingleSongFragment.this);
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

    /**
     * Animation handler
     */
    private void setDrawable(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            playingState = getActivity().getDrawable(R.drawable.avd_anim);
            pauseState = getActivity().getDrawable(R.drawable.avd_anim_reverse);
            startAnimation(mPlayer.isPlaying());
        }
        else {
            playingState = getActivity().getResources().getDrawable(R.drawable.ic_pause_grey_600_24dp);
            pauseState = getActivity().getResources().getDrawable(R.drawable.ic_play_arrow_grey_600_24dp);
        }
    }

    private void startAnimation(boolean isPlaying){
        if(pauseState instanceof Animatable && playingState instanceof Animatable) {
            if (isPlaying) {
                ((Animatable) playingState).start();
            } else
                ((Animatable) pauseState).start();
        }
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

        Glide.with(mView).asDrawable()
                .placeholder(R.drawable.song_placeholder)
                .load(ID3Tags.getBinaryArtwork(mSong.getFilePath()))
                .into(PictureUtils.getTarget(mCover));

        mTitle.setText(mSong.getTitle());
        mTitle.setSelected(true);
        mArtist.setText(mSong.getArtist());
        mSeekBar.setMax(mPlayer.getDuration());

        setDrawable();

        if (mPlayer.isPlaying())
            mPlayPause.setImageDrawable(playingState);
        else
            mPlayPause.setImageDrawable(pauseState);

        startAnimation(mPlayer.isPlaying());
    }

    private void SeekBar() {
        mSeekToRun = new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                mHandler.postDelayed(this, 130);
            }
        };
        getActivity().runOnUiThread(mSeekToRun);

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
        mForward.setOnClickListener(view ->mPlayer.goForward());
        mBackward.setOnClickListener(view ->mPlayer.goBackward());

        mShuffle.setOnClickListener(view ->mPlayer.Shuffle(!mPlayer.isShuffle()));

        mRepeat.setOnClickListener(view ->mPlayer.ListLoop(!mPlayer.isListLoop()));

        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()

    {
        @Override
        public void onProgressChanged (CircularSeekBar circularSeekBar,float progress,
        boolean fromUser){
        if (fromUser) {
            mPlayer.Seek(((int) progress));
        }
    }

        @Override
        public void onStopTrackingTouch (CircularSeekBar seekBar){
    }

        @Override
        public void onStartTrackingTouch (CircularSeekBar seekBar){
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
//            mHandler.removeCallbacks(mSeekToRun);
//            mHandler.removeCallbacks(UpdateSongTime);
        }
    }
}
