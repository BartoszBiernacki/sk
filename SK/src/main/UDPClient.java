package main;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;



public class UDPClient 
{
	public static LinkedList<String> ListOfNamesOfWorkingKnots = new LinkedList<String>();
	
	LinkedList<String> ListOfIdMessages;
	LinkedList<String> ListOfAskedServers;
	
	public UDPClient()
	{
		ListOfIdMessages =  new LinkedList<String>();
		ListOfAskedServers =  new LinkedList<String>();
	}

	
	public static void meetNodes( ) throws Exception
	{
       DatagramSocket s = new DatagramSocket();
       ListOfNamesOfWorkingKnots = new LinkedList<String>();
       
       byte[] message = "Say your name!".getBytes("utf8");

       DatagramPacket packet = new DatagramPacket(message, message.length);
       packet.setPort(Config.PORT);
       packet.setAddress(Config.MULTICAST_ADDRESS);
       s.send(packet);

       s.setSoTimeout(1000);
       while (true)
       {
    	   DatagramPacket response = new DatagramPacket(new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
           try{
               s.receive(response);
               String nazwaWezlaKtoryOdpowiedzial = new String(response.getData(), 0,  response.getLength(), "utf8");
               ListOfNamesOfWorkingKnots.add(nazwaWezlaKtoryOdpowiedzial);
           	}
           catch (SocketTimeoutException e)
           {
               break;
           }
       }
       s.close();
    }
	
	public static String getInputFromConsole()
	{
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		
		return input;
	}
	
	public static int getNumberOfNodesFromUser() throws Exception
	{
		meetNodes();
		
		String numberOfNodesTypedInByUserString = "";
		
		System.out.println("Przez ile wezlow chcesz aby szla twoja wiadomosc?");
		System.out.println("Maksmalna ilsoc dostepnych wezlow to: " + ListOfNamesOfWorkingKnots.size());
		System.out.println("Wprowadz liczbe wezlow: ");
		
		int numberOfNodesInBetween = 0;
		
		
		boolean typeInError = false;
		while (true)
		{
	        numberOfNodesTypedInByUserString = getInputFromConsole();
	        try 
	        {
	        	 numberOfNodesInBetween = Integer.parseInt(numberOfNodesTypedInByUserString);
	        	 typeInError = false;
			} catch (Exception e) 
	        {
				System.out.println("Wprowadz poprawna liczbe calkowita z przedzialu od 0 do " + ListOfNamesOfWorkingKnots.size());
				typeInError = true;
			}
	        
	        if(!typeInError)
	        {
	        	if (numberOfNodesInBetween < 0 || numberOfNodesInBetween > ListOfNamesOfWorkingKnots.size() )
		        {
		        	System.out.println("Wprowadz poprawna liczbe calkowita z przedzialu od 0 do " + ListOfNamesOfWorkingKnots.size());
		        }
		        else 
		        {
		        	break;
				}
	        }
	        
		}
        return numberOfNodesInBetween;
	}
	
	public static String getServerName ()
	{	
        System.out.println("Wprowadz nazwe servera:");
        String sreverName = getInputFromConsole();
        
        return  sreverName;
	}
	
	public static void wait(int ms)
	{
	    try
	    {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException ex)
	    {
	        Thread.currentThread().interrupt();
	    }
	}
	
	public static int getRandomNumberUsingNextInt(int min, int max) 
	{
	    Random random = new Random();
	    return random.nextInt(max - min) + min;
	}
	
	public static LinkedList<String> createListOfNodesToVisit(int numberOfNodes) throws Exception
	{
		LinkedList<String> ListOfNodesToVisit = new LinkedList<String>();
		meetNodes();
		LinkedList<String> ListOfNotVisitedNodes = ListOfNamesOfWorkingKnots;
		
		for (int i=0; i <numberOfNodes; i++)
		{
			int n = getRandomNumberUsingNextInt(0, ListOfNotVisitedNodes.size()-1);
			ListOfNodesToVisit.add(ListOfNotVisitedNodes.get(n));
			ListOfNotVisitedNodes.remove(n);
			if (ListOfNotVisitedNodes.size() == 0)	//zabezpieczenie
			{
				break;
			}
		}
		return ListOfNodesToVisit;
	}
	
	public static boolean sendAnythingToSpecyficNode(String message, String nodeID) throws Exception 
	{
		InetAddress serverAddress = InetAddress.getByName(nodeID);

        DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
        byte[] stringContents = message.getBytes("utf8"); //Pobranie strumienia bajtów z wiadomosci

        DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
        sentPacket.setAddress(serverAddress);
        sentPacket.setPort(Config.PORT);
        socket.send(sentPacket);
        
        //Odbiera potwierdzenie otrzymania wiadomosci z serwera
        DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
        socket.setSoTimeout(1010);

        boolean isMessageDelivered = false;
        try{
            socket.receive(recievePacket);
            isMessageDelivered = true;
        }catch (SocketTimeoutException ste){
            isMessageDelivered = false;
        }
        socket.close();
        return isMessageDelivered;
	}
	
	
	public static String createUserMessageToSend()
	{
        System.out.println("Wprowadz wiadomosc do przeslania:");
        String message = getInputFromConsole();
        
        return message;
	}
	
	public static String generatePathToSendToFirstNode(LinkedList<String> ListOfNodes, String serverName)
	{
		String message = "";
		for (int i=1; i<ListOfNodes.size(); i++)		//od int=1 bo jak chce wyslac przez A->B->C->D->Serwer to do A wystarczy wyslac B->C->D->Server
		{
			message = message + ListOfNodes.get(i) + ",";
		}
		message = message + serverName;		//Wymyslilem sobie ze wezly wiadmomosci sa oddzielone przecinkami, a po nazwie serwera juz nic nie ma
		
		return message;
	}
	
	public static String generateMessageID()
	{
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    String generatedString = buffer.toString();
	    
	    return generatedString;
	}
	
	public static Boolean sendMessageId(LinkedList<String> ListOfNodes, String serverName, String messageID) throws Exception
	{
		boolean indicator = false;
		
		ListOfNodes.add(serverName);
	    for(int i=0; i<3; i++)
	    {
	    	if (sendAnythingToSpecyficNode(messageID, ListOfNodes.getFirst()))
	    	{
	    		indicator = true;
	    		break;
	    	}
	    	wait(50);
	    }
	   return indicator;
	}
	
	
	public boolean sendMessage(int numberOfNodes, String serverName, String userMessage) throws Exception
	{
		
		LinkedList<String> ListOfNodes = createListOfNodesToVisit(numberOfNodes);
		
		String generatedMessageId = generateMessageID();
		sendMessageId(ListOfNodes, serverName, generatedMessageId);
		ListOfIdMessages.add(generatedMessageId);
		wait(50);
		
		String pathToSend = generatePathToSendToFirstNode(ListOfNodes, serverName);
		sendAnythingToSpecyficNode(pathToSend, ListOfNodes.getFirst());
		ListOfAskedServers.add(serverName);
		wait(50);
		
		sendAnythingToSpecyficNode(userMessage, ListOfNodes.getFirst());
		wait(50);
		
		return true;
	}
	
	
	
	
	
    public static void main(String[] args) throws Exception 
    {
    	int numberOfNodes = getNumberOfNodesFromUser();
    	String message = createUserMessageToSend();
    	String serverName = getServerName();
    	
    	UDPClient Client1 =  new UDPClient();
    	Client1.sendMessage(numberOfNodes, serverName, message);
    	System.out.println("x");
    	
        
    }
}