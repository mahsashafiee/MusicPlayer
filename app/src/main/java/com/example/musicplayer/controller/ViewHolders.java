package com.example.musicplayer.controller;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Song;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolders {

    private PlayerManager playerManager;
    private CallBacks callBacks;
    private List<Song> mPlayList;

    public ViewHolders(Context context , List<Song> playList){
        callBacks = (CallBacks) context;
        playerManager = new PlayerManager(context);
        mPlayList = playList;
    }

    public interface CallBacks {
        void SingleSong(Song song);
    }

    public void Releaser(){
        playerManager.Release();
    }


    public class MusicItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack {
        private ConstraintLayout parentLayout;
        private TextView mTVMusicName, mTVMusicArtist, mTVMusicDuration;
        private CircleImageView mIVMusicCover;
        private View itemView;

        public MusicItems(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            mIVMusicCover = itemView.findViewById(R.id.item_music_cover);
            mTVMusicArtist = itemView.findViewById(R.id.item_artist_name);
            mTVMusicDuration = itemView.findViewById(R.id.item_music_duration);
            mTVMusicName = itemView.findViewById(R.id.item_music_name);
            this.itemView = itemView;

        }
        @Override
        public void bindHolder(Song song){

            mTVMusicArtist.setText(song.getArtist());
            mTVMusicName.setText(song.getTitle());
            mTVMusicDuration.setText(song.getDuration());
            mIVMusicCover.setImageBitmap(PictureUtils.getScaledBitmap(song.getArtworkPath(),mIVMusicCover));

            itemView.setOnClickListener(view -> {
                playerManager.Play(mPlayList,mPlayList.indexOf(song));
                //playerManager.Play(song.getPath());
                //callBacks.SingleSong(song);
            });
        }
    }

    public class AlbumItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack {

        private View itemView;

        public AlbumItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

        }

        @Override
        public void bindHolder(Song song){

            itemView.setOnClickListener(view -> {
            });
        }
    }

    public class ArtistItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack {

        private View itemView;

        public ArtistItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

        }

        @Override
        public void bindHolder(Song song){

            itemView.setOnClickListener(view -> {
            });
        }
    }

}
