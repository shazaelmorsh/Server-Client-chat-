package Milestone2;

import java.io.*; 
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*; 
  
public class ServerB 
{ 
    public static ArrayList clients=new ArrayList(); 
    public static ArrayList clientsA=new ArrayList();
    public static Socket serverB;
    //checks whether the new client name already exists 
    public static boolean exists(String x)
    {
    	for(int i=0;i<clients.size();i++)
    	{
    		if(((ClientHandlerB)(clients.get(i))).name.equals(x))
    			return true;
    	}
    	for(int i=0;i<clientsA.size();i++)
    	{
    		if((clientsA.get(i)).equals(x))
    			return true;
    	}
    	return false;
    }
    
    
    public static void main(String[] args) throws IOException  
    { 
    	System.out.println("Server B Listening......");
    	//creates an unlimited pool of threads
        ExecutorService pool = Executors.newCachedThreadPool();
        
        
        //Clients Socket
      
        Thread clientsThread = new Thread(new Runnable()  
        { 
        	public void run() {
        	try (ServerSocket listener = new ServerSocket(6002)) 
            {
            while (true)  
            { 
            	
                Socket s = listener.accept();      
                BufferedReader inFromClient=new BufferedReader(new InputStreamReader(s.getInputStream()));
    			String name = inFromClient.readLine();
    			
    			//waits for client to enter a unique name to be used as their identifier 
    			while(exists(name))
    			{
    			//sends 0 to the client to have him send another name
    			String MsgToSend="0";
    			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
              	out.println(MsgToSend);
              	System.out.println(MsgToSend);
              	name = inFromClient.readLine();}
    			
    			//sends 1 to the client to confirm that the name was accepted
    			String MsgToSend="1";
    			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
    	        out.println(MsgToSend);
    	        System.out.println(MsgToSend);
                ClientHandlerB mtch = new ClientHandlerB(s,name);
                System.out.println("New client : " +name+"   "+s);  
                for (int j=0;j<ServerB.clients.size();j++)  
                {   ClientHandlerB mc=(ClientHandlerB) ServerB.clients.get(j);
                    if (mc.name.equals("#ServerA#"))  
                    { 
                    	PrintWriter out1 = new PrintWriter(mc.s.getOutputStream(), true);
                      	out1.println("add-"+name+"\n");
                    	break;
                    } 
                }
                clients.add(mtch); 
                pool.execute(mtch);
                
            }
            } catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}}
        	
        }); 
        
        
      //Server Socket
        
        Thread serverThread = new Thread(new Runnable()  
        { 
        	public void run() {
            	try {
					Socket s= new Socket("localhost",6001);
					DataOutputStream dos=new DataOutputStream(s.getOutputStream());
			        BufferedReader dis=new BufferedReader(new InputStreamReader(System.in));
			        BufferedReader inFromServer =new BufferedReader(new InputStreamReader(s.getInputStream()));
			        
			        //send name to serverA
			        String name="#ServerB#";
			        dos.writeBytes(name + '\n');
			        ClientHandlerB serverA=new ClientHandlerB(s,"#ServerA#");
			        name="#ServerA#";
			        System.out.println("New client : " +name+"   "+s);  
			        ServerB.clients.add(serverA);
	                pool.execute(serverA);

			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

        	}
        	
        }); 
        
        serverThread.start();
        clientsThread.start();
    
        
        
        
   } 
       
    }
class ClientHandlerB implements Runnable  
{ 
    Socket s; 
    String name;
      
