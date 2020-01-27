package frc.robot.datum;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.wpi.first.wpilibj.SerialPort.Port;

import frc.robot.datum.DatumSerial;

class DatumSensor extends SubsystemBase {

    JsonNode datum;
    String receiveBuffer = "";
    String sensorType = "datum-Sensor";
    String friendlyName = "friendlyName";
    DatumSerial datumSerial;

    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    // Constructors for the datum sensors. The first constructor accepts
    // a string argument corresponding to the port name of the host OS.
    // The DatumSerial class depends on jSerialComm. To use this in the
    // simulator or on the roboRIO you must include the following line in
    // your build.gradle file under dependencies. More information on
    // jSerialComm can be found at https://fazecast.github.io/jSerialComm/.
    //
    // compile 'com.fazecast:jSerialComm:[2.0.0,3.0.0)'
    //
    // The second constructor uses the WPILib SerialPort.Port to identify
    // using the serial port libraries included in WPILib.

    public DatumSensor(String port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }

    public DatumSensor(Port port) {
        // Autodetect using the WPI libraries is not supported at this time.
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }

    // Incoming data is captured by overriding the periodic method of
    // SubsystemBase. The data is read in and if a new line is detected
    // the received data packet is passed to the object mapper for
    // parsing. TimedRobot projects must also override the robotPeriodic
    // method in Robot.java. Command based projects should already have
    // this override in place.

    @Override
    public void periodic() {
        while (datumSerial.getBytesReceived() > 0) {
            byte[] inputChar = datumSerial.read(1);
            receiveBuffer += new String(inputChar);
            if (inputChar[0] == 13) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    datum = mapper.readValue(receiveBuffer, JsonNode.class);
                    traverse(datum, sensorType + "/" + friendlyName);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                receiveBuffer = "";
            }
        }
    }

    // This method uses recursive calls to traverse to parse the received
    // JSON data packet.  When an array is detected it determines the type
    // of data stored and then published it to a network table with the 
    // appropriate key.  
    
    public void traverse(JsonNode root, String networkTableKey) {

        if (root.isObject()) {
            Iterator<String> fieldNames = root.fieldNames();

            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = root.get(fieldName);
                traverse(fieldValue, networkTableKey + "/" + fieldName);
            }
        } else if (root.isArray()) {
            ArrayNode arrayNode = (ArrayNode) root;
            NetworkTableEntry entry = inst.getEntry(networkTableKey);
            ObjectMapper mapper = new ObjectMapper();

            if (arrayNode.get(0).isDouble()) {
                Double[] data = mapper.convertValue(arrayNode, Double[].class);
                entry.setNumberArray(data);
            } else if (arrayNode.get(0).isInt()) {
                Integer[] data = mapper.convertValue(arrayNode, Integer[].class);
                entry.setNumberArray(data);
            } else {
                String[] data = mapper.convertValue(arrayNode, String[].class);
                entry.setStringArray(data);
            }
        } else {
            NetworkTableEntry entry = inst.getEntry(networkTableKey);
            entry.setDouble(root.asDouble());
        }
    }

    // The following method automatically sets automaticReporting
    // and compactReports to true.  This is necessary to support
    // parsing the JSON data returned by each sensor.  This example
    // assumes that datum sensors attached to the system have been 
    // preconfigured offline.
    
    public void configureSensor() {

        ObjectMapper mapper = new ObjectMapper();

        String response = new String();
        datumSerial.sendCommand("set /config?automaticReporting=false&compactReport=true&reportRate=5");
        response = datumSerial.sendCommand("get /device/product");
        try {
            datum = mapper.readTree(response);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sensorType = datum.get("product").toString();

        response = datumSerial.sendCommand("get /config/friendlyName");
        try {
            datum = mapper.readTree(response);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        friendlyName = datum.get("friendlyName").toString();

        datumSerial.sendCommand("set /config?automaticReporting=true");
    }

}

public class DatumSensors {

    DatumSerial datumSerial = new DatumSerial();
    DatumSensor[] datumSensor = new DatumSensor[25];
    
    public DatumSensors(){
        
        List<String> portNames = datumSerial.getPorts();
        //System.out.println("\nAvailable Ports:\n");        

        for (int i = 0; i < portNames.size(); ++i){
            //System.out.println(portNames.get(i));
            datumSensor[i] = new DatumSensor(portNames.get(i));
        }            
    }

}