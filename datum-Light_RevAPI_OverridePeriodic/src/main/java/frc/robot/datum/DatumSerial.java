package frc.robot.datum;

import java.io.OutputStream;
import edu.wpi.first.wpilibj.Timer;

import com.fazecast.jSerialComm.*;

public class DatumSerial {

    SerialPort serialPort;

    public DatumSerial(int baud, String port){
        try{
            serialPort = SerialPort.getCommPort(port);
            serialPort.setComPortParameters(baud, 8, 1, SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 100);
            serialPort.openPort();
        }
        catch(SerialPortInvalidPortException ex){
            System.out.println(ex);
        }
    }

    public void close(){
        serialPort.closePort();
    }
    
    /*
    public void	disableTermination(){

    }

    public void	enableTermination(){

    }

    public void	enableTermination​(char terminator){

    }

    public void	flush(){

    }*/

    public int getBytesReceived(){
        return serialPort.bytesAvailable();
    }

    public byte[] read(int count){
        byte[] receivedData = new byte[count];
        serialPort.readBytes(receivedData, count);
        return receivedData;
    }

    public String readString(int count){
        //byte[] receivedData = read(count);
        //return receivedData.toString();    
        return read(count).toString();
    }
    
    public String readString(){
        Integer byteCount = serialPort.bytesAvailable();
        return readString(byteCount);
    }

    /*
    public void	reset(){

    }

    public void	setFlowControl​(SerialPort.FlowControl flowControl){

    }

    public void	setReadBufferSize​(int size){

    }

    public void	setTimeout​(Double timeout){

    }

    public void	setWriteBufferMode​(SerialPort.WriteBufferMode mode){

    }

    public void	setWriteBufferSize​(int size){

    }*/
    
    public int write(byte[] buffer, int count){
        return serialPort.writeBytes(buffer, count);
    }

    public int writeString(String data){
        try {
            //data = data + "\r\n";
            //OutputStream dataOut = serialPort.getOutputStream();
            //dataOut.write(data.getBytes());
            return write(data.getBytes(), data.length());            
        }
        catch (Exception ex) {
            System.out.println(ex);
            return -1;
        }
    }
} 