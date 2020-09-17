#ifndef mqttHelper_h
#define mqttHelper_h
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WebServer.h>
#include <EEPROM.h>




#define sizeString 100 //tamaño del Payload almacenado
#define sizeIP 40
#define sizePORT 6
#define sizeMQTT 16
#define ReconnectMQTTTime 30000 //tiempo que intento reconectar a MQTT
#define WiFiSignalTime 60000 //tiempo que publico la señal WiFi
#define Retain 1
#define QOS1 1
#define StOFFLINE "TOFFLINE" //mensaje LWT
#define StONLINE "TONLINE" //mensaje cuando conecta a MQTT
#define subTopicST "ST"
#define subTopicHALL "Hall"
#define subTopicPOTE "Pote"
#define R1windowTime 60000 //tiempo sino llega un R0,ventana de tiempo para mandar estados
#define KeepAlive 120 // keepalive de MQTT en segundos
#define ReconnectWiFiTime 30000 // teimpo de reconexión de WiFi
#define APonTime 120000 //tiempo acción del punto de acceso


void setupMQTT();
bool isConnectedWiFi();//revisa que este conectado a wifi 
void launchWeb();// iniciar el servidor web en 192.168.4.1
void setupAP(); //seteo parametros del punto de acceso y lo inicia
void readEEPROM();//lee de la EEPROM
void setupWiFi();//intenta conectar al WIFI con los datos de la EEPROM
void WiFiHandle();//se llama en caso de una desconexión del wifi e intenta reconectar
void writeEEPROM();//escribe en al EEPROM
void clearEEPROM();// borra EEPROM
void createWebServer();//crea el servidor web,aqui esta HTML
void checkWiFiMQTT();//revisa que este conectada a wifi y  mqtt
void setupTopics();//arma los tópicos
bool isConnectToMQTT();// revisA que este conectado a  mqtt
bool mqttConnect();//conecta a mqtt
void mqttHandle();//en caso de desconexión de mqtt,reconecta

bool checkBoundVolt(float newValue, float prevValue,float maxDiff);
bool checkBoundHall(float newValue,float prevValue);
void publishValue();
void refreshHall(float newHall);
void refreshVolt(float newVolt);

#endif
