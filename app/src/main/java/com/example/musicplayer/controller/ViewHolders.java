package com.example.musicplayer.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Song;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolders {

    private CallBacks callBacks;
    private Context mContext;

    public ViewHolders(Context context){
        callBacks = (CallBacks) context;
        mContext = context;
    }

    public interface CallBacks {
        void SingleSong(Song song);
        void SongList(String albumName);
    }


    public class MusicItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Song> {
        private ConstraintLayout parentLayout;
        private TextView mTVMusicName, mTVMusicArtist, mTVMusicDuration;
        private CircleImageView mIVMusicCover;
        private View itemView;
        private Song mSong;

        public MusicItems(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            mIVMusicCover = itemView.findViewById(R.id.item_music_cover);
            mTVMusicArtist = itemView.findViewById(R.id.item_artist_name);
            mTVMusicDuration = itemView.findViewById(R.id.item_music_duration);
            mTVMusicName = itemView.findViewById(R.id.item_music_name);
            this.itemView = itemView;

            itemView.setOnClickListener(view -> {
                callBacks.SingleSong(mSong);
            });

        }
        @Override
        public void bindHolder(Song song){

            mSong = song;
            mTVMusicArtist.setText(song.getArtist());
            mTVMusicName.setText(song.getTitle());
            mTVMusicDuration.setText(song.getDuration());
            if(song.getArtworkPath() != null) {
                Glide.with(mContext).asDrawable().load(song.getArtworkPath()).into(PictureUtils.getTarget(mIVMusicCover));
            }

        }
    }

    public class AlbumItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Album> {

        private View itemView;
        private ImageView mAlbumArt;
        private TextView mTitle;
        private TextView mArtist;
        private Album mAlbum;


        public AlbumItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            mAlbumArt = itemView.findViewById(R.id.item_album_art);
            mTitle = itemView.findViewById(R.id.item_album_title);
            mArtist = itemView.findViewById(R.id.item_album_artist);
            itemView.setOnClickListener(view -> {
                callBacks.SongList(mAlbum.getTitle());
            });

        }

        @Override
        public void bindHolder(Album album) {
            mAlbum = album;
            if(album.getArtworkPath() != null) {
                Glide.with(mContext).asDrawable().load(album.getArtworkPath()).into(PictureUtils.getTarget(mAlbumArt));
            }
            mTitle.setText(album.getTitle());
            mArtist.setText(album.getAlbumArtist());
        }
    }

    public class ArtistItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Song> {

        private View itemView;

        public ArtistItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(view -> {
            });

        }

        @Override
        public void bindHolder(Song song){
        }
    }

}
