package com.example.musicplayer.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.model.Album;
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


    public class MusicItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Song> {
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

    public class AlbumItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Album> {

        private View itemView;
        private ImageView mAlbumArt;
        private TextView mAlbum;
        private TextView mArtist;


        public AlbumItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            mAlbumArt = itemView.findViewById(R.id.item_album_art);
            mAlbum = itemView.findViewById(R.id.item_album_title);
            mArtist = itemView.findViewById(R.id.item_album_artist);

        }

        @Override
        public void bindHolder(Album album) {
            mAlbumArt.setImageBitmap(PictureUtils.getScaledBitmap(album.getArtworkPath(),mAlbumArt));
            mAlbum.setText(album.getTitle());
            mArtist.setText(album.getAlbumArtist());
            itemView.setOnClickListener(view -> {
            });
        }
    }

    public class ArtistItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Song> {

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
