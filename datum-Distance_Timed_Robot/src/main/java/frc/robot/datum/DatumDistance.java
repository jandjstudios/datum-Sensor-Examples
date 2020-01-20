
package frc.robot.datum;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.SerialPort.Port;

import frc.robot.datum.DatumSerial;

public class DatumDistance extends SubsystemBase {

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

    public DatumDistance(String port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }

    public DatumDistance(Port port) {
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

    // These methods demonstrate how to access the data parsed from the
    // incoming JSON data packet. The incoming data packets are mapped 
    // to a JsonNode in the DatumSerial class when a complete packet 
    // has arrived.  Individual data elements can be accessed by using 
    // the keys from the incoming data packet.  The values are 
    // returned as an array.  Accessing the first value is done by the 
    // '.get(0)' instruction. 

    public void configureDistanceSensor(){
        datumSerial.sendCommand("set /sensor/distance/config?enabled=true&units=mm");
        datumSerial.sendCommand("set /sensor/distance/config?showStatusInfo=true&showRateReturnInfo=true");
        datumSerial.sendCommand("set /sensor/distance/config?distanceMode=short");
        datumSerial.sendCommand("set /sensor/distance/config?timingBudget=20&interMeasurementPeriod=25");
        datumSerial.sendCommand("set /sensor/distance/config?filter=none&sampleRate=50&dataRate=50");
    }

    public void configureSensor(){
        datumSerial.sendCommand("set /config?automaticReporting=false&compactReport=true&reportRate=50");
        configureDistanceSensor();
        datumSerial.sendCommand("set /config?automaticReporting=true");
    }
    
    public double getTimestamp(){
        return datum.get("timestamp").asDouble();
    }

    public double getDistance(){
        return datum.get("distance").get("distance").get(0).asDouble();
    }

    public double getSignalRateReturn(){
        return datum.get("distance").get("signalRateReturn").get(0).asDouble();
    }

    public double getAmbientRateReturn(){
        return datum.get("distance").get("ambientRateReturn").get(0).asDouble();
    }

    public String getStatus(){
        return datum.get("distance").get("status").get(0).toString();
    }    
}