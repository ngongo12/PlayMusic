package com.ps14237.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ps14237.R;
import com.ps14237.adapter.PlaylistSongAdapter;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.Song;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecentFragment extends Fragment {

    RecyclerView rcv;
    TextView tvSongName, tvSingerName;
    SeekBar seekbar;
    ImageView ivPause, ivRight, ivLeft;
    SwipeRefreshLayout swipe;
    ArrayList<Song> list;
    Song song;
    PlaylistSongAdapter adapter;
    String url, arr;
    String uid;
    int id_playing;
    boolean seekBarRunning = false;
    SharedPreferences pref;

    int UPDATE_TIME = 500;
    private final Handler handler = new Handler();
    private final Runnable updatePositionRunable = new Runnable() {
        @Override
        public void run() {
            updatePossition();
        }
    };

    private void updatePossition() {
        handler.removeCallbacks(updatePositionRunable);
        seekbar.setProgress(player.getCurrentPosition());
        handler.postDelayed(updatePositionRunable, UPDATE_TIME);
    }


    public static MediaPlayer player;
    boolean isPlaying = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSongName = getView().findViewById(R.id.tvSongName);
        tvSingerName = getView().findViewById(R.id.tvSingerName);
        seekbar = getView().findViewById(R.id.seekbar);
        ivPause = getView().findViewById(R.id.ivPause);
        ivRight = getView().findViewById(R.id.ivRight);
        ivLeft = getView().findViewById(R.id.ivLeft);
        swipe = getView().findViewById(R.id.swipe);
        pref = getContext().getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");
        rcv = getView().findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        getSongList();
        changeStateButton(false);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSongList();
                swipe.setRefreshing(false);
            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player == null){
                    startMusic(song);
                }
                else{
                    if (player.isPlaying()){
                        pauseMusic();
                    }
                    else {
                        playMusic();
                    }
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(player != null && seekBarRunning){
                    player.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarRunning = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarRunning = false;
            }
        });

    }

    private void startMusic(Song song) {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //set seekbar
        seekbar.setProgress(0, true);


        //tắt hoạt động của seekbar
        handler.removeCallbacks(updatePositionRunable);

        try {
            player.setDataSource(song.getUrl());
            player.prepare();
            changeStateButton(true);
            seekbar.setMax(player.getDuration());
            Log.d("Seekbar", "startMusic: " + player.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
        playMusic();
    }

    private void playMusic() {
        if(!player.isPlaying()) player.start();
        updatePossition();
        changeStateButton(true);
    }

    private void pauseMusic(){
        player.pause();
        changeStateButton(false);
    }

    private void changeStateButton(boolean b){
        if (b){
            ivPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_play));
        }
        else{
            ivPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_pause));
        }
    }

    public void stopMusic(){
        handler.removeCallbacks(updatePositionRunable);
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
        seekbar.setProgress(0);
        changeStateButton(false);
    }

    public void getSongList() {
        arr = pref.getString(Constants.ARRAY_PLAYING, "");
        id_playing = pref.getInt(Constants.ID_PLAYING, 6);
        Log.d("Song List", "getSongList ");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Log.d("Song List", "playlish: " +arr);
        Call<ServerResponse> response = api.getListSong(Constants.PLAYING_LIST, uid, arr);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Song List", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    list = resp.getSongs();
                    Log.d("Song List", "onResponse: " + list.size());
                    adapter = new PlaylistSongAdapter(getContext(), list, RecentFragment.this);
                    rcv.setAdapter(adapter);

                    //lấy bài hát đang phát
                    for(Song item : list){
                        if (item.getId() == id_playing){
                            song = item;
                            tvSongName.setText(item.getName());
                            tvSingerName.setText(item.getSinger_name());
                            Log.d("URL", "onResponse: " + item.getUrl());
                            if(player != null) {
                                changeStateButton(player.isPlaying());
                            }

                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Song List", "onFailure: " + t.getMessage());
            }
        });
    }
}
