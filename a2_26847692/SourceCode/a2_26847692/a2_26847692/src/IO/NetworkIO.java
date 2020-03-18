package IO;


import java.io.*;
import java.net.Socket;

public class NetworkIO {
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    //    private String request;
    private String URL;

    /**
     * Constructor for the NetworkIO Class to communicate over a network
     * @param client Socket for which the server will communicate
     */
    public NetworkIO(Socket client) {
        this.client = client;
    }

    /**
     * Method receives input from clients and appends it to a string, the messages sent from
     * clients are termianted with a special character '\u001a' to denote the nd of the loop
     * @return String containing client request message
     * @throws IOException If theres an overall error with comms
     */
    public String toServer() throws IOException {
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        int check;
        StringBuilder request = new StringBuilder();
        String s="";
        //uses a special end character to denote end of reading
        while ((check=in.read()) != '\u001a') {
            request.append((char)check);
        }
        return request.toString();
    }

    /**
     *Returns response back to client and closes all the port for comms
     * @param returnMessage The response from the server
     * @return Message indicating if the message was sent or not
     */
    public String toClient(String returnMessage)  {
        try {
            out = new PrintWriter(client.getOutputStream());
            out.write(returnMessage);
            out.flush();
            out.close();
            in.close();
            return "Sent response to the client!";
        }
        catch (IOException e) {
            return "failed to send reply to client!";
        }

    }




}
