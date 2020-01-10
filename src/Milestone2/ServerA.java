//server
package Milestone2;

import java.io.*; 
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*; 
  



public class ServerA  
{ 
    public static ArrayList clients=new ArrayList(); 
    public static ArrayList clientsB=new ArrayList();
    
  
    //checks whether new client name already exists in the clients array
    public static boolean exists(String x)
    {
    	for(int i=0;i<clients.size();i++)
    	{
    		if(((ClientHandler)(clients.get(i))).name.equals(x))
    			return true;
    	}
    	for(int i=0;i<clientsB.size();i++)
    	{
    		if(x.equals((ServerA.clientsB.get(i)).toString()))
    			{return true;}
    	}
    	return false;
    }
    
    

    
    public static void main(String[] args) throws IOException  
    { 
    	System.out.println("Server A Listening......");
        ExecutorService pool = Executors.newCachedThreadPool();
        Socket s; 
        try (ServerSocket listener = new ServerSocket(6001)) {
        
        while (true)  
        { 
        	
            s = listener.accept();      
            BufferedReader inFromClient=new BufferedReader(new InputStreamReader(s.getInputStream()));
			String name = inFromClient.readLine();
			
			while(exists(name))
			{  
            String MsgToSend="0";
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
          	out.println(MsgToSend);
          	System.out.println(MsgToSend);
          	name = inFromClient.readLine();
          	}
			if(!name.equals("#ServerB#"))
			{String MsgToSend="1";
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
	        out.println(MsgToSend);
	        System.out.println(MsgToSend);}
			
            ClientHandler mtch = new ClientHandler(s,name);
            System.out.println("New client : " +name+"   "+s);  
            for (int j=0;j<ServerA.clients.size();j++)  
            {   ClientHandler mc=(ClientHandler) ServerA.clients.get(j);
                if (mc.name.equals("#ServerB#"))  
                { 
                	PrintWriter out = new PrintWriter(mc.s.getOutputStream(), true);
                  	out.println("add-"+name+"\n");
                	break;
                } 
            } 
            clients.add(mtch); 
            pool.execute(mtch);
            
        }
        }
    }
} 
  
class ClientHandler implements Runnable  
{ 
    Socket s; 
    String name;
      
    public ClientHandler(Socket s,String name) { 
    	
       
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
    	boolean found=false;
    	System.out.println("x= "+x);
    	for(int i=0;i<ServerA.clients.size();i++)
    	{
    		if(x.equals(((ClientHandler)(ServerA.clients.get(i))).name))
    			{found=true;
    			break;}
    	}
    	for(int i=0;i<ServerA.clientsB.size();i++)
    	{
    		if(x.equals(((ClientHandler)(ServerA.clients.get(i))).toString()))
    			{found=true;
    			break;}
    	}
    	//System.out.println(ServerA.clients);
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
		              {ServerA.clients.remove(this);
		              System.out.print("Updated ArrayList= ");
		              for(int x=0;x<ServerA.clients.size();x++)
	                	{
	                		System.out.print(((ClientHandler)(ServerA.clients.get(x))).name+" ");
	                	}
		              System.out.println();
		              }
				if(received.equals("getMemberList")) {
                	
                	String MsgToSend="";
                	for(int x=0;x<ServerA.clients.size();x++)
                	{
                		if(!((ClientHandler)(ServerA.clients.get(x))).name.equals("#ServerB#"))
                		MsgToSend=MsgToSend+((ClientHandler)(ServerA.clients.get(x))).name+"  " ;
                	}
                	for(int x=0;x<ServerA.clientsB.size();x++)
                	{
                		MsgToSend=MsgToSend+ServerA.clientsB.get(x).toString() ;
                	}
                	PrintWriter out = new PrintWriter(s.getOutputStream(), true);
               		out.println(MsgToSend);

                	
                	               	
                  }
                   if(received.contains("add-")) {
                   	
                   	String name="";
                   	name=received.substring(4);
                   	ServerA.clientsB.add(name);
                   	System.out.println(ServerA.clientsB.toString());
                  	                	                   	               	
                     }
				
				else {
				//System.out.println(received);
				int i=received.length();
                String clientName=this.getClientName(received);
                int ttl=this.getTTL(received);
                String src=this.getSource(received);
                String MsgToSend = src+" : "+this.getMessage(received); 
                System.out.print(MsgToSend);
                
                if(ttl>0)
                {
                System.out.println("ttl"+ttl);
                if(!received.equals("getMemberList")&&!this.checkIfOnline(clientName) && !MsgToSend.equals("Couldn't send message")) {
                	clientName=this.name;
                	System.out.println("MsgToSend= "+MsgToSend);
                	MsgToSend="Couldn't send message - client is offline";
               	
                }
                
                
                
                if(MsgToSend.equals("Couldn't send message - client is offline"))
                {
                	ttl=ttl-1;
                	MsgToSend="*"+getSource(received)+"*"+ttl+"*"+getClientName(received)+"-"+this.getMessage(received);
                	for (int j=0;j<ServerA.clients.size();j++)  
                    {   ClientHandler mc=(ClientHandler) ServerA.clients.get(j);
                        if (mc.name.equals("#ServerB#"))  
                        { 
                        	PrintWriter out = new PrintWriter(mc.s.getOutputStream(), true);
                          	out.println(MsgToSend);
                        	break;
                        } 
                    } 
                }
                else
                for (int j=0;j<ServerA.clients.size();j++)  
                    {   ClientHandler mc=(ClientHandler) ServerA.clients.get(j);
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
                	if(!this.checkIfOnline(clientName) && !MsgToSend.equals("Couldn't send message")) {
                    	clientName=this.getClientName(received);
                    	System.out.println("MsgToSend= "+MsgToSend);
                    	MsgToSend="Couldn't send message - client is offline";
                   	
                    }
                	for (int j=0;j<ServerA.clients.size();j++)  
                    {   ClientHandler mc=(ClientHandler) ServerA.clients.get(j);
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

