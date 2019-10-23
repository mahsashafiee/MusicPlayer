package com.example.musicplayer.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicHolder> {

    private List<Song> mList ;
    private Context mContext;

    public MusicRecyclerAdapter(List<Song> list, Context context) {
        mList = list;
        mContext = context;
    }

    public void setList(List<Song> list) {
        mList = list;
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.music_list_item,parent,false);

        return new MusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        holder.bindHolder(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MusicHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout parentLayout;
        private TextView mTVMusicName, mTVMusicArtist, mTVMusicDuration;
        private CircleImageView mIVMusicCover;

        public MusicHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            mIVMusicCover = itemView.findViewById(R.id.item_music_cover);
            mTVMusicArtist = itemView.findViewById(R.id.item_artist_name);
            mTVMusicDuration = itemView.findViewById(R.id.item_music_duration);
            mTVMusicName = itemView.findViewById(R.id.item_music_name);

        }
        public void bindHolder(Song song){

            mTVMusicArtist.setText(song.getArtist());
            mTVMusicName.setText(song.getTitle());
            mTVMusicDuration.setText(song.getDuration());
            mIVMusicCover.setImageBitmap(song.getArtwork());
        }
    }
}
