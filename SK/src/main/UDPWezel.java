package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//11-06-2021 23:00
//tak jak na schemacie z rysunku, tylko, ze dziala dla 1 klienta na raz
//jak zdobyc adres i port wezla do ktorego przesylac dalej zgodnie z lista? (line 102)


public class UDPWezel {
	
	String IPadress;
	InetAddress powrotAddress; //adres wezla od ktorego odbieramy (dla wielu klientow to bedzie lista)
	int powrotPort; //port wezla od ktorego odbieramy (dla wielu klientow to bedzie lista)
	
	static int counter = 0; //licznik wywolanych wezlow
	
	public UDPWezel() {
		IPadress = Config.geterateIP();
		counter = counter + 1;
	}
	
    public static void main(String[] args) throws Exception{
    	
    	//konstruktor
    	UDPWezel wezel = new UDPWezel();
    	System.out.print("\nUtworzono wezel nr ");
    	System.out.print(counter);
    	System.out.println("O aresie IP: " + wezel.IPadress );
    	System.out.println();
    	
        //Otwarcie gniazda z okreslonym portem
        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

        byte[] byteResponse = "OK".getBytes("utf8");
        byte[] byteResponse2 = "OK".getBytes("utf8");
        
        while (true){

            DatagramPacket receivedPacket1 = new DatagramPacket(
            		new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
            datagramSocket.receive(receivedPacket1);
            int length = receivedPacket1.getLength();
            String message =
                    new String(receivedPacket1.getData(), 0, length, "utf8");
            InetAddress address = receivedPacket1.getAddress();
            int port = receivedPacket1.getPort();
            
            if(message == "Say your name!") //klient sie polaczyl i pyta o nazwe
            {
            	//podanie nazwy wezla do klienta
            	byteResponse = wezel.IPadress.getBytes("utf8");
                DatagramPacket response = new DatagramPacket(
                			byteResponse, byteResponse.length, address, port);
                Thread.sleep(1000);
                datagramSocket.send(response);
            }
            else
            {
            	
            	//-------------------------------klient->serwer--------------
            	//przesylaj pakiet i infoWezly do 1szego wezla z listy i usun go z listy
            	// (n-1) ---receivedPacket1---> wezel -----pakiet-----> (n+1)
                // (n-1) ---receivedPacket2---> wezel -----infoWezly-----> (n+1)
            	
            	
            	//-------------------------------serwer->klient--------------
            	//przesylaj pakiet do wezla o nazwie wezel.powrot
            	// (n+1) ---receivedPacket1---> wezel -----pakiet-----> (n-1)
            	
            	
            	
            	System.out.println("Wiadomosc:");
    		    System.out.println(message);
    		    
    		    byteResponse = message.getBytes("utf8");
    		    
    		    //nie wysylaj jeszcze wiadomosci, bo nie wiesz do kogo.
    		    //Odbierz 2ga wiadomosc, jesli to bedzie od tego samego adresu,
    		    //to z zawartej tam listy wydobadz adres i port nastepcy
    		    //a jak nie ma 2giej wiadomosci to wyslij do wezel.powrot
    		    
    		    DatagramPacket receivedPacket2 = new DatagramPacket(
            			new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
    		    
    		    datagramSocket.receive(receivedPacket2);
    		    
                if(receivedPacket1.getAddress() == receivedPacket2.getAddress()) //klient->serwer
                {
                	
                	//zapamietaj powrot
                	wezel.powrotAddress = receivedPacket2.getAddress();
                	wezel.powrotPort = receivedPacket2.getPort();

        		    int length2 = receivedPacket2.getLength();
        		    String message2 = new String(receivedPacket2.getData(), 0, length2, "utf8");
        		    
        		    System.out.println("Wezly:");
                    System.out.println(message2);
                    
                    String[] wezlyStr = message2.split(", ");
                    
                    //address i port wezla o nazwie wezlyStr[0]
                    //address = ?????????
                    //port = ?????????
                    
                    DatagramPacket pakiet = new DatagramPacket(
            				byteResponse, byteResponse.length, address, port);
                    
                    message = "";
                    for(int i=1; i<wezlyStr.length - 1; i++) //bez 1szego z listy (i=0)
                    	message = message + wezlyStr[i] + ", ";
                    //aby nie konczylo sie przecinkiem
                    message = message + wezlyStr[wezlyStr.length - 1]; 
                    
                    byteResponse2 = message.getBytes("utf8");
                    
                    DatagramPacket infoWezly = new DatagramPacket(
            				byteResponse2, byteResponse2.length, address, port);
                    
                    /*
                    //moze sie przyda
                    int[] wezly = new int[wezlyStr.length];
                    for(int i=0; i<wezlyStr.length; i++)
                    	wezly[i] = Integer.parseInt(wezlyStr[i]);  
                    */
                    
                    Thread.sleep(1000); 
        		    datagramSocket.send(pakiet);
        		    Thread.sleep(100); 
        		    datagramSocket.send(infoWezly);
                }
                else //serwer->klient
                {
                	DatagramPacket pakiet = new DatagramPacket(
            				byteResponse, byteResponse.length, wezel.powrotAddress, wezel.powrotPort);

                    Thread.sleep(1000); 
        		    datagramSocket.send(pakiet);
                
                }

            }
  
        } //while oczekiwania na polaczenie
    } //main
} //class