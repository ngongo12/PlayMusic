package com.ps14237.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ps14237.DeviceSongsActivity;
import com.ps14237.R;
import com.ps14237.fragment.BottomSheetAddSongFragment;
import com.ps14237.fragment.LibraryFragment;
import com.ps14237.model.Song;

import java.util.ArrayList;

public class SongsDeviceAdapter extends RecyclerView.Adapter<SongsDeviceAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> list;
    DeviceSongsActivity activity;

    public SongsDeviceAdapter(Context context, ArrayList<Song> list, DeviceSongsActivity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_song_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song item = list.get(position);
        holder.tvSongName.setText(item.getName());
        holder.tvSingerName.setText(item.getSinger_name());
        holder.ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showAddSongBottomSheet(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView layout;
        TextView tvSongName, tvSingerName;
        ImageView ivUpload, ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvSingerName = itemView.findViewById(R.id.tvSingerName);
            ivUpload = itemView.findViewById(R.id.ivUpload);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }

}
