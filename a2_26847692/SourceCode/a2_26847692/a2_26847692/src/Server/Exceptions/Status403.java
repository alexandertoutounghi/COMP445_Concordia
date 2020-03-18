package Server.Exceptions;

public class Status403 extends Exception {
    private String msg = "Status 403 Forbidden";
    public Status403() {
        super();
    }
    @Override
    public String getMessage(){
        return msg;
    }
}
