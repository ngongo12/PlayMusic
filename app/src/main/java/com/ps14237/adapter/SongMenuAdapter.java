package com.ps14237.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ps14237.R;
import com.ps14237.model.Song;
import com.ps14237.support.Constants;

import java.util.ArrayList;

public class SongMenuAdapter extends BaseAdapter {

    Context context;
    ArrayList<MenuItem> list;
    String uid;
    SharedPreferences pref;
    Song song;

    public SongMenuAdapter(Context context, Song song) {
        this.context = context;
        this.song = song;
        list = new ArrayList<>();
        pref = context.getSharedPreferences("user", 0);
        uid = pref.getString(Constants.UID, "");
        if(song.isLiked()) {
            list.add(new MenuItem(R.drawable.ic_no_heart, "Xóa khỏi Danh sách yêu thích"));
        }else {
            list.add(new MenuItem(R.drawable.ic_empty_heart, "Thêm vào Danh sách yêu thích"));
        }
        list.add(new MenuItem(R.drawable.ic_add_list, "Thêm vào danh sách đang phát"));
        if(song.getUser_id().equals(uid))
            list.add(new MenuItem(R.drawable.ic_edit, "Thay đổi thông tin bài hát"));

        list.add(new MenuItem(R.drawable.ic_download, "Tải về"));
        list.add(new MenuItem(R.drawable.ic_shared, "Chia sẻ"));
        if(song.getUser_id().equals(uid))
            list.add(new MenuItem(R.drawable.ic_delete, "Xóa bài hát"));

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder{
        TextView tvName;
        ImageView iv;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        ViewHolder viewHolder;
        if (view == null)
        {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.setting_menu_layout, null);
            viewHolder.tvName = view.findViewById(R.id.tvName);
            viewHolder.iv = view.findViewById(R.id.iv);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        MenuItem item = list.get(i);
        viewHolder.tvName.setText(item.getText());
        viewHolder.iv.setImageDrawable(context.getDrawable(item.getSrc()));

        return view;
    }
}

class SongMenuItem{
    private int src;
    private String text;

    public SongMenuItem(int src, String text) {
        this.src = src;
        this.text = text;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
