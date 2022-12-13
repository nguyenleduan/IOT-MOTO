#include <SoftwareSerial.h>
#include <OneButton.h>

SoftwareSerial BTSerial(3, 2); // RX | TX  --->TX  | RX (HC-05)

OneButton button (A7, true); // vàng
OneButton buttonYenXe (11, true); // vàng
OneButton buttonRF1 (8, false); // pin rf 1 | 1 remote
OneButton buttonRF2 (7, false); // pin rf 3 | 2 remote
OneButton buttonRF3 (6, false); // pin rf 4 | 3 remote
//OneButton buttonRF4 (9 , false); // pin rf 2 | 4 remote
#define PinConnectBluetooth 4
//#define PinGuard 10
#define PinLedButton A0// xanh lá 9 KIET
#define PinCoi A5
#define PinOutYen 12
int countGuard = 0;
int countNotGuard = 0;
int state = 0;
int  pinPower = A1;
int  pinHaza = A2;
int  pinStartUp = A3; 
int countConnect = 0;
unsigned long hientai = 0;
unsigned long thoigian;
int timecho = 1000;
int Time = 0;
int powerTime = 0;
int timeLockAll = 30;
int lock = 0;
int guard = 0;
boolean guardMotor = false;
boolean statusMotor = false;
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
  button.attachMultiClick(attachMultiClicks);
  /////////////////////Yen xe /////////
  buttonYenXe.attachMultiClick(OnLongClickYenXe);

  buttonYenXe.attachClick(onClick);

  buttonYenXe.attachDoubleClick(doubleLick);
  buttonYenXe.attachLongPressStart(OnClickYenxe);

  //  buttonYenXe.attachDoubleClick(doubleLick);


  //  buttonYenXe.attachMultiClick(attachMultiClicks);
  /// button remote 1
  buttonRF1.attachClick(onClickRF1) ; // bật tắt nguồn
  buttonRF1.attachLongPressStart(longClickRF1); // bật máy đề xe
  /// button remote 2
  
  buttonRF2.attachClick(onClickRF2) ;  // bật tắt trạng thái
  buttonRF2.attachLongPressStart(longClickRF2); // bật tắt chống trộm
  /// BUTTON REMOTE 3
  buttonRF3.attachClick(onClickRF3) ;
  pinMode(PinOutYen, OUTPUT);
  pinMode(pinHaza, OUTPUT);
  pinMode(pinPower, OUTPUT);
  pinMode(pinStartUp, OUTPUT);
  pinMode(PinLedButton, OUTPUT);
