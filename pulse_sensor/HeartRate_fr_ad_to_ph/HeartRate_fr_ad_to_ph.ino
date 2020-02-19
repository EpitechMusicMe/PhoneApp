#include <SoftwareSerial.h> //시리얼통신 라이브러리 호출

// pulse main

/*  Pulse Sensor Amped 1.5    by Joel Murphy and Yury Gitman   http://www.pulsesensor.com

----------------------  Notes ----------------------  ----------------------
This code:
1) Blinks an LED to User's Live Heartbeat   PIN 13
2) Fades an LED to User's Live HeartBeat    PIN 5
3) Determines BPM
4) Prints All of the Above to Serial

Read Me:
https://github.com/WorldFamousElectronics/PulseSensor_Amped_Arduino/blob/master/README.md
 ----------------------       ----------------------  ----------------------
*/

#define PROCESSING_VISUALIZER 1
#define SERIAL_PLOTTER  2

//  Variables
int pulsePin = 0;                 // Pulse Sensor purple wire connected to analog pin 0
int blinkPin = 13;                // pin to blink led at each beat
int fadePin = 5;                  // pin to do fancy classy fading blink at each beat
int fadeRate = 0;                 // used to fade LED on with PWM on fadePin

// Volatile Variables, used in the interrupt service routine!
volatile int BPM;                   // int that holds raw Analog in 0. updated every 2mS
volatile int Signal;                // holds the incoming raw data
volatile int IBI = 600;             // int that holds the time interval between beats! Must be seeded!
volatile boolean Pulse = false;     // "True" when User's live heartbeat is detected. "False" when not a "live beat".
volatile boolean QS = false;        // becomes true when Arduoino finds a beat.

static boolean serialVisual = true; 
// SET THE SERIAL OUTPUT TYPE TO YOUR NEEDS
// PROCESSING_VISUALIZER works with Pulse Sensor Processing Visualizer
//      https://github.com/WorldFamousElectronics/PulseSensor_Amped_Processing_Visualizer
// SERIAL_PLOTTER outputs sensor data for viewing with the Arduino Serial Plotter
//      run the Serial Plotter at 115200 baud: Tools/Serial Plotter or Command+L
static int outputType = SERIAL_PLOTTER;


void ledFadeToBeat(){
    fadeRate -= 15;                         //  set LED fade value
    fadeRate = constrain(fadeRate,0,255);   //  keep LED fade value from going into negative numbers!
    analogWrite(fadePin,fadeRate);          //  fade LED
  }


// 블루투스 통신
 
int blueTx=2;   //Tx (보내는핀 설정)at
int blueRx=3;   //Rx (받는핀 설정)
SoftwareSerial Phone(blueTx, blueRx);  //시리얼 통신을 위한 객체선언
 
void setup() 
{
  Serial.begin(9600);   //시리얼모니터 // computer
  Phone.begin(9600); //블루투스 시리얼 // phone

  // pulse
  pinMode(blinkPin,OUTPUT);         // pin that will blink to your heartbeat!
  pinMode(fadePin,OUTPUT);          // pin that will fade to your heartbeat!
  //Serial.begin(9600);             // we agree to talk fast!
  interruptSetup();                 // sets up to read Pulse Sensor signal every 2mS
   // IF YOU ARE POWERING The Pulse Sensor AT VOLTAGE LESS THAN THE BOARD VOLTAGE,
   // UN-COMMENT THE NEXT LINE AND APPLY THAT VOLTAGE TO THE A-REF PIN
//   analogReference(EXTERNAL);
}
void loop()
{
  serialOutput() ;
  ledFadeToBeat();                      // Makes the LED Fade Effect Happen
  delay(1000);                             //  take a break

  // result
  if (Phone.available()) {    // if phone is available,
    if (QS == true){     // A Heartbeat Was Found
                       // BPM and IBI have been Determined
                       // Quantified Self "QS" true when arduino finds a heartbeat
        fadeRate = 255;         // Makes the LED Fade Effect Happen
                                // Set 'fadeRate' Variable to 255 to fade LED with pulse
        serialOutputWhenBeatHappens();   // A Beat Happened, Output that to serial.
        QS = false;                      // reset the Quantified Self flag for next time
  }   
    Serial.write(Phone.read());  //블루투스측(핸드폰) 내용을 시리얼모니터에 출력 // computer write. phone read.
    
  }
  if (Serial.available()) {         
    Phone.write(Serial.read());  //시리얼 모니터 내용을 블루투스(핸드폰) 측에 WRITE // phone write. com read.
  }

}


