
package frc.robot.datum;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Timer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.revrobotics.ColorSensorV3.RawColor;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;

import frc.robot.datum.DatumSerial;
//import edu.wpi.first.wpilibj.SerialPort;
//import edu.wpi.first.wpilibj.SerialPort.Port;

public class DatumLight extends SubsystemBase {

    JsonNode datum;
    String receiveBuffer = "";
    
    DatumSerial serialPort;
    public DatumLight(String port) {
        serialPort = new DatumSerial(921600, port);
        configureSensor();
    }

    /*
    SerialPort serialPort;
    public DatumLight(Port port) {
        serialPort = new SerialPort(921600, port);
        configureSensor();
    }
    */

    @Override
    public void periodic() {

        while (serialPort.getBytesReceived() > 0) {
            byte[] inputChar = serialPort.read(1);
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

    public void write(String command){        
        try {
            command = command + "\r\n";
            serialPort.writeString(command);
            Timer.delay(0.05);
            if (getResponse() == false){
                System.out.print(command);
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public boolean getResponse(){
        String response = serialPort.readString();
        if (response.contains("200 OK")){
            return true;
        }        
        else {
            System.out.print(response);
            return false;
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
        write("set /config?automaticReporting=true");
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