package com.example.iotapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.jar.Attributes;

public class MQTThelper {
    public MqttAndroidClient mqttAndroidClient;
    private String serverUri; //tcp://192.168.0.247:1883
    final String clientId;
    final String []  subscriptionTopic  ;
    //final String IPmqtt;
    //final String Portmqtt;

    //final String username = "xxxxxxx";
    //final String password = "yyyyyyyyyy";
    //public MQTThelper(final Context context,String client_Id,String [] subscription_Topic,String IP_mqtt,String Port_mqtt){
    public MQTThelper(final Context context,String client_Id,String [] subscription_Topic){
        clientId = client_Id;
        subscriptionTopic = subscription_Topic;
        //IPmqtt=IP_mqtt;
        //Portmqtt=Port_mqtt;
        SharedPreferences preferences = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        serverUri = preferences.getString("Broker_MQTT_IP", "192.168.0.16");
        String port = preferences.getString("Broker_MQTT_Port", "1883");
        serverUri = "tcp://"+ serverUri +":"+port;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

                Log.w("mqtt", s);

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        connect();
    }



    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        //mqttConnectOptions.setUserName(username);
        //mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, new int[subscriptionTopic.length],
                    this, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.w("Mqtt","Subscribed!");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.w("Mqtt", "Subscribed fail!");
                        }
                    });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishToTopic(String publishTopic, String msg) {
        MqttMessage mqtt_msg = new MqttMessage(msg.getBytes());
        try {
            mqttAndroidClient.publish(publishTopic, mqtt_msg);
        } catch (MqttException ex) {
            System.err.println("Exception whilst publishing");
            ex.printStackTrace();
        }
    }

}