// pulse all~


//////////
/////////  All Serial Handling Code,
/////////  It's Changeable with the 'outputType' variable
/////////  It's declared at start of code.
/////////

void serialOutput(){   // Decide How To Output Serial.
  switch(outputType){
    case PROCESSING_VISUALIZER:
      sendDataToSerial('S', Signal);     // goes to sendDataToSerial function
      break;
    case SERIAL_PLOTTER:  // open the Arduino Serial Plotter to visualize these data
      Serial.print(BPM);
      Serial.print(",");
      Serial.print(IBI);
      Serial.print(",");
      Serial.println(Signal);

      Phone.print(BPM);
      Phone.print(",");
      Phone.print(IBI);
      Phone.print(",");
      Phone.println(Signal);
      break;
    default:
      break;
  }

}

//  Decides How To OutPut BPM and IBI Data
void serialOutputWhenBeatHappens(){
  switch(outputType){
    case PROCESSING_VISUALIZER:    // find it here https://github.com/WorldFamousElectronics/PulseSensor_Amped_Processing_Visualizer
      sendDataToSerial('B',BPM);   // send heart rate with a 'B' prefix
      sendDataToSerial('Q',IBI);   // send time between beats with a 'Q' prefix
      break;

    default:
      break;
  }
}

//  Sends Data to Pulse Sensor Processing App, Native Mac App, or Third-party Serial Readers.
void sendDataToSerial(char symbol, int data ){
    Serial.print(symbol);
    Serial.println(data);
  }

// pulse interrupt




volatile int rate[10];                    // array to hold last ten IBI values
volatile unsigned long sampleCounter = 0;          // used to determine pulse timing
volatile unsigned long lastBeatTime = 0;           // used to find IBI
volatile int P =512;                      // used to find peak in pulse wave, seeded
volatile int T = 512;                     // used to find trough in pulse wave, seeded
volatile int thresh = 530;                // used to find instant moment of heart beat, seeded
volatile int amp = 0;                   // used to hold amplitude of pulse waveform, seeded
volatile boolean firstBeat = true;        // used to seed rate array so we startup with reasonable BPM
volatile boolean secondBeat = false;      // used to seed rate array so we startup with reasonable BPM


void interruptSetup(){  // CHECK OUT THE Timer_Interrupt_Notes TAB FOR MORE ON INTERRUPTS 
  // Initializes Timer2 to throw an interrupt every 2mS.
  TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
  TCCR2B = 0x06;     // DON'T FORCE COMPARE, 256 PRESCALER
  OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
  TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
  sei();             // MAKE SURE GLOBAL INTERRUPTS ARE ENABLED
}


