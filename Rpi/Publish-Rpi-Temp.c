#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "MQTTClient.h"

#define ADDRESS     "tcp://localhost:1883"
#define CLIENTID    "ExampleClientPub"
#define TOPIC       "1111/Rpi/Temp"
#define PAYLOAD     "Hello World!"
#define QOS         1
#define TIMEOUT     10000L


void temperatura_cpu (char* temp ){
	
	FILE *file = fopen("/sys/class/thermal/thermal_zone0/temp", "r"); 
	
	if (file == NULL) 
    {
        printf("No se pudo encontrar file\n");
		exit(1);
	}
	
	while(fgets(temp, 10, file)) 
	{
		printf("Valor de Temperatura leido %s",temp);
		
	}
		fclose(file);
		
}

int main(int argc, char* argv[])
{
    MQTTClient client;
    MQTTClient_connectOptions conn_opts = MQTTClient_connectOptions_initializer;
    MQTTClient_message pubmsg = MQTTClient_message_initializer;
    MQTTClient_deliveryToken token;
    int rc;
    char temp[10]="";
 
    MQTTClient_create(&client, ADDRESS, CLIENTID,
        MQTTCLIENT_PERSISTENCE_NONE, NULL);
    conn_opts.keepAliveInterval = 20;
    conn_opts.cleansession = 1;

    while ((rc = MQTTClient_connect(client, &conn_opts)) != MQTTCLIENT_SUCCESS)
    {
        printf("Failed to connect to mqtt, return code %d\n", rc);
        exit(-1);
    }
    pubmsg.qos = QOS;
    pubmsg.retained = 0;
    while (1){
        temperatura_cpu(temp);
        pubmsg.payload = temp;
        pubmsg.payloadlen = strlen(temp);
        MQTTClient_publishMessage(client, TOPIC, &pubmsg, &token);
        sleep(5);
    }
 
    MQTTClient_disconnect(client, 10000);
    MQTTClient_destroy(&client);
    return rc;
}
