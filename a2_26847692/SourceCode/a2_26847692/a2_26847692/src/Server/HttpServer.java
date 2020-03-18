package Server;

import CheckSyntax.ValidateClient;
import CheckSyntax.ValidateServer;
import IO.FileIO;
import IO.NetworkIO;
import Server.Exceptions.ServerException;
import Server.Exceptions.Status400;
import Server.Exceptions.Status403;
import Server.Exceptions.Status404;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";


    private final String SPACE = " ";
    private final String LINE_FEED = "\r\n";
    private final int DEFAULT_PORT = 8080;
    private final String DEFAULT_DIR = "src/ServerFiles";
    private final String HTTP_REQUEST = "HTTP/1.0";

    private final String CONTENT_LEN = "Content-Length:";
    private final String CONTENT_DISPOSITION_FILE = "Content-Disposition:attachment; filename=";
    private final String CHARSET = "; charset=utf-8";
    private final String CONTENT_DISPOSITION_INLINE = "Content-Disposition:Inline";
    private final String CONTENT_TYPE = "Content-Type:";
    private final String TEXT_HTML = "text/html";

    private final String STATUS_200 = "200 OK";
    private final String STATUS_201 = "201 Created";
    private ServerSocket server;
    private NetworkIO nio;
    private ValidateServer vs;
    private ValidateClient vc;
    private String workingDirectory;
    private boolean hasVerbose;


    /**
     * Constructor to instatiate the server, which is responsible for reply to requests from clients and
     * replying back with appropriate messages. Args optionally provide tbe port, verbosity, and working file directory
     *
     * @param args command line arguments from user input
     * @throws ServerException If
     */
    public HttpServer(String[] args) throws ServerException {
        vs = new ValidateServer(args);
        //set the server port, directory, and verbosity.
        setServer();
        if (hasVerbose)
            System.out.println(ANSI_GREEN + "Starting the FTP server...Listening at Port " + server.getLocalPort() + ANSI_RESET);
        //run the server
        run();
    }

    /**
     *Running the server over a constant loop
     */
    private void run() {
        while (true) {
            if (hasVerbose)
                System.out.println(ANSI_BLUE + "Waiting for a new connection..." + ANSI_RESET);
            receiveRequest();
        }

    }

    /**
     *Sets all the paramneter for the server such as Ports,Directories, and verbosity
     * @throws ServerException if any of the set values are incorrect.
     */
    private void setServer() throws ServerException {
        //set the working port
        setPorts();
        //set working directory
        setDirectory();
        //set verbosity.
        setHasVerbose();
    }


    /**
     * This method sets set working port for the server
     *
     * @throws ServerException
     */
    public void setPorts() throws ServerException {
        try {
            if (vs.hasPort())
                server = new ServerSocket(vs.setPort());
            else
                server = new ServerSocket(DEFAULT_PORT);
        } catch (IOException e) {
            throw new ServerException("Error: cannot open socket with given port");
        }

    }

    /**
     * This method sets the working file directory for the server based on what was passed when calling httpfs
     *
     * @throws ServerException If the provided directory doesnt exist or is outside the allowed permissble directory/
     */
    public void setDirectory() throws ServerException {
        if (vs.hasDirectory())
            if (vs.isDirectoryValid() && vs.fileExists())
                this.workingDirectory = vs.setDirectory();
            else
                throw new ServerException("Invalid Directory, Only directories allowed are in ServerFiles Folder");
        else
            this.workingDirectory = DEFAULT_DIR;
    }

    public void setHasVerbose() {
        this.hasVerbose = vs.hasVerbose();
    }

    /**
     * Method responsible for opening the socket to open the server to receive requests from clients,
     * Followed by a response by calling replyRequestmethod
     */
    private void receiveRequest() {
        try {
            Socket client = server.accept();
            if (hasVerbose)
                System.out.println(ANSI_YELLOW + "Client Connected! Receiving Transmission..." + ANSI_RESET);
            nio = new NetworkIO(client);
            String s = nio.toServer();

            if (hasVerbose) {
                System.out.println(ANSI_YELLOW + "Transmission Received!" + ANSI_RESET);
            }
            replyRequest(s);
        } catch (Exception e) {
            System.err.println();
        }


    }

    //this if for when an exception occcurs

    /**
     * Overloaded method for adding header for response message, typically used for when there is an error
     *
     * @param request client request message
     * @return headers
     */
    public String addHeaders(String request) {
        StringBuilder reply = new StringBuilder();
        String[] headers = ValidateClient.getHeaders(request);
        if (headers.length != 0) {
            for (String s : headers)
                reply.append(s + LINE_FEED);
            return reply.toString();
        } else
            return LINE_FEED;
    }

    /**
     * Method responsible for adding headers back to the response message
     *
     * @return headers
     */
    public String addHeaders() {
        StringBuilder reply = new StringBuilder();
        String[] headers = vc.getHeaders();

        if (headers.length != 0) {
            for (String s : vc.getHeaders())
                reply.append(s + LINE_FEED);
            return reply.toString();
        } else
            return null;
    }

    /**
     * Processess the request received from the client to send back
     *
     * @throws Status403 if the file cannot read/written
     */
    private String processRequest() throws Status403 {
        //instnatiate fileio for reading/writing
        FileIO fio = new FileIO(vc.getDirectory());
        StringBuilder reply = new StringBuilder();
        reply.append(HTTP_REQUEST + SPACE);
        //if the request is a post request
        if (vc.isPost()) {
            String data = vc.getData();
            //if the post request provides no file to post, then resent the message back to the client
            if (vc.isListDirectory()) {
                reply.append(STATUS_200 + LINE_FEED + addHeaders() + CONTENT_LEN + SPACE
                        + data.length() + LINE_FEED + CONTENT_TYPE + SPACE);
                //get the file name and data type if file post
                if (vc.isPostFile())
                    reply.append(vc.getPostedFileExtension() + CHARSET + LINE_FEED +
                            CONTENT_DISPOSITION_FILE + vc.getPostedFileName() + LINE_FEED + data);
                    //if inline data set that response
                else
                    reply.append(TEXT_HTML + CHARSET + LINE_FEED + CONTENT_DISPOSITION_INLINE + LINE_FEED + data);

                return reply.toString();
            }
            //if the post request from client provides a file to write to
            else {
                fio.writeToFile(data);
                //if the file didnt exist, and was created
                if (fio.createdNewFile) {
                    reply.append(STATUS_201 + LINE_FEED);
                    fio.createdNewFile = false;
                }
                //If the file already exists, so we overwrite it.
                else
                    reply.append(STATUS_200 + LINE_FEED);
            }

            reply.append(addHeaders() + LINE_FEED);
        }
        //GET request
        else {
            reply.append(STATUS_200 + LINE_FEED);
            //display contents of the workingDirectory.
            if (vc.isListDirectory())
                reply.append(addHeaders() + "Printed Directories:" + LINE_FEED + fio.printAllFiles(new File(vc.getDirectory()), 0));
                //get a file from the server
            else {
                String data = fio.readFromFile(vc.getDirectory());
                reply.append(addHeaders() + CONTENT_LEN + SPACE + data.length() + LINE_FEED + CONTENT_TYPE
                        + SPACE + vc.getFileExtension() + LINE_FEED + CONTENT_DISPOSITION_FILE + vc.getFileName() + LINE_FEED + data);
            }

        }
        return reply.toString();
    }

    /**
     * Utility method for when there are Status errors in replyRequest method
     * @param statusMessage type of error status code
     * @param request the request message from the client
     * @return resulting response message
     */
    private String processErrors(String statusMessage, String request) {
        String result = HTTP_REQUEST + SPACE + statusMessage + LINE_FEED + addHeaders(request) + LINE_FEED;
        return result;
    }

    /**
     * Method is responsible for replying to the client request, it invokes ValidateClient
     * to check the validity of the client request message anf if correct, will process the
     * response in processRequest method, then send a reply
     *
     * Otherwise if there are errors, it will reply back with error messages to client
     *
     * @param request The received client request messages
     */
    private void replyRequest(String request) {
        StringBuilder reply = new StringBuilder();  //reply message to client
        //if there are any errors it will be logged here and sent to client
        StringBuilder error = new StringBuilder();
        try {
            //process the request for validity and arguments
            vc = new ValidateClient(request, this.workingDirectory);
            //process the request to write/read to/from files respectively and create a response
            reply.append(processRequest());
            //send response back to the client
            nio.toClient(reply.toString());
        }
        //server doesnt understand the request because the message from client
        //does not fit the http message model
        catch (Status400 e) {
            error.append(processErrors(e.getMessage(), request));
            reply.append(nio.toClient(error.toString()));
            reply.append(error);
        }
        //the given URI for a file doesnt match the directories inside the server. Basically
        //cannot find the file
        catch (Status404 e) {
            error.append(processErrors(e.getMessage(), request));
            reply.append(nio.toClient(error.toString()));
            reply.append(error + "\n Could not find the requested url " + ValidateClient.getDirectory(request));
        }
        //Cannot read/write to/from file for request as its privilidges forbid reading/writing.
        catch (Status403 e) {
            error.append(processErrors(e.getMessage(), request));
            reply.append(nio.toClient(error.toString()));
            reply.append(error + "No Read/Write Access to " + ValidateClient.getDirectory(request));
        }
        //print debug message to screen of sent back reply if verbose flag activated.
        finally {
            if (hasVerbose) {
                System.out.println(ANSI_CYAN + "Message:\n" + reply.toString() + ANSI_RESET +
                        ANSI_BLUE + "\nEnd Connection!" + ANSI_RESET);
            }
        }

    }


}
