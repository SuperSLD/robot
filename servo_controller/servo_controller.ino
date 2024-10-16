#include <Servo.h>
#include <Wire.h>

#define I2C_ADDRESS 0x8

Servo servo[] = { Servo() };
int angleState[] = { 90 };

void setup() {
  // Init servo
  servo[0].attach(A0);
  
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
    servo[i].write(angleState[i]);
  }
  delay(15);
}
