package ca.concordia.Network;

import ca.concordia.Network.CheckSyntax.UtilityClass;

public class InvalidHttpRequest extends Exception {
    private final String GENERALHELPMESSAGE =
            UtilityClass.ANSI_GREEN + "\nhttpc is a curl-like application but supports HTTP protocol only.\n" +
                    "Usage:\n" +
                    "   httpc command [arguments]\n" +
                    "The commands are:\n" +
                    "   get executes a HTTP GET request and prints the response.\n" +
                    "   post executes a HTTP POST request and prints the response.\n" +
                    "   help prints this screen.\n" +
                    "Use \"httpc help [command]\" for more information about a command." + UtilityClass.ANSI_RESET;
    private final String GETHELPMESSAGE = UtilityClass.ANSI_GREEN + "usage: httpc get [-v] [-h key:value] URL [-o filename]\n" +
            "Get executes a HTTP GET request for a given URL.\n" +
            "   -v              Prints the detail of the response such as protocol, status,\n" +
            "and headers.\n" +
            "   -h key:value    Associates headers to HTTP Request with the format\n" +
            "'key:value'.\n" +
            "   -o filename      Pipes output from host server into a file" + UtilityClass.ANSI_RESET;
    private final String POSTHELPMESSAGE = UtilityClass.ANSI_GREEN + "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL [-o filename]\n" +
            "Post executes a HTTP POST request for a given URL with inline data or from\n" +
            "file.\n" +
            "   -v              Prints the detail of the response such as protocol, status,\n" +
            "and headers.\n" +
            "   -h key:value    Associates headers to HTTP Request with the format\n" +
            "'key:value'.\n" +
            "   -d string       Associates an inline data to the body HTTP POST request.\n" +
            "   -f file         Associates the content of a file to the body HTTP POST\n" +
            "request.\n" +
            "   -o filename     Pipes output from host server into a file\n" +
            "Either [-d] or [-f] can be used but not both.\n" + UtilityClass.ANSI_RESET;

    public InvalidHttpRequest() {
        System.out.println(GENERALHELPMESSAGE);
    }


    public InvalidHttpRequest(String message) {
        super();
        if (message.equalsIgnoreCase("post"))
            System.err.println(POSTHELPMESSAGE);
        else
            System.err.println(GETHELPMESSAGE);
    }
}
