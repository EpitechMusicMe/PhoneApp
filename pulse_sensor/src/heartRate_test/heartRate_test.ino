#define heartratePin A1
#include "DFRobot_Heartrate.h"

DFRobot_Heartrate heartrate(DIGITAL_MODE); ///< ANALOG_MODE or DIGITAL_MODE

void setup() {
  Serial.begin(115200);
}

void loop() {
  uint8_t rateValue;
  heartrate.getValue(heartratePin); ///< A1 foot sampled values
  rateValue = heartrate.getRate(); ///< Get heart rate value 
  if(rateValue)  {
    Serial.println(rateValue);
  }
  delay(20);
}
