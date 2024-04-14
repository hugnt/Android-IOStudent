package com.example.iostudent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends ArrayAdapter<AppInfo> {
    private LayoutInflater inflater;

    public AppAdapter(Context context, List<AppInfo> appList) {
        super(context, 0, appList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        AppInfo appInfo = getItem(position);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(appInfo.getAppName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        AppInfo appInfo = getItem(position);

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(appInfo.getAppName());

        return convertView;
    }
}