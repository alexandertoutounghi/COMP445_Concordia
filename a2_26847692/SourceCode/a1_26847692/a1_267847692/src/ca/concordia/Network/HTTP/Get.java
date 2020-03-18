package ca.concordia.Network.HTTP;

import ca.concordia.Network.IO.FileIO;
import ca.concordia.Network.InvalidHttpRequest;
import ca.concordia.Network.CheckSyntax.ParseString;

public class Get extends Http {
    protected final String GET = "GET";

    public Get(ParseString request) throws InvalidHttpRequest {
        super(request);
        setFlags();
        sendRequest();
    }


    public void sendRequest() throws InvalidHttpRequest {
        StringBuilder s =  generateRequest(GET);
        if (!s.toString().contains("\r\n\r\n"))
            s.append(ENDLINE);
        if (ps.isLocalhost())
            s.append('\u001a');
        generateOutput(s);
    }

    @Override
    public void setFlags() throws InvalidHttpRequest {
        super.setFlags();
        if (outputToFile)
            writeReadFile = new FileIO(ps.getFileNameOutput());
        else
            writeReadFile = new FileIO();
    }
}
