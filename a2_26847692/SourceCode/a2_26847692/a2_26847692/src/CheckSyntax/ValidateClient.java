package CheckSyntax;

import IO.FileIO;
import Server.Exceptions.Status400;
import Server.Exceptions.Status403;
import Server.Exceptions.Status404;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class ValidateClient {
    private String request;
    private String[] headers;
    private String directory;
    private int dataSize;
    private String data;
    private String rootDirectory;
    private String relativePathDir;
    private static final String DEFAULT_PATH = "src/ServerFiles";

    //Map to associate files with content types
    private static final Map<String, String> CONTENT_TYPE = new HashMap<>() {{
        put(".txt", "text/html");
        put(".html", "text/html");
        put(".json", "application/json");
        put(".xml", "text/xml");
        put(".xhtml", "text/xml");
    }};

    //Overall Regex to validate client request message
    private static final String SPACE_REGEX = "\\s";
    private static final String POST_REQUEST = "(POST){1}";
    private static final String GET_REQUEST = "(GET){1}";
    private static final String METHOD_REQUEST = "(GET|POST){1}";
    private static final String STRING_VALUE = "('[^']*'|\"[^\"]*\"|[^\\s]*)";
    private static final String DIRECTORY_REQUEST = "(/" + STRING_VALUE + ")+";
    private static final String HTTP_REQUEST = "HTTP/1.0";
    private static final String LINE_FEED = "\r\n";
    private static final String ENDSTATEMENT = LINE_FEED + LINE_FEED;
    private static final String END_OPTIONAL = "((" + LINE_FEED + LINE_FEED + ")|(" + LINE_FEED + "))";
//    private static final String HEADER_FLAG_REGEX = "((([^:\\s\"']*:[^:\\s\"]*)|(\"[^:\"]*:[^:\"]*\")|('[^:']*:[^:']*'))(" + LINE_FEED + "|" + ENDSTATEMENT + "))*";
    private static final String HEADER_FLAG_REGEX = "((([^:\"']*:[^:\"]*)|(\"[^:\"]*:[^:\"]*\")|('[^:']*:[^:']*'))(" + LINE_FEED + "|" + ENDSTATEMENT + "))*";
    private static final String CONTENT_LEN_REGEX = "(Content-Length:" + SPACE_REGEX + "(\\d+))";
    private static final char END_MESSAGE = '\u001a';
    private static final String BOUNDARY = "ENDREQUEST";
    private static final String POST_FILE = "Content-Type:" + SPACE_REGEX + "multipart/form-data;" + SPACE_REGEX + "boundary=" + BOUNDARY + ENDSTATEMENT;
    private static final String MULTIPART_FORM_DATA = "--" + BOUNDARY + "\nContent-Disposition:" + SPACE_REGEX + "form-data;" + SPACE_REGEX + "name=" + STRING_VALUE + SPACE_REGEX + "filename=" + STRING_VALUE + "\n\n" + "(.*\n)*"
            + "--" + BOUNDARY + "--";
    private static final String POST_FILE_REGEX = POST_REQUEST + SPACE_REGEX + DIRECTORY_REQUEST + SPACE_REGEX + HTTP_REQUEST + LINE_FEED + HEADER_FLAG_REGEX +
            CONTENT_LEN_REGEX + LINE_FEED + POST_FILE + MULTIPART_FORM_DATA;
    private static final String POST_DATA_REGEX = POST_REQUEST + SPACE_REGEX + DIRECTORY_REQUEST + SPACE_REGEX + HTTP_REQUEST + END_OPTIONAL + HEADER_FLAG_REGEX + CONTENT_LEN_REGEX + ENDSTATEMENT + ".*";
    private static final String POST_REGEX = POST_REQUEST + SPACE_REGEX + DIRECTORY_REQUEST + SPACE_REGEX + HTTP_REQUEST + END_OPTIONAL + HEADER_FLAG_REGEX;
    private static final String GET_FILE_REGEX = GET_REQUEST + SPACE_REGEX + DIRECTORY_REQUEST + SPACE_REGEX + HTTP_REQUEST + END_OPTIONAL + HEADER_FLAG_REGEX;


    /**
     * @param request       The request from the client
     * @param rootDirectory The working directory for the server.
     * @throws Status400 If the server doesnt understand the request because it is not valid
     * @throws Status403 If the server doesnt permit the request for read/write
     * @throws Status404 If the server requested file (GET request) doesnt exist
     */
    public ValidateClient(String request, String rootDirectory) throws Status400, Status403, Status404 {
        this.rootDirectory = rootDirectory;
        this.request = request;
        ValidateString();
    }


    /**
     * Checks if the the client request message is valid/accepted
     *
     * @throws Status400 If the server doesnt understand the request because it is not valid
     * @throws Status403 If the server doesnt permit the request for read/write
     * @throws Status404 If the server requested file (GET request) doesnt exist
     */
    public void ValidateString() throws Status400, Status403, Status404 {
        //if not valid, throw an error
        if (!isValidRequest())
            throw new Status400();
        //otherwise set all the values
        setValues();
        //check if the directory provided is allowed given our current working directory
        if (!isDirectoryValid())
            throw new Status403();

        //if the request is a post
        if (isPost()) {
            //if the file doesnt exist yet, create  one
            if (!fileExists()) {
                FileIO.createFile(getDirectory());
            }
            //if we are not allowed to write, throw error
            else if (!canWrite())
                throw new Status403();
        }
        //GET request
        else if (isGet()) {
            //if we are trying to get a file that doesnt exist, throw error
            if (!fileExists())
                throw new Status404();
            //if we cannot read the file throw error
            else if (!canRead())
                throw new Status403();
        }
    }

    /**
     * Checks if the file actually exists inside the directory
     *
     * @return true or false if the file exists
     */
    private boolean fileExists() {
        return FileIO.fileExists(this.directory);
    }

    /**
     * Checks if the file is writeable
     *
     * @return true or falses if the file is writeable
     */
    private boolean canWrite() {
        return FileIO.isWriteable((this.directory));
    }

    /**
     * Checks if the file is readable
     *
     * @return true or false if the file readable
     */
    private boolean canRead() {
        return FileIO.isReadable(this.directory);
    }

    /**
     * Checks if the reqest from client is a valid GET/PORT request according to the
     * HTTP 1.0 protocol
     *
     * @return true or false
     */
    private boolean isValidRequest() {
        return (this.request.matches(GET_FILE_REGEX) || this.request.matches(POST_FILE_REGEX)
                || this.request.matches(POST_DATA_REGEX) || this.request.matches(POST_REGEX));
    }

    /**
     * Checks if the the request method is POST
     *
     * @return true or false if method is POST
     */
    public boolean isPost() {
        return this.request.contains("POST");
    }

    /**
     * Checks if the request is a GET
     *
     * @return true or false if method is GET
     */
    public boolean isGet() {
        return this.request.contains("GET");
    }

    /**
     * Static method Checks if the client post request message is a file
     *
     * @param request Message from the client
     * @return True or False if the message is a post File
     */
    public static boolean isPostFile(String request) {
        return request.matches(POST_FILE_REGEX);
    }

    /**
     * Static method Checks if the client post request message is a inline data
     *
     * @param request Message from the client
     * @return True or False if the message is inline data
     */
    public static boolean isPostData(String request) {
        return request.matches(POST_DATA_REGEX);
    }

    /**
     * Checks if the client post request message is file
     *
     * @return True or False if the message is file
     */
    public boolean isPostFile() {
        return this.request.matches(POST_FILE_REGEX);
    }

    /**
     * True or False if the message is inline data
     *
     * @return True or False if the message is inline data
     */
    public boolean isPostData() {
        return this.request.matches(POST_DATA_REGEX);
    }

    /**
     * Checks if the post is a regular post request
     *
     * @return True or False if the request is normal
     */
    public boolean isPostRegular() {
        return this.request.matches(POST_REGEX);
    }

    /**
     * Set the directory values from the client request
     */
    private void setDirectory() {
        this.directory = this.request.replaceAll(METHOD_REQUEST + SPACE_REGEX, "");
        this.directory = this.directory.replaceAll(HTTP_REQUEST + "(.*\r*\n*)*", "").trim();
        this.relativePathDir = this.directory;
        this.directory = rootDirectory + this.directory;
//        if (isListDirectory())
//            this.directory += ".";
    }

    /**
     * Sets the values for the class; Headers, Directory, Data, Data Size,
     * File Extension Name
     */
    private void setValues() {
        //set the headers
        setHeaders();
        //set the directory
        setDirectory();
        //if posting a file/data set the size of data and value
        if (isPostFile() | isPostData()) {
            setDataSize();
            setData();
        }
    }

    /**
     * Get the file name from the client request
     *
     * @return
     */
    public String getFileName() {
        return this.relativePathDir.replaceAll(".*/", "");

    }

    /**
     * Get the content-type from the file client request
     *
     * @return conntent type of the file
     */
    public String getFileExtension() {
        Path path = Paths.get(relativePathDir);
        return CONTENT_TYPE.get(path.getFileName().toString().replaceAll(".*[.]", "."));
    }


    /**
     *Gets the file name of the POST request from client that sends data by reading from a file
     * @return
     */
    public String getPostedFileName() {
        String s = this.request.replaceAll("(.*\r*\n*)*filename=", "");
        s = s.replaceAll("(\n\n*.*)*", "");
        return s;
    }

    public String getPostedFileExtension() {
        String s = this.request.replaceAll("(.*\r*\n*)*filename=.*[.]", ".");
        s = s.replaceAll("(\n\n*.*)*", "");
        return CONTENT_TYPE.get(s);
    }

    /**
     * Extracts the headers from the request from the client
     */
    public void setHeaders() {
        String result = this.request.replaceAll((".*" + HTTP_REQUEST + LINE_FEED), "").trim();
        result = result.replaceAll(CONTENT_LEN_REGEX + "((.*\r*\n*)*|(.*\n*)*)", "");
        result = result.replaceAll("\r\n\r\n", "");
        headers = result.split(LINE_FEED);
    }

    /**
     * Static method for the class to extract the headers from the request from the client
     *
     * @param s String value for client request to extract headers
     * @return String array of header values
     */
    public static String[] getHeaders(String s) {
        String result = s.replaceAll((".*" + HTTP_REQUEST + LINE_FEED), "").trim();
        result = result.replaceAll("Content-Length(.*\r*\n*)*", "");
        return result.split(LINE_FEED);

    }

    /**
     * Static method to get the data size of the inline data/file from
     * the post request from the client
     *
     * @param request Post request from the client
     * @return Size of the data
     */
    public static int getData(String request) {
        String s = request.replaceAll("Content-Type(.*\r*\n*)*", "");
        s = s.replaceAll(ENDSTATEMENT + "(.*)*", "");
        s = s.replaceAll("(.*\\s*)*Content-Length:\\s", "");
        return Integer.parseInt(s);
    }

    /**
     * Method to get the the data size of the inline data/file from
     * the post request from the client and sets it to the request
     */
    public void setDataSize() {
        String s;
        if (isPostFile()) {
            s = this.request.replaceAll("Content-Type(.*\r*\n*)*", "");
//            s = s.replaceAll(ENDSTATEMENT + "(.*)*", "");
            s = s.replaceAll("(\r*\n*.*)*Content-Length:\\s", "");
            s = s.replaceAll("[^\\d](\r*\n*.*)*", "");
        } else {
            s = this.request.replaceAll("(\r*\n*.*)*Content-Length:\\s", "");
            s = s.replaceAll("[^\\d](\r*\n*.*)*", "");
        }
        this.dataSize = Integer.parseInt(s);
    }

    /**
     * Gets the data from the POST request for files or inlinde data
     */
    public void setData() {
        String s;
        if (isPostFile()) {
            s = this.request.replaceAll("(.*\r*\n*)*\\sfilename=.*\n\n", "");
            s = s.replaceAll("\n--" + BOUNDARY + "--", "");
            s = s.replaceAll("\\u001a", "");
        } else {
            s = this.request.replaceAll("(\r*\n*.*)*\r\n\r\n", "");
        }

        this.data = s;
    }

    /**
     * Gets the headers from the client post request
     *
     * @return headers from the client
     */
    public String[] getHeaders() {
        return headers.clone();
    }


    /**
     * Sets the directory from the client request message
     *
     * @param directory
     */
    public void setDirectory(String directory) {
        this.directory = this.request.replaceAll(METHOD_REQUEST, "");
        this.directory = this.directory.replaceAll(HTTP_REQUEST + ".*", "").trim();
    }

    /**
     * Checks if the directory requested by the client is valid inside the Serverfiles directory
     *
     * @return True or False if the directory is withing the ServerFiles
     */
    public boolean isDirectoryValid() {
        Path defaultPath = Paths.get(DEFAULT_PATH);
        Path requested = Paths.get(this.directory);

        return requested.toAbsolutePath().startsWith(defaultPath.toAbsolutePath());
    }


    /**
     * Static method to get the directory from the client request message
     *
     * @param request Client request message
     * @return Directorys
     */
    public static String getDirectory(String request) {
        String directory = request.replaceAll(METHOD_REQUEST + SPACE_REGEX, "");
        directory = directory.replaceAll(HTTP_REQUEST + "(.*\r\n)*", "").trim();
        directory = DEFAULT_PATH + directory;
        return directory;
    }

    /**
     * Returns the directory of the client request message
     *
     * @return the directory of the request message
     */
    public String getDirectory() {
        return this.directory;
    }

    /**
     * Checks if the client request message directory has no file
     * attached (i.e. ../ ), This ask the server to list the directory
     * contents and files
     *
     * @return Lists the Server directory contents
     */
    public boolean isListDirectory() {
        return relativePathDir.equals("/");
    }

    /**
     * Gets the data from the client POST  request
     *
     * @return The data
     */
    public String getData() {
        return data;
    }

}