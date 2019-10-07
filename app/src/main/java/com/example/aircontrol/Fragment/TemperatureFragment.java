package com.example.aircontrol.Fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
    public TemperatureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
        txtTemp = rootView.findViewById(R.id.txtTemp);
        txtHumidity = rootView.findViewById(R.id.txtHumidity);
        startMqtt();

        // สร้าง ListView
        this.onListViewSettingAirConditioner(rootView);
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

    public void onListViewSettingAirConditioner(View rootView)
    {
        String[] listName = { "แจ้งเตือนเมื่อค่าอุณหภูมิและความชื้นเกินกว่าที่กำหนด", "เปิดเครื่องปรับอากาศเมื่อค่าอุณหภูมิเกินกว่าที่กำหนด", "เปิดเครื่องปรับอากาศตามเวลาที่กำหนดไว้", "ปิดเครื่องปรับอากาศเมื่อผู้ใช้ไม่อยู่ภายในบ้าน"};
        boolean[] listCheckedSetting = { true, true, true, false };
        CustomAdapter adapter = new CustomAdapter(getActivity(), listName, listCheckedSetting);
        ListView listView = rootView.findViewById(R.id.listViewAirConditioner);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //String selected_item= String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(getActivity(),"aaa",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onButtonPowerAirConditioner(View rootView)
    {
        final Button powerButton = rootView.findViewById(R.id.btnPowerAirConditioner);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheckPowerBtn) {
                    isCheckPowerBtn = false;
                    powerButton.setBackgroundResource(R.drawable.power_off);
                } else {
                    isCheckPowerBtn = true;
                    powerButton.setBackgroundResource(R.drawable.power_on);
                }
            }
        });
    }

    public void onButtonSettingTemp(View rootView)
    {
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
        timeOffHour = calendar.get(Calendar.HOUR_OF_DAY);
        timeOffMinute = calendar.get(Calendar.MINUTE);
        Button settingOffTimerButton = rootView.findViewById(R.id.btnSettingOffTimerTemp);
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
                timePickerDialog.setTitle("ตั้งเวลาเปิดเครื่องปรับอากาศ");
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
