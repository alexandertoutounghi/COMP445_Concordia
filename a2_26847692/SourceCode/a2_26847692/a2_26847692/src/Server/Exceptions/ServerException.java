package Server.Exceptions;

public class ServerException extends Exception {
    private String help = "httpfs is a simple file server.\n" +
            "usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]\n" +
            "-v Prints debugging messages.\n" +
            "-p Specifies the port number that the server will listen and serve at-- Range:[1024,65535].\n" +
            "Default is 8080.\n" +
            " -d Specifies the directory that the server will use to read/write\n" +
            "requested files. Default is the current directory when launching the\n" +
            "application.";


    public ServerException() {
        System.out.println(help);
    }

    public ServerException(String message) {
        super(message);
        System.err.println(message);
        System.out.println(help);
    }
}

