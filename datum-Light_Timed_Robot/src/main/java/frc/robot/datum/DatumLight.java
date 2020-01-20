package frc.robot.datum;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.revrobotics.ColorSensorV3.RawColor;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;
import edu.wpi.first.wpilibj.SerialPort.Port;

import frc.robot.datum.DatumSerial;

public class DatumLight extends SubsystemBase {

    JsonNode datum;
    String receiveBuffer = "";
    DatumSerial datumSerial;

    // Constructors for the datum sensors.  The first constructor accepts
    // a string argument corresponding to the port name of the host OS.
    // The DatumSerial class depends on jSerialComm.  To use this in the 
    // simulator or on the roboRIO you must include the following line in
    // your build.gradle file under dependencies.  More information on
    // jSerialComm can be found at https://fazecast.github.io/jSerialComm/.
    //
    // compile 'com.fazecast:jSerialComm:[2.0.0,3.0.0)'
    //
    // The second constructor uses the WPILib SerialPort.Port to identify
    // using the serial port libraries included in WPILib.
    
    public DatumLight(String port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }

    public DatumLight(Port port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }
    
    // Incoming data is captured by overriding the periodic method of
    // SubsystemBase.  The data is read in and if a new line is detected
    // the received data packet is passed to the object mapper for
    // parsing.  TimedRobot projects must also override the robotPeriodic
    // method in Robot.java.  Command based projects should already have
    // this override in place.

    @Override
    public void periodic() {     
        while (datumSerial.getBytesReceived() > 0) {
            byte[] inputChar = datumSerial.read(1);
            receiveBuffer += new String(inputChar);

            if (inputChar[0] == 13) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    datum = mapper.readValue(receiveBuffer, JsonNode.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                receiveBuffer = "";
            }
        } 
    }
     
    // The following methods provide a convenient way to configure
    // the sensor specifically for you application.  If your sensor
    // has been preconfigured it is not necessary to configure it 
    // again.  All settings are stored directly on each datum sensor.

    public void configureProximitySensorLED(){
        datumSerial.sendCommand("set /sensor/proximity/config?LEDstrength=25");        
    }

    public void configureProximitySensor(){
        datumSerial.sendCommand("set /sensor/proximity/config?enabled=true&units=counts");        
        datumSerial.sendCommand("set /sensor/proximity/config?gain=1");        
        datumSerial.sendCommand("set /sensor/proximity/config?filter=mean&sampleRate=100&dataRate=20");        
    }

    public void configureColorSensor(){
        datumSerial.sendCommand("set /sensor/proximity/config?enabled=true&units=counts");        
        datumSerial.sendCommand("set /sensor/color/config?gain=1");        
        datumSerial.sendCommand("set /sensor/color/config?filter=mean&sampleRate=100&dataRate=20");
    }

    public void configureSensor(){
        datumSerial.sendCommand("set /config?automaticReporting=false&compactReport=true&reportRate=20");
        configureColorSensor();
        configureProximitySensor();
        configureProximitySensorLED();
        datumSerial.sendCommand("set /config?automaticReporting=true");
    }
    
    // These methods demonstrate how to access the data parsed from the
    // incoming JSON data packet. The incoming data packets are mapped 
    // to a JsonNode in the DatumSerial class when a complete packet 
    // has arrived.  Individual data elements can be accessed by using 
    // the keys from the incoming data packet.  The values are 
    // returned as an array.  Accessing the first value is done by the 
    // '.get(0)' instruction. 
    //
    // These methods have been specifically modelled after the Rev Robotics
    // Color Sensor API.  As such it is necessary to include the  Rev
    // Robotics vendor dependencies.  More information is available at their
    // web site, http://www.revrobotics.com/rev-31-1557/. 

    public double getTimestamp(){
        return datum.get("timestamp").asDouble();
    }

    public Color getColor() {

        double red = getRed()/65535.0;
        double green = getGreen()/65535.0;
        double blue = getBlue()/65535.0;
        ColorShim color = new ColorShim(red, green, blue);
        return color;
    }

    public RawColor getRawColor(){
        int red = getRed();
        int green = getGreen();
        int blue = getBlue();
        int IR = getIR();
        return new RawColor(red, green, blue, IR);
    }

    public int getRed(){
        int color = datum.get("color").get("red").get(0).asInt();
        return color;
    }

    public int getGreen(){
        int color = datum.get("color").get("green").get(0).asInt();
        return color;
    }

    public int getBlue(){
        int color = datum.get("color").get("blue").get(0).asInt();
        return color;
    }

    public int getIR(){
        int color = datum.get("color").get("ambient").get(0).asInt();
        return color;
    }

    public int getProximity(){
        int proximity = datum.get("proximity").get("proximity").get(0).asInt();
        return proximity;
    }

    public boolean hasReset(){
        return false;
    }
}