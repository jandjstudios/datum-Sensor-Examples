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
    String friendlyName = "";

    public DatumLight(String port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
        friendlyName = datumSerial.sendCommand("get /config/friendlyName");
        System.out.println("friendlyName: " + friendlyName);
    }

    public DatumLight(Port port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }
    
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