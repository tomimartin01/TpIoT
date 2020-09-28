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

public class AccFragment extends Fragment {

    TextView dataReceivedAcc;
    CHARThelper mChart;
    LineChart chart;
    View view;
    String acc_ini;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_acc,container,false);
        dataReceivedAcc = view.findViewById(R.id.dataReceivedAcc);
        chart = view.findViewById(R.id.chart);
        mChart = new CHARThelper(chart);

        if(acc_ini!= null){
            acc_ini = getActivity().getIntent().getStringExtra("Acc_Sensor");
            dataReceivedAcc.setText("Acceleration: "+ acc_ini.toString() +" G ");
            mChart.addEntry(Float.parseFloat(acc_ini.toString()));
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Acc"));
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
            String acc_sensor = i.getStringExtra("Acc_Sensor");

            if(acc_sensor != null) {
                dataReceivedAcc.setText("Acceleration: "+ acc_sensor.toString() +" G ");
                mChart.addEntry(Float.parseFloat(acc_sensor.toString()));
            }

        }
    };

}
