#include <SoftwareSerial.h>
#include <TinyGPS.h>

float prev_lat = 0.0;
float prev_lng = 0.0;
//Set this value equal to the baud rate of your GPS
#define GPSBAUD 9600
 
// Create an instance of the TinyGPS object
TinyGPS gps;
// Initialize the NewSoftSerial library to the pins you defined above
SoftwareSerial uart_gps(2, 3);
float latitude, longitude;
 

void getgps(TinyGPS &gps);
 
void setup()
{
  Serial.begin(9600);
  //Sets baud rate of your GPS
  uart_gps.begin(GPSBAUD);
  
  Serial.println("");
  Serial.println("GPS Shield QuickStart Example Sketch v12");
  Serial.println("       ...waiting for lock...           ");
  Serial.println("");
}
 
void loop()
{
  while(uart_gps.available())     // While there is data on the RX pin...
  {
      int c = uart_gps.read();    // load the data into a variable...
      if(gps.encode(c))      // if there is a new valid sentence...
      {
        getgps(gps);         // then grab the data.
      }
   
  }

  while(Serial.available())
  {
  switch(Serial.read())
  {
    case 'P':
      SendTextMessage();
      break;
    case 'D':
      sendSQL();
      break;
  }
 }
}

 
// The getgps function will get and print the values we want.
void getgps(TinyGPS &gps)
{
  
  // Define the variables that will be used
  // Then call this function
  gps.f_get_position(&latitude, &longitude);
  // You can now print variables latitude and longitude


  if((latitude != prev_lat) || (longitude != prev_lng))
  {
  prev_lat = latitude;
  prev_lng = longitude;
  sendSQL();
  }
  
  // Same goes for date and time
  int year;
  byte month, day, hour, minute, second, hundredths;
  gps.crack_datetime(&year,&month,&day,&hour,&minute,&second,&hundredths);
  // Print data and time
  //Serial.print("Date: "); Serial.print(month, DEC); Serial.print("/"); 
  //Serial.print(day, DEC); Serial.print("/"); Serial.print(year);
  //Serial.print("  Time: "); Serial.print(hour, DEC); Serial.print(":"); 
  //Serial.print(minute, DEC); Serial.print(":"); Serial.print(second, DEC); 
  //Serial.print("."); Serial.println(hundredths, DEC);
  //Since month, day, hour, minute, second, and hundr

  //Serial.print("Altitude (meters): "); Serial.println(gps.f_altitude());  
  // Same goes for course
  //Serial.print("Course (degrees): "); Serial.println(gps.f_course()); 
  // And same goes for speed
  //Serial.print("Speed(kmph): "); Serial.println(gps.f_speed_kmph());
  //Serial.println();
  
  // Here you can print statistics on the sentences.
  unsigned long chars;
  unsigned short sentences, failed_checksum;
  gps.stats(&chars, &sentences, &failed_checksum);
  //Serial.print("Failed Checksums: ");Serial.print(failed_checksum);
  //Serial.println(); Serial.println();
}


void SendTextMessage() // mesaj atar
{
  Serial.println("Sending Text...");
  Serial.print("AT+CMGF=1\r"); 
  delay(100);
 
  Serial.println("AT+CMGS = \"+905xxxxxxxxx\"");
  delay(100);
  Serial.print("Lat/Long: "); 
  delay(50);
  Serial.print(latitude,5); 
  Serial.print(", "); 
  Serial.println(longitude,5);
  delay(100);
  Serial.print((char)26);
  delay(100);
  Serial.println();
  Serial.println("Text Sent.");

}

void sendSQL()
{
  Serial.println("AT+SAPBR=3,1,\"CONTYPE\",\"GPRS\"");
  delay(500);
  Serial.println("AT+SAPBR=3,1,\"APN\",\"CMNET\"");
  delay(500);
  Serial.println("AT+SAPBR=1,1");
  delay(500);
  Serial.println("AT+SAPBR=2,1");
  delay(3000);
  Serial.println("AT+HTTPINIT");
  delay(500);
  Serial.println("AT+HTTPPARA=\"CID\",1");
  delay(500);
  Serial.print("AT+HTTPPARA=\"URL\",\"http://localhost/arduino.php?");
  Serial.print("addLat=");
  Serial.print(latitude,5); 
  Serial.print("&addLng=");
  Serial.print(longitude,5);
  Serial.println("\"");
  delay(3000);
  Serial.println("AT+HTTPACTION=0");
  delay(15000);
  Serial.println("AT+HTTPTERM");
  delay(500);
  Serial.println("AT+SAPBR=0,1");
  delay(500);
  Serial.println("Send Database Complete");
}
