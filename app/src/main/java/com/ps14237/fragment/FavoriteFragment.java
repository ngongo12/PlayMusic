package com.ps14237.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ps14237.R;
import com.ps14237.SearchActivity;
import com.ps14237.adapter.SongAdapter;
import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.Song;
import com.ps14237.model.User;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoriteFragment extends Fragment {

    RecyclerView rcv;
    RelativeLayout layoutSearch;
    EditText edSearch;
    SwipeRefreshLayout swipe;
    ArrayList<Song> list;
    Song song;
    SongAdapter adapter;
    String url;
    String uid;
    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipe = getView().findViewById(R.id.swipe);
        layoutSearch = getView().findViewById(R.id.layoutSearch);
        edSearch = getView().findViewById(R.id.edSearch);
        pref = getContext().getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");
        rcv = getView().findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        getSongList();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSongList();
                swipe.setRefreshing(false);
            }
        });

        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE){
                    handled = true;
                    String keySearch = edSearch.getText().toString();
                    if(!keySearch.isEmpty()){
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra("key", keySearch);
                        startActivity(intent);
                    }
                }
                return handled;
            }
        });
    }

    private void getSongList() {
        Log.d("Song List", "getSongList ");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<ServerResponse> response = api.getListSong(Constants.FAVORITES, uid);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Song List", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    list = resp.getSongs();
                    Log.d("Song List", "onResponse: " + list.size());
                    adapter = new SongAdapter(getContext(), list);
                    rcv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Song List", "onFailure: " + t.getMessage());
            }
        });
    }
}
