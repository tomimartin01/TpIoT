package com.example.iotapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class HallFragment extends Fragment {

    TextView dataReceivedHall;
    View view;
    String hall_sensor_ini;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hall,container,false);
        dataReceivedHall = view.findViewById(R.id.dataReceivedHall);
        if(hall_sensor_ini!= null){
            hall_sensor_ini = getActivity().getIntent().getStringExtra("Hall_Sensor");
            dataReceivedHall.setText("The sensor is : "+ hall_sensor_ini.toString());

        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Hall"));
        return view;
    }

    @Override
    public void onDestroyView() {
        if(getActivity()!= null){
            super.onDestroyView();
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        }
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent i ) {
            String hall_sensor = i.getStringExtra("Hall_Sensor");

            if(hall_sensor != null) {
                dataReceivedHall.setText("The Sensor is : "+ hall_sensor.toString());

            }

        }
    };
}