//  pinMode(PinGuard, INPUT);
  pinMode(PinCoi, OUTPUT); 
  pinMode(PinLedButton, OUTPUT);
  pinMode(PinConnectBluetooth, INPUT);
  digitalWrite(PinOutYen, LOW);
  digitalWrite(pinHaza, HIGH);
  digitalWrite(pinPower, HIGH);
  digitalWrite(pinStartUp, HIGH);
  digitalWrite(PinLedButton, HIGH);
    digitalWrite(PinCoi, HIGH);
  notification(3, 400);
}
void loop()
{

  //  Serial.println(digitalRead(PinOutYen));
  //
  //  digitalWrite(PinOutYen, LOW);
  //  delay(5000);
  //  Serial.println(digitalRead(PinOutYen));
  //  digitalWrite(PinOutYen, HIGH);

  //  delay(5000);

//  if (guardMotor) {
//    chongtrom();
//  }
  buttonYenXe.tick();
  button.tick();
  buttonRF1.tick();
  buttonRF2.tick();
  buttonRF3.tick();
  delayMililis();
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
void haza(int index) {
  for (int i = 0; i < index; i++) {
    digitalWrite(pinHaza, LOW);
    delay(500);
    digitalWrite(pinHaza, HIGH);
    delay(500);
  }
}
void attachMultiClicks() {
  Serial.println("lock 1");
  if (lock != 1) {
    countConnect = timeLockAll - 5;
    lockAll();
    notification(2, 500);
  }
}
//////////////////////////////////////////////////////////// Yên xe
void OnClickYenxe() {
  Serial.println("Mở yên");
  digitalWrite(PinOutYen, HIGH);
  delay(1000);
  digitalWrite(PinOutYen, LOW);
}

void OnLongClickYenXe() {
  if (digitalRead(pinHaza) == LOW) {
    digitalWrite(pinHaza, HIGH);
  } else {
    digitalWrite(pinHaza, LOW);
  }


}

//////////////////////////////////////////////////////////// action RF 1
void onClickRF1() {
  if (digitalRead(pinPower) == HIGH) {
    statusMotor = true;
    digitalWrite(pinPower, LOW);
    digitalWrite(A4, HIGH);
    Serial.print("\nPOWER ON");
    countConnect = 0;
    delay(1000);
    haza(1);
  } else {
    haza(1);
    delay(500);
    digitalWrite(pinPower, HIGH);
    digitalWrite(A4, LOW);
    Serial.print("\nPOWER OFF");
    statusMotor = false;
  }
}
void longClickRF1() {
  digitalWrite(pinPower, LOW);
  Serial.print("\n POWER ON");
  delay(1500);
  Serial.print("\n đề xe");
  powerTime = 0;
  guardMotor = false;
  digitalWrite(pinStartUp, LOW);
  delay(1500); ///////////////////////// thời gian đề xe
  digitalWrite(pinStartUp, HIGH);
}
//////////////////////////////////////////////////////////// action RF 2
void onClickRF2() {
  if (statusMotor) {
    Serial.println("\nStatus false");
    statusMotor = false;

  } else {
    statusMotor = true;
    Serial.println("\nStatus true ");
  }
}
void longClickRF2() {
  if (guardMotor) {
    guardMotor = false;
    Serial.println("Tắt chống trộm");
    haza(1);
  } else {
    guardMotor = true;
    Serial.println("Bất chống trộm");
    // notification
    lockAll();
    haza(2);
  }
}
//////////////////////////////////////////////////////////// action RF 3
void onClickRF3() { 
  Serial.println("Mở yên");
  digitalWrite(PinOutYen, HIGH);
  delay(1000);
  digitalWrite(PinOutYen, LOW);
} 
////////////////////////////////////////////////////////////////// Button
void onClick() {
  Serial.println("\nButton click");
  if (countConnect <= timeLockAll && lock == 0 ) {
    startPower();
  } else if (powerTime == 1) {
    startPower();
  }
  notification(2, 500);
}
void longClick() {
  //  if (lock != 1) {
  //    lock = 1;
  countConnect = timeLockAll + 3;
  lockAll();
  //    guardMotor = true;
  notification(4, 400);
  //  }
}
void doubleLick() { /// bật nguồn đề
  if (digitalRead(pinPower) == HIGH) {
    startPower();
    Serial.println("Bật nguồn");
    notification(2, 400);
    startUp(1500);
    Serial.println("Đề máy");
  } else {
    startPower();
  }
}

void notiHaza(int n, int time) {
  for (int i = 0; i < n; i++) {
    digitalWrite(pinHaza, LOW);
    delay(500);
    digitalWrite(pinHaza, HIGH);
    delay(500);
    notification(1, time);
  }
}

void chongtrom() {
//  if (digitalRead(PinGuard) == LOW) {
//    countGuard ++;
//    if (countGuard == 1) {
//      Serial.print("\n----- countGuard - 1 --------");
//      notiHaza(2, 400);
//      delay(1000);
//    }
//    if (countGuard == 2) {
//      Serial.print("\n----- countGuard - 2 --------");
//      notiHaza(3, 400);
//      delay(1000);
//    }
//    if (countGuard >= 3) {
//      Serial.print("\n----- countGuard - 3 --------");
//      notiHaza(1, 10000);
//      delay(1000);
//      countGuard = 0;
//    }
//  } else {
//    countNotGuard ++;
//    if (countNotGuard >= 200) {
//      Serial.print("\n----- countGuard - 0 --------");
//      countNotGuard = 0;
//    }
//  }


}

void delayMililis() {
  thoigian = millis();
  if (thoigian - hientai >= timecho) {
    hientai = millis();
    if (digitalRead(pinPower) == LOW || statusMotor || digitalRead(PinConnectBluetooth) == HIGH) {

      //      Serial.print("\n----- Connected --------");
      // khi xe dang hoat dongg
      // mỏ trang thái

      digitalWrite(A4, HIGH);
    } else {
      Serial.print("\nTime delayy :");
      Serial.println(countConnect);
      if (lock != 1) {
        if (countConnect == timeLockAll) {
          lockAll();
          Serial.println("\n off all");
          countConnect = countConnect + 1;
        } else {
          if (countConnect >= timeLockAll - 5 && countConnect <= timeLockAll + 1) {
            digitalWrite(A4, LOW);
            delay(100);
            Serial.println("\n Warning off");
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
void startUp(int time) {
  if (lock != 1) {
    digitalWrite(pinStartUp, HIGH);
    digitalWrite(pinStartUp, LOW);
    delay(time);
    digitalWrite(pinStartUp, HIGH);
  }
}
void evenCoi(int time) {
  digitalWrite(pinHaza, HIGH);
  digitalWrite(pinHaza, LOW);
  delay(time);
  digitalWrite(pinHaza, HIGH);
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
        statusMotor = false;
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
          statusMotor = false;
        }
      } else {
        digitalWrite(pinPower, HIGH);
        Serial.print("\n POWER OFF");
        statusMotor = false;
      }
    }
  }
}
void lockAll() {
  Serial.print("\nLock ALL");
  powerTime = 0;
  digitalWrite(A4, LOW);
  digitalWrite(pinPower, HIGH); 
  digitalWrite(pinStartUp, HIGH);
  statusMotor = false;
  notification(4, 200);
}

void notification(int n, int time) {
  for (int i = 0; i < n; i++) {
    digitalWrite(A5, HIGH);
    delay(time);
    digitalWrite(A5, LOW);
    delay(time);
  }
}
void checkData(char  data) {
  switch (data) {
    case '2':
      Serial.print("\n nhip 1");
      evenCoi(200);
      break;
    case '3':
      Serial.print("\n nhip 2");
      notiHaza(2, 400);
      break;
    //    case '4':
    //      Serial.print("\n nhip 3");
    //      digitalWrite(A0, HIGH);
    //      delay(2000);
    //      digitalWrite(A0, LOW);
    //      break;
    //    case '5':
    //      notification(2, 200);
    //      Serial.print("\n Coi 1s");
    //      evenCoi(1000);
    //      break;
    //    case '6':
    //      notification(2, 200);
    //      Serial.print("\n Coi 2s");
    //      evenCoi(2000);
    //      break;
    //    case '7':
    //      notification(2, 200);
    //      Serial.print("\n Coi 3s");
    //      evenCoi(3000);
    //      break;
    //    case '8':
    //      notification(2, 200);
    //      Serial.print("\n Coi 4s");
    //      evenCoi(4000);
    //      break;
    //    case '9':
    //      notification(2, 200);
    //      Serial.print("\n Coi 5s");
    //      evenCoi(5000);
    //      break;
    ////////////// Đề
    case 'a':
      notification(2, 200);
      Serial.print("\n De 1s");
      startUp(1000);
      break;
    case 'b':
      notification(2, 200);
      Serial.print("\n De 2s");
      startUp(2000);
      break;
    case 'c':
      notification(2, 200);
      Serial.print("\n De 3s");
      startUp(3000);
      break;
    case 'd':
      notification(2, 200);
      Serial.print("\n De 4s");
      startUp(4000);
      break;
    case 'e':
      notification(2, 200);
      Serial.print("\n De 5s");
      startUp(5000);
      break;
    case 'f':
      notification(2, 200);
      Serial.print("\n De 6s");
      startUp(6000);
      break;
    case 'g':
      notification(2, 200);
      Serial.print("\n De 7s");
      startUp(7000);
      break;
    case 'h':
      notification(2, 200);
      Serial.print("\n De 8s");
      startUp(8000);
      break;
    ////////////////// power
    case 'i': //// click
      notification(2, 200);
      Serial.print("\n  Power now");
      startPower();
      break;
    case 'k': // long click
      Serial.print("\n  Power long time");
      powerTime = 1;
      notification(4, 200);

      break;
    case 'l':
      notification(2, 200);
      Serial.print("\n  Power 3");
      //      powerTime = 1;
      //      startPower();
      break;
    //////////////// Screen
    case 'm':
      notification(2, 200);
      Serial.print("\n Chi tim xe");
      break;
    case 'n':
      notification(2, 200);
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
      notification(2, 200);
      break;
    case 'p':
      notification(2, 200);
      Serial.print("\n tim xe coi 1s ");
      evenCoi(1000);
      break;
    /// bảo vệ
    case 'w':
      Serial.println("\n Bật bảo vệ");
      notification(4, 200);
      guard = 1;
      break;
    //////////////  Connect
    case 'x':
      countConnect = 0;
      //      notification(1, 200);
      Serial.print("\n smart phone runing");
      break;
    case 'v':    /// lock
      lock = 1;
      countConnect = timeLockAll + 3;
      lockAll();
      notification(5, 200);
      break;
    case 't': /// unlock
      lock = 0;
      countConnect = 0;
      notification(2, 200);
      break;
    case 'z':

      digitalWrite(pinPower, LOW);
      Serial.print("\n POWER ON");
      delay(1500);
      Serial.print("\n đề xe");
      powerTime = 0;
      guardMotor = false;
      digitalWrite(pinStartUp, LOW);
      delay(1500); ///////////////////////// thời gian đề xe
      digitalWrite(pinStartUp, HIGH);
      break;
  }
}
