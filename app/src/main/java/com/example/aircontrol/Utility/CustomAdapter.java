package com.example.aircontrol.Utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.aircontrol.R;

public class CustomAdapter extends BaseAdapter {
    Context mContext;
    String[] strName;
    boolean[] checkedSetting;
    String fragmentName;
    MqttHelper mqttHelper;
    public CustomAdapter(Context context, String[] strName, boolean[] checkedSetting, String fragmentName, MqttHelper mqttHelper) {
        this.mContext= context;
        this.strName = strName;
        this.checkedSetting = checkedSetting;
        this.fragmentName = fragmentName;
        this.mqttHelper = mqttHelper;
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

        // สร้าง Switch
        this.onSwitch(position, view);

        TextView txtView = view.findViewById(R.id.textView);
        txtView.setText(strName[position]);

        return view;
    }

    public void onSwitch(final int position, View rootView)
    {
        Switch sw = rootView.findViewById(R.id.switchSetting);
        //sw.setChecked(checkedSetting[position]);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (fragmentName.equals("AirQualityFragment"))
                {
                    switch (position){
                        case 0:
                            if (isChecked) {
                                final String publishMessage = "ON";
                                final String publishTopic = "sensor/relay";
                                boolean success = mqttHelper.publishMessage(publishTopic, publishMessage);
                                if (success) {

                                } else {

                                }
                            }
                            break;
                        case 1:

                            break;
                        case 2:

                            break;

                        default:
                            Log.d("Error","Error switch");
                    }
                } else if (fragmentName.equals("TemperatureFragment"))
                {

                }
            }
        });
    }
}