package com.example.aircontrol.Fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aircontrol.R;
import com.example.aircontrol.Utility.CustomAdapter;
import com.example.aircontrol.Utility.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class TemperatureFragment extends Fragment {
    MqttHelper mqttHelper;
    public boolean isCheckPowerBtn = true;
    public int timeOnHour;
    public int timeOnMinute;
    public int timeOffHour;
    public int timeOffMinute;
    TextView txtTemp;
    TextView txtHumidity;
    TextView txtModeAirConditioner;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String P_NAME = "App_Config";
    public TemperatureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
        txtTemp = rootView.findViewById(R.id.txtTemp);
        txtHumidity = rootView.findViewById(R.id.txtHumidity);
        txtModeAirConditioner = rootView.findViewById(R.id.txtModeAirConditioner);
        startMqtt();

        // สร้าง ListView
        this.onListViewSettingAirConditioner(rootView, mqttHelper);
        //สร้าง Button เปิด-ปิด เครื่องปรับอากาศ
        this.onButtonPowerAirConditioner(rootView);
        //สร้าง Button ตั้งค่าสภาพอากาศให้เหมาะสมแก่กลุ่มคนแต่ละประเภท
        this.onButtonSettingTemp(rootView);
        //สร้าง Button ตั้งเวลาเปิดเครื่องปรับอากาศ
        this.onButtonSettingOnTimer(rootView);
        //สร้าง Button ตั้งเวลาปิดเครื่องปรับอากาศ
        this.onButtonSettingOffTimer(rootView);

        return rootView;
    }

    public void onListViewSettingAirConditioner(View rootView, MqttHelper mqttHelper)
    {
        String[] listName = { "แจ้งเตือนเมื่อค่าอุณหภูมิและความชื้นเกินกว่าที่กำหนด", "เปิดเครื่องปรับอากาศเมื่อค่าอุณหภูมิเกินกว่าที่กำหนด", "เปิดเครื่องปรับอากาศตามเวลาที่กำหนดไว้", "ปิดเครื่องปรับอากาศตามเวลาที่กำหนดไว้", "เปิดเครื่องปรับอากาศเมื่อผู้ใช้อยู่ภายในบ้าน", "ปิดเครื่องปรับอากาศเมื่อผู้ใช้ไม่อยู่ภายในบ้าน"};
        String fragmentName = "TemperatureFragment";
        CustomAdapter adapter = new CustomAdapter(getActivity(), listName, fragmentName, mqttHelper);
        ListView listView = rootView.findViewById(R.id.listViewAirConditioner);
        listView.setAdapter(adapter);
    }

    public void onButtonPowerAirConditioner(View rootView)
    {
        final Button powerButton = rootView.findViewById(R.id.btnPowerAirConditioner);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckPowerBtn) {
                    final String publishMessage = "ON";
                    final String publishTopic = "setting/powerairconditioner";
                    boolean success = mqttHelper.publishMessage(publishTopic,publishMessage);
                    if(success)
                    {
                        isCheckPowerBtn = false;
                        powerButton.setBackgroundResource(R.drawable.power_off);
                        Toast.makeText(getActivity(),"เปิดเครื่องปรับอากาศ",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(),"เครื่องปรับอากาศไม่สามารถเปิดได้",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    final String publishMessage = "OFF";
                    final String publishTopic = "setting/powerairconditioner";
                    boolean success = mqttHelper.publishMessage(publishTopic,publishMessage);
                    if(success)
                    {
                        isCheckPowerBtn = true;
                        powerButton.setBackgroundResource(R.drawable.power_on);
                        Toast.makeText(getActivity(),"ปิดเครื่องปรับอากาศ",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(),"เครื่องปรับอากาศไม่สามารถปิดได้",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onButtonSettingTemp(View rootView)
    {
        sp = getActivity().getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        int modeAirConditioner = sp.getInt("ModeAirConditioner", 2);
        if (modeAirConditioner == 1) {
            txtModeAirConditioner.setText("โหมดผู้ที่เป็นภูมิแพ้, เด็ก, คนชรา");
        }
        else
        {
            txtModeAirConditioner.setText("โหมดคนทั่วไป");
        }
        editor = sp.edit();
        final String publishTopic = "setting/modeairconditioner";
        Button settingTempButton = rootView.findViewById(R.id.btnSettingTemp);
        settingTempButton.setOnClickListener(new View.OnClickListener() {
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
                                editor.putInt("ModeAirConditioner", 1);
                                editor.commit();
                                txtModeAirConditioner.setText("โหมดผู้ที่เป็นภูมิแพ้, เด็ก, คนชรา");
                                mqttHelper.publishMessage(publishTopic, "1");
                                break;
                            case 1:
                                editor.putInt("ModeAirConditioner", 2);
                                editor.commit();
                                txtModeAirConditioner.setText("โหมดคนทั่วไป");
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
        String settimeOnAirconditioner = sp.getString("SettimeOnAirconditioner", time);
        String[] timeOn = settimeOnAirconditioner.split(":");
        timeOnHour = Integer.parseInt(timeOn[0]);
        timeOnMinute = Integer.parseInt(timeOn[1]);

        Button settingOnTimerButton = rootView.findViewById(R.id.btnSettingOnTimerTemp);
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
                            editor.putString("SettimeOnAirconditioner", time);
                            editor.commit();
                            final String publishMessage = time;
                            final String publishTopic = "setting/settimeonairconditioner";
                            boolean success = mqttHelper.publishMessage(publishTopic, publishMessage);
                        }
                    }
                };
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timePickerListener, timeOnHour, timeOnMinute, true);
                timePickerDialog.setTitle("ตั้งเวลาเปิดเครื่องปรับอากาศ");
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
        String settimeOffAirconditioner = sp.getString("SettimeOffAirconditioner", time);
        String[] timeOff = settimeOffAirconditioner.split(":");
        timeOffHour = Integer.parseInt(timeOff[0]);
        timeOffMinute = Integer.parseInt(timeOff[1]);
        Button settingOffTimerButton = rootView.findViewById(R.id.btnSettingOffTimerTemp);
        settingOffTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            timeOffHour = hourOfDay;
                            timeOffMinute = minute;
                            String time = timeOffHour + ":" + timeOffMinute;
                            editor = sp.edit();
                            editor.putString("SettimeOffAirconditioner", time);
                            editor.commit();
                            final String publishMessage = time;
                            final String publishTopic = "setting/settimeoffairconditioner";
                            boolean success = mqttHelper.publishMessage(publishTopic, publishMessage);
                        }
                    }
                };
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timePickerListener, timeOffHour, timeOffMinute, true);
                timePickerDialog.setTitle("ตั้งเวลาปิดเครื่องปรับอากาศ");
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
                    case "sensor/temp":
                        txtTemp.setText(mqttMessage.toString());
                        break;
                    case "sensor/humidity":
                        txtHumidity.setText(mqttMessage.toString());
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
