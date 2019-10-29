package com.example.musicplayer.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

import java.util.List;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Song> mList ;
    private ViewHolders viewHolders;
    private BindCallBack bindCallBack;
    private Context mContext;
    private String flag;

    public static String MUSIC_ITEM = "music_item";
    public static String ALBUM_ITEM = "album_item";
    public static String ARTIST_ITEM = "artist_item";

    public MusicRecyclerAdapter(List<Song> list,@NonNull Context context ,@NonNull String view_flag) {
        mList = list;
        viewHolders = new ViewHolders(context,mList);
        flag = view_flag;
        mContext = context;
    }

    public void setList(List<Song> list) {
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if(flag.equals(MUSIC_ITEM)) {
            view = inflater.inflate(R.layout.song_list_item, parent, false);
            bindCallBack = viewHolders.new MusicItems(view);
        }else if(flag.equals(ALBUM_ITEM)){
            view = inflater.inflate(0, parent,false);
            bindCallBack = viewHolders.new AlbumItems(view);

        }else if(flag.equals(ARTIST_ITEM)) {
            view = inflater.inflate(0, parent, false);
            bindCallBack = viewHolders.new ArtistItems(view);
        }

        return (RecyclerView.ViewHolder) bindCallBack;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        bindCallBack.bindHolder(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface BindCallBack{
        void bindHolder(Song song);
    }

    public void Releaser(){
        viewHolders.Releaser();
    }
}
