package main;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class Config {
    public static final int PORT = 9000;
    public static final int BUFFER_SIZE = 1024;
    public static final InetAddress MULTICAST_ADDRESS;
    public static final int MULTICAST_PORT = 9000;
    public static final InetAddress BCAST_ADDRESS;
    
    static {
        try{
            MULTICAST_ADDRESS = InetAddress.getByName("239.255.42.99");
            BCAST_ADDRESS = InetAddress.getByName("192.168.1.255");
        }catch (UnknownHostException e){
            throw new RuntimeException(e);
        }
    }
    
    
    
    
    public static int getRandomNumberUsingNextInt(int min, int max) 
	{
	    Random random = new Random();
	    return random.nextInt(max - min) + min;
	}
    
    public static String geterateIP()
	{
		int part1 = getRandomNumberUsingNextInt(0, 255);
		int part2 = getRandomNumberUsingNextInt(0, 255);
		int part3 = getRandomNumberUsingNextInt(0, 255);
		int part4 = getRandomNumberUsingNextInt(0, 255);
		
		String IP = part1 + "." + part2 + "." + part3 + "." + part4;
		
		return IP;
	}
}