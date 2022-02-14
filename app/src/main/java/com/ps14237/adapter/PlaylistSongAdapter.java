package com.ps14237.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ps14237.PlayingActivity;
import com.ps14237.R;
import com.ps14237.fragment.RecentFragment;
import com.ps14237.model.Song;
import com.ps14237.support.Constants;

import java.util.ArrayList;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> list;
    SharedPreferences pref;
    RecentFragment fragment;
    int id_playing;
    Drawable bgPlay, bgNone, dPlay, dPause;

    public PlaylistSongAdapter(Context context, ArrayList<Song> list, RecentFragment fragment) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
        pref = context.getSharedPreferences("user", 0);
        id_playing = pref.getInt(Constants.ID_PLAYING, 6);
        bgPlay = context.getDrawable(R.drawable.gradient_horizon_bg_color);
        bgNone = context.getDrawable(R.drawable.bg_white);
        dPause = context.getDrawable(R.drawable.ic_pause);
        dPlay = context.getDrawable(R.drawable.ic_play);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_song_layout, parent, false);
        return new PlaylistSongAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song item = list.get(position);
        holder.tvSongName.setText(item.getName());
        holder.tvSingerName.setText(item.getSinger_name());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.edit().putInt(Constants.ID_PLAYING, item.getId()).apply();
                fragment.getSongList();
                fragment.stopMusic();
                Log.d("Songlist", "onClick: ");
            }
        });
        if(id_playing == item.getId()){
            holder.layout.setBackground(bgPlay);
            holder.ivPause.setImageDrawable(dPlay);
            holder.tvSongName.setTextColor(Color.parseColor("#ffffff"));
            holder.tvSingerName.setTextColor(Color.parseColor("#ffffff"));
        }
        else {
            holder.layout.setBackground(bgNone);
            holder.ivPause.setImageDrawable(dPause);
            holder.tvSongName.setTextColor(R.color.grey);
            holder.tvSingerName.setTextColor(R.color.grey);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView layout;
        TextView tvSongName, tvSingerName;
        ImageView ivPause, ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvSingerName = itemView.findViewById(R.id.tvSingerName);
            ivPause = itemView.findViewById(R.id.ivPause);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