// THIS IS THE TIMER 2 INTERRUPT SERVICE ROUTINE.
// Timer 2 makes sure that we take a reading every 2 miliseconds
ISR(TIMER2_COMPA_vect){                         // triggered when Timer2 counts to 124
  cli();                                      // disable interrupts while we do this
  Signal = analogRead(pulsePin);              // read the Pulse Sensor
  sampleCounter += 2;                         // keep track of the time in mS with this variable
  int N = sampleCounter - lastBeatTime;       // monitor the time since the last beat to avoid noise

    //  find the peak and trough of the pulse wave
  if(Signal < thresh && N > (IBI/5)*3){       // avoid dichrotic noise by waiting 3/5 of last IBI
    if (Signal < T){                        // T is the trough
      T = Signal;                         // keep track of lowest point in pulse wave
    }
  }

  if(Signal > thresh && Signal > P){          // thresh condition helps avoid noise
    P = Signal;                             // P is the peak
  }                                        // keep track of highest point in pulse wave

  //  NOW IT'S TIME TO LOOK FOR THE HEART BEAT
  // signal surges up in value every time there is a pulse
  if (N > 250){                                   // avoid high frequency noise
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI/5)*3) ){
      Pulse = true;                               // set the Pulse flag when we think there is a pulse
      digitalWrite(blinkPin,HIGH);                // turn on pin 13 LED
      IBI = sampleCounter - lastBeatTime;         // measure time between beats in mS
      lastBeatTime = sampleCounter;               // keep track of time for next pulse

      if(secondBeat){                        // if this is the second beat, if secondBeat == TRUE
        secondBeat = false;                  // clear secondBeat flag
        for(int i=0; i<=9; i++){             // seed the running total to get a realisitic BPM at startup
          rate[i] = IBI;
        }
      }

      if(firstBeat){                         // if it's the first time we found a beat, if firstBeat == TRUE
        firstBeat = false;                   // clear firstBeat flag
        secondBeat = true;                   // set the second beat flag
        sei();                               // enable interrupts again
        return;                              // IBI value is unreliable so discard it
      }


      // keep a running total of the last 10 IBI values
      word runningTotal = 0;                  // clear the runningTotal variable

      for(int i=0; i<=8; i++){                // shift data in the rate array
        rate[i] = rate[i+1];                  // and drop the oldest IBI value
        runningTotal += rate[i];              // add up the 9 oldest IBI values
      }

      rate[9] = IBI;                          // add the latest IBI to the rate array
      runningTotal += rate[9];                // add the latest IBI to runningTotal
      runningTotal /= 10;                     // average the last 10 IBI values
      BPM = 60000/runningTotal;               // how many beats can fit into a minute? that's BPM!
      QS = true;                              // set Quantified Self flag
      // QS FLAG IS NOT CLEARED INSIDE THIS ISR
    }
  }

  if (Signal < thresh && Pulse == true){   // when the values are going down, the beat is over
    digitalWrite(blinkPin,LOW);            // turn off pin 13 LED
    Pulse = false;                         // reset the Pulse flag so we can do it again
    amp = P - T;                           // get amplitude of the pulse wave
    thresh = amp/2 + T;                    // set thresh at 50% of the amplitude
    P = thresh;                            // reset these for next time
    T = thresh;
  }

  if (N > 2500){                           // if 2.5 seconds go by without a beat
    thresh = 530;                          // set thresh default
    P = 512;                               // set P default
    T = 512;                               // set T default
    lastBeatTime = sampleCounter;          // bring the lastBeatTime up to date
    firstBeat = true;                      // set these to avoid noise
    secondBeat = false;                    // when we get the heartbeat back
  }

  sei();                                   // enable interrupts when youre done!
}// end isr

// pulse timer

