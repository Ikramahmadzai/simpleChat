package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginId, String host, int port) 
  {
    try 
    {
      client= new ChatClient(host, port, loginId, this);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  
  
  
  private void handleCommand(String line) {
	  try {
		  if (line.equals("#quit")) {
			  if (client.isConnected()) client.closeConnection();
			  System.out.println("Client exiting.");
			  System.exit(0);
			  
		  } else if (line.equals("#logoff")) {
			  if (client.isConnected()) client.closeConnection();
			  else System.out.println("Already logged off.");
			  
		  }else if (line.startsWith("#sethost ")) {
		      if (client.isConnected()) { System.out.println("Error: must be logged off."); return; }
		      client.setHost(line.substring(9).trim());
		      System.out.println("Host set to " + client.getHost());
		      
		  } else if (line.startsWith("#setport ")) {
			  if (client.isConnected()) { System.out.println("Error: must be logged off."); return; }
			  int p = Integer.parseInt(line.substring(9).trim());
			  client.setPort(p);
			  System.out.println("Port set to " + client.getPort());

		  } else if (line.equals("#login")) {
			  if (client.isConnected()) { System.out.println("Error: already connected."); return;}
			  client.openConnection();
			  System.out.println("Logged in to " + client.getHost() + ":" + client.getPort());
			  
		  }else if (line.equals("#gethost")) {
			  System.out.println(client.getHost());
			  
		  } else if (line.equals("#getport")) {
			  System.out.println(client.getPort());
			  
		  }else {
			  System.out.println("Unknown command.");
			  
		  }
	  } catch (Exception e) {
		  System.out.println("Command failed: " + e);
	  }
  }
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        if (message.startsWith("#")) {
        	handleCommand(message);
        } else {
        	client.handleMessageFromClientUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) {
	
	  
  	if  (args.length < 1) {
  		System.out.println("Usage: ClientConsole <loginId> [host] [port]");
  		System.exit(1);
  	}
  	
  	String loginId = args[0];
  	String host = (args.length >= 2) ? args[1] : "localhost";
  	
  	int port = DEFAULT_PORT;
  	
  	if (args.length >= 3) {
  		try {
  			port = Integer.parseInt(args[2]);
  		} catch (NumberFormatException e) {
  			System.out.println("Invalid port. Using default " + DEFAULT_PORT);
  			
  		}
  	}
    ClientConsole chat= new ClientConsole(loginId, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class

