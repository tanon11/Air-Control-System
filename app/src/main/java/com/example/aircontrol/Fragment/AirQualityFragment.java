package com.example.aircontrol.Fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aircontrol.R;
import com.example.aircontrol.Utility.CustomAdapter;
import com.example.aircontrol.Utility.MqttHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AirQualityFragment extends Fragment {
    MqttHelper mqttHelper;
    public boolean isCheckPowerBtn = true;
    public int timeOnHour;
    public int timeOnMinute;
    public int timeOffHour;
    public int timeOffMinute;
    TextView txtAirQuality;
    TextView txtModeAirPurifier;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String P_NAME = "App_Config";
    public AirQualityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_air_quality, container, false);

        txtAirQuality = rootView.findViewById(R.id.txtPM);
        txtModeAirPurifier = rootView.findViewById(R.id.txtModeAirPurifier);
        startMqtt();
        //txtAirQuality.setText("030");

        // สร้าง ListView
        this.onListViewSettingAirPurifier(rootView, mqttHelper);
        //สร้าง Button เปิด-ปิด เครื่องกรองอากาศ
        this.onButtonPowerAirPurifier(rootView);
        //สร้าง Button ตั้งค่าสภาพอากาศให้เหมาะสมแก่กลุ่มคนแต่ละประเภท
        this.onButtonSettingPM(rootView);
        //สร้าง Button ตั้งเวลาเปิดเครื่องกรองอากาศ
        this.onButtonSettingOnTimer(rootView);
        //สร้าง Button ตั้งเวลาปิดเครื่องกรองอากาศ
        this.onButtonSettingOffTimer(rootView);

        return rootView;
    }

    public void onListViewSettingAirPurifier(View rootView, MqttHelper mqttHelper)
    {
        String[] listName = { "แจ้งเตือนเมื่อค่าฝุ่นละอองเกินกว่าที่กำหนด", "เปิดเครื่องกรองอากาศเมื่อค่าฝุ่นละอองเกินกว่าที่กำหนด", "เปิดเครื่องกรองอากาศตามเวลาที่กำหนดไว้", "ปิดเครื่องกรองอากาศตามเวลาที่กำหนดไว้", "เปิด-ปิดเครื่องกรองอากาศโดยดูจากตำแหน่งที่อยู่"};
        String fragmentName = "AirQualityFragment";
        CustomAdapter adapter = new CustomAdapter(getActivity(), listName, fragmentName, mqttHelper);
        ListView listView = rootView.findViewById(R.id.listViewAirPurifier);
        listView.setAdapter(adapter);
    }

    public void onButtonPowerAirPurifier(View rootView)
    {
        final Button powerButton = rootView.findViewById(R.id.btnPowerAirPurifier);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckPowerBtn) {
                    final String publishMessage = "ON";
                    final String publishTopic = "setting/powerairpurifier";
                    boolean success = mqttHelper.publishMessage(publishTopic,publishMessage);
                    if(success)
                    {
                        isCheckPowerBtn = false;
                        powerButton.setBackgroundResource(R.drawable.power_off);
                        Toast.makeText(getActivity(),"เปิดเครื่องกรองอากาศ",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(),"เครื่องกรองอากาศไม่สามารถเปิดได้",Toast.LENGTH_SHORT).show();
                    }
                } else {

                    final String publishMessage = "OFF";
                    final String publishTopic = "setting/powerairpurifier";
                    boolean success = mqttHelper.publishMessage(publishTopic,publishMessage);
                    if(success)
                    {
                        isCheckPowerBtn = true;
                        powerButton.setBackgroundResource(R.drawable.power_on);
                        Toast.makeText(getActivity(),"ปิดเครื่องกรองอากาศ",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(),"เครื่องกรองอากาศไม่สามารถปิดได้",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onButtonSettingPM(View rootView)
    {
        sp = getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        int modeAirConditioner = sp.getInt("ModeAirPurifier", 2);
        if (modeAirConditioner == 1) {
            txtModeAirPurifier.setText("โหมดผู้ที่เป็นภูมิแพ้, เด็ก, คนชรา");
        }
        else
        {
            txtModeAirPurifier.setText("โหมดคนทั่วไป");
        }
        editor = sp.edit();
        final String publishTopic = "setting/modeairpurifier";
        Button settingPMButton = rootView.findViewById(R.id.btnSettingPM);
        settingPMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("ตั้งค่าสภาพอากาศ");
                String[] types = {"ผู้ที่เป็นภูมิแพ้, เด็ก, คนชรา", "คนทั่วไป"};
                builder.setItems(types, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch(which){
                            case 0:
                                editor.putInt("ModeAirPurifier", 1);
                                editor.commit();
                                txtModeAirPurifier.setText("โหมดผู้ที่เป็นภูมิแพ้, เด็ก, คนชรา");
                                mqttHelper.publishMessage(publishTopic, "1");
                                break;
                            case 1:
                                editor.putInt("ModeAirPurifier", 2);
                                editor.commit();
                                txtModeAirPurifier.setText("โหมดคนทั่วไป");
                                mqttHelper.publishMessage(publishTopic, "2");
                                break;
                        }
                    }

                });

                builder.show();
            }
        });
    }

    public void onButtonSettingOnTimer(View rootView)
    {
        // สร้าง Calendar
        final Calendar calendar = Calendar.getInstance();
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        sp = getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        String settimeOnAirpurifier = sp.getString("SettimeOnAirpurifier", time);
        String[] timeOn = settimeOnAirpurifier.split(":");
        timeOnHour = Integer.parseInt(timeOn[0]);
        timeOnMinute = Integer.parseInt(timeOn[1]);

        Button settingOnTimerButton = rootView.findViewById(R.id.btnSettingOnTimerPM);
        settingOnTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            //calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            //calendar.set(Calendar.MINUTE, minute);
                            timeOnHour = hourOfDay;
                            timeOnMinute = minute;
                            String time = timeOnHour + ":" + timeOnMinute;
                            editor = sp.edit();
                            editor.putString("SettimeOnAirpurifier", time);
                            editor.commit();
                            final String publishMessage = time;
                            final String publishTopic = "setting/settimeonairpurifier";
                            boolean success = mqttHelper.publishMessage(publishTopic, publishMessage);
                        }
                    }
                };
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timePickerListener, timeOnHour, timeOnMinute, true);
                timePickerDialog.setTitle("ตั้งเวลาเปิดเครื่องกรองอากาศ");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }

        });
    }

    public void onButtonSettingOffTimer(View rootView)
    {
        // สร้าง Calendar
        final Calendar calendar = Calendar.getInstance();
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        sp = getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        String SettimeOffAirpurifier = sp.getString("SettimeOffAirpurifier", time);
        String[] timeOff = SettimeOffAirpurifier.split(":");
        timeOffHour = Integer.parseInt(timeOff[0]);
        timeOffMinute = Integer.parseInt(timeOff[1]);
        Button settingOffTimerButton = rootView.findViewById(R.id.btnSettingOffTimerPM);
        settingOffTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            //calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            //calendar.set(Calendar.MINUTE, minute);
                            timeOffHour = hourOfDay;
                            timeOffMinute = minute;
                            String time = timeOffHour + ":" + timeOffMinute;
                            editor = sp.edit();
                            editor.putString("SettimeOffAirpurifier", time);
                            editor.commit();
                            final String publishMessage = time;
                            final String publishTopic = "setting/settimeoffairpurifier";
                            boolean success = mqttHelper.publishMessage(publishTopic, publishMessage);
                        }
                    }
                };
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timePickerListener, timeOffHour, timeOffMinute, true);
                timePickerDialog.setTitle("ตั้งเวลาเปิดเครื่องกรองอากาศ");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }

        });
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getActivity());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.d("Debug",topic);
                switch (topic){
                    case "sensor/airquality":
                        txtAirQuality.setText(mqttMessage.toString());
                        break;
                    default:
                        Log.d("Error","Error ocquired");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

}