/*
  These notes put together by Joel Murphy for Pulse Sensor Amped, 2015
  The code that this section is attached to uses a timer interrupt
  to sample the Pulse Sensor with consistent and regular timing.
  The code is setup to read Pulse Sensor signal at 500Hz (every 2mS).
  The reasoning for this can be found here:
  http://pulsesensor.com/pages/pulse-sensor-amped-arduino-v1dot1

  There are issues with using different timers to control the Pulse Sensor sample rate.
  Sometimes, user will need to switch timers for access to other code libraries.
  Also, some other hardware may have different timer setup requirements. This page
  will cover those different needs and reveal the necessary settings. There are two
  part of the code that will be discussed. The interruptSetup() routine, and
  the interrupt function call. Depending on your needs, or the Arduino variant that you use,
  check below for the correct settings.


  ******************************************************************************************
  ARDUINO UNO, Pro 328-5V/16MHZ, Pro-Mini 328-5V/16MHz (or any board with ATmega328P running at 16MHz)

 >> Timer2

    Pulse Sensor Arduino UNO uses Timer2 by default.
    Use of Timer2 interferes with PWM on pins 3 and 11.
    There is also a conflict with the Tone library, so if you want tones, use Timer1 below.

      void interruptSetup(){
        // Initializes Timer2 to throw an interrupt every 2mS.
        TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
        TCCR2B = 0x06;     // DON'T FORCE COMPARE, 256 PRESCALER
        OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
        TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
        sei();             // MAKE SURE GLOBAL INTERRUPTS ARE ENABLED
      }

    use the following interrupt vector with Timer2

      ISR(TIMER2_COMPA_vect)

 >> Timer1

    Use of Timer1 interferes with PWM on pins 9 and 10.
    The Servo library also uses Timer1, so if you want servos, use Timer2 above.

      void interruptSetup(){
        // Initializes Timer1 to throw an interrupt every 2mS.
        TCCR1A = 0x00; // DISABLE OUTPUTS AND PWM ON DIGITAL PINS 9 & 10
        TCCR1B = 0x11; // GO INTO 'PHASE AND FREQUENCY CORRECT' MODE, NO PRESCALER
        TCCR1C = 0x00; // DON'T FORCE COMPARE
        TIMSK1 = 0x01; // ENABLE OVERFLOW INTERRUPT (TOIE1)
        ICR1 = 16000;  // TRIGGER TIMER INTERRUPT EVERY 2mS
        sei();         // MAKE SURE GLOBAL INTERRUPTS ARE ENABLED
      }

    Use the following ISR vector for the Timer1 setup above

      ISR(TIMER1_OVF_vect)

 >> Timer0

    DON'T USE TIMER0! Timer0 is used for counting delay(), millis(), and micros().
                      MESSING WITH Timer0 IS HIGHLY UNADVISED!

  ******************************************************************************************
  ARDUINO Fio, Lilypad, ProMini328-3V/8MHz (or any board with ATmega328P running at 8MHz)

  >> Timer2

    Pulse Sensor Arduino UNO uses Timer2 by default.
    Use of Timer2 interferes with PWM on pins 3 and 11.
    There is also a conflict with the Tone library, so if you want tones, use Timer1 below.

      void interruptSetup(){
        // Initializes Timer2 to throw an interrupt every 2mS.
        TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
        TCCR2B = 0x05;     // DON'T FORCE COMPARE, 128 PRESCALER
        OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
        TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
        sei();             // MAKE SURE GLOBAL INTERRUPTS ARE ENABLED
      }

    use the following interrupt vector with Timer2

      ISR(TIMER2_COMPA_vect)

 >> Timer1

    Use of Timer1 interferes with PWM on pins 9 and 10.
    The Servo library also uses Timer1, so if you want servos, use Timer2 above.

      void interruptSetup(){
        // Initializes Timer1 to throw an interrupt every 2mS.
        TCCR1A = 0x00; // DISABLE OUTPUTS AND PWM ON DIGITAL PINS 9 & 10
        TCCR1B = 0x11; // GO INTO 'PHASE AND FREQUENCY CORRECT' MODE, NO PRESCALER
        TCCR1C = 0x00; // DON'T FORCE COMPARE
        TIMSK1 = 0x01; // ENABLE OVERFLOW INTERRUPT (TOIE1)
        ICR1 = 8000;  // TRIGGER TIMER INTERRUPT EVERY 2mS
        sei();         // MAKE SURE GLOBAL INTERRUPTS ARE ENABLED
      }

    Use the following ISR vector for the Timer1 setup above

      ISR(TIMER1_OVF_vect)

 >> Timer0

    DON'T USE TIMER0! Timer0 is used for counting delay(), millis(), and micros().
                      MESSING WITH Timer0 IS HIGHLY UNADVISED!


  ******************************************************************************************
  ARDUINO Leonardo (or any board with ATmega32u4 running at 16MHz)

  >> Timer1

    Use of Timer1 interferes with PWM on pins 9 and 10.

      void interruptSetup(){
          TCCR1A = 0x00;
          TCCR1B = 0x0C; // prescaler = 256
          OCR1A = 0x7C;  // count to 124
          TIMSK1 = 0x02;
          sei();
      }

  The only other thing you will need is the correct ISR vector in the next step.

      ISR(TIMER1_COMPA_vect)


  ******************************************************************************************
  ADAFRUIT Flora, ARDUINO Fio v3 (or any other board with ATmega32u4 running at 8MHz)

  >> Timer1

    Use of Timer1 interferes with PWM on pins 9 and 10.

      void interruptSetup(){
          TCCR1A = 0x00;
          TCCR1B = 0x0C; // prescaler = 256
          OCR1A = 0x3E;  // count to 62
          TIMSK1 = 0x02;
          sei();
      }

  The only other thing you will need is the correct ISR vector in the next step.

      ISR(TIMER1_COMPA_vect)
  ******************************************************************************************
  ADAFRUIT Gemma, ADAFRUIT Trinket 8MHz, Digispark Pro 8MHz, (or any other board with ATtiny85 running at 8MHz)

    NOTE: Gemma does not do serial communication! Comment out or remove the Serial code in the Arduino sketch!
    
    NOTE: You must use Software Serial with the Trinket or Digispark! 
          A the top of the main code page put these lines
          
            #define rxPin 3
            #define txPin 4
            SoftwareSerial uart(rxPin, txPin);

          Then, whenever the word 'Serial' is used, replace it with 'uart'
            example:
              change Serial.begin(115200); to uart.begin(57600);
            
    NOTE: Use pin 2 to connect the Pulse Sensor Purple Pin on Trinket and Gemma!

  Timer1

    Use of Timer1 breaks PWM output on pin D1

      void interruptSetup(){
        TCCR1 = 0x88;      // Clear Timer on Compare, Set Prescaler to 128 TEST VALUE
        GTCCR &= 0x81;     // Disable PWM, don't connect pins to events
        OCR1C = 0x7C;      // Set the top of the count to  124 TEST VALUE
        OCR1A = 0x7C;      // Set the timer to interrupt after counting to TEST VALUE
        bitSet(TIMSK,6);   // Enable interrupt on match between TCNT1 and OCR1A
        sei();             // Enable global interrupts
      }
    The only other thing you will need is the correct ISR vector in the next step.

      ISR(TIMER1_COMPA_vect)

  ******************************************************************************************
  ADAFRUIT Trinket with 16MHz software setting, Digispark Pro 16MHz, (or any other board with ATtiny85 running at 16MHz)

    NOTE: Use analog pin 2 for the Pulse Sensor purple wire.
    
    NOTE: You must use Software Serial with the Trinket or Digispark! 
          A the top of the main code page put these lines
          
            #define rxPin 3
            #define txPin 4
            SoftwareSerial uart(rxPin, txPin);

          Then, whenever the word 'Serial' is used, replace it with 'uart'
            example:
              change Serial.begin(115200); to uart.begin(57600);

  Timer1

    Use of Timer1 breaks PWM output on pin D1

      void interruptSetup(){
        TCCR1 = 0x89;      // Clear Timer on Compare, Set Prescaler to 256
        GTCCR &= 0x81;     // Disable PWM, don't connect pins to events
        OCR1C = 0x7C;      // Set the top of the count to  124
        OCR1A = 0x7C;      // Set the timer to interrupt after counting to 124
        bitSet(TIMSK,6);   // Enable interrupt on match between TCNT1 and OCR1A
        sei();             // Enable global interrupts
      }
    The only other thing you will need is the correct ISR vector in the next step.

      ISR(TIMER1_COMPA_vect)
      
  ******************************************************************************************

  IF YOU DON'T SEE THE MICROCONTROLLER YOU ARE USING, BUT YOU WANT A QUICK AND DIRTY SOLUTION

  So many new micros are coming out that it's kind of mind boggling. We will add to this list with
  code that uses interupts when we can, but if your micro is not listed here, and you are not willing
  or able to grab a hardware timer yourself, here is a shortcut that will work.
  It won't have the tight timing of a hardware interrupt, but it just might be good enough.
  We are calling this the 'Software Interrupt' version.
  The code below will set up a microsecond timer and 'trigger' every 2mS (or so).
  
  FIRST:
  You will need to change the name of the funcion in the Interrupts tab from
  'ISR(TIMER2_COMPA_vect)'
  to
  'void getPulse()'
  
  THEN:
  Comment out the entire interruptSetup() function in the interrupts tab in order for this to work.
  
  USE:
  The code example below. Notice that we are using the micros() and the millis() to time the sample rate and the fade rate.
  DO NOT put any delays in the loop, or it will break the sample timing!

  Happy Hacking!



  // FIRST, CREATE VARIABLES TO PERFORM THE SAMPLE TIMING AND LED FADE FUNCTIONS
  unsigned long lastTime; // used to time the Pulse Sensor samples
  unsigned long thisTime; // used to time the Pulse Sensor samples
  unsigned long fadeTime; // used to time the LED fade
  
  void setup(){
    pinMode(blinkPin,OUTPUT);         // pin that will blink to your heartbeat!
    pinMode(fadePin,OUTPUT);          // pin that will fade to your heartbeat!
    Serial.begin(115200);             // we agree to talk fast!
    // ADD THIS LINE IN PLACE OF THE interruptSetup() CALL
    lastTime = micros();              // get the time so we can create a software 'interrupt'
    // IF YOU ARE POWERING The Pulse Sensor AT VOLTAGE LESS THAN THE BOARD VOLTAGE,
    // UN-COMMENT THE NEXT LINE AND APPLY THAT VOLTAGE TO THE A-REF PIN
    //   analogReference(EXTERNAL);
  } //end of setup()

  //IN THE LOOP, ADD THE CODE THAT WILL DO THE 2mS TIMING, AND CALL THE getPulse() FUNCTION.
  void loop(){

    serialOutput() ;

    thisTime = micros();            // GET THE CURRENT TIME
    if(thisTime - lastTime > 2000){ // CHECK TO SEE IF 2mS HAS PASSED
      lastTime = thisTime;          // KEEP TRACK FOR NEXT TIME
      getPulse();                   //CHANGE 'ISR(TIMER2_COMPA_vect)' TO 'getPulse()' IN THE INTERRUPTS TAB!
    }

  if (QS == true){     // A Heartbeat Was Found
                       // BPM and IBI have been Determined
                       // Quantified Self "QS" true when arduino finds a heartbeat
        fadeRate = 255;         // Makes the LED Fade Effect Happen
                                // Set 'fadeRate' Variable to 255 to fade LED with pulse
        fadeTime = millis();    // Set the fade timer to fade the LED
        serialOutputWhenBeatHappens();   // A Beat Happened, Output that to serial.
        QS = false;                      // reset the Quantified Self flag for next time
  }
  
  if(millis() - fadeTime > 20){
    fadeTime = millis();
    ledFadeToBeat();                      // Makes the LED Fade Effect Happen
    }
    
} // end of loop



  ******************************************************************************************





  ******************************************************************************************





  ******************************************************************************************





  ******************************************************************************************





  ******************************************************************************************





  ******************************************************************************************



*/
