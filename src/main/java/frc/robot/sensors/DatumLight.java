
package frc.robot.sensors;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.revrobotics.ColorSensorV3.RawColor;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.ColorShim;

import com.fazecast.jSerialComm.*;

public class DatumLight {

    SerialPort serialPort;
    JsonNode datum;
    ObjectMapper mapper = new ObjectMapper();

    public DatumLight(String port) {
        try {
            serialPort = SerialPort.getCommPort(port);
            serialPort.setComPortParameters(921600, 8, 1, SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 100);

            serialPort.openPort();
            serialPort.addDataListener(new SerialPortMessageListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
                }

                @Override
                public byte[] getMessageDelimiter() {
                    return new byte[] { (byte) 0x0d, (byte) 0x0a };
                }

                @Override
                public boolean delimiterIndicatesEndOfMessage() {
                    return true;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    byte[] delimitedMessage = event.getReceivedData();
                    try {
                        datum = mapper.readValue(delimitedMessage, JsonNode.class);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }                
                }               
            });             
        }
        catch (Exception  ex){
            System.out.println(ex);
        }
        configureSensor();
    }

    public void write(String command){        
        try {
            command = command + "\r\n";
            OutputStream dataOut = serialPort.getOutputStream();
            dataOut.write(command.getBytes());
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
        write("set /config?automaticReporting=true");
    }
    
    public Color getColor() {

        double red = datum.get("color").get("red").get(0).asInt()/65535.0;
        double green = datum.get("color").get("green").get(0).asInt()/65535.0;
        double blue = datum.get("color").get("blue").get(0).asInt()/65535.0;
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