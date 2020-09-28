package com.example.iotapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


class MQTThelper {
    private MqttAndroidClient mqttAndroidClient;
    private String serverUri;
    final String clientId;
    final String []  subscriptionTopic  ;
    final String IPmqtt;
    final String Portmqtt;


    MQTThelper(final Context context, String client_Id, String[] subscription_Topic, String IPserver, String PORTserver){
        clientId = client_Id;
        subscriptionTopic = subscription_Topic;
        IPmqtt=IPserver;
        Portmqtt=PORTserver;
        SharedPreferences preferences = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        serverUri = preferences.getString("Broker_MQTT_IP", IPserver);
        String port = preferences.getString("Broker_MQTT_Port", PORTserver);
        serverUri = "tcp://"+ serverUri +":"+port;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        connect();
    }



   void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        //mqttConnectOptions.setUserName(username);
        //mqttConnectOptions.setPassword(password);

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

    void publishToTopic(String msg) {
        MqttMessage mqtt_msg = new MqttMessage(msg.getBytes());
        try {
            mqttAndroidClient.publish("1111/miniESP/C", mqtt_msg);
        } catch (MqttException ex) {
            System.err.println("Exception whilst publishing");
            ex.printStackTrace();
        }
    }

}
