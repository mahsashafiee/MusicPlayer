package com.example.musicplayer.controller.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.utils.ID3Tags;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.PlayList;

import org.jaudiotagger.tag.datatype.Artwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.claucookie.miniequalizerlibrary.EqualizerView;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.SongViewHolder> {

    private Context mContext;
    private List<Song> mSongs = new ArrayList<>();
    private CallBacks mCallBack;
    private int mSelectedItem = -1;
    private Map<CircleImageView, String> mImageMap = new HashMap<>();
    private Handler handler;
    private boolean isNotify = false;
    private int mNotify ;

    public SongRecyclerAdapter(Context context) {
        mContext = context;
        mCallBack = (CallBacks) context;
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
        notifyDataSetChanged();

    }

    public interface CallBacks {
        void PlaySong(Song song);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.setOnKeyListener((v, keyCode, event) -> {
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

            // Return false if scrolled to the bounds and allow focus to move off the list
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return tryMoveSelection(lm, 1);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return tryMoveSelection(lm, -1);
                }
            }

            return false;
        });

        PlayList.getLiveSong().observe((LifecycleOwner) mContext, song -> recyclerView.post(() -> {
            notifyItemChanged(mSelectedItem);
            mSelectedItem = mSongs.indexOf(song);
            notifyItemChanged(mSelectedItem);
            notifyItemChanged((mSelectedItem - 1 + mSongs.size()) % mSongs.size());
            notifyItemChanged((mSelectedItem + 1) % mSongs.size());
        }));
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int nextSelectItem = mSelectedItem + direction;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (nextSelectItem >= 0 && nextSelectItem < getItemCount()) {
            notifyItemChanged(mSelectedItem);
            mSelectedItem = nextSelectItem;
            notifyItemChanged(mSelectedItem);
            lm.scrollToPosition(mSelectedItem);
            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_list_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.itemView.setSelected(mSelectedItem == position);
        holder.bindHolder(position);
        mNotify++;
        if (mNotify == 3 && isNotify){
            isNotify = false;
            mNotify = 0;
        }
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        private TextView mTVMusicName, mTVMusicArtist, mDuration;
        private CircleImageView mIVMusicCover;
        private EqualizerView mEqualizer;
        private Song mSong;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);

            mIVMusicCover = itemView.findViewById(R.id.item_song_art);
            mTVMusicArtist = itemView.findViewById(R.id.item_song_artist);
            mTVMusicName = itemView.findViewById(R.id.item_song_title);
            mEqualizer = itemView.findViewById(R.id.equalizer);
            mDuration = itemView.findViewById(R.id.item_song_duration);

            itemView.setOnClickListener(view -> {
                isNotify = true;
                mNotify = 0;
                mCallBack.PlaySong(mSong);
                notifyItemChanged(mSelectedItem);
            });

        }


        void bindHolder(int position) {

            mSong = mSongs.get(position);

            mTVMusicArtist.setText(mSong.getArtist());
            mTVMusicName.setText(mSong.getTitle());
            mDuration.setText(mSong.getDuration());
            if ((position == mSelectedItem || position == mSelectedItem + 1 || position == mSelectedItem - 1) && isNotify){
            }
            else
                mIVMusicCover.setImageDrawable(mContext.getResources().getDrawable(R.drawable.song_placeholder));

            if (itemView.isSelected()) {
                mEqualizer.setVisibility(View.VISIBLE);
                mEqualizer.animateBars();
            } else {
                mEqualizer.setVisibility(View.INVISIBLE);
                mEqualizer.stopBars();
            }

            mImageMap.put(mIVMusicCover, mSong.getFilePath());
        }

    }

    public void setImage() {

        if (mImageMap.size() == 0) {
            if (handler != null)
                return;
            handler = new Handler();
            handler.postDelayed(this::setImage, 1000);
        }
        for (CircleImageView iView : mImageMap.keySet()) {
            if (!iView.getDrawable().getConstantState().equals(mContext.getResources().getDrawable(R.drawable.song_placeholder).getConstantState()))
                continue;
            SetArt art = new SetArt(iView);
            art.execute();
        }
    }

    private class SetArt extends AsyncTask<Void, Void, byte[]> {
        private CircleImageView mImageView;

        public SetArt(CircleImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            try {
                Artwork artwork = ID3Tags.getArtwork(mImageMap.get(mImageView));
                return artwork.getBinaryData();

            } catch (OutOfMemoryError error) {
                return null;
            } catch (NullPointerException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Glide.with(mImageView).asDrawable()
                    .load(bytes)
                    .override(100, 100)
                    .placeholder(R.drawable.song_placeholder)
                    .into(mImageView);
        }
    }

}
