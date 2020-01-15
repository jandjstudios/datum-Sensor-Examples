package frc.robot.sensors;

import com.fasterxml.jackson.databind.JsonNode;

import frc.robot.sensors.DatumSerial;
import com.revrobotics.ColorSensorV3.RawColor;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;


public class DatumLight {

    DatumSerial serial;
    String port;

    JsonNode datum;
   
    public DatumLight(String port) {
        serial = new DatumSerial(port);
        configureSensor();
    }

    public void configureProximitySensorLED(){
        serial.write("set /sensor/proximity/config?LEDstrength=25");        
    }

    public void configureProximitySensor(){
        serial.write("set /sensor/proximity/config?enabled=true&units=counts");        
        serial.write("set /sensor/proximity/config?gain=1");        
        serial.write("set /sensor/proximity/config?filter=mean&sampleRate=100&dataRate=20");
        
    }

    public void configureColorSensor(){
        serial.write("set /sensor/proximity/config?enabled=true&units=counts");        
        serial.write("set /sensor/color/config?gain=1");        
        serial.write("set /sensor/color/config?filter=mean&sampleRate=100&dataRate=20");
        
    }

    public void configureSensor(){
        serial.write("set /config?automaticReporting=false&compactReport=true&reportRate=20");
        
        configureColorSensor();
        configureProximitySensor();
        configureProximitySensorLED();
        serial.write("set /config?automaticReporting=true");
    }
    
    public Color getColor() {
        
        double red = 0; //datum.get("color").get("red").asDouble();
        double green = 0; //datum.get("color").get("green").asDouble();
        double blue = 0; //datum.get("color").get("blue").asDouble();
        
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
        //int color = datum.get("color").get("red").asInt();
        int color = 0;
        return color;
    }

    public int getGreen(){
        //int color = datum.get("color").get("green").asInt();
        int color = 0;
        return color;
    }

    public int getBlue(){
        //int color = datum.get("color").get("blue").asInt();
        int color = 0;
        return color;
    }

    public int getIR(){
        int color = 0;
        return color;
    }

    public int getProximity(){
        int proximity = datum.get("proximity").get("proximity").asInt();
        return proximity;
    }

    public boolean hasReset(){
        return false;
    }
}