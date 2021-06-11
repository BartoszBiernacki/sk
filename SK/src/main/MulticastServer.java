package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer {
    public static void main(String[] args) throws Exception{

        byte[] responseBytes = "ACK".getBytes();

        InetAddress group = Config.MULTICAST_ADDRESS;
        MulticastSocket s = new MulticastSocket(Config.MULTICAST_PORT);
        s.joinGroup(group);

        try{
            while (true){
                DatagramPacket recv = new DatagramPacket(new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
                s.receive(recv);
                String stringMsg = new String(recv.getData(), 0, recv.getLength(), "utf8");
                System.err.println("Got message: \"" + stringMsg + "\"");
                DatagramSocket responseSocket = new DatagramSocket();
                DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length);
                response.setAddress(recv.getAddress());
                response.setPort(recv.getPort());
                Thread.sleep(1000); // Ta linijka powoduje wstrzymanie wysy³ania odpowiedzi przez
                // jedn¹ sekundê --- nie ma ona zwi¹zku z obs³ug¹ UDP.
                responseSocket.send(response);
            }
        }finally {
            s.leaveGroup(group);
        }
    }
}