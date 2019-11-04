package com.example.musicplayer.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

import java.util.List;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List mList ;
    private ViewHolders viewHolders;
    private BindCallBack bindCallBack;
    private Context mContext;
    private String flag;

    public static String MUSIC_ITEM = "music_item";
    public static String ALBUM_ITEM = "album_item";
    public static String ARTIST_ITEM = "artist_item";

    public MusicRecyclerAdapter(@NonNull Context context ,@NonNull String view_flag) {
        viewHolders = new ViewHolders(context);
        flag = view_flag;
        mContext = context;
    }

    public void setList(List list) {
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        if(flag.equals(MUSIC_ITEM)) {
            view = inflater.inflate(R.layout.music_list_item, parent, false);
            bindCallBack = viewHolders.new MusicItems(view);
        }else if(flag.equals(ALBUM_ITEM)){
            view = inflater.inflate(R.layout.mi_album_item, parent,false);
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

    public interface BindCallBack<O>{
        void bindHolder(O model);
    }
}
