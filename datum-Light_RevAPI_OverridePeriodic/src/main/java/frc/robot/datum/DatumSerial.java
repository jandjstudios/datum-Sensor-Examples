package frc.robot.datum;

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

    public byte[] read(){
        int count = getBytesReceived();
        return read(count);
    }

    public String readString(int count){             
        return new String(read(count));
    }
    
    public String readString(){
        int count = serialPort.bytesAvailable();
        return readString(count);
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