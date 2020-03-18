package ca.concordia.Network.HTTP;

import ca.concordia.Network.IO.FileIO;
import ca.concordia.Network.InvalidHttpRequest;
import ca.concordia.Network.CheckSyntax.ParseString;

public class Post extends Http {
    private final String BOUNDARY = "ENDREQUEST";
    private final String POST_FILE = "Content-Type:" + SPACE + "multipart/form-data;" + SPACE + "boundary=" + BOUNDARY + ENDREQUEST;
    private final String MULTIPART_FORM_DATA = "--" + BOUNDARY + "\nContent-Disposition:" + SPACE + "form-data;" + SPACE + "name=";
    private final String CONTENT_LENGTH = "Content-Length:" + SPACE;
    private final String POST = "POST";

    private String inlineData;
    private boolean inlineDataFlag = false;
    private boolean toFileFlag = false;
    private String fileNamePost;


    public Post(ParseString request) throws InvalidHttpRequest {
        super(request);
        setFlags();
        sendRequest();
    }

    @Override
    void sendRequest() throws InvalidHttpRequest {
        StringBuilder s = generateRequest(POST);

        if (inlineDataFlag) {
            s.append(CONTENT_LENGTH);
            s.append(inlineData.length());
            s.append(ENDREQUEST);
            s.append(inlineData);
        } else if (toFileFlag) {
            String data = MULTIPART_FORM_DATA + fileNamePost + "; filename=" + fileNamePost + "\n\n" + FileIO.readFromFile(fileNamePost)
                    + "\n--" + BOUNDARY + "--";
            s.append(CONTENT_LENGTH);
            s.append(data.length());
            s.append(ENDLINE);
            s.append(POST_FILE);
            s.append(data);
        }
        if (ps.isLocalhost())
            s.append('\u001a');
        generateOutput(s);

    }

    @Override
    void setFlags() throws InvalidHttpRequest {
        super.setFlags();
        inlineDataFlag = ps.hasInlineData();
        inlineData = ps.getInlineData();
        toFileFlag = ps.hasFileData();
        fileNamePost = ps.getFileDataName();
    }
}

