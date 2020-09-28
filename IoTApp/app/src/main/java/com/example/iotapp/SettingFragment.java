package com.example.iotapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;
import java.util.Random;

public class SettingFragment extends Fragment {
    private  EditText Name;
    private EditText Password;
    private  EditText IPmqtt;
    private  EditText Portmqtt;
    private TextView Info;
    private Button Login;
    private int counter=5;
    public static String string;
    private MQTThelper mqttHelper;
    private String Pote_value ="0";
    private String Hall_value ="0";
    private String Temp_value ="0";
    private String Acc_value ="0";
    private String Bright_value ="0";
    private String Sw1_value ="0";
    private String Sw3_value ="0";
    private LocalBroadcastManager localBroadcastManager;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_setting,container,false);
        Name = view.findViewById(R.id.etname);
        Password= view.findViewById(R.id.etpassword);
        IPmqtt= view.findViewById(R.id.etipmqtt);
        Portmqtt= view.findViewById(R.id.etportmqtt);
        Info = view.findViewById(R.id.tvinfo);
        Login=view.findViewById(R.id.btnlogin);
        Button startdatamqtt = view.findViewById(R.id.btnstart);
        Button stopdatamqtt = view.findViewById(R.id.btnstop);
        Info.setText("No of attempts remaining :5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(Name.getText().toString(),Password.getText().toString(),IPmqtt.getText().toString(),Portmqtt.getText().toString())){
                    startMqtt(Name.getText().toString(),IPmqtt.getText().toString(),Portmqtt.getText().toString());
                }


            }
        });
        startdatamqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Start Data from KL46",Toast.LENGTH_SHORT).show();
                mqttHelper.publishToTopic("S");

            }
        });
        stopdatamqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Stop Data from KL46",Toast.LENGTH_SHORT).show();
                mqttHelper.publishToTopic("D");

            }
        });
        localBroadcastManager = LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()));
        return view;
    }

    @SuppressLint("SetTextI18n")
    private boolean validate(String userName, String userPassword, String userIPmqtt, String userPortmqtt){
        if(userName.equals("admin") && userPassword.equals("1234") && userIPmqtt.length() != 0 && userPortmqtt.length() != 0){
            Toast.makeText(getActivity(),"Correct data setting",Toast.LENGTH_SHORT).show();
            return true;
        }else{
            counter --;
            Info.setText("No of attempts remaining : " + counter);
            if (counter == 0){
                Login.setEnabled(false);
                return false;
            }
            return false;
        }
    }
    private void startMqtt(String client,String IPserver, String PORTserver) {
        mqttHelper = new MQTThelper(Objects.requireNonNull(getActivity()).getApplicationContext(), client,
                new String[]{ "1111/ESP/Pote","1111/ESP/Hall","1111/Rpi/Temp","1111/miniESP/Acc","1111/miniESP/Bright","1111/miniESP/Sw1","1111/miniESP/Sw3"},IPserver,PORTserver);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(getActivity(),"Connect to MQTT server",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(getActivity(),"Connection lost to MQTT server",Toast.LENGTH_SHORT).show();
            }


            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Intent intent;
                String mqttMsg = mqttMessage.toString();
                Log.w("Debug", mqttMessage.toString());

                switch (topic){
                    case "1111/ESP/Pote":
                        Pote_value = mqttMsg;
                        intent =new Intent("Volt");
                        intent.putExtra("Pote_Sensor",Pote_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;

                    case "1111/ESP/Hall":
                        Hall_value = mqttMsg;
                        intent =new Intent("Hall");
                        intent.putExtra("Hall_Sensor",Hall_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "1111/Rpi/Temp":
                       Temp_value = mqttMsg;
                        intent =new Intent("Temp");
                        intent.putExtra("Temp_Sensor",Temp_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "1111/miniESP/Acc":
                        Acc_value = mqttMsg;
                        intent =new Intent("Acc");
                        intent.putExtra("Acc_Sensor",Acc_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "1111/miniESP/Bright":
                        Bright_value = mqttMsg;
                        intent =new Intent("Bright");
                        intent.putExtra("Bright_Sensor",Bright_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "1111/miniESP/Sw1":
                        Sw1_value = mqttMsg;
                        intent =new Intent("Sw1");
                        intent.putExtra("Sw1_Sensor",Sw1_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "1111/miniESP/Sw3":
                        Sw3_value = mqttMsg;
                        intent =new Intent("Sw3");
                        intent.putExtra("Sw3_Sensor",Sw3_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;

                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

    }

}
