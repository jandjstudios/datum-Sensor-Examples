
package frc.robot.datum;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Timer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import frc.robot.datum.DatumSerial;
//import edu.wpi.first.wpilibj.SerialPort;
//import edu.wpi.first.wpilibj.SerialPort.Port;

public class DatumDistance extends SubsystemBase {

    JsonNode datum;
    String receiveBuffer = "";
    
    DatumSerial serialPort;
    public DatumDistance(String port) {
        serialPort = new DatumSerial(921600, port);
        configureSensor();
    }

    /*
    SerialPort serialPort;
    public DatumDistance(Port port) {
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
                //System.out.println(receiveBuffer);
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

    public void configureDistanceSensor(){
        write("set /sensor/distance/config?enabled=true&units=mm");
        write("set /sensor/distance/config?showStatusInfo=true&showRateReturnInfo=true");
        write("set /sensor/distance/config?distanceMode=short");
        write("set /sensor/distance/config?timingBudget=20&interMeasurementPeriod=25");
        write("set /sensor/distance/config?filter=none&sampleRate=50&dataRate=50");
    }

    public void configureSensor(){
        write("set /config?automaticReporting=false&compactReport=true&reportRate=50");
        configureDistanceSensor();
        write("set /config?automaticReporting=true");
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