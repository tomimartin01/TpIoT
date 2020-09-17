#include "mqttHelper.h"

int i = 0;//recorre la memoria EEPROM
int statusCode; //devuelve el codigo al servidor si fue exitoso o no la adquisición de datos
const char* ssid = "text";
const char* passphrase = "text";
String st;
String content;//contiene el código HTML

long previousAPonTime=0;//contador para mantener el punto de acceso
long currentAPonTime=0;

char rsid[32]="";//variables que leen la memoria EEPROM
char rpass[64] = "";
char rmqttServer[32] = "";
char rmqttServerDNS[16];
char rmqttPort[8] = "";
char rsubscriber[8] = "";
int rmqttPortInt;

String wsid;//variables que escriben en la EEPROM
String wpass;
String wmqttServer;
String wmqttPort;
String wsubscriber;
ESP8266WebServer server(80);

unsigned long WiFiPreviousTime=0;//contador para la conexión del wifi
unsigned long WiFiCurrentTime;


WiFiClient espClient;
PubSubClient client(rmqttServer,rmqttPortInt,espClient);
unsigned long mqttPreviousTime;//contador para reconectar a mqtt
unsigned long mqttCurrentTime;

char stTopic [sizeString];//Topico XXX/NNNN/ST
char poteTopic [sizeString];//Topico XXX/NNNN/C
char hallTopic[sizeString];//Topico XXX/NNNN/WiFi

long lastMsg = 0;
float volt = 0.0;
bool hall = 0;
float voltdiff = 0.1;
bool volt_change= false;
bool hall_change = false;
unsigned long volt_last_change=0;
unsigned long hall_last_change=0;




void setupWiFi(){
  Serial.println("Disconnecting previously connected WiFi");
  WiFi.disconnect();
  EEPROM.begin(512); //Inicializa la EEPROM en 512 bytes disponibles
  delay(15000);
  pinMode(LED_BUILTIN, OUTPUT);
  Serial.println();
  Serial.println();
  Serial.println("Startup WiFi Communicator");
  readEEPROM();
  WiFi.begin(rsid, rpass);//intenta conectarse al wifi con los parámetros guardados en EEPROM
}

void checkWiFiMQTT(){
  if (isConnectedWiFi()and mqttConnect())
  {
    Serial.println("Succesfully Connected to WiFi! :)");
    Serial.println("Succesfully Connected to MQTT! :)");
    return;
  }
  else
  {
    Serial.println("Turn AP On");
    WiFiPreviousTime=millis();
    //launchWeb();
    setupAP();// Setea punto de acceso
    previousAPonTime=millis();
  }
  currentAPonTime=millis();
  while ((WiFi.status() != WL_CONNECTED) and abs(currentAPonTime-previousAPonTime)<APonTime)//levanta el punto de acceso en caso de que no se pueda conectar y este en la vantana de tiempo
  {
    currentAPonTime=millis();
    Serial.print(".");
    delay(1000);
    Serial.print(":");
    server.handleClient();//mantiene levantado el punto de acceso
  }
  if(WiFi.status() != WL_CONNECTED){//en caso de que paso la ventana de tiempo y no se conecto tirar el web server e intentar arrancar con los parametros seteados
  Serial.println("Stop Server Web");
  server.stop();
  Serial.println("Stop AP");
  WiFi.softAPdisconnect (true);
  
  }
}
void readEEPROM(){
  Serial.println("Reading EEPROM memory");
  Serial.println("Looking for data saved");
  for (int i = 0; i < 32; ++i) //guardo del byte 0 al 32 la ssid
  {
    rsid [i]= char(EEPROM.read(i));
  }
  Serial.print("SSID: ");
  Serial.println(rsid);
  
  for (int i = 32; i < 96; ++i)//guardo del byte 32 al 96 la contraseña
  {
    rpass [i-32]= char(EEPROM.read(i));
  }
  Serial.print("PASS: ");
  Serial.println(rpass);

  
  for (int i = 96; i < 128; ++i)
  {
    rmqttServer [i-96]= char(EEPROM.read(i));//guardo del byte 96 al 128 el servidor mqtt
  }
  Serial.print("mqttServer: ");
  Serial.println(rmqttServer);
  
  
  
  for (int i = 128; i < 136; ++i)
  {
    rmqttPort [i-128]= char(EEPROM.read(i));//guardo del byte 128 al 136 el puerto mqtt
  }
  Serial.print("mqttPort: ");
  Serial.println(rmqttPort);
  rmqttPortInt=atoi(rmqttPort);
  for (int i = 136; i < 144; ++i)//guardo del 136 al 144 la empresa
  {
    rsubscriber [i-136]= char(EEPROM.read(i));
  }
  Serial.print("subscriber: ");
  Serial.println(rsubscriber); 
}


bool isConnectedWiFi(void)//espera cierto tiempo para conectarse al wifi
{
  int c = 0;
  Serial.println("Waiting for Wifi to connect");
  while (WiFi.status() != WL_CONNECTED and c<10 ) {
    digitalWrite(LED_BUILTIN, LOW);
    delay(500);
    Serial.print("*");
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500);
    Serial.print("#");
    c++;
  }
  if(WiFi.status() != WL_CONNECTED){
    Serial.println("Connect timed out to connect WiFi");
    Serial.println("Not connected to WiFi :(");
    return false;
  }else{
    return true;
  }
}

 
void launchWeb()
{
  Serial.println("");
  if (WiFi.status() == WL_CONNECTED)
    Serial.println("WiFi connected");
  Serial.print("Local IP: ");
  Serial.println(WiFi.localIP());
  Serial.println("GO to the SoftAP IP in a browser");
  Serial.print("SoftAP IP: ");
  Serial.println(WiFi.softAPIP());//setea la IP 192.168.4.1
  createWebServer();
  server.begin();//levanta el servidor
  Serial.println("WebServer started");
}
 
void setupAP(void)
{
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);
  int n = WiFi.scanNetworks();//se escanean todas las redes wifi disponibles y se muestran en la pagina
  if (n == 0)
    Serial.println("no networks found");
  else
  {
    Serial.print(n);
    Serial.println(" networks found");
    for (int i = 0; i < n; ++i)
    {
      // Print SSID and RSSI for each network found
      Serial.print(i + 1);
      Serial.print(": ");
      Serial.print(WiFi.SSID(i));
      Serial.print(" (");
      Serial.print(WiFi.RSSI(i));
      Serial.print(")");
      Serial.println((WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*");
      delay(10);
    }
  }
  Serial.println("");
  st = "<ol>";
  for (int i = 0; i < n; ++i)
  {
    // Print SSID and RSSI for each network found
    st += "<li>";
    st += WiFi.SSID(i);
    st += " (";
    st += WiFi.RSSI(i);
 
    st += ")";
    st += (WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*";
    st += "</li>";
  }
  st += "</ol>";
  delay(100);
  WiFi.softAP("WiFiCommunicator", "");
  Serial.print("Connect to AP");
  Serial.println(" WiFiCommunicator");
  launchWeb();
  
}

void createWebServer()
{
 {
    server.on("/", []() {
 
      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
      content = "<!DOCTYPE HTML>\r\n<html>Hello from ESP8266 at ";
      content += "<form action=\"/scan\" method=\"POST\"><input type=\"submit\" value=\"scan\"></form>";
      content += ipStr;
      content += "<p>";
      content += st;
      content += "</p><form method='post' action='setting'><label>SSID: </label><input name='ssid' length=32><br><label>Password: </label><input name='pass' type='password' length=64><br><label>MQTT SERVER: </label><input name='mqttServer' length=64><br><label>MQTT PORT: </label><input name='mqttPort' length=64><br><label>Subscriber: </label><input name='subscriber' length=64><br><input type='submit'></form>";
      content += "</html>";
      server.send(200, "text/html", content);
    });
    server.on("/scan", []() {
      //setupAP();
      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
 
      content = "<!DOCTYPE HTML>\r\n<html>go back";
      server.send(200, "text/html", content);
    });
 
    server.on("/setting", []() {
      wsid = server.arg("ssid");
      wpass = server.arg("pass");
      wmqttServer = server.arg("mqttServer"); //32
      wmqttPort =  server.arg("mqttPort"); //8 
      wsubscriber = server.arg("subscriber");//8
      if (wsid.length() > 0 && wpass.length() > 0 && wmqttServer.length()> 0 && wmqttPort.length()> 0 &&  wsubscriber.length()> 0) {
        clearEEPROM();
        writeEEPROM();
        content = "{\"Success\":\"saved to eeprom... reset to boot into new wifi\"}";
        statusCode = 200;
        server.sendHeader("Access-Control-Allow-Origin", "*");
        server.send(statusCode, "application/json", content);
        delay(10000);
        Serial.println("");
        Serial.println("Stop Server Web");
        server.stop();
        Serial.println("Stop AP");
        WiFi.softAPdisconnect (true);
        Serial.println("RESET ESP");
        ESP.reset();
      } else {
        content = "{\"Error\":\"404 not found\"}";
        statusCode = 404;
        Serial.println("Sending 404");
        server.sendHeader("Access-Control-Allow-Origin", "*");
        server.send(statusCode, "application/json", content);
      }
    });
  } 
}
void writeEEPROM(){ //escribe en la EEPROM los datos de la pagina,respetando los lugares establecidos en readEEPROM
  Serial.println("writing eeprom ssid:");
  for (int i = 0; i < wsid.length(); ++i)
    {
     EEPROM.write(i, wsid[i]);
     Serial.print("Wrote: ");
     Serial.println(wsid[i]);
     }
   Serial.println("writing eeprom pass:");
   for (int i = 0; i < wpass.length(); ++i)
    {
     EEPROM.write(32 + i, wpass[i]);
     Serial.print("Wrote: ");
     Serial.println(wpass[i]);
     }
   Serial.println("writing eeprom mqttServer:");
   for (int i = 0; i < wmqttServer.length(); ++i)
     {
      EEPROM.write(96 + i, wmqttServer[i]);
      Serial.print("Wrote: ");
      Serial.println(wmqttServer[i]);
      }
    Serial.println("writing eeprom mqttPort:");
    for (int i = 0; i < wmqttPort.length(); ++i)
     {
      EEPROM.write(128 + i, wmqttPort[i]);
      Serial.print("Wrote: ");
      Serial.println(wmqttPort[i]);
      }
     Serial.println("writing eeprom subscriber:");
     for (int i = 0; i < wsubscriber.length(); ++i)
     {
      EEPROM.write(136 + i, wsubscriber[i]);
      Serial.print("Wrote: ");
      Serial.println(wsubscriber[i]);
      }
     EEPROM.commit();//importante dar commit para guardar los cambios en memoria
}
void clearEEPROM(){
  Serial.println("clearing eeprom");
  for (int i = 0; i < 152; ++i) {
    EEPROM.write(i, 0);
  }
}
void setupMQTT(){
  IPAddress test;
  WiFi.hostByName(rmqttServer, test);// obtener de DNS una IP
  test.toString().toCharArray(rmqttServerDNS, 16);//cambiar de IPAddress a char[]
  client.setServer(rmqttServerDNS,rmqttPortInt); //seteo IP y puerto del servidor MQTT
  client.setKeepAlive(KeepAlive);//seteo el keepalive
  if (mqttConnect()){
    mqttPreviousTime = millis();
  }
  else{
    mqttPreviousTime = millis();
  }
}

void WiFiHandle(){
  if(WiFi.status() != WL_CONNECTED){
    WiFiCurrentTime=millis();
    if (abs(WiFiCurrentTime - WiFiPreviousTime) > ReconnectWiFiTime) {
      WiFiPreviousTime = WiFiCurrentTime;
      WiFi.begin(rsid, rpass);
      if (isConnectedWiFi()) {
        Serial.println(F("WiFi disconnected, successfully reconnected."));
        WiFiPreviousTime = 0;
      }
      
    }
  }
}
void mqttHandle() {
  if(WiFi.status() == WL_CONNECTED){
    if (!client.connected()) {
     mqttCurrentTime = millis();
      if (abs(mqttCurrentTime - mqttPreviousTime) > ReconnectMQTTTime) {
         mqttPreviousTime= mqttCurrentTime;
        if (mqttConnect()) {
          Serial.println(F("Successfully reconnected to MQTT."));
          mqttPreviousTime = 0;
        }
        else Serial.println(F("MQTT disconnected, failed to reconnect."));
      }
    }
    else client.loop();
  }
}

bool isConnectToMQTT(){ ///name changed verifCon
  return client.connected();
}

void setupTopics(){
  snprintf(stTopic,sizeof stTopic,"%s/ESP/%s",rsubscriber,subTopicST);// XXX/NNNN/ST
  snprintf(poteTopic,sizeof poteTopic,"%s/ESP/%s",rsubscriber,subTopicPOTE);// XXX/NNNN/C
  snprintf(hallTopic,sizeof hallTopic,"%s/ESP/%s",rsubscriber,subTopicHALL);// XXX/NNNN/WiFi
  
}

bool mqttConnect() {
  if(WiFi.status() == WL_CONNECTED){
    if (client.connect(rsubscriber,stTopic,QOS1,Retain,StOFFLINE)) {// seteo LWT,la bandera de retain para el tópico /ST
      Serial.print(F("MQTT connected: "));
      Serial.println(rmqttServer);
      client.subscribe(poteTopic,QOS1);
      client.publish(stTopic,StONLINE,Retain);
    }
    else {
      Serial.print(F("MQTT connection failed: "));
      Serial.println(rmqttServer);
    }
    return client.connected();
  }
  return false;
}
bool checkBoundHall(float newValue, float prevValue) {
  if(newValue != prevValue){
    return true;
  }
  else {
    return false;
  }
}
bool checkBoundVolt(float newValue, float prevValue, float maxDiff) {
  return newValue < prevValue - maxDiff || newValue > prevValue + maxDiff;
}

void refreshHall(float newhall){
  //SI EL VALOR ES DISINTO AL ANTERIOR LO ACTUALIZA O SI ES MUY VIEJO
   if (checkBoundHall(newhall, hall)) {
      hall = newhall;
      Serial.print(millis());
      Serial.print(" - Cambio en el SENSOR:");
      Serial.println(hall);
      hall_change= true;
  }
  else {
        if ( (millis()>hall_last_change + 600000) or millis()<hall_last_change) {
          hall = newhall;
          Serial.print(millis());
          Serial.print(" - Mucho tiempo sin cambio :");
          Serial.println(hall);
          hall_change=true;
        }    
  }
}

void refreshVolt(float newvolt){
    if (checkBoundVolt(newvolt, volt, voltdiff)) {
      volt = newvolt;
      Serial.print(millis());
      Serial.print(" - Cambio en la tensión:");
      Serial.println(volt);
      volt_change= true;
  }
  else {
        if ( (millis()>volt_last_change + 600000) or millis()<volt_last_change) {
          volt = newvolt;
          Serial.print(millis());
          Serial.print(" - Mucho tiempo sin cambio en la tensión:");
          Serial.println(volt);
          volt_change=true;
        }
  }
}

void publishValue(){
     if (hall_change or volt_change ){
       if (isConnectToMQTT() and WiFi.status() == WL_CONNECTED ){
       //if (client.connect(mqtt_clientID, mqtt_username, mqtt_password)){
          delay(20);
          if (hall_change ) {
            client.publish(hallTopic, String(hall).c_str(), true);
            hall_last_change=millis();
          }
          if (volt_change ) {
            client.publish(poteTopic, String(volt).c_str(), true);
            volt_last_change=millis();
          }
          volt_change=false;
          hall_change=false;
       }
       else Serial.println("No se pudo conectar al server MQTT: no se transmite.");
  }
  else Serial.println("Sin cambios en las variables: no se transmite.");
  delay(2000);
}
