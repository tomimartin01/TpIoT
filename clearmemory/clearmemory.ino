#include <EEPROM.h>

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  EEPROM.begin(512);
  delay(3000);
  Serial.println("clearing eeprom");
  for (int i = 0; i < 152; ++i) {
    EEPROM.write(i, 0);
   }
   EEPROM.commit();
  String esid;
  delay(3000);
  for (int i = 0; i < 152; ++i)
  {
    esid += char(EEPROM.read(i));
   
  }
  delay(3000);
  Serial.print("la memoria contiene");
  delay(3000);
  Serial.println(EEPROM.read(1));
}

void loop() {
  // put your main code here, to run repeatedly:
  Serial.println(":");
  delay(2000);
}