    public ClientHandlerB(Socket s,String name) { 
    	
       
        this.s = s; 
        this.name=name;
    } 
    public static String getClientName(String x)
    {
    	String name="";
    	int stars=0;
    	int i;
    	for(i=0;i<x.length();i++)
    	{
    		if(stars==3)
    			break;
    		if(x.charAt(i)=='*')
    		stars+=1;
    	}
    	for(int j=i;j<x.length();j++)
    	{
    		if(x.charAt(j)!='-')
        		name+=x.charAt(j);
        		else 
        		break;
    	}
    	return name;
    }
    public static String getSource(String x)
    {
    	String src="";
    	for(int i=1;i<x.length();i++)
    	{
    		if(x.charAt(i)!='*')
    		src+=x.charAt(i);
    		else 
    		break;
    	}
    	return src;
    }
    public static int getTTL(String x)
    {
    	String ttl="";
    	int stars=0;
    	int i;
    	for(i=0;i<x.length();i++)
    	{
    		if(stars==2)
    			break;
    		if(x.charAt(i)=='*')
    		stars+=1;
    	}
    	for(int j=i;j<x.length();j++)
    	{
    		if(x.charAt(j)!='*')
        		ttl=ttl+x.charAt(j);
        	else 
        		break;
    	}
    	return Integer.parseInt(ttl);
    }
    public static String getMessage(String x)
    {
    	String message="";
    	boolean found=false;
    	for(int i=0;i<x.length() && !found;i++)
    	{
    	  if(x.charAt(i)=='-')
    	  {
    		  for(int j=i+1;j<x.length();j++)
    		  {
    			  message+=x.charAt(j);
    		  }
    		 found=true;
    	  }
    	}
    	if(found)
    	return message;
    	else
    	return "";
    }
    public boolean checkIfOnline(String x)
    {   
    	System.out.println(x);
    	boolean found=false;
    	for(int i=0;i<ServerB.clients.size();i++)
    	{
    		if(x.equals(((ClientHandlerB)(ServerB.clients.get(i))).name))
    			{found=true;}
    	}
    	System.out.println(((ClientHandlerB)(ServerB.clients.get(0))).name);
    	return found;
    }
	@Override
    public void run() { 
  
        String received; 
        while (true)  
        { 
                
				try {

			   BufferedReader inFromClient=new BufferedReader(new InputStreamReader(s.getInputStream()));
				received = inFromClient.readLine();
				if(received.equals("quit"))
		              {ServerB.clients.remove(this);
		              System.out.print("Updated ArrayList= ");
		              for(int x=0;x<ServerB.clients.size();x++)
	                	{
	                		System.out.print(((ClientHandlerB)(ServerB.clients.get(x))).name+" ");
	                	}
		              System.out.println();
		              }
                   if(received.equals("getMemberList")) {
                	
                	String MsgToSend="";
                	for(int x=0;x<ServerB.clients.size();x++)
                	{
                		if(!((ClientHandlerB)(ServerB.clients.get(x))).name.equals("#ServerA#"))
                		MsgToSend=MsgToSend+((ClientHandlerB)(ServerB.clients.get(x))).name+"  " ;
                	}
                	for(int x=0;x<ServerB.clientsA.size();x++)
                	{
                		MsgToSend=MsgToSend+ServerB.clientsA.get(x).toString() ;
                	}
                	PrintWriter out = new PrintWriter(s.getOutputStream(), true);
               		out.println(MsgToSend);
               	                	
                	               	
                  }
                   if(received.contains("add-")) {
                   	
                   	String name="";
                   	name=received.substring(4);
                   	ServerB.clientsA.add(name);
                   	System.out.println(ServerB.clientsA.toString());
                  	                	                   	               	
                     }
                   
					
                	                           	
                
				else {
				System.out.println(received);
				int i=received.length(); 
                String clientName=this.getClientName(received);
                int ttl=this.getTTL(received);
                String src=this.getSource(received);
                String MsgToSend = src+" : "+this.getMessage(received);  
                
                if(ttl>0)
                {                System.out.println("ttl"+ttl);

                if(!this.checkIfOnline(clientName) && !MsgToSend.equals("Couldn't send message")) {
                	clientName=this.name;
                	MsgToSend="Couldn't send message - client is offline";
                }
                
                
                if(MsgToSend.equals("Couldn't send message - client is offline"))
                {ttl=ttl-1;
            	MsgToSend="*"+getSource(received)+"*"+ttl+"*"+getClientName(received)+"-"+this.getMessage(received);
                	for (int j=0;j<ServerB.clients.size();j++)  
                    {   ClientHandlerB mc=(ClientHandlerB) ServerB.clients.get(j);
                        if (mc.name.equals("#ServerA#"))  
                        { 
                        	PrintWriter out = new PrintWriter(mc.s.getOutputStream(), true);
                          	out.println(MsgToSend);
                        	break;
                        } 
                    } 
                }
                else
                for (int j=0;j<ServerB.clients.size();j++)  
                    {   ClientHandlerB mc=(ClientHandlerB) ServerB.clients.get(j);
                        if (mc.name.equals(clientName))  
                        { 
                        	PrintWriter out = new PrintWriter(mc.s.getOutputStream(), true);
                          	out.println(MsgToSend);
                        	break;
                        } 
                    } 
                }
         else {
	              System.out.println(ttl);

                	if(!received.equals("getMemberList")&&!this.checkIfOnline(clientName) && !MsgToSend.equals("Couldn't send message")) {
                    	clientName=this.getClientName(received);
                    	System.out.println("MsgToSend= "+MsgToSend);
                    	MsgToSend="Couldn't send message - client is offline";
                   	
                    }
                	for (int j=0;j<ServerB.clients.size();j++)  
                    {   ClientHandlerB mc=(ClientHandlerB) ServerB.clients.get(j);
                        if (mc.name.equals(src))  
                        { 
                        	PrintWriter out = new PrintWriter(mc.s.getOutputStream(), true);
                          	out.println(MsgToSend);
                        	break;
                        } 
                    } 
                	
                	
                }
				}}
				
                catch (Exception e) {
					// TODO Auto-generated catch block
					
				}
				
            
            
              
        } 
        
    } 
}


