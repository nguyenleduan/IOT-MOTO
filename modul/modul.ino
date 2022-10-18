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
int powerTime = 0;
int timeLockAll = 60;
boolean main;
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
    char c = BTSerial.read();
    Serial.print("\n Data:  ");
    Serial.println(c);
    checkData(c);
  } else {
    if (countConnect == timeLockAll) {
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
    if (countConnect >= timeLockAll) {
      lockAll();
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
void onClick(){
  startPower();
}
void longClick(){
  lockAll();
}
void startUp(int time) {
  digitalWrite(pinStartUp, HIGH);
  digitalWrite(pinStartUp, LOW);
  delay(time);
  digitalWrite(pinStartUp, HIGH);
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
    if (countConnect < timeLockAll) {
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
  powerTime = 0;
  digitalWrite(pinCoi, LOW);
  digitalWrite(pinPower, LOW);
  digitalWrite(pinDen, LOW);
  digitalWrite(pinStartUp, LOW);
}

void checkData(char  data) {
  switch (data) {
    case '2':
      Serial.print("\n nhip 1");
      evenCoi(500);
      break;
    case '3':
      Serial.print("\n nhip 2");
      evenCoi(500); 
      evenCoi(500);
      delay(1000);
      evenCoi(500);
      evenCoi(500); 
      break;
    case '4':
      Serial.print("\n nhip 3");
      evenCoi(500);    
      delay(1000);
      evenCoi(500);
      delay(1000);
      evenCoi(500);
      evenCoi(500); 
      break;
    case '5':
      Serial.print("\n Coi 1s");
      evenCoi(1000);
      break;
    case '6':
      Serial.print("\n Coi 2s");
      evenCoi(2000);
      break;
    case '7':
      Serial.print("\n Coi 3s");
      evenCoi(3000);
      break;
    case '8':
      Serial.print("\n Coi 4s");
      evenCoi(4000);
      break;
    case '9':
      Serial.print("\n Coi 5s");
      evenCoi(5000);
      break;
    ////////////// Đề
    case 'a':
      Serial.print("\n De 1s");
      startUp(1000);
      break;
    case 'b':
      Serial.print("\n De 2s");
      startUp(2000);
      break;
    case 'c':
      Serial.print("\n De 3s");
      startUp(3000);
      break;
    case 'd':
      Serial.print("\n De 4s");
      startUp(4000);
      break;
    case 'e':
      Serial.print("\n De 5s");
      startUp(5000);
      break;
    case 'f':
      Serial.print("\n De 6s");
      startUp(6000);
      break;
    case 'g':
      Serial.print("\n De 7s");
      startUp(7000);
      break;
    case 'h':
      Serial.print("\n De 8s");
      startUp(8000);
      break;
    ////////////////// power
    case 'i': //// click
      Serial.print("\n  Power now");
      startPower();
      break;
    case 'k': // long click
      Serial.print("\n  Power long time");
      powerTime = 1;
      startPower();
      break;
    case 'l': // long click
      Serial.print("\n  Power 3");
//      powerTime = 1;
//      startPower();
      break;
    //////////////// Screen
    case 'm':
      Serial.print("\n Chi tim xe");
      break;
    case 'n':
      Serial.print("\n tim xe bat nguon");  
      digitalWrite(pinPower, LOW); 
      break;
    case 'o':
      Serial.print("\n tim xe bat nguon de 3s"); 
      digitalWrite(pinPower, LOW); 
      delay(2000);
      startUp(3000);
      break;
    case 'p':
      Serial.print("\n tim xe coi 2s ");
      evenCoi(1000);
      break;
    //////////////  Connect
    case 'x':
      countConnect = 0;
      Serial.print("\n smart phone runing");
      break;
  }
}
