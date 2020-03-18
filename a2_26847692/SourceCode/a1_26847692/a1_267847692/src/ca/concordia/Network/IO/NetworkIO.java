package ca.concordia.Network.IO;

import ca.concordia.Network.InvalidHttpRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NetworkIO {
    private final int WEBPORT = 80;
    private InetAddress web;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private int port;
    //    private String request;
    private String URL;


    public NetworkIO(String URL,int port) {
        this.URL = URL;
        this.port = port;
    }

    public String sendRequest(String request) throws InvalidHttpRequest {
        try {
            this.web = InetAddress.getByName(this.URL);

            this.socket = new Socket(this.web, port);
            this.out = new PrintWriter(socket.getOutputStream());
            this.in = new Scanner(socket.getInputStream());
            out.write(request);
            out.flush();
            StringBuilder reply = new StringBuilder();
            while (in.hasNextLine())
                reply.append(in.nextLine()).append("\n");
            //close all the streams
            out.close();
            in.close();
            socket.close();
            return reply.toString();
        }
        catch (Exception e) {
            System.err.println("Error accessing/requesting to server " + this.URL);
            throw new InvalidHttpRequest();
        }
    }



}
