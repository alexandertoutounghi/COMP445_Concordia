package Server.Exceptions;

public class Status404 extends Exception {
    String msg = "Status 404: Not Found";
    public Status404() {
        super();
    }

    @Override
    public String getMessage() {
        return this.msg;
    }
}
