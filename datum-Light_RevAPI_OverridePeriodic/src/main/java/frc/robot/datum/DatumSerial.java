package frc.robot.datum;

import com.fazecast.jSerialComm.*;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.Timer;

public class DatumSerial {

    SerialPort jSerialCommPort;
    edu.wpi.first.wpilibj.SerialPort wpiSerialPort;
    private boolean usejSerialCommPort = true;

    public DatumSerial(int baud, String port){
        usejSerialCommPort = true;
        try{
            SerialPort[] ports = SerialPort.getCommPorts();
            System.out.println("\nAvailable Ports:\n");
            for (int i = 0; i < ports.length; ++i)
                System.out.println(ports[i].getSystemPortName() + ": " + 
                    ports[i].getDescriptivePortName() + ', ' + ports[i].getPortDescription());

            jSerialCommPort = SerialPort.getCommPort(port);
            jSerialCommPort.setComPortParameters(baud, 8, 1, SerialPort.NO_PARITY);
            jSerialCommPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 100);
            jSerialCommPort.openPort();
        }
        catch(SerialPortInvalidPortException ex){
            System.out.println(ex);
        }
    }

    public DatumSerial(int baud, Port port){
        usejSerialCommPort = false;
        wpiSerialPort = new edu.wpi.first.wpilibj.SerialPort(baud, port);
    }

    public void close(){
        if (usejSerialCommPort){
            jSerialCommPort.closePort();
        }
        else {
            wpiSerialPort.close();
        }
    }
    
    public int getBytesReceived(){
        if (usejSerialCommPort){
            return jSerialCommPort.bytesAvailable();
        }
        else {
            return wpiSerialPort.getBytesReceived();
        }   
    }

    public byte[] read(int count){
        if (usejSerialCommPort){
            byte[] receivedData = new byte[count];
            jSerialCommPort.readBytes(receivedData, count);
            return receivedData;
        }
        else {
            return wpiSerialPort.read(count);
        }
    }

    public byte[] read(){
        int count = getBytesReceived();
        return read(count);
    }

    public String readString(int count){             
        return new String(read(count));
    }
    
    public String readString(){
        int count = getBytesReceived();
        return readString(count);
    }
    
    public int write(byte[] buffer, int count){
        if (usejSerialCommPort) {
            return jSerialCommPort.writeBytes(buffer, count);
        }
        else {
            return wpiSerialPort.write(buffer, count);
        }
    }

    public int writeString(String data){
        try {
            return write(data.getBytes(), data.length());            
        }
        catch (Exception ex) {
            System.out.println(ex);
            return -1;
        }
    }

    // The following methods are specific to the datum sensors for confirming commands sent 
    // have been received.

    public boolean getResponse(){
        String response = readString();
        if (response.contains("200 OK")){
            return true;
        }        
        else {
            System.out.print(response);
            return false;
        }
    }

    public void sendCommand(String command){        
        try {
            command = command + "\r\n";
            writeString(command);

            Timer.delay(0.05);
            if (getResponse() == false){
                System.out.print(command);
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /* 
    public void	disableTermination(){}

    public void	enableTermination(){}

    public void	enableTermination​(char terminator){}

    public void	flush(){}

    public void	reset(){}

    public void	setFlowControl​(SerialPort.FlowControl flowControl){}

    public void	setReadBufferSize​(int size){}

    public void	setTimeout​(Double timeout){}

    public void	setWriteBufferMode​(SerialPort.WriteBufferMode mode){}

    public void	setWriteBufferSize​(int size){}
    */    
} 

