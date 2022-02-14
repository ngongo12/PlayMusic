package com.ps14237.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ps14237.R;
import com.ps14237.adapter.SongAdapter;
import com.ps14237.model.ServerRequest;
import com.ps14237.model.ServerResponse;
import com.ps14237.model.Song;
import com.ps14237.support.API;
import com.ps14237.support.Constants;

import java.io.File;
import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadSongService extends Service {
    FirebaseStorage storage;
    StorageReference storageRef;
    private String songName;
    private String progress;
    private String url;
    private String id;
    private String uid;
    private int singer_id;
    NotificationCompat.Builder notification;
    SharedPreferences pref;

    @Override
    public void onCreate() {
        super.onCreate();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        pref = getSharedPreferences("user", 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(getApplicationContext(), "Uploading ...", Toast.LENGTH_SHORT).show();
        songName = intent.getStringExtra("song_name");
        url = intent.getStringExtra("url");
        progress = "Uploading";
        singer_id = intent.getIntExtra("singer_id", 1);

        notification = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setContentTitle("Upload: " + songName)
                .setContentText(progress)
                .setSmallIcon(R.drawable.song_avatar)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());

        uploadToStorage();

        return START_STICKY;
    }

    public void uploadToStorage(){
        Uri uri = Uri.fromFile(new File(url));
        songName = songName.replaceAll("/", "-");
        id = songName + LocalDateTime.now().toString().substring(5);
        StorageReference filePath = storageRef.child("/audio/" + id);
        filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            saveToDB(uri.toString());
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "Upload " + songName + " thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Upload", "onFailure: " + e.toString());
            }
        });


    }

    private void saveToDB(String url) {
        uid = pref.getString(Constants.UID, "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);

        Song song = new Song();
        song.setName(songName);
        song.setSinger_id(singer_id+"");
        song.setUser_id(uid);
        song.setUrl(url);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD);
        request.setData(song);

        Call<ServerResponse> response = api.songOperation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Log.d("Song Add", "onResponse: " + response.toString());
                if (resp.getResult().equals("true")){
                    Toast.makeText(getApplicationContext(), "Upload " + songName + " thành công", Toast.LENGTH_SHORT).show();
                    notification.setContentText("Success");
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Upload " + songName + " không thành công", Toast.LENGTH_SHORT).show();
                    notification.setContentText("Failure");
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("Song Add", "onFailure: " + t.getMessage());
                notification.setContentText("Failure");
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(Constants.NOTIFICATION_ID, notification.build());
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
