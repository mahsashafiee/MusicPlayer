package com.example.musicplayer.controller.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import es.claucookie.miniequalizerlibrary.EqualizerView;


public class ViewHolders {

    private CallBacks callBacks;
    private Context mContext;

    public ViewHolders(Context context) {
        callBacks = (CallBacks) context;
        mContext = context;
    }

    //Handle PlaySong and SongList fragment invoker;
    public interface CallBacks {
        void PlaySong(Song song);

        void SongList(String albumOrArtist, Qualifier qualifier);
    }

    /**
     * SONG VIEW HOLDER CLASS
     */

    public class MusicItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Song> {

        private TextView mTVMusicName, mTVMusicArtist, mDuration;
        private ImageView mIVMusicCover;
        private Song mSong;

        public MusicItems(@NonNull View itemView) {
            super(itemView);

            mIVMusicCover = itemView.findViewById(R.id.item_song_art);
            mTVMusicArtist = itemView.findViewById(R.id.item_song_artist);
            mTVMusicName = itemView.findViewById(R.id.item_song_title);
            mDuration = itemView.findViewById(R.id.item_song_duration);

            itemView.setOnClickListener(view -> {
                callBacks.PlaySong(mSong);
                mTVMusicName.setSelected(true);
            });

        }

        @Override
        public void bindHolder(Song song) {

            mSong = song;

            mTVMusicArtist.setText(mSong.getArtist());
            mTVMusicName.setText(mSong.getTitle());
            mDuration.setText(mSong.getDuration());
            mIVMusicCover.setBackground(mContext.getResources().getDrawable(R.drawable.song_placeholder));

            SetArt art = new SetArt();
            art.execute();

        }

        private class SetArt extends AsyncTask<Void, Void, byte[]> {

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

        private SquareImage mAlbumArt;
        private TextView mTitle;
        private TextView mArtist;
        private Album mAlbum;


        public AlbumItems(@NonNull View itemView) {
            super(itemView);

            mAlbumArt = itemView.findViewById(R.id.item_album_art);
            mTitle = itemView.findViewById(R.id.item_album_title);
            mArtist = itemView.findViewById(R.id.item_album_artist);
            itemView.setOnClickListener(view -> {
                callBacks.SongList(mAlbum.getTitle(), Qualifier.ALBUM);
                mTitle.setSelected(true);
            });

        }

        @Override
        public void bindHolder(Album album) {
            mAlbum = album;
            mTitle.setText(album.getTitle());
            mArtist.setText(album.getAlbumArtist());
            mAlbumArt.setBackground(mContext.getResources().getDrawable(R.drawable.song_placeholder));

            SetArt art = new SetArt();
            art.execute();
        }

        private class SetArt extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                return mAlbum.getArtworkPath();
            }

            @Override
            protected void onPostExecute(String artFile) {
                Glide.with(mContext).asDrawable()
                        .load(artFile)
                        .into(PictureUtils.getTarget(mAlbumArt));
            }
        }
    }


    /**
     * ARTIST VIEW HOLDER CLASS
     */

    public class ArtistItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Artist> {

        private SquareImage mImage;
        private TextView mName;
        private Artist mArtist;

        public ArtistItems(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.item_song_artist);
            mImage = itemView.findViewById(R.id.item_artist_art);

            itemView.setOnClickListener(view -> {
                callBacks.SongList(mArtist.getName(), Qualifier.ARTIST);
                mName.setSelected(true);
            });

        }

        @Override
        public void bindHolder(Artist artist) {
            mArtist = artist;
            mName.setText(artist.getName());
        }
    }

}
