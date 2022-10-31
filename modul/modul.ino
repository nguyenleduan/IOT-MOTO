#include <SoftwareSerial.h>
#include <OneButton.h>

SoftwareSerial BTSerial(2, 3); // RX | TX  --->TX  | RX (HC-05)

OneButton button (A2, true);
//#define ledPin 7
int state = 0;
int  pinPower = 4;
int  pinCoi = 5;
int  pinStartUp = 6;
int  pinDen = 7;
int countConnect = 0;
unsigned long hientai = 0;
unsigned long thoigian;
int timecho = 1000;
int Time = 0;
int powerTime = 0;
int timeLockAll = 30;
//boolean main;
// Pass 1112

// A4 // đèn button
void setup()
{
  Serial.begin(9600);
  Serial.println("Enter AT commands:");
  BTSerial.begin(9600);
  button.attachClick(onClick);
  button.attachLongPressStart(longClick);
  pinMode(pinCoi, OUTPUT);
  pinMode(pinPower, OUTPUT);
  pinMode(pinDen, OUTPUT);
  pinMode(pinStartUp, OUTPUT);
  pinMode(A0, OUTPUT);
  pinMode(A4, OUTPUT);
  digitalWrite(pinCoi, HIGH);
  digitalWrite(pinPower, HIGH);
  digitalWrite(pinDen, HIGH);
  digitalWrite(pinStartUp, HIGH);
  notification(2);
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
    if (digitalRead(pinPower) == LOW) {
      // khi xe dang hoat dongg
    } else {
      if (countConnect == timeLockAll) {
        lockAll();
        Serial.print("\n off all");
        countConnect = countConnect + 1;
      } else {
        if (countConnect >= timeLockAll - 10 && countConnect <= timeLockAll + 1) {
          notification(1);
          digitalWrite(A4, LOW);
          delay(500);
          Serial.print("\n Warning off");
        }
        if (powerTime == 1) {
          // code long lick
        } else {
          if (countConnect > timeLockAll + 2) {
            digitalWrite(A4, LOW);
          } else {
            countConnect = countConnect + 1;
            digitalWrite(A4, HIGH);
          }
        }
      }
    }
  }
}
void onClick() {
  startPower();
  notification(2);
}
void longClick() {
  countConnect = timeLockAll - 10;
  lockAll();
  notification(6);
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
      digitalWrite(A4, HIGH);
      Serial.print("\n POWER ON");
    } else {
      digitalWrite(pinPower, HIGH);
      digitalWrite(A4, LOW);
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

  notification(6);
  powerTime = 0;
  digitalWrite(A4, LOW);
  digitalWrite(pinCoi, HIGH);
  digitalWrite(pinPower, HIGH);
  digitalWrite(pinDen, HIGH);
  digitalWrite(pinStartUp, HIGH);
}

void notification(int n) {
  for (int i = 0; i < n; i++) {
    digitalWrite(A0, HIGH);
    delay(200);
    digitalWrite(A0, LOW);
    delay(200);
  }
}
void checkData(char  data) {
  switch (data) {
    case '2':
      notification(2);
      Serial.print("\n nhip 1");
      evenCoi(500);
      break;
    case '3':
      notification(2);
      Serial.print("\n nhip 2");
      evenCoi(500);
      evenCoi(500);
      delay(1000);
      evenCoi(500);
      evenCoi(500);
      break;
    case '4':
      notification(2);
      Serial.print("\n nhip 3");
      evenCoi(500);
      delay(1000);
      evenCoi(500);
      delay(1000);
      evenCoi(500);
      evenCoi(500);
      break;
    case '5':
      notification(2);
      Serial.print("\n Coi 1s");
      evenCoi(1000);
      break;
    case '6':
      notification(2);
      Serial.print("\n Coi 2s");
      evenCoi(2000);
      break;
    case '7':
      notification(2);
      Serial.print("\n Coi 3s");
      evenCoi(3000);
      break;
    case '8':
      notification(2);
      Serial.print("\n Coi 4s");
      evenCoi(4000);
      break;
    case '9':
      notification(2);
      Serial.print("\n Coi 5s");
      evenCoi(5000);
      break;
    ////////////// Đề
    case 'a':
      notification(2);
      Serial.print("\n De 1s");
      startUp(1000);
      break;
    case 'b':
      notification(2);
      Serial.print("\n De 2s");
      startUp(2000);
      break;
    case 'c':
      notification(2);
      Serial.print("\n De 3s");
      startUp(3000);
      break;
    case 'd':
      notification(2);
      Serial.print("\n De 4s");
      startUp(4000);
      break;
    case 'e':
      notification(2);
      Serial.print("\n De 5s");
      startUp(5000);
      break;
    case 'f':
      notification(2);
      Serial.print("\n De 6s");
      startUp(6000);
      break;
    case 'g':
      notification(2);
      Serial.print("\n De 7s");
      startUp(7000);
      break;
    case 'h':
      notification(2);
      Serial.print("\n De 8s");
      startUp(8000);
      break;
    ////////////////// power
    case 'i': //// click
      notification(2);
      Serial.print("\n  Power now");
      startPower();
      break;
    case 'k': // long click
      Serial.print("\n  Power long time");
      if (digitalRead(pinPower) == HIGH) {
        /// lock all
        lockAll();
      } else {
        powerTime = 1;
        digitalWrite(pinPower, LOW);
      }
      //      startPower();
      notification(4);
      break;
    case 'l':
      notification(2);
      Serial.print("\n  Power 3");
      //      powerTime = 1;
      //      startPower();
      break;
    //////////////// Screen
    case 'm':
      notification(2);
      Serial.print("\n Chi tim xe");
      break;
    case 'n':
      notification(2);
      Serial.print("\n tim xe bat nguon");
      digitalWrite(pinPower, LOW);
      break;
    case 'o':
      notification(2);
      Serial.print("\n tim xe bat nguon de 3s");
      if (digitalRead(pinPower) == HIGH) { 
        digitalWrite(pinPower, LOW);
        delay(2000);
        startUp(1000);
      }
      break;
    case 'p':
      notification(2);
      Serial.print("\n tim xe coi 1s ");
      evenCoi(1000);
      break;
    //////////////  Connect
    case 'x':
      countConnect = 0;
      Serial.print("\n smart phone runing");
      break;
  }
}
