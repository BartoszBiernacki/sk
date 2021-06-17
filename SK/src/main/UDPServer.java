package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
	

    public static void main(String[] args) throws Exception{

    	System.out.println("SERVER WYSTARTOWAL");
        //Otwarcie gniazda z okreslonym portem
        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

        byte[] byteResponse = "OK".getBytes("utf8");

        while (true){

            DatagramPacket receivedPacket = new DatagramPacket(
                     new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);

            datagramSocket.receive(receivedPacket);

            int length = receivedPacket.getLength();
            String message = new String(receivedPacket.getData(), 0, length, "utf8");
            
            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            
            System.out.println("Wiadomosc do przerobienia:");
            System.out.println(message);
            
            /*
            //liczy int+int=suma
            String[] liczbaStr = message.split("+");
            int[] liczba = new int[liczbaStr.length];
            for(int i=0; i<liczbaStr.length; i++)
            	liczba[i] = Integer.parseInt(liczbaStr[i]);  
            int suma = liczba[0] + liczba[1];
            String zwrotna = Integer.toString(suma);
            */
            
            //odsyla z powrotem do ostatniego wezla
            String zwrotna = "MOJA ODP";
            byteResponse = zwrotna.getBytes("utf8");
            Thread.sleep(1000); 
            DatagramPacket responce = new DatagramPacket( 
            		byteResponse, byteResponse.length, address, port);
            datagramSocket.send(responce);

        }
    }
}