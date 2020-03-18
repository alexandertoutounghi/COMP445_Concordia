package ca.concordia.Network.HTTP;

import ca.concordia.Network.CheckSyntax.UtilityClass;
import ca.concordia.Network.IO.FileIO;
import ca.concordia.Network.InvalidHttpRequest;
import ca.concordia.Network.IO.NetworkIO;
import ca.concordia.Network.CheckSyntax.ParseString;

public abstract class Http extends UtilityClass {
    //   if write to file
    //write to a File for output or read from file for POST
    protected FileIO writeReadFile;
    protected NetworkIO network;
    protected boolean verboseFlag = false;
    protected boolean outputToFile = false;
    protected String query;
    protected ParseString ps;
    protected String[] headers;


    protected final String HTTPVERSION = "HTTP/1.0";
    protected final String ENDLINE = "\r\n";
    protected final String ENDREQUEST = "\r\n\r\n";
    protected final char SPACE = ' ';


    protected String filePath;


    public Http(ParseString request) {
        ps = new ParseString(request);
        network = new NetworkIO(ps.getURL(), ps.getPort());
    }

    /**
     * @throws InvalidHttpRequest
     */
    void setFlags() throws InvalidHttpRequest {
        verboseFlag = ps.hasVerbose();
        query = ps.getDirectory();
        outputToFile = ps.hasFileOutput();
        if (outputToFile)
            writeReadFile = new FileIO(ps.getFileNameOutput());
        headers = ps.getHeaders();
    }

    abstract void sendRequest() throws InvalidHttpRequest;

    protected StringBuilder generateRequest(String requestMethod) {
        StringBuilder s = new StringBuilder(requestMethod + SPACE + query + SPACE + HTTPVERSION + ENDLINE);
        if (headers.length != 0) {
            for (String h : headers) {
                s.append(h);
                s.append(ENDLINE);
            }
        }
//        s.append(ENDLINE);
        return s;
    }

    protected void generateOutput(StringBuilder s) throws InvalidHttpRequest {
        if (verboseFlag)
            System.out.println(ANSI_YELLOW + "Sending Request to server..." + ANSI_RESET);
        String response = network.sendRequest(s.toString());
        if (outputToFile)
            writeReadFile.writeToFile(response);
        if (verboseFlag)
            System.out.println(ANSI_YELLOW + "Request Sent! Awaiting Reply...\n" + ANSI_RESET + ANSI_BLUE +
                    "Response Received! Message:\n" + ANSI_RESET + ANSI_CYAN + response + ANSI_RESET);

    }

/*    protected void generateOutput(StringBuilder s, String fileData) throws InvalidHttpRequest {
        if (verboseFlag)
            System.out.println("Sending Request to server...");
        String response = network.sendRequest(s.toString());
        if (outputToFile)
            writeReadFile.writeToFile(response);
        if (verboseFlag)
            System.out.println("Request Sent! Awaiting Reply...\nResponse Received! Message:\n" + response);
    }*/


}
