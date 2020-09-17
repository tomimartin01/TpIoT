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

public class BrightFragment extends Fragment {
    TextView dataReceivedBright;
    CHARThelper mChart;
    LineChart chart;
    View view;
    String bright_ini;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bright,container,false);
        dataReceivedBright = view.findViewById(R.id.dataReceivedBright);
        chart = view.findViewById(R.id.chart);
        mChart = new CHARThelper(chart);

        if(bright_ini!= null){
            bright_ini = getActivity().getIntent().getStringExtra("Bright_Sensor");
            dataReceivedBright.setText("VALUE : "+ bright_ini.toString() );
            mChart.addEntry(Float.parseFloat(bright_ini.toString()));
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener,
                new IntentFilter("Bright"));
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
            String bright_sensor = i.getStringExtra("Bright_Sensor");

            if(bright_sensor != null) {
                dataReceivedBright.setText("VALUE : "+ bright_sensor.toString() );
                mChart.addEntry(Float.parseFloat(bright_sensor.toString()));
            }

        }
    };
}
