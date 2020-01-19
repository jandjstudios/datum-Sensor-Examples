
package frc.robot.datum;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import frc.robot.datum.DatumSerial;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class DatumIMU extends SubsystemBase {

    JsonNode datum;
    String receiveBuffer = "";
    DatumSerial datumSerial;

    public DatumIMU(String port) {
        datumSerial = new DatumSerial(921600, port);
        configureSensor();
    }

    public DatumIMU(Port port) {
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

    public class DataPacket{
        public double t;
        public double x;
        public double y;
        public double z;
    }

    public void configureAccelerometer(){
        datumSerial.sendCommand("set /sensor/accelerometer/config?enabled=true");
        datumSerial.sendCommand("set /sensor/accelerometer/config?units=g&range=2");
        datumSerial.sendCommand("set /sensor/accelerometer/config?filterType=mean&sampleRate=119&dataRate=25");
    }

    public void configureGyro(){
        datumSerial.sendCommand("set /sensor/gyro/config?enabled=true");
        datumSerial.sendCommand("set /sensor/gyro/config?units=dps&range=245 dps");
        datumSerial.sendCommand("set /sensor/gyro/config?filterType=mean&sampleRate=119&dataRate=25");
    }

    public void configureMagnetometer(){
        datumSerial.sendCommand("set /sensor/magnetometer/config?enabled=true");
        datumSerial.sendCommand("set /sensor/magnetometer/config?units=G&range=4 G");
        datumSerial.sendCommand("set /sensor/magnetometer/config?filterType=mean&sampleRate=80&dataRate=25");
    }

    public void configureSensor(){
        datumSerial.sendCommand("set /config?automaticReporting=false&compactReport=true&reportRate=25");
        configureAccelerometer();
        configureGyro();
        configureMagnetometer();
        datumSerial.sendCommand("set /config?automaticReporting=true");
    }
    
    public double getTimestamp(){
        return datum.get("timestamp").asDouble();
    }

    public DataPacket getAccelerometer(){
        DataPacket accelerometer = new DataPacket();
        accelerometer.t = datum.get("accelerometer").get("t").get(0).asDouble();
        accelerometer.x = datum.get("accelerometer").get("x").get(0).asDouble();
        accelerometer.y = datum.get("accelerometer").get("y").get(0).asDouble();
        accelerometer.z = datum.get("accelerometer").get("z").get(0).asDouble();
        return accelerometer;
    }

    public DataPacket getGyro(){
        DataPacket gyro = new DataPacket();
        gyro.t = datum.get("gyro").get("t").get(0).asDouble();
        gyro.x = datum.get("gyro").get("x").get(0).asDouble();
        gyro.y = datum.get("gyro").get("y").get(0).asDouble();
        gyro.z = datum.get("gyro").get("z").get(0).asDouble();
        return gyro;
    }

    public DataPacket getMagnetometer(){
        DataPacket magnetometer = new DataPacket();
        magnetometer.t = datum.get("magnetometer").get("t").get(0).asDouble();
        magnetometer.x = datum.get("magnetometer").get("x").get(0).asDouble();
        magnetometer.y = datum.get("magnetometer").get("y").get(0).asDouble();
        magnetometer.z = datum.get("magnetometer").get("z").get(0).asDouble();
        return magnetometer;
    }  
}