package Milestone2;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*; 
import java.net.*; 
import java.util.Scanner;

import javax.swing.JButton;

public class ClientA extends Frame implements ActionListener 
{ 
    static String name;
    //.........................................
    TextArea ta;
	PrintWriter pw;
	static  TextField tf;
	String text;
	String msg="";
	//.......................................
	
	public ClientA() throws UnknownHostException, IOException {
    	Socket s= new Socket("localhost",6001);
        DataOutputStream dos=new DataOutputStream(s.getOutputStream());
        BufferedReader dis=new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inFromServer =new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        //...................................
        Frame f=new Frame("Chatting");
        f.setLayout(new FlowLayout());
        Button b=new Button("Send");
 
        b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				text=tf.getText();
				try {
				if(text.startsWith("name:")) {

							name=text.substring(5);	
							dos.writeBytes(text.substring(5) + '\n');
							tf.setText("");
						
				
				}
				else if(text.equals("quit"))
                {   System.exit(0);
                	s.close();
                	dos.close();
                	dis.close();
                	inFromServer.close();
                	System.out.println("Logged out");
                	

                	
                }
				else if(text.equals("getMemberList")) {
                	dos.writeBytes(text + '\n');
                	tf.setText("");
                }
                
                else {
                	 String msg1="*"+name+"*"+2+"*"+text;
                	dos.writeBytes(msg1 + '\n');
                	tf.setText("");
                }
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				

							
			}

        	
        });
        
        tf=new TextField(29);
        ta=new TextArea(30,40);
        ta.setBackground(Color.LIGHT_GRAY);
        f.add(tf);//Add TextField to the frame       
        f.add(b);//Add send Button to the frame
        f.add(ta);
        f.setSize(500,500);//set the size        
        f.setVisible(true);      
        f.setLocation(100,300);//set the location      
        f.validate();
        f.addWindowListener(new w1());

        //...................................
        //prompt client to input their nameScanner sc=new Scanner(System.in); 
        ta.append("Enter your name to log in:");
    	
        
        //client tries to input a name for themselves 
        while(true)
        {	
        	 String reply=inFromServer.readLine();
        	 //if reply==0 then client must enter another name
        	 if(reply.equals("0"))
        	 {
        		 ta.append("\n"+"This name already exist! Enter another name:");
        	 }
        	//if reply==1 then client name got accepted
        	 else
        	 {  
        		 if(reply.equals("1"))
        		 {
        			    ta.append("________________________________________________"+"\n");
        		        ta.append("-Enter 'getMemberList' to see all members on this network"+"\n");
        		        ta.append("-Enter 'quit' to logout"+"\n");
        		        ta.append("-Send a message in this format: clientName-message"+"\n");
        		        ta.append("________________________________________________"+"\n");
        		        ta.repaint();
        		        break;}
        	 }
       
        }
        
     
        
       
        
        
        //sending thread
        Thread send = new Thread(new Runnable()  
        { 
        	 boolean loggedOut=false;
        	 
        	 
            public void run() { 
            	
                while (true && !loggedOut) { 
  
                    if(text.equals("quit"))
					{   
						this.loggedOut=true;
						
					} 
                } 
            } 
        }); 
        
        
        //receiving thread
        Thread recieve = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
  
                    try { 

                        String reply=inFromServer.readLine();
                        System.out.println(reply);
                        ta.append(reply+"\n");
                    } catch (IOException e) { 
                      break; 
                    } 
                } 
            } 
        }); 
        send.start(); 
        recieve.start(); 
        
  	
	}
    public static void main(String args[]) throws UnknownHostException, IOException  
    {  
    	ClientA a=new ClientA();

    }
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	} 

}

class w1 extends WindowAdapter  
{
	 
	public void windowClosing(WindowEvent we)
	
	  {
	
	   System.exit(0);
	
	  }
	 
	

}
