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

public class PoteFragment extends Fragment  {

    private TextView dataReceivedPote;
    private CHARThelper mChart;
    private LineChart chart;
    private View view;
    private String pote_sensor_ini;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pote,container,false);
        dataReceivedPote = view.findViewById(R.id.dataReceivedPote);
        chart = view.findViewById(R.id.chart);
        mChart = new CHARThelper(chart);

        if(pote_sensor_ini!= null){
            pote_sensor_ini = getActivity().getIntent().getStringExtra("Pote_Sensor");
            dataReceivedPote.setText("Voltage: "+ pote_sensor_ini +" V ");
            mChart.addEntry(Float.parseFloat(pote_sensor_ini.toString()));
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Volt"));
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
            String pote_sensor = i.getStringExtra("Pote_Sensor");

            if(pote_sensor != null) {
                dataReceivedPote.setText("Voltage: "+ pote_sensor +" V ");
                mChart.addEntry(Float.parseFloat(pote_sensor));
            }

        }
    };


}
