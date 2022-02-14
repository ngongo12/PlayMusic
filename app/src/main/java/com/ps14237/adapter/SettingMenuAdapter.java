package com.ps14237.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ps14237.R;

import java.util.ArrayList;

public class SettingMenuAdapter extends BaseAdapter {

    Context context;
    ArrayList<MenuItem> list;


    public SettingMenuAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        list.add(new MenuItem(R.drawable.ic_info, "Phiên bản"));
        list.add(new MenuItem(R.drawable.ic_help, "Trợ giúp"));
        list.add(new MenuItem(R.drawable.ic_message, "Góp ý, báo lỗi"));
        list.add(new MenuItem(R.drawable.ic_file, "Điều khoản dịch vụ"));
        list.add(new MenuItem(R.drawable.ic_change, "Đổi mật khẩu"));
        list.add(new MenuItem(R.drawable.ic_logout, "Đăng xuất"));
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

class MenuItem{
    private int src;
    private String text;

    public MenuItem(int src, String text) {
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
