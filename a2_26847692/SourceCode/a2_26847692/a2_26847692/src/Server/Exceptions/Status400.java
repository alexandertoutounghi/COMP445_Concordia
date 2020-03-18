package Server.Exceptions;

public class Status400 extends Exception {
    private String message = "Status 400 Bad Request";

    public Status400() {
        super();
    }

    @Override
    public String getMessage() {
        return message;
    }
}

