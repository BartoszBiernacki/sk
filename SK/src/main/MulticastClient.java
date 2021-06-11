package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;


public class MulticastClient {

public static void main(String[] args) throws Exception{
       DatagramSocket s = new DatagramSocket();

       byte[] message = "Test".getBytes("utf8");

 
       DatagramPacket packet = new DatagramPacket(message, message.length);
       packet.setPort(Config.PORT);
       packet.setAddress(Config.MULTICAST_ADDRESS);
       s.send(packet);

       System.out.println("Wys≥a≥em pakiet");
       s.setSoTimeout(1000);
       DatagramPacket response = new DatagramPacket(new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
       try{
           s.receive(response);
           System.out.println("Odpowiedü: ");
           System.out.print(new String(response.getData(), 0,  response.getLength(), "utf8"));
       }catch (SocketTimeoutException e){
           System.out.println("Nie otrzyma≥em odpowiedzi");
       }
    }
}