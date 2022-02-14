package com.ps14237;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.ps14237.adapter.SongAdapter;
import com.ps14237.adapter.SongsDeviceAdapter;
import com.ps14237.fragment.BottomSheetAddSongFragment;
import com.ps14237.model.Song;

import java.util.ArrayList;

public class DeviceSongsActivity extends AppCompatActivity {

    ArrayList<Song> list;
    RecyclerView rcv;
    SongsDeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_songs);
        rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read storage access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please confirm Read storage access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , 999);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            999);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                taoListNhac();
            }
        }
        else{
            taoListNhac();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 999: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    taoListNhac();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Không có permission", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("Range")
    private void taoListNhac() {
        list.clear();
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while(c.moveToNext()){
            Song song = new Song();
            song.setName(c.getString(c.getColumnIndex(MediaStore.MediaColumns.TITLE)));
            song.setUrl(c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA)));
            song.setSinger_name(c.getString(c.getColumnIndex(MediaStore.MediaColumns.ARTIST)));
            song.setImage(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            list.add(song);
        }

        adapter = new SongsDeviceAdapter(this, list, this);
        rcv.setAdapter(adapter);
        Toast.makeText(this , "Tạo list nhạc thành công", Toast.LENGTH_SHORT).show();
    }

    public void showAddSongBottomSheet(Song song){
        BottomSheetAddSongFragment bottomSheetAddSongFragment = new BottomSheetAddSongFragment(song);
        bottomSheetAddSongFragment.show(getSupportFragmentManager(), bottomSheetAddSongFragment.getTag());
    }
}