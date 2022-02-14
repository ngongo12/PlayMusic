package com.ps14237.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.ps14237.R;
import com.ps14237.adapter.SongMenuAdapter;
import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.Singer;
import com.ps14237.model.Song;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetMenuSongFragment extends BottomSheetDialogFragment {

    private Song song;
    TextView tvSongName, tvSingerName;
    ListView lv;

    ArrayList<Singer> singers;
    SongMenuAdapter adapter;
    SharedPreferences pref;
    String uid;

    public BottomSheetMenuSongFragment(Song song) {
        this.song = song;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singers = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_menu_bottomsheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pref = getContext().getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");

        lv = getView().findViewById(R.id.lv);
        tvSongName = getView().findViewById(R.id.tvSongName);
        tvSingerName = getView().findViewById(R.id.tvSingerName);

        tvSongName.setText(song.getName());
        tvSingerName.setText(song.getSinger_name());

        adapter = new SongMenuAdapter(getActivity(), song);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:{
                        if(song.isLiked()){
                            removeLike(song);
                        }
                        else {
                            insertLike(song);
                        }
                        dismiss();
                        break;
                    }
                    case 1:{
                        addToPlaying(song);
                        break;
                    }
                    case 2:{
                        editSongInfo();
                        break;
                    }
                    case 5:{
                        askForDelete();
                        break;
                    }
                }
                BottomSheetMenuSongFragment.this.dismiss();
            }
        });

    }

    private void addToPlaying(Song song) {
        String arr = pref.getString(Constants.ARRAY_PLAYING, "");
        if (!arr.equals("")){
            String[] array = arr.split(",");
            if(Arrays.asList(array).indexOf(song.getId()+"")>=0){
                Toast.makeText(getContext(), "Bài hát đã có sẵn trong playlist", Toast.LENGTH_SHORT).show();
                return;
            }
            arr += "," + song.getId();
        }
        else{
            arr += song.getId();
        }
        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
        pref.edit().putString(Constants.ARRAY_PLAYING, arr).apply();
        dismiss();
    }

    private void insertLike(Song item) {
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
                    item.setLiked(true);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("like", "onFailure: " + t.getMessage());
            }
        });
    }

    private void removeLike(Song item) {
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
                    item.setLiked(false);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("like", "onFailure: " + t.getMessage());
            }
        });
    }

    private void askForDelete() {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.bottom_nav),"Xóa: " + song.getName(), Snackbar.LENGTH_LONG )
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteSong();
                                }
                            });
        snackbar.show();
    }

    private void deleteSong() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE);
        request.setData(song);

        Call<ServerResponse> response = api.songOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Delete Song", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    Log.d("Delete Song", "Xóa thành công" + resp.getMessage());
                    dismiss();
                }
                else{
                    Log.d("Delete Song", "Xóa không thành công "+ resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Delete Song", "onFailure: " + t.getMessage());
            }
        });
    }

    private void editSongInfo() {
        BottomSheetEditSongFragment bottomSheetEditSongFragment = new BottomSheetEditSongFragment(song);
        bottomSheetEditSongFragment.show(getFragmentManager(), bottomSheetEditSongFragment.getTag());
    }


}
