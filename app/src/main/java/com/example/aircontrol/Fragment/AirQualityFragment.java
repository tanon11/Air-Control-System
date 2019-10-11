package com.example.aircontrol.Fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
    public boolean isCheckPowerBtn = false;
    public int timeOnHour;
    public int timeOnMinute;
    public int timeOffHour;
    public int timeOffMinute;
    TextView txtAirQuality;
    public AirQualityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_air_quality, container, false);

        txtAirQuality = rootView.findViewById(R.id.txtPM);
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
        String[] listName = { "แจ้งเตือนเมื่อค่าฝุ่นละอองเกินกว่าที่กำหนด", "เปิดเครื่องกรองอากาศเมื่อค่าฝุ่นละอองเกินกว่าที่กำหนด", "เปิด-ปิดเครื่องกรองอากาศตามเวลาที่กำหนดไว้", "เปิดเครื่องกรองอากาศเมื่อผู้ใช้อยู่ภายในบ้าน", "ปิดเครื่องกรองอากาศเมื่อผู้ใช้ไม่อยู่ภายในบ้าน"};
        String fragmentName = "AirQualityFragment";
        CustomAdapter adapter = new CustomAdapter(getActivity(), listName, fragmentName, mqttHelper);
        ListView listView = rootView.findViewById(R.id.listViewAirPurifier);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String selected_item= String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(getActivity(),"aaa",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onButtonPowerAirPurifier(View rootView)
    {
        final Button powerButton = rootView.findViewById(R.id.btnPowerAirPurifier);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckPowerBtn) {
                    final String publishMessage = "ON";
                    final String publishTopic = "sensor/relay";
                    boolean success = mqttHelper.publishMessage(publishTopic,publishMessage);
                    if(success)
                    {
                        isCheckPowerBtn = false;
                        powerButton.setBackgroundResource(R.drawable.power_off);
                    }
                    else{
                        Toast.makeText(getActivity(),"เครื่องกรองอากาศไม่สามารถเปิดได้",Toast.LENGTH_SHORT).show();
                    }
                } else {

                    final String publishMessage = "OFF";
                    final String publishTopic = "sensor/relay";
                    boolean success = mqttHelper.publishMessage(publishTopic,publishMessage);
                    if(success)
                    {
                        isCheckPowerBtn = true;
                        powerButton.setBackgroundResource(R.drawable.power_on);
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
                                Toast.makeText(getActivity(),"aaaaaa",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(getActivity(),"bbbb",Toast.LENGTH_SHORT).show();
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
        timeOnHour = calendar.get(Calendar.HOUR_OF_DAY);
        timeOnMinute = calendar.get(Calendar.MINUTE);
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
        timeOffHour = calendar.get(Calendar.HOUR_OF_DAY);
        timeOffMinute = calendar.get(Calendar.MINUTE);
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
                        int airQuality = Integer.parseInt(mqttMessage.toString());
                        if(airQuality <= 25) {
                            txtAirQuality.setTextColor(Color.parseColor("#66FFFF"));
                        }
                        else if(airQuality > 25 && airQuality <= 50) {
                            txtAirQuality.setTextColor(Color.parseColor("#33FF00"));
                        }
                        else if(airQuality > 51 && airQuality <= 100) {
                            txtAirQuality.setTextColor(Color.parseColor("#FFFF00"));
                        }
                        else if(airQuality > 100) {
                            txtAirQuality.setTextColor(Color.parseColor("#DD0000"));
                        }
                        break;
                    case "sensor/relay":

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
