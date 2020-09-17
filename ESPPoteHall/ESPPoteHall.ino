
#include "mqttHelper.h"

#define SENSOR 14
#define POTE AO

bool newHall;
int sensorValue;
float newVolt;

void setup() {
  Serial.begin(9600);
  setupWiFi();
  setupTopics();
  setupMQTT();
  checkWiFiMQTT();
  pinMode(SENSOR,INPUT);
}


void loop() {
  WiFiHandle();
  mqttHandle();
  newHall=digitalRead(SENSOR);
  sensorValue = analogRead(A0); //Lectura del ADC 
  newVolt = sensorValue * (3.30 / 1024); //escalamos a voltaje
  refreshHall(newHall);// revisa si cambio el estado del sensor HALL 
  refreshVolt(newVolt);// revisa si cambio el estado del pote
  publishValue();//si hubo un cambio en alguno de los 2 sensores publica el cambio 

}
