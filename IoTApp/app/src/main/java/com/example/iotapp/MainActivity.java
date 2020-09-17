package com.example.iotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    MQTThelper mqttHelper;

    private LocalBroadcastManager localBroadcastManager;

    private String Pote_value ="0";
    private String Hall_value ="0";
    private String Temp_value ="0";
    private String Acc_value ="0";
    private String Bright_value ="0";
    private String Sw1_value ="0";
    private String Sw3_value ="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String IPmqtt;
        String Portmqtt;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //DEFAULT FRAGMENT
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_setting);


        }
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        SettingFragment SF=new SettingFragment();
        Log.w("Debug AAAAAAAAAAAAAAAAAA", String.valueOf(SF.flag));
        startMqtt();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingFragment()).commit();
                break;
            case R.id.nav_pote:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PoteFragment()).commit();
                break;
            case R.id.nav_temp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TempFragment()).commit();
                break;
            case R.id.nav_hall:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HallFragment()).commit();
                break;
            case R.id.nav_acc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccFragment()).commit();
                break;
            case R.id.nav_bright:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BrightFragment()).commit();
                break;
            case R.id.nav_button:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ButtonFragment()).commit();
                break;

            case R.id.nav_start:
                Toast.makeText(this,"Start Data from KL46",Toast.LENGTH_SHORT).show();
                mqttHelper.publishToTopic("ESP/C", "S");
                break;
            case R.id.nav_stop:
                Toast.makeText(this,"Stop Data from KL46",Toast.LENGTH_SHORT).show();
                mqttHelper.publishToTopic("ESP/C", "D");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void startMqtt() {
        mqttHelper = new MQTThelper(getApplicationContext(), new Random().toString(),  //agregar port,ip
                new String[]{ "/Rpi/Pote","/Rpi/Hall","/Rpi/Temp","/Rpi/Acc","/Rpi/Bright","/Rpi/Sw1","/Rpi/Sw3"});
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }


            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Intent intent;
                String mqttMsg = mqttMessage.toString();
                Log.w("Debug", mqttMessage.toString());

                switch (topic){
                    case "/Rpi/Pote":
                        Pote_value = mqttMsg;
                        intent =new Intent("Volt");
                        intent.putExtra("Pote_Sensor",Pote_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;

                    case "/Rpi/Hall":
                        Hall_value = mqttMsg;
                        intent =new Intent("Hall");
                        intent.putExtra("Hall_Sensor",Hall_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "/Rpi/Temp":
                        Temp_value = mqttMsg;
                        intent =new Intent("Temp");
                        intent.putExtra("Temp_Sensor",Temp_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "/Rpi/Acc":
                        Acc_value = mqttMsg;
                        intent =new Intent("Acc");
                        intent.putExtra("Acc_Sensor",Acc_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "/Rpi/Bright":
                        Bright_value = mqttMsg;
                        intent =new Intent("Bright");
                        intent.putExtra("Bright_Sensor",Bright_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "/Rpi/Sw1":
                        Sw1_value = mqttMsg;
                        intent =new Intent("Sw1");
                        intent.putExtra("Sw1_Sensor",Sw1_value);
                        localBroadcastManager.sendBroadcast(intent);
                        break;
                    case "/Rpi/Sw3":
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


    public void publishMessage() {
        mqttHelper.publishToTopic("/test/tom", "aloja");
    }
}
