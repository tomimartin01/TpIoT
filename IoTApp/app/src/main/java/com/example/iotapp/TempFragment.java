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

import com.github.mikephil.charting.charts.LineChart;

public class TempFragment extends Fragment {

    TextView dataReceivedTemp;
    CHARThelper mChart;
    LineChart chart;
    View view;
    String temp_sensor_ini;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temp,container,false);
        dataReceivedTemp = view.findViewById(R.id.dataReceivedTemp);
        chart = view.findViewById(R.id.chart);
        mChart = new CHARThelper(chart);

        if(temp_sensor_ini!= null){
            temp_sensor_ini = getActivity().getIntent().getStringExtra("Temp_Sensor");
            dataReceivedTemp.setText("Temperature: "+ temp_sensor_ini.toString() +" ºmC ");
            mChart.addEntry(Float.parseFloat(temp_sensor_ini));
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Temp"));
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
            String temp_sensor = i.getStringExtra("Temp_Sensor");

            if(temp_sensor != null) {
                dataReceivedTemp.setText("Temperature: "+ temp_sensor.toString() +" ºmC ");
                mChart.addEntry(Float.parseFloat(temp_sensor.toString()));
            }

        }
    };

}
