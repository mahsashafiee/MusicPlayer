package com.example.musicplayer.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List mList = new ArrayList();
    private ViewHolders viewHolders;
    private Context mContext;
    private Qualifier flag;

    public MusicRecyclerAdapter(@NonNull Context context, @NonNull Qualifier view_flag) {
        viewHolders = new ViewHolders(context);
        flag = view_flag;
        mContext = context;
    }

    public void setList(List list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (flag.equals(Qualifier.ALLSONG))
            return viewHolders.new MusicItems(inflater.inflate(R.layout.song_list_item, parent, false));
         else if (flag.equals(Qualifier.ALBUM))
            return viewHolders.new AlbumItems(inflater.inflate(R.layout.mi_album_item, parent, false));

        return viewHolders.new ArtistItems(inflater.inflate(R.layout.mi_artist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((BindCallBack)holder).bindHolder(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface BindCallBack<O>{
        void bindHolder(O model);
    }
}
