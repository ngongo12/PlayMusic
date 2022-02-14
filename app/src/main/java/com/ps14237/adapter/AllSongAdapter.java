package com.ps14237.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ps14237.PlayingActivity;
import com.ps14237.R;
import com.ps14237.SearchActivity;
import com.ps14237.fragment.BottomSheetMenuSongFragment;
import com.ps14237.fragment.LibraryFragment;
import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.Song;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllSongAdapter extends RecyclerView.Adapter<AllSongAdapter.ViewHolder> {

    Context context;
    ArrayList<Song> list;
    ArrayList<Integer> favoriteArr;
    Drawable dLiked, dUnlike;
    SharedPreferences pref;
    String uid;
    LibraryFragment fragment;
    SearchActivity activity;

    public AllSongAdapter(Context context, ArrayList<Song> list, ArrayList<Integer> favoriteArr, LibraryFragment fragment) {
        this.context = context;
        this.list = list;
        this.favoriteArr = favoriteArr;
        this.fragment = fragment;
        this.activity = null;
        dLiked = context.getDrawable(R.drawable.ic_full_heart);
        dUnlike = context.getDrawable(R.drawable.ic_empty_heart);
        pref = context.getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");
    }
    public AllSongAdapter(Context context, ArrayList<Song> list, ArrayList<Integer> favoriteArr, SearchActivity activity) {
        this.context = context;
        this.list = list;
        this.favoriteArr = favoriteArr;
        this.fragment = null;
        this.activity = activity;
        dLiked = context.getDrawable(R.drawable.ic_full_heart);
        dUnlike = context.getDrawable(R.drawable.ic_empty_heart);
        pref = context.getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_layout, parent, false);
        return new AllSongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song item = list.get(position);
        holder.tvSongName.setText(item.getName());
        holder.tvSingerName.setText(item.getSinger_name());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(favoriteArr.indexOf(item.getId())>=0){
            holder.ivLike.setImageDrawable(dLiked);
            item.setLiked(true);
        }
        else
        {
            holder.ivLike.setImageDrawable(dUnlike);
            item.setLiked(false);
        }
        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.ivLike.setEnabled(false);
                if(item.isLiked()){
                    removeLike(item, holder.ivLike);
                }
                else{
                    insertLike(item, holder.ivLike);
                }
            }
        });

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(fragment != null){
                    fragment.showMenuFragment(item);
                }

                if(activity != null){
                    activity.showMenuFragment(item);
                }
                return false;
            }
        });
    }

    private void insertLike(Song item, ImageView ivLike) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LIKE);

        Song song = new Song();
        song.setId(item.getId());
        song.setUser_id(uid);
        request.setData(song);

        Call<ServerResponse> response = api.songOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("like", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    ivLike.setEnabled(true);
                    item.setLiked(true);
                    ivLike.setImageDrawable(dLiked);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("like", "onFailure: " + t.getMessage());
                ivLike.setEnabled(true);
            }
        });
    }

    private void removeLike(Song item, ImageView ivLike) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UNLIKE);

        Song song = new Song();
        song.setId(item.getId());
        song.setUser_id(uid);
        request.setData(song);

        Call<ServerResponse> response = api.songOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("like", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    ivLike.setEnabled(true);
                    item.setLiked(false);
                    ivLike.setImageDrawable(dUnlike);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("like", "onFailure: " + t.getMessage());
                ivLike.setEnabled(true);
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
        ImageView ivLike, ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvSingerName = itemView.findViewById(R.id.tvSingerName);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
