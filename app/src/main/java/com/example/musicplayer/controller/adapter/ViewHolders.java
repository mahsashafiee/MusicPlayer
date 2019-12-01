package com.example.musicplayer.controller.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ID3Tags;
import com.example.musicplayer.Utils.PictureUtils;
import com.example.musicplayer.Utils.SquareImage;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolders {

    private CallBacks callBacks;
    private Context mContext;

    public ViewHolders(Context context) {
        callBacks = (CallBacks) context;
        mContext = context;
    }

    //Handle SingleSong and SongList fragment invoker;
    public interface CallBacks {
        void SingleSong(Song song);

        void SongList(String albumOrArtist, Qualifier qualifier);
    }

    /**
     * SONG VIEW HOLDER CLASS
     */

    public class MusicItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Song> {

        private TextView mTVMusicName, mTVMusicArtist, mDuration;
        private ImageView mIVMusicCover;
        private View itemView;
        private Song mSong;

        public MusicItems(@NonNull View itemView) {
            super(itemView);

            mIVMusicCover = itemView.findViewById(R.id.item_song_art);
            mTVMusicArtist = itemView.findViewById(R.id.item_song_artist);
            mTVMusicName = itemView.findViewById(R.id.item_song_title);
            mDuration = itemView.findViewById(R.id.item_song_duration);
            this.itemView = itemView;

            itemView.setOnClickListener(view -> {
                callBacks.SingleSong(mSong);
            });

            FindFilesArt art = new FindFilesArt();
            art.execute();

        }

        @Override
        public void bindHolder(Song song) {

            mSong = song;

            mTVMusicArtist.setText(song.getArtist());
            mTVMusicName.setText(song.getTitle());
            mDuration.setText(song.getDuration());

        }

        private class FindFilesArt extends AsyncTask<Void, Void, byte[]> {


            @Override
            protected byte[] doInBackground(Void... voids) {
                if (mSong != null)
                    return ID3Tags.getBinaryArtwork(mSong.getFilePath());
                else return null;
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                Glide.with(mContext).asDrawable()
                        .load(bytes)
                        .placeholder(R.drawable.song_placeholder)
                        .into(PictureUtils.getTarget(mIVMusicCover));
            }
        }

    }


    /**
     * ALBUM VIEW HOLDER CLASS
     */
    public class AlbumItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Album> {

        private View itemView;
        private SquareImage mAlbumArt;
        private TextView mTitle;
        private TextView mArtist;
        private Album mAlbum;


        public AlbumItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            mAlbumArt = itemView.findViewById(R.id.item_album_art);
            mTitle = itemView.findViewById(R.id.item_album_title);
            mArtist = itemView.findViewById(R.id.item_album_artist);
            itemView.setOnClickListener(view -> callBacks.SongList(mAlbum.getTitle(), Qualifier.ALBUM));

        }

        @Override
        public void bindHolder(Album album) {
            mAlbum = album;
            if (album.getArtworkPath() != null) {
                Glide.with(mContext).asDrawable().load(album.getArtworkPath()).into(PictureUtils.getTarget(mAlbumArt));
            }
            mTitle.setText(album.getTitle());
            mArtist.setText(album.getAlbumArtist());
        }
    }


    /**
     * ARTIST VIEW HOLDER CLASS
     */

    public class ArtistItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Artist> {

        private View itemView;
        private SquareImage mImage;
        private TextView mName;
        private Artist mArtist;

        public ArtistItems(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            mName = itemView.findViewById(R.id.item_song_artist);
            mImage = itemView.findViewById(R.id.item_artist_art);
            itemView.setOnClickListener(view -> {
                callBacks.SongList(mArtist.getName(), Qualifier.ARTIST);
            });

        }

        @Override
        public void bindHolder(Artist artist) {
            mArtist = artist;
            mName.setText(artist.getName());
        }
    }

}
