
package frc.robot.datum;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Timer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import frc.robot.datum.DatumSerial;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class DatumDistance extends SubsystemBase {

    JsonNode datum;
    String receiveBuffer = "";
    
    DatumSerial datumSerial;

    public DatumDistance(String port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }

    public DatumDistance(Port port) {
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
                //System.out.println(receiveBuffer);
                receiveBuffer = "";
            }
        }        
    }

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