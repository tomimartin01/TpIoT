package com.example.iotapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ButtonFragment extends Fragment {

    TextView dataReceivedSw1;
    TextView dataReceivedSw3;
    View view;
    String sw1_ini;
    String sw3_ini;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_button,container,false);
        dataReceivedSw1 = view.findViewById(R.id.dataReceivedSw1);
        dataReceivedSw3 = view.findViewById(R.id.dataReceivedSw3);
        if(sw1_ini!= null){
            sw1_ini = getActivity().getIntent().getStringExtra("Sw1_Sensor");
            dataReceivedSw1.setText("The Switch 1 is : "+ sw1_ini);
        }
        if(sw3_ini!= null){
            sw3_ini = getActivity().getIntent().getStringExtra("Sw3_Sensor");
            dataReceivedSw3.setText("The Switch 3 is : "+ sw3_ini.toString());
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Sw1"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Sw3"));
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
            String Sw1_sensor = i.getStringExtra("Sw1_Sensor");
            String Sw3_sensor = i.getStringExtra("Sw3_Sensor");

            if(Sw1_sensor != null) {
                dataReceivedSw1.setText("The Switch 1 is : "+ Sw1_sensor);

            }
            if(Sw3_sensor != null) {
                dataReceivedSw3.setText("The Switch 3 is : "+ Sw3_sensor.toString());

            }

        }
    };
}
