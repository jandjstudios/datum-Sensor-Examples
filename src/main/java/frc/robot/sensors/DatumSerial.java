package frc.robot.sensors;

import java.io.OutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.fazecast.jSerialComm.*;

public class DatumSerial{

    String portName;
    SerialPort serialPort;
    ObjectMapper mapper = new ObjectMapper();
    byte portNum = 0;
    int handle = 0;

    public DatumSerial(String port) {

        System.out.println("DatumSerial");        
        //serialPort = new SerialPort(921600, SerialPort.Port.valueOf("COM3"));       
                
        System.out.println("DatumSerial-port created");   
        
        try {                
            serialPort = SerialPort.getCommPort("COM3");
            System.out.println("DatumSerial-port selected");      
            serialPort.setComPortParameters(115200, 8, 1, SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 100);

            serialPort.openPort();
            System.out.println("DatumSerial-port opened");              
            /*
            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
                @Override
                public void serialEvent(SerialPortEvent event)
                {
                   byte[] newData = event.getReceivedData();
                   for (int i = 0; i < newData.length; ++i)
                      System.out.print((char)newData[i]);

                }
             });            
             */
             
            serialPort.addDataListener(new SerialPortMessageListener() {
                @Override
                public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
             
                @Override
                public byte[] getMessageDelimiter() { return new byte[] { (byte)0x0d, (byte)0x0a }; }
             
                @Override
                public boolean delimiterIndicatesEndOfMessage() { return true; }
             
                @Override
                public void serialEvent(SerialPortEvent event)
                {
                   byte[] delimitedMessage = event.getReceivedData();
                   //System.out.println("Received the following delimited message: " + delimitedMessage.toString());
                   for (int i = 0; i < delimitedMessage.length; ++i)
                      System.out.print((char)delimitedMessage[i]);                   
                }               
            }); 
        }
        catch (Exception  ex){
            System.out.println(ex);
        }
    }
    public void write(String command){        
        try {
            command = command + "\r\n";
            System.out.print(command);
            OutputStream dataOut = serialPort.getOutputStream();
            dataOut.write(command.getBytes());
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void read(){
        System.out.println("read");

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
    *
    //static class SerialPortReader implements SerialPortEventListener {
    class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {

        }
    }*/   
}