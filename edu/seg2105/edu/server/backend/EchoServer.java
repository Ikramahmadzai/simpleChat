package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	  
	  if (!(msg instanceof String)) return;

	  String line = (String) msg;
	  String login = (String) client.getInfo("loginId");

	  try {
	    if (line.startsWith("#login")) {
	      if (login != null) {
	        client.sendToClient("ERROR - Already logged in.");
	        client.close();
	        return;
	      }
	      String[] parts = line.split("\\s+", 2);
	      if (parts.length < 2 || parts[1].trim().isEmpty()) {
	        client.sendToClient("ERROR - Login id missing.");
	        client.close();
	        return;
	      }
	      String loginId = parts[1].trim();
	      System.out.println("Message received: #login " + loginId + " from null.");
	      client.setInfo("loginId", loginId);
	      System.out.println(loginId + " has logged on.");
	      sendToAllClients(loginId + " has logged on.");
	      return;
	    }

	    // any other message requires a prior login
	    if (login == null) {
	      client.sendToClient("ERROR - You must login first.");
	      client.close();
	      return;
	    }

	    System.out.println("Message received: " + line + " from " + login);
	    sendToAllClients(login + "> " + line);

	  } catch (Exception e) {
	    System.out.println("Error handling client message: " + e);
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A new client has connected to the server.");
  }
  
  synchronized protected void clientDisconnected (ConnectionToClient client) {
	  System.out.println("Client disconnected: " + client);
	  super.clientDisconnected(client);
  }
  
  public void handleServerCommand(String cmnd) {
	  try {
		  if (cmnd.equals("#quit")) {
			  close();
			  System.out.println("Server quiting.");
			  System.exit(0);
			  
		  } else if (cmnd.equals("#stop")) {
			  stopListening();
		      System.out.println("Stopped listening for new clients.");
		      
		  } else if (cmnd.equals("#close")) {
			  close();
			  System.out.println("Closed server and disconnected clients.");
			  
		  } else if (cmnd.startsWith("#setport ")) {
		      if (isListening() || getNumberOfClients() > 0) { System.out.println("Error: server must be closed."); return; }
		      int p = Integer.parseInt(cmnd.substring(9).trim());
		      setPort(p);
		      System.out.println("Port set to " + getPort());
			  
		  } else if (cmnd.equals("#start")) {
			  if (isListening()) {System.out.println("Already Listening."); return; }
			  listen();
			  
		  } else if (cmnd.equals("#getport")) {
			  System.out.println(getPort());
			  
		  } else {
			  System.out.println("Unknown server command.");
			  
		  }
		  
	  } catch (Exception e) {
		  
		  System.out.println("Server command failed: " + e);
		  
	  }
	  
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
      
      new Thread(new ServerConsole(sv), "ServerConsole").start();
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
