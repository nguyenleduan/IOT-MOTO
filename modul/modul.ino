#include <SoftwareSerial.h>
#include <OneButton.h>

SoftwareSerial BTSerial(3, 2); // RX | TX  --->TX  | RX (HC-05)

OneButton button (12, true);
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
int lock = 0;
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
  button.attachDoubleClick(doubleLick);
  button.attachLongPressStop(asdasd);
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
  notification(3);
}
void asdasd() {
  Serial.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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
      if (lock != 1) {
        if (countConnect == timeLockAll) {
          lockAll();
          Serial.print("\n off all");
          countConnect = countConnect + 1;
        } else {
          if (countConnect >= timeLockAll - 5 && countConnect <= timeLockAll + 1) {
            notification(1);
            digitalWrite(A4, LOW);
            delay(100);
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
}
void onClick() {
  if (countConnect <= timeLockAll && lock == 0 ) {
    startPower();
    notification(2);
  } else if (powerTime == 1) {
    startPower();
    notification(2);
  }
}
void longClick() {

  Serial.println("\n LongClick");
  //  if (lock != 1) {
  //    lock = 1;
  //    countConnect = timeLockAll + 3;
  //    lockAll();
  //    notification(4);
  //  }
}
void doubleLick() {
  if (lock != 1) {
    countConnect = timeLockAll - 5;
    lockAll();
    notification(6);
  }
}
void startUp(int time) {
  if (lock != 1) {
    digitalWrite(pinStartUp, HIGH);
    digitalWrite(pinStartUp, LOW);
    delay(time);
    digitalWrite(pinStartUp, HIGH);
  }
}
void evenCoi(int time) {
  digitalWrite(pinCoi, HIGH);
  digitalWrite(pinCoi, LOW);
  delay(time);
  digitalWrite(pinCoi, HIGH);
}
void startPower() {
  if (lock != 1) {
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
}
void lockAll() {

  powerTime = 0;
  digitalWrite(A4, LOW);
  digitalWrite(pinCoi, HIGH);
  digitalWrite(pinPower, HIGH);
  digitalWrite(pinDen, HIGH);
  digitalWrite(pinStartUp, HIGH);
  notification(6);
}

void notification(int n) {
  //  for (int i = 0; i < n; i++) {
  //    digitalWrite(A0, HIGH);
  //    delay(200);
  //    digitalWrite(A0, LOW);
  //    delay(200);
  //  }
}
void checkData(char  data) {
  switch (data) {
    case '2':
      Serial.print("\n nhip 1");
      evenCoi(200);
      break;
    case '3':
      Serial.print("\n nhip 2");
      evenCoi(200);
      delay(300);
      evenCoi(200);
      delay(100);
      evenCoi(200);
      delay(200);
      evenCoi(200);
      notification(2);
      break;
    case '4':
      Serial.print("\n nhip 3");
      digitalWrite(A0, HIGH);
      delay(2000);
      digitalWrite(A0, LOW);
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
      powerTime = 1;
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
      Serial.print("\n tim xe bat nguon de 3s");
      if (lock != 1) {
        if (digitalRead(pinPower) == HIGH) {
          digitalWrite(pinPower, LOW);
          delay(2500);
          startUp(1000);
        }
      }
      notification(2);
      break;
    case 'p':
      notification(2);
      Serial.print("\n tim xe coi 1s ");
      evenCoi(1000);
      break;
    //////////////  Connect
    case 'x':
      countConnect = 0;
      notification(1);
      Serial.print("\n smart phone runing");
      break;
    case 'v':    /// lock
      lock = 1;
      countConnect = timeLockAll + 3;
      lockAll();
      notification(5);
      break;
    case 't': /// unlock
      lock = 0;
      countConnect = 0;
      notification(2);
      break;
  }
}
