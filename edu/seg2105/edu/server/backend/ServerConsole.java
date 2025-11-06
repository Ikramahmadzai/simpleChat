package edu.seg2105.edu.server.backend;

import edu.seg2105.client.common.ChatIF;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF, Runnable {
	
	private final EchoServer server;
	
	public ServerConsole(EchoServer server) { this.server = server; }
	
	public void display(String message) {
		System.out.println(message);
	}
	
	public void run() {
		try (BufferedReader br = new BufferedReader (new InputStreamReader(System.in))){
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					server.handleServerCommand(line);
				} else {
					String msg = "SERVER MSG> " + line;
					System.out.println(msg);
					server.sendToAllClients(msg);
				}
			}
		} catch (Exception e) {
			System.out.println("Server console error: " + e);
		}
	}
}
