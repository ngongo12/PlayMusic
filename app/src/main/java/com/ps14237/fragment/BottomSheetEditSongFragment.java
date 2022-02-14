package com.ps14237.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ps14237.R;
import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.Singer;
import com.ps14237.model.Song;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetEditSongFragment extends BottomSheetDialogFragment {

    private Song song;
    Spinner spinner;
    EditText edSongName;
    Button btnUpload;
    private String uid;
    SharedPreferences pref;

    ArrayList<Singer> singers;
    ArrayAdapter<Singer> adapter;
    Singer selectedSinger;
    int indexOfSinger = 0;

    public BottomSheetEditSongFragment(Song song) {
        this.song = song;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singers = new ArrayList<>();
        pref = getContext().getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_song_bottomsheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = getView().findViewById(R.id.spinner);
        edSongName = getView().findViewById(R.id.tvSongName);
        btnUpload = getView().findViewById(R.id.btnUpload);

        edSongName.setText(song.getName());

        getListSinger();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSinger = (Singer) adapterView.getAdapter().getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpload();
            }
        });
    }

    private void onUpload() {
        uid = pref.getString(Constants.UID, "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        song.setName(edSongName.getText().toString());
        song.setSinger_id(selectedSinger.getId()+"");

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.EDIT);
        request.setData(song);

        Call<ServerResponse> response = api.songOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Edit Song", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    Toast.makeText(getContext(), "Upload " + song.getName() + " thành công", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Upload " + song.getName() + " không thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Edit Song", "onFailure: " + t.getMessage());
            }
        });
    }

    private void getListSinger() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<ServerResponse> response = api.getSigners();
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Song List", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    singers = resp.getSingers();
                    adapter = new ArrayAdapter<Singer>(getContext(), R.layout.support_simple_spinner_dropdown_item, singers);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
                for(int i = 0; i <= singers.size(); i++){
                    if (song.getSinger_id().equals(singers.get(i).getId()+"")){
                        indexOfSinger = i;
                        spinner.setSelection(indexOfSinger);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Singer List", "onFailure: " + t.getMessage());
            }
        });

    }

}
