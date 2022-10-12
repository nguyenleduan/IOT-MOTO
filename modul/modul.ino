#include <SoftwareSerial.h>
#include <OneButton.h>

SoftwareSerial BTSerial(2, 3); // RX | TX  --->TX  | RX (HC-05)

OneButton button (4, true);
#define ledPin 7
int state = 0;  
int  pinPower = 5;
int  pinCoi = 6;
int  pinStartUp = 7;
int  pinDen = 8;
int power = 0;
int countConnect = 0;
unsigned long hientai = 0;
unsigned long thoigian;
int timecho = 5000;
// Pass 1112
void setup()
{
  Serial.begin(9600);
  Serial.println("Enter AT commands:"); 
  BTSerial.begin(9600); 
  button.attachClick(startPower); 
  button.attachLongPressStart(lockPower);   
  pinMode(pinCoi, OUTPUT);
  pinMode(pinPower, OUTPUT);
  pinMode(pinDen, OUTPUT);
  pinMode(pinStartUp, OUTPUT); 
  digitalWrite(pinCoi, LOW);
  digitalWrite(pinPower, LOW);
  digitalWrite(pinDen, LOW);
  digitalWrite(pinStartUp, LOW);
}
void loop()
{ 
  delayMililis();
  button.tick();
  if (BTSerial.available()){ 
//    Serial.print("\n Data:"); // doc data tu app
//     Serial.write(BTSerial.read());
    int a =  BTSerial.read();
    countConnect = 0; 
    checkData(a);
  }else{  
    if(countConnect == 60){
      lockPower();
    }
  } 
  if (Serial.available()){ // AT
    BTSerial.write(Serial.read()); 
  }
}

void delayMililis(){
  thoigian = millis();
  if(thoigian - hientai >= timecho){
    hientai = millis(); 
    Serial.print("\nTime delayy :");
    Serial.print(countConnect);
    if(countConnect >= 10){
      // off all
    Serial.print("\n off all");
    }else{ 
    countConnect = countConnect +1; 
    }
  }
}
void checkData(int  data){
   
   switch (data) {
 case 49:
    Serial.print("\n Power on");
    break;
  case 50:
    Serial.print("\n Start up");
    break;
  case 51:
    Serial.print("\n Coi xe");
    break; 
  case 120:
  countConnect = 0;
    Serial.print("\n smart phone runing");
    break; 
  }
}
void startPower(){
  
} 
void lockPower(){
  power = 0; 
  
  digitalWrite(pinCoi, LOW);
  digitalWrite(pinPower, LOW);
  digitalWrite(pinDen, LOW);
  digitalWrite(pinStartUp, LOW);
}
