package ca.concordia.Network;

import ca.concordia.Network.CheckSyntax.ParseString;
import ca.concordia.Network.CheckSyntax.UtilityClass;
import ca.concordia.Network.HTTP.Get;
import ca.concordia.Network.HTTP.Http;
import ca.concordia.Network.HTTP.Post;

public class httpc extends UtilityClass {

    public static void main(String[] args) throws InvalidHttpRequest {
        args = new String[]{"get","-h", "\"mycontent: length\"","-h", "\"User-Agent: Concordia\"", "-v","http://127.0.0.1:1026/HTMLFiles/Template.html"};
//        args = new String[] {"get","-h","hello:world","-h","hello:world","-h","hello:world","http://httpbin.org/get?course=networking&assignment=1","-o","hello.txt"};
//        args = new String[] {"get","-h","hello:world","-h","hello:world","-h","hello:world","http://localhost:8000/helloworld.txt"};
//        args = new String[]{"post", "-d", "helloworld", "-v", "-h", "hello:world", "-h", "hello:world", "-h", "hello:world", "http://localhost:8080/goob.txt"};
//        args = new String[] {"post","-v","-h","hello:world","-f","goodbye.txt","https://httpbin.org/post","-o","hello.txt"};
//        args = new String[] {"post","-v","-h","hello:world","-d","goodbye.txt","https://httpbin.org/post","-o","hello.txt"};
//        args = new String[]{"post","-v", "-h", "my:application","-d", "TestHTML.html", "-h" , "'application:\"json\"'","https://localhost:1026/newfile"};

        ParseString ps = new ParseString(args.clone());
        if (ps.hasVerbose())
            System.out.println(ANSI_GREEN + "Starting the Client Server...sending " + args[0].toUpperCase() + " Request to " + ps.getURL() + " port " + ps.getPort() + ANSI_RESET);
        if (ps.isGet()) {
            Http request = new Get(ps);
        } else if (ps.isPost()) {
            Http request = new Post(ps);
        }
        if (ps.hasVerbose())
            System.out.println(ANSI_GREEN + "Client Server Terminated." + ANSI_RESET);
    }

}
