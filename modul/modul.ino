#include <SoftwareSerial.h>

SoftwareSerial BTSerial(2, 3); // RX | TX  --->TX  | RX (HC-05)
#define ledPin 7
int state = 0;
void setup()
{
  Serial.begin(9600);
  Serial.println("Enter AT commands:");
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  BTSerial.begin(9600);  // HC-05 default speed in AT command more
}

void loop()
{
  
  // Keep reading from HC-05 and send to Arduino Serial Monitor
  if (BTSerial.available()){ 
    Serial.write(BTSerial.read()); 
  Serial.println("Enter AT asdaaaaaaa:");
  } 
}
