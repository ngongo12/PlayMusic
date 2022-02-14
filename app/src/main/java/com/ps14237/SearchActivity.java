package com.ps14237;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ps14237.adapter.AllSongAdapter;
import com.ps14237.fragment.BottomSheetMenuSongFragment;
import com.ps14237.fragment.LibraryFragment;
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

public class SearchActivity extends AppCompatActivity {

    ImageView ivSearch;
    EditText edSearch;
    TextView tvResult;
    SwipeRefreshLayout swipe;
    RecyclerView rcv;
    AllSongAdapter adapter;
    ArrayList<Song> list;
    ArrayList<Integer> favoriteArr;
    SharedPreferences pref;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edSearch = findViewById(R.id.edSearch);
        tvResult = findViewById(R.id.tvResult);
        swipe = findViewById(R.id.swipe);
        rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(this));

        pref = getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");

        //
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        if (key != null){
            gotoSearch(key);
        }

        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE){
                    handled = true;
                    String keySearch = edSearch.getText().toString();
                    if(keySearch.isEmpty()){
                        tvResult.setText("Từ khóa bị rỗng");
                        if(list != null && adapter != null){
                            list.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        gotoSearch(keySearch);
                    }
                }
                return handled;
            }
        });
    }

    private void gotoSearch(String keySearch) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<ServerResponse> response = api.search(Constants.SEARCH, uid, keySearch);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Song List", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    list = resp.getSongs();
                    favoriteArr = resp.getFavoriteArr();
                    Log.d("Song List", "onResponse: " + list.size());
                    adapter = new AllSongAdapter(SearchActivity.this, list, favoriteArr, SearchActivity.this);
                    rcv.setAdapter(adapter);
                    tvResult.setText("Tìm thấy " + list.size() + " kết quả phù hợp");
                }
                else {
                    tvResult.setText("Không tìm thấy kết quả nào");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Song List", "onFailure: " + t.getMessage());
                tvResult.setText("Không tìm thấy kết quả nào");
            }
        });
    }
    public void showMenuFragment(Song song){
        BottomSheetMenuSongFragment bottomSheetMenuSongFragment = new BottomSheetMenuSongFragment(song);
        bottomSheetMenuSongFragment.show(getSupportFragmentManager(), bottomSheetMenuSongFragment.getTag());
    }
}