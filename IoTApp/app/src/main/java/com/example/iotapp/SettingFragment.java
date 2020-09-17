package com.example.iotapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class SettingFragment extends Fragment {
    private  EditText Name;
    private EditText Password;
    private  EditText IPmqtt;
    private  EditText Portmqtt;
    private TextView Info;
    private Button Login;
    private int counter=5;
    public static String string;
    public boolean flag=false;

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

        Info.setText("No of attempts remaining :5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(),Password.getText().toString(),IPmqtt.getText().toString(),Portmqtt.getText().toString());

            }
        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void validate(String userName, String userPassword, String userIPmqtt, String userPortmqtt){
        if(userName.equals("admin") && userPassword.equals("1234") && userIPmqtt.length() != 0 && userPortmqtt.length() != 0){
            Toast.makeText(getActivity(),"Validated Data",Toast.LENGTH_SHORT).show();
           /* Intent i =new Intent(getActivity(),MainActivity.class);
            i.putExtra("Servermqtt",userIPmqtt);
            Intent ii =new Intent(getActivity(),MainActivity.class);
            ii.putExtra("Portmqtt",userPortmqtt);*/
            flag=true;
        }else{
            counter --;

            Info.setText("No of attempts remaining : " + counter);
            if (counter == 0){
                Login.setEnabled(false);
            }
        }
    }

}
