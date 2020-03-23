package com.example.musicplayer.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.sharedPreferences.MusicPreferences;
import com.example.musicplayer.utils.ID3Tags;
import com.example.musicplayer.utils.PictureUtils;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;
import com.example.musicplayer.repository.SongRepository;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jaudiotagger.tag.datatype.Artwork;

public class PlayBackBottomBar {

    private RelativeLayout mParentLayout;
    private SeekBar mSeekBar;
    private RoundedImageView mCover;
    private MutableLiveData<Integer> mDominantColor;
    private TextView mSongName, mSongArtist;
    private ImageView mForward, mBackward;
    private ImageView mPlay;
    private Activity mActivity;
    private PlayerService mPlayer;
    private Song mSong;
    private ForBackListener mForwardListener = new ForBackListener() {
        @Override
        public void run() {
            mPlayer.onFastForward();
            super.run();
        }
    };
    private ForBackListener mBackwardListener = new ForBackListener() {
        @Override
        public void run() {
            mPlayer.onFastBackward();
            super.run();
        }
    };

    public PlayBackBottomBar(Activity activity) {
        mActivity = activity;
        mDominantColor = SongRepository.getInstance(mActivity).getDominantColor();
        initView();
        setLastSong();
        mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_play_arrow));
    }

    private void initView() {
        mParentLayout = mActivity.findViewById(R.id.song_bar);
        mCover = mActivity.findViewById(R.id.song_bar_artwork);
        mSeekBar = mActivity.findViewById(R.id.seekBar);
        mForward = mActivity.findViewById(R.id.songBar_skip_next);
        mBackward = mActivity.findViewById(R.id.songBar_skip_previous);
        mPlay = mActivity.findViewById(R.id.play_pause);
        mSongArtist = mActivity.findViewById(R.id.song_bar_artist);
        mSongName = mActivity.findViewById(R.id.song_bar_title);
    }

    private void setLastSong() {
        mSong = SongRepository.getInstance(mActivity).findSongById(MusicPreferences.getLastMusic(mActivity));
        if (mSong != null) {
            setupArtwork();
            mParentLayout.setVisibility(View.VISIBLE);
            PlayList.getLiveSong().setValue(mSong);
            mSeekBar.setMax(mSong.getIntDuration());
            mSeekBar.setProgress(MusicPreferences.getMusicPosition(mActivity));
        }
        else
            mParentLayout.setVisibility(View.GONE);

        if(PlayList.getSongList() == null)
            PlayList.setSongList(SongRepository.getInstance(mActivity).getLiveSong().getValue());
    }

    public void initService(PlayerService service) {
        mPlayer = service;
        mPlayer.setIndex(PlayList.getSongList().indexOf(mSong));

        PlayList.getLiveSong().observe((LifecycleOwner) mActivity, song -> {
            if (song != null) {
                mSong = song;
                setupArtwork();
                SeekBar();
                mParentLayout.setVisibility(View.VISIBLE);
            }
        });

        mPlayer.isPaused().observe((LifecycleOwner) mActivity, aBoolean -> {
            if(aBoolean)
                mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_play_arrow));
            else
                mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_pause));
        });

        BottomAppBarListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void BottomAppBarListener() {
        mParentLayout.setOnClickListener(view -> mActivity.startActivity(SingleSongActivity.newIntent(mActivity, mSong)));
        mPlay.setOnClickListener(view -> {
            if (mPlayer.isStop()) {
                mActivity.startService(PlayerService.newIntent(mActivity, mSong));
                SeekBar();
            } else
                mPlayer.Pause();
            if (!mPlayer.isPlaying())
                mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_play_arrow));
            else
                mPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_pause));
        });

        mForward.setOnClickListener(view -> mPlayer.goForward());
        mBackward.setOnClickListener(view -> mPlayer.goBackward());

        mForward.setOnTouchListener(mForwardListener);
        mBackward.setOnTouchListener(mBackwardListener);

        mForward.setOnLongClickListener(mForwardListener);
        mBackward.setOnLongClickListener(mBackwardListener);
    }

    private void SeekBar() {
        Handler mSeekHandler = new Handler();
        Runnable mSeekToRun = new Runnable() {
            @Override
            public void run() {
                if(mPlayer.isStop())
                    return;
                mSeekBar.setMax(mPlayer.getDuration());
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                MusicPreferences.setMusicPosition(mActivity, mPlayer.getCurrentPosition());
                mSeekHandler.postDelayed(this, 130);
            }
        };
        mSeekHandler.post(mSeekToRun);

    }

    private void setupArtwork() {

        Artwork artwork = ID3Tags.getArtwork(mSong.getFilePath());
        Bitmap bitmap;

        if (artwork == null)
            bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.song_placeholder);
        else
            bitmap = BitmapFactory.decodeByteArray(artwork.getBinaryData(), 0, artwork.getBinaryData().length);

        Glide.with(mActivity).asDrawable()
                .placeholder(R.drawable.song_placeholder)
                .load(bitmap)
                .override(100, 100)
                .into(mCover);

        PictureUtils.getDominantColor(mActivity, mDominantColor, bitmap);

        mDominantColor.observe((LifecycleOwner) mActivity, integer -> {
            PictureUtils.setBackgroundGradient(mActivity, integer);
        });


        mSongArtist.setText(mSong.getArtist());
        mSongName.setText(mSong.getTitle());
    }

    public static class ForBackListener implements View.OnLongClickListener, Runnable, View.OnTouchListener {

        private Handler mHandler = new Handler();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                mHandler.removeCallbacks(ForBackListener.this);
            return false;
        }


        @Override
        public boolean onLongClick(View view) {
            mHandler.post(this);
            return true;
        }

        @Override
        public void run() {
            mHandler.postDelayed(this, 500);
        }
    }
}
