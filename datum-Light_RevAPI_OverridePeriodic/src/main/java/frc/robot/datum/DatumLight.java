
package frc.robot.datum;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.revrobotics.ColorSensorV3.RawColor;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

//import com.fazecast.jSerialComm.*;
import frc.robot.datum.DatumSerial;

public class DatumLight extends SubsystemBase {

    DatumSerial serialPort;
    JsonNode datum;
    String receiveBuffer = "";

    public DatumLight(String port) {
        serialPort = new DatumSerial(921600, port);
        configureSensor();
    }

    @Override
    public void periodic() {
        //System.out.println("DatumLight periodic");
        while (serialPort.getBytesReceived() > 0){
            System.out.println(serialPort.getBytesReceived());
            String receiveChar = serialPort.readString(1);
            System.out.println(receiveChar);
        }
    }

    public void write(String command){        
        try {
            command = command + "\r\n";
            serialPort.writeString(command);
            //OutputStream dataOut = serialPort.getOutputStream();
            //dataOut.write(command.getBytes());
            Timer.delay(0.01);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void configureProximitySensorLED(){
        write("set /sensor/proximity/config?LEDstrength=25");        
    }

    public void configureProximitySensor(){
        write("set /sensor/proximity/config?enabled=true&units=counts");        
        write("set /sensor/proximity/config?gain=1");        
        write("set /sensor/proximity/config?filter=mean&sampleRate=100&dataRate=20");        
    }

    public void configureColorSensor(){
        write("set /sensor/proximity/config?enabled=true&units=counts");        
        write("set /sensor/color/config?gain=1");        
        write("set /sensor/color/config?filter=mean&sampleRate=100&dataRate=20");
    }

    public void configureSensor(){
        write("set /config?automaticReporting=false&compactReport=true&reportRate=20");
        configureColorSensor();
        configureProximitySensor();
        configureProximitySensorLED();
        //write("set /config?automaticReporting=true");
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
        int color = 0; //datum.get("color").get("red").get(0).asInt();
        return color;
    }

    public int getGreen(){
        int color = 0; //datum.get("color").get("green").get(0).asInt();
        return color;
    }

    public int getBlue(){
        int color = 0; //datum.get("color").get("blue").get(0).asInt();
        return color;
    }

    public int getIR(){
        int color = 0; //datum.get("color").get("ambient").get(0).asInt();
        return color;
    }

    public int getProximity(){
        int proximity = 0; //datum.get("proximity").get("proximity").get(0).asInt();
        return proximity;
    }

    public boolean hasReset(){
        return false;
    }
}