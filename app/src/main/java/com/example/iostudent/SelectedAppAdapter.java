package com.example.iostudent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SelectedAppAdapter extends BaseAdapter {
    private List<AppInfo> appInfoList;
    private LayoutInflater inflater;
    private Context context;
    public SelectedAppAdapter(Context context, List<AppInfo> appInfoList) {
        this.appInfoList = appInfoList;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }
    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.app_name, parent, false);
        }
        TextView appNameTxt = (TextView) view.findViewById(R.id.app_name);
        appNameTxt.setText(appInfoList.get(position).getAppName());

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Tạo PopupMenu
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.delete_menu, popupMenu.getMenu());

                // Thiết lập sự kiện khi chọn menu item "Xóa"
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_delete) {
                            // Xóa item khi chọn "Xóa"
                            appInfoList.remove(position);
                            notifyDataSetChanged();
                            return true;
                        }
                        return false;
                    }
                });

                // Hiển thị PopupMenu
                popupMenu.show();
                return true;
            }
        });
        return view;
    }
}
