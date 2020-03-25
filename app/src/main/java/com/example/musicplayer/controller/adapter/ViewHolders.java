package com.example.musicplayer.controller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.repository.PlayList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolders {

    private static final String TAG = "ViewHolders";

    private CallBacks callBacks;
    private Context mContext;

    public ViewHolders(Context context) {
        callBacks = (CallBacks) context;
        mContext = context;
    }

    //Handle PlaySong and SongList fragment invoker;
    public interface CallBacks {
        void SongList(String albumOrArtist, Qualifier qualifier);
    }


    /**
     * ALBUM VIEW HOLDER CLASS
     */
    public class AlbumItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Album> {

        private CircleImageView mAlbumArt;
        private TextView mTitle;
        private TextView mArtist;
        private Album mAlbum;
        private TextView mNumberOfSongs;


        public AlbumItems(@NonNull View itemView) {
            super(itemView);

            mAlbumArt = itemView.findViewById(R.id.item_album_cover);
            mTitle = itemView.findViewById(R.id.item_album_title);
            mArtist = itemView.findViewById(R.id.item_album_artist);
            mNumberOfSongs = itemView.findViewById(R.id.item_total_songs);
            itemView.setOnClickListener(view ->
                    callBacks.SongList(mAlbum.getTitle(), Qualifier.ALBUM));

        }

        @Override
        public void bindHolder(Album album) {
            mAlbum = album;
            mTitle.setText(album.getTitle());
            mArtist.setText(album.getAlbumArtist());
            mAlbumArt.setImageDrawable(mContext.getResources().getDrawable(R.drawable.song_placeholder));
            String items = mContext.getResources()
                    .getQuantityString(R.plurals.total_songs, mAlbum.getSongsNumber(), mAlbum.getSongsNumber());
            mNumberOfSongs.setText(items);

/*            mAlbumArt.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
            itemView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));*/

            Glide.with(mContext).asDrawable()
                    .load(album.getArtworkPath())
                    .placeholder(R.drawable.song_placeholder)
                    .override(100, 100)
                    .into(mAlbumArt);

        }
    }


    /**
     * ARTIST VIEW HOLDER CLASS
     */

    public class ArtistItems extends RecyclerView.ViewHolder implements MusicRecyclerAdapter.BindCallBack<Artist> {

        private CircleImageView mImage;
        private TextView mName;
        private Artist mArtist;
        private TextView mNumberOfSongs;
        private List<String> mImageUrls;

        public ArtistItems(@NonNull View itemView) {
            super(itemView);

            mImageUrls = PlayList.getArtistURLs().getValue();
            mName = itemView.findViewById(R.id.item_song_artist);
            mImage = itemView.findViewById(R.id.item_artist_art);
            mNumberOfSongs = itemView.findViewById(R.id.item_total_songs);

            itemView.setOnClickListener(view -> callBacks.SongList(mArtist.getName(), Qualifier.ARTIST));
        }

        @Override
        public void bindHolder(Artist artist) {
            if (mImageUrls != null) {
                for (String string : mImageUrls) {
                    if (string.equalsIgnoreCase(artist.getName()))
                        Glide.with(mContext).load(string).into(mImage);
                    Log.d(TAG, "onChanged: " + string);
                }
            }
            mArtist = artist;
            mName.setText(artist.getName());
            String items = mContext.getResources()
                    .getQuantityString(R.plurals.total_songs, Integer.valueOf(mArtist.getNumberOfSongs()),
                            Integer.valueOf(mArtist.getNumberOfSongs()));
            mNumberOfSongs.setText(items);
        }
    }
}