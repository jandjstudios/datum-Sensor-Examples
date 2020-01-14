package frc.robot.sensors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import jssc.SerialPort;
import jssc.SerialPortEvent; 
import jssc.SerialPortEventListener; 
import jssc.SerialPortException;

public class DatumSerial{

    String portName;
    SerialPort serialPort;
    ObjectMapper mapper = new ObjectMapper();

    public DatumSerial(String port) {
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(921600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
    }
    public void write(String command){
        try {
            serialPort.writeBytes(command.getBytes());
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public void read(){

    }

    public String readSensor() {

        String response = "{\"timestamp\":12621.403,\"color\":{\"time\":[12621.275,12621.375],\"ambient\":[15,15],\"red\":[10,10],\"green\":[7,7],\"blue\":[7,7]},\"proximity\":{\"time\":[12621.278,12621.378],\"proximity\":[6,7]}}";

        try {
            JsonNode datum = mapper.readValue(response, JsonNode.class);
            JsonNode timestamp = datum.get("timestamp");
            JsonNode color = datum.get("color");
            Double colorDouble = datum.get("color").get("time").asDouble();
            JsonNode ambient = datum.get("color").get("ambient");
            System.out.println(timestamp);
            System.out.println(color);

        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        

        return response;
    }    
    /*
    * In this class must implement the method serialEvent, through it we learn about 
    * events that happened to our port. But we will not report on all events but only 
    * those that we put in the mask. In this case the arrival of the data and change the 
    * status lines CTS and DSR
    */
    //static class SerialPortReader implements SerialPortEventListener {
    class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR()){//If data is available
                if(event.getEventValue() == 10){//Check bytes count in the input buffer
                    //Read data, if 10 bytes available 
                    try {
                        byte buffer[] = serialPort.readBytes(10);
                    }
                    catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }    
}