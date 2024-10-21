#include <Servo.h>
#include <Wire.h>

#define I2C_ADDRESS 0x8

Servo servo0;
Servo servo1;
Servo servo2;
Servo servo3;
Servo servo4;
Servo servo5;
Servo servo6;
Servo servo7;
int angleState[] = { 90, 90, 90, 90, 90, 90, 90, 90 };

void setup() {
  // Init servo
  servo0.attach(7);
  servo1.attach(6);
  servo2.attach(5);
  servo3.attach(4);
  servo4.attach(3);
  servo5.attach(2);
  servo6.attach(8);
  servo7.attach(9);
  
  // Init I2C
  Wire.begin(I2C_ADDRESS);
  Wire.onReceive(receiveEvent);
  Serial.begin(9600);
  Serial.println("setup finish");
}

bool isReadCommand = false;
String recivedCommand = "";

void receiveEvent(int howMany) {
  while (Wire.available()) { // loop through all but the last
    char c = Wire.read(); // receive byte as a character
    if (c == 0x0) isReadCommand = true;
    if (c == 0x1) {
      isReadCommand = false;
      Serial.println(recivedCommand);
      moveServo();
      recivedCommand = "";
    }
    if (isReadCommand) {
      recivedCommand += c;
    }
  }
}

void moveServo() {
  int servoIndex = recivedCommand.substring(1, 2).toInt();
  int angle = recivedCommand.substring(3, recivedCommand.length()).toInt();
  if (servoIndex < sizeof(angleState)) {
    angleState[servoIndex] = angle;
  }
}

void loop() {
  for(int i = 0; i < sizeof(angleState); i++) {
    switch(i) {
      case 0:
        servo0.write(angleState[i]);
        break;
      case 1:
        servo1.write(angleState[i]);
        break;
      case 2:
        servo2.write(angleState[i]);
        break;
      case 3:
        servo3.write(angleState[i]);
        break;
      case 4:
        servo4.write(angleState[i]);
        break;
      case 5:
        servo5.write(angleState[i]);
        break;
      case 6:
        servo6.write(angleState[i]);
        break;
      case 7:
        servo7.write(angleState[i]);
        break;
    }
  }
  delay(20);
}
