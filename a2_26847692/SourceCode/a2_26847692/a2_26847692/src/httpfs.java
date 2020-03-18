import Server.Exceptions.ServerException;
import Server.HttpServer;

public class httpfs {
    public static void main(String[] args) throws ServerException {
        args = new String[] {"-v","-d","src/ServerFiles/TextFiles"};
//        args = new String[] {"-v","-p","1234"};
//        args = new String[] {"help"};
        HttpServer server = new HttpServer(args);
    }
}
