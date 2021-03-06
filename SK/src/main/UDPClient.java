package main;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;




public class UDPClient //implements Runnable
{
	public static LinkedList<String> ListOfIPofWorkingKnots = new LinkedList<String>();
	LinkedList<String> ListOfIdMessages;
	LinkedList<String> ListOfAskedServers;
	String clientIP;
	
	public UDPClient()
	{
		this.clientIP = Config.geterateIP();
		ListOfIdMessages =  new LinkedList<String>();
		ListOfAskedServers =  new LinkedList<String>();
	}

	
	public static void meetNodes( ) throws Exception
	{
       DatagramSocket s = new DatagramSocket();
       ListOfIPofWorkingKnots = new LinkedList<String>();
       
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
               String IPwezlaKtoryOdpowiedzial = response.getAddress().toString();
               //String nazwaWezlaKtoryOdpowiedzial = new String(response.getData(), 0,  response.getLength(), "utf8");
               ListOfIPofWorkingKnots.add(IPwezlaKtoryOdpowiedzial);
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
		System.out.println("Maksmalna ilosc dostepnych wezlow to: " + ListOfIPofWorkingKnots.size());
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
				System.out.println("Wprowadz poprawna liczbe calkowita z przedzialu od 0 do " + ListOfIPofWorkingKnots.size());
				typeInError = true;
			}
	        
	        if(!typeInError)
	        {
	        	if (numberOfNodesInBetween < 0 || numberOfNodesInBetween > ListOfIPofWorkingKnots.size() )
		        {
		        	System.out.println("Wprowadz poprawna liczbe calkowita z przedzialu od 0 do " + ListOfIPofWorkingKnots.size());
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
	
	
	
	public static LinkedList<String> createListOfNodesToVisit(int numberOfNodes) throws Exception
	{
		LinkedList<String> ListOfNodesToVisit = new LinkedList<String>();
		meetNodes();
		LinkedList<String> ListOfNotVisitedNodes = ListOfIPofWorkingKnots;
		
		for (int i=0; i <numberOfNodes; i++)
		{
			int n = Config.getRandomNumberUsingNextInt(0, ListOfNotVisitedNodes.size()-1);
			ListOfNodesToVisit.add(ListOfNotVisitedNodes.get(n));
			ListOfNotVisitedNodes.remove(n);
			if (ListOfNotVisitedNodes.size() == 0)	//zabezpieczenie
			{
				break;
			}
		}
		return ListOfNodesToVisit;
	}
	
	public static String sendAnythingToSpecyficNode(String message, String nodeID) throws Exception 
	{
		InetAddress serverAddress = InetAddress.getByName(nodeID);

        DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
        byte[] stringContents = message.getBytes("utf8"); //Pobranie strumienia bajt??w z wiadomosci

        DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length, serverAddress, Config.PORT);
        socket.send(sentPacket);
        
        //Odbiera potwierdzenie otrzymania wiadomosci z serwera
        DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
        socket.setSoTimeout(1010);

        String answer = null;
        try{
            socket.receive(recievePacket);
            answer = new String(recievePacket.getData());
        }catch (SocketTimeoutException ste){
        	answer = null;
        }
        socket.close();
        return answer;
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
	    	if (sendAnythingToSpecyficNode(messageID, ListOfNodes.getFirst()) != null)
	    	{
	    		indicator = true;
	    		break;
	    	}
	    	wait(50);
	    }
	   return indicator;
	}
	
	
	public String sendMessage(int numberOfNodes, String serverName, String userMessage) throws Exception
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
		
		String response = sendAnythingToSpecyficNode(userMessage, ListOfNodes.getFirst());
		wait(50);
		
		return response;
	}
	
	
	
	
	
    public static void main(String[] args) throws Exception 
    {
    	int numberOfNodes = getNumberOfNodesFromUser();
    	String message = createUserMessageToSend();
    	String serverName = getServerName();
    	
    	
    	UDPClient Client1 =  new UDPClient();
    	String response = Client1.sendMessage(numberOfNodes, serverName, message);
    	
    	System.out.println(response);
    }


    /*
	@SuppressWarnings("resource")
	@Override
	public void run()
	{
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		DatagramSocket datagramSocket;
		datagramSocket = new DatagramSocket(Config.PORT);
		
		
		//watek odbierajacy
		Runnable connection = new Runnable() 
		{	
			@Override
			public void run() 
			{
				while (true)
				{	//otwarcie gniazda z okreslonym portem
					try 
					{	//serwer w petli odbiera przychodzace pakiety
						while (true)
						{
							DatagramPacket recivedPacket = new DatagramPacket(new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
							datagramSocket.receive(recivedPacket);
							int length = recivedPacket.getLength();
							String message = new String(recivedPacket.getData(), 0, length, "utf8");
							
							System.out.println("Watek odbierajacy: " + message + " IP: " + recivedPacket.getAddress().toString() + " PORT: " + recivedPacket.getPort());
						}
					} catch (SocketException e) 
					{
						e.printStackTrace();
					}
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				
			}
		};
		executorService.submit(connection);
		
		
		//watek wysylajacy
		Runnable connection2 = new Runnable() 
		{
			@Override
			public void run() 
			{
				while (true)
				{
					try 
					{
						byte[] stringContents = ("Tekst wysylany na broadcast").getBytes("utf8");
						DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
						sentPacket.setAddress(Config.BCAST_ADDRESS);
						sentPacket.setPort(Config.PORT);
						
						datagramSocket.send(sentPacket);
						System.out.println("Watek wysylajacy: tekst wysylany na broadcast co 1s");
						Thread.sleep(1000);
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				
			}
		};
		executorService.submit(connection2);
		
	}
	*/
}
