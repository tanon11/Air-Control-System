package com.example.aircontrol.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.aircontrol.R;

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    String[] strName;
    boolean[] checkedSetting;

    public CustomAdapter(Context context, String[] strName, boolean[] checkedSetting) {
        this.mContext= context;
        this.strName = strName;
        this.checkedSetting = checkedSetting;
    }

    public int getCount() {
        return strName.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
        {
            view = mInflater.inflate(R.layout.listview_row, parent, false);
        }

        Switch sw = view.findViewById(R.id.switchSetting);
        sw.setChecked(checkedSetting[position]);

        TextView txtView = view.findViewById(R.id.textView);
        txtView.setText(strName[position]);

        return view;
    }
}