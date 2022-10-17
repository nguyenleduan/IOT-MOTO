#include <SoftwareSerial.h>
#include <OneButton.h>

SoftwareSerial BTSerial(2, 3); // RX | TX  --->TX  | RX (HC-05)

OneButton button (9, true);
//#define ledPin 7
int state = 0;
int  pinPower = 4;
int  pinCoi = 5;
int  pinStartUp = 6;
int  pinDen = 7; 
int countConnect = 0;
unsigned long hientai = 0;
unsigned long thoigian;
int timecho = 5000;
int Time = 0;
int powerTime =0;
// Pass 1112
void setup()
{
  Serial.begin(9600);
  Serial.println("Enter AT commands:");
  BTSerial.begin(9600);
  button.attachClick(startPower);
  button.attachLongPressStart(lockAll);
  pinMode(pinCoi, OUTPUT);
  pinMode(pinPower, OUTPUT);
  pinMode(pinDen, OUTPUT);
  pinMode(pinStartUp, OUTPUT);
  digitalWrite(pinCoi, HIGH);
  digitalWrite(pinPower, HIGH);
  digitalWrite(pinDen, HIGH);
  digitalWrite(pinStartUp, HIGH);
}
void loop()
{
  delayMililis();
  button.tick();
  if (BTSerial.available()) {
//    Serial.print("\n Data:");
//    Serial.print(BTSerial.read()); // doc data tu app
    //        Serial.print("\n app:");
    //         Serial.write(BTSerial.read());
        int a =  BTSerial.read();
    countConnect = 0;
        checkData(a);
  } else {
    if (countConnect == 60) {
      lockAll();
    }
  }
  if (Serial.available()) { // AT
    BTSerial.write(Serial.read());
  }
}

void delayMililis() {
  thoigian = millis();
  if (thoigian - hientai >= timecho) {
    hientai = millis();
    Serial.print("\nTime delayy :");
    Serial.print(countConnect);
    if (countConnect >= 10) {
      // off all
      Serial.print("\n off all");
    } else {
      if (powerTime == 1) {
          // code long lick 
      } else {
        countConnect = countConnect + 1;
      }
    }
  }
}
void checkData(int  data) {
  switch (data) {
    //////////// Coi
    case 50:
      Serial.print("\n nhip 1");
      evenCoi(500);
      break;
    case 51:
      Serial.print("\n nhip 2");
      evenCoi(500);
      break;
    case 52:
      Serial.print("\n nhip 3");
      evenCoi(500);
      break;
    case 53:
      Serial.print("\n Coi 1s");
      evenCoi(1000);
      break;
    case 54:
      Serial.print("\n Coi 2s");
      evenCoi(2000);
      break;
    case 55:
      Serial.print("\n Coi 3s");
      evenCoi(3000);
      break;
    case 56:
      Serial.print("\n Coi 4s");
      evenCoi(4000);
      break;
    case 57:
      Serial.print("\n Coi 5s");
      evenCoi(5000);
      break;
    ////////////// Đề
    case 97:
      Serial.print("\n De 1s");
      startUp(1000);
      break;
    case 98:
      Serial.print("\n De 2s");
      startUp(2000);
      break;
    case 99:
      Serial.print("\n De 3s");
      startUp(3000);
      break;
    case 100:
      Serial.print("\n De 4s");
      startUp(4000);
      break;
    case 101:
      Serial.print("\n De 5s");
      startUp(5000);
      break;
    case 102:
      Serial.print("\n De 6s");
      startUp(6000);
      break;
    case 103:
      Serial.print("\n De 7s");
      startUp(7000);
      break;
    case 104:
      Serial.print("\n De 8s");
      startUp(8000);
      break;
    ////////////////// power
    case 105: //// click 
      Serial.print("\n  Power now");
         startPower(); 
      break;
    case 107: // long click 
      Serial.print("\n  Power long time");
      powerTime = 1; 
         startPower(); 
      break;
    //////////////// Screen
    case 109:
      Serial.print("\n Chi tim xe");
      break;
    case 110:
      Serial.print("\n tim xe bat nguon");
      break;
    case 111:
      Serial.print("\n tim xe bat nguon de 3s");
      break;
    case 112:
      Serial.print("\n tim xe ");
      break;
    //////////////  Connect
    case 120:
      countConnect = 0;
      Serial.print("\n smart phone runing");
      break;
  }
}
void startUp(int time) {
  digitalWrite(pinPower, HIGH);
  digitalWrite(pinPower, LOW);
  delay(time);
  digitalWrite(pinPower, HIGH);
}
void evenCoi(int time) {
  digitalWrite(pinCoi, HIGH);
  digitalWrite(pinCoi, LOW);
  delay(time);
  digitalWrite(pinCoi, HIGH);
}
void startPower() {
  if (powerTime == 1) { 
      if (digitalRead(pinPower) == HIGH) {
        digitalWrite(pinPower, LOW);
      Serial.print("\n POWER ON");
      } else {
        digitalWrite(pinPower, HIGH);
      Serial.print("\n POWER OFF");
      }
  } else { 
    if (countConnect < 10) {
      if (digitalRead(pinPower) == HIGH) {
        digitalWrite(pinPower, LOW);
      Serial.print("\n POWER ON");
      } else {
        digitalWrite(pinPower, HIGH);
      Serial.print("\n POWER OFF");
      }
    } else {
      digitalWrite(pinPower, HIGH);
      Serial.print("\n POWER OFF");
    }
  }
}
void lockAll() { 
  powerTime =0;
  digitalWrite(pinCoi, LOW);
  digitalWrite(pinPower, LOW);
  digitalWrite(pinDen, LOW);
  digitalWrite(pinStartUp, LOW);
}
