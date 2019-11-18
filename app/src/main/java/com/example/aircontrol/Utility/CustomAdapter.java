package com.example.aircontrol.Utility;

import android.content.Context;
import android.content.SharedPreferences;
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
    String fragmentName;
    MqttHelper mqttHelper;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String P_NAME = "App_Config";

    public CustomAdapter(Context context, String[] strName, String fragmentName, MqttHelper mqttHelper) {
        this.mContext= context;
        this.strName = strName;
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

        // set default
        if (fragmentName.equals("AirQualityFragment"))
        {
            switch (position){
                case 0:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isNotificationAirPurifier = sp.getBoolean("NotificationAirPurifier", false);
                    sw.setChecked(isNotificationAirPurifier);
                    break;
                case 1:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isOnLimitAirPurifier = sp.getBoolean("OnLimitAirPurifier", false);
                    sw.setChecked(isOnLimitAirPurifier);
                    break;
                case 2:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isOnTimeAirPurifier = sp.getBoolean("OnTimeAirPurifier", false);
                    sw.setChecked(isOnTimeAirPurifier);
                    break;
                case 3:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isOffTimeAirPurifier = sp.getBoolean("OffTimeAirPurifier", false);
                    sw.setChecked(isOffTimeAirPurifier);
                    break;
                case 4:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isGPSAirPurifier = sp.getBoolean("GPSAirPurifier", false);
                    sw.setChecked(isGPSAirPurifier);
                    break;

                default:
                    Log.d("Error","Error switch");
            }
        }
        else if (fragmentName.equals("TemperatureFragment"))
        {
            switch (position){
                case 0:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isNotificationAirConditioner = sp.getBoolean("NotificationAirConditioner", false);
                    sw.setChecked(isNotificationAirConditioner);
                    break;
                case 1:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isOnLimitAirConditioner = sp.getBoolean("OnLimitAirConditioner", false);
                    sw.setChecked(isOnLimitAirConditioner);
                    break;
                case 2:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isOnTimeAirConditioner = sp.getBoolean("OnTimeAirConditioner", false);
                    sw.setChecked(isOnTimeAirConditioner);
                    break;
                case 3:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isOffTimeAirConditioner = sp.getBoolean("OffTimeAirConditioner", false);
                    sw.setChecked(isOffTimeAirConditioner);
                    break;
                case 4:
                    sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                    boolean isGPSAirConditioner = sp.getBoolean("GPSAirConditioner", false);
                    sw.setChecked(isGPSAirConditioner);
                    break;
                default:
                    Log.d("Error","Error switch");
            }
        }
        //sw.setChecked(checkedSetting[position]);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (fragmentName.equals("AirQualityFragment"))
                {
                    String publishMessage = "";
                    String publishTopic = "";
                    switch (position){
                        case 0:
                            publishTopic = "setting/notificationairpurifier";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("NotificationAirPurifier", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("NotificationAirPurifier", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 1:
                            publishTopic = "setting/onlimitairpurifier";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("OnLimitAirPurifier", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("OnLimitAirPurifier", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 2:
                            publishTopic = "setting/ontimeairpurifier";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("OnTimeAirPurifier", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("OnTimeAirPurifier", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 3:
                            publishTopic = "setting/offtimeairpurifier";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("OffTimeAirPurifier", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("OffTimeAirPurifier", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 4:
                            publishTopic = "setting/gpsairpurifier";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("GPSAirPurifier", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("GPSAirPurifier", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        default:
                            Log.d("Error","Error switch");
                    }
                }
                else if (fragmentName.equals("TemperatureFragment"))
                {
                    String publishMessage = "";
                    String publishTopic = "";
                    switch (position){
                        case 0:
                            publishTopic = "setting/notificationairconditioner";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("NotificationAirConditioner", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("NotificationAirConditioner", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 1:
                            publishTopic = "setting/onlimitairconditioner";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("OnLimitAirConditioner", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("OnLimitAirConditioner", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 2:
                            publishTopic = "setting/ontimeairconditioner";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("OnTimeAirConditioner", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("OnTimeAirConditioner", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 3:
                            publishTopic = "setting/offtimeairconditioner";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("OffTimeAirConditioner", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("OffTimeAirConditioner", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        case 4:
                            publishTopic = "setting/gpsairconditioner";
                            sp = mContext.getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            editor = sp.edit();
                            if (isChecked) {
                                publishMessage = "ON";
                                editor.putBoolean("GPSAirConditioner", true);
                                editor.commit();
                            }
                            else {
                                publishMessage = "OFF";
                                editor.putBoolean("GPSAirConditioner", false);
                                editor.commit();
                            }
                            mqttHelper.publishMessage(publishTopic, publishMessage);
                            break;
                        default:
                            Log.d("Error","Error switch");
                    }
                }
            }
        });
    }
}