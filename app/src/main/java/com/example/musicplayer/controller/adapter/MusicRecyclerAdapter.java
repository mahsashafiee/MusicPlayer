package com.example.musicplayer.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Qualifier;

import java.util.List;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List mList ;
    private ViewHolders viewHolders;
    private BindCallBack bindCallBack;
    private Context mContext;
    private Qualifier flag;

    public static String MUSIC_ITEM = "music_item";
    public static String ALBUM_ITEM = "album_item";
    public static String ARTIST_ITEM = "artist_item";

    public MusicRecyclerAdapter(@NonNull Context context ,@NonNull Qualifier view_flag) {
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

        if(flag.equals(Qualifier.ALLSONG)) {
            view = inflater.inflate(R.layout.song_list_item, parent, false);
            bindCallBack = viewHolders.new MusicItems(view);
        }else if(flag.equals(Qualifier.ALBUM)){
            view = inflater.inflate(R.layout.mi_album_item, parent,false);
            bindCallBack = viewHolders.new AlbumItems(view);

        }else if(flag.equals(Qualifier.ARTIST)) {
            view = inflater.inflate(R.layout.mi_artist_item, parent, false);
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
