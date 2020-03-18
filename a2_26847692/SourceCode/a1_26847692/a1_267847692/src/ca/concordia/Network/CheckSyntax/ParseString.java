package ca.concordia.Network.CheckSyntax;

import ca.concordia.Network.InvalidHttpRequest;

public class ParseString {

    private String commandLine;
    private String URL;
    private String shortenedURL;

    private static final String HELP = "help";
    private static final String POST_HELP = "help post.*";
    private static final String GET_HELP = "help get.*";
    private static final String POST_REQUEST_REGEX = "(post){1}";
    private static final String GET_REQUEST_REGEX = "(get){1}";
    private static final String VERBOSE_FLAG_REGEX = "(\\s(-v))?";
    private static final String HEADER_FLAG_REGEX = "((\\s-h\\s)(([^:\\s\"']*:[^:\\s\"]*)|(\"[^:\"]*:[^:\"]*\")|('[^:']*:[^:']*')))*";
    private static final String HEADER_SEPARATOR = "(([^:\\s\"']*:[^:\\s\"]*)|(\"[^:\"]*:[^:\"]*\")|('[^:']*:[^:']*')))*";
    private static final String STRING_VALUE = "('[^']*'|\"[^\"]*\"|[^\\s]*)";
    private static final String STRING_VALUE_DATA = "('[^']*'|\"[^\"]*\"|.*)";
    //    private static final String STRING_VALUE = "('[^']*'|\"[^\"]*\")";
    private static final String POST_FILE_FLAG_REGEX = "(\\s-f\\s" + STRING_VALUE_DATA + "){1}";
    private static final String POST_INLINE_DATA_FLAG_REGEX = "(\\s-d\\s" + STRING_VALUE_DATA + "){1}";
    //    private static final String FILE_OR_INLINE_DATA = "((" + POST_FILE_FLAG_REGEX + "(?!" + POST_INLINE_DATA_FLAG_REGEX + "))|((" + POST_INLINE_DATA_FLAG_REGEX + ")(?!" + POST_FILE_FLAG_REGEX + "))){1}";
    private static final String FILE_OR_INLINE_DATA = "((" + POST_FILE_FLAG_REGEX + "(?!" + POST_INLINE_DATA_FLAG_REGEX + "))|((" + POST_INLINE_DATA_FLAG_REGEX + ")(?!" + POST_FILE_FLAG_REGEX + "))){1}";
    private static final String HTTP_REGEX = "(http|https)[^\\s\"']*";
    private static final String HTTP_REGEX_STRING = "(\\s((\"" + HTTP_REGEX + "\")" + "|('" + HTTP_REGEX + "')|(" + HTTP_REGEX + "))){1}";
    private static final String TO_FILE_FLAG_REGEX = "(\\s-o\\s" + STRING_VALUE + ")?";
    //    private static final String POST_OVERALL_REGEX = POST_REQUEST_REGEX + VERBOSE_FLAG_REGEX + HEADER_FLAG_REGEX + FILE_OR_INLINE_DATA + HTTP_REGEX_STRING + TO_FILE_FLAG_REGEX;
    private static final String POST_REGEX_1 = POST_REQUEST_REGEX + HEADER_FLAG_REGEX + FILE_OR_INLINE_DATA + HEADER_FLAG_REGEX + VERBOSE_FLAG_REGEX + HEADER_FLAG_REGEX + HTTP_REGEX_STRING + TO_FILE_FLAG_REGEX;
    private static final String POST_REGEX_2 = POST_REQUEST_REGEX + HEADER_FLAG_REGEX + VERBOSE_FLAG_REGEX + HEADER_FLAG_REGEX + FILE_OR_INLINE_DATA + HEADER_FLAG_REGEX + HTTP_REGEX_STRING + TO_FILE_FLAG_REGEX;
    private static final String POST_OVERALL_REGEX = "(" + POST_REGEX_1 + "|" + POST_REGEX_2 + ")";
    private static final String GET_OVERALL_REGEX = GET_REQUEST_REGEX + HEADER_FLAG_REGEX + VERBOSE_FLAG_REGEX + HEADER_FLAG_REGEX + HTTP_REGEX_STRING + TO_FILE_FLAG_REGEX;

    private static final String LOCALHOSTNAME = "localhost";
    private static final String LOCALHOSTIP = "127.0.0.1";
    private static final int DEFAULT_PORT = 80;

    public ParseString(ParseString p) {
        commandLine = p.commandLine;
        URL = p.URL;
        shortenedURL = p.shortenedURL;
    }

    public ParseString(String[] args) throws InvalidHttpRequest {
        ConvertToString(args);
        CheckIfValid();
        for (String s : args) {
            if (s.contains("http") || s.contains("https")) {
                this.URL = s;
                if (isLocalhost()) {
                    this.shortenedURL = s.replaceAll("(https|http)://", "");
                    this.shortenedURL = this.shortenedURL.replaceAll(":.*", "");
                    this.shortenedURL = this.shortenedURL.replaceAll("/.*", "");
                    this.shortenedURL = this.shortenedURL.replaceAll(LOCALHOSTIP, LOCALHOSTNAME);
                    break;
                } else {
//                this.shortenedURL =  s.replaceAll("(https|http)://","www.");
                    this.shortenedURL = s.replaceAll("(https|http)://", "www.");
                    this.shortenedURL = this.shortenedURL.replaceAll("/.*", "");
                    break;
                }
            }
        }
    }

    public boolean isLocalhost() {
        return (this.URL.matches("(https|http)://(" + LOCALHOSTNAME + "|" + LOCALHOSTIP + ").*"));
    }

    boolean hasPort() {
        return (this.URL.matches(".*:((\\d\\d\\d\\d)|(\\d\\d\\d\\d\\d)).*"));
    }

    public int getPort() {
        if (hasPort()) {
            String s = this.URL.replaceAll(".*:", "");
            s = s.replaceAll("/.*", "").trim();
            return Integer.parseInt(s);
        } else
            return DEFAULT_PORT;
    }

    void ConvertToString(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (String val : args)
            sb.append(val).append(" ");
        this.commandLine = sb.toString().trim();
    }

    void CheckIfValid() throws InvalidHttpRequest {
        if (commandLine.matches(POST_HELP))
            throw new InvalidHttpRequest("post");
        else if (commandLine.matches(GET_HELP))
            throw new InvalidHttpRequest("get");
        else if (commandLine.matches(POST_OVERALL_REGEX) || commandLine.matches(GET_OVERALL_REGEX))
            return;
        else
            throw new InvalidHttpRequest();
    }

    public boolean hasVerbose() {
        return this.commandLine.contains("-v");
    }

    public boolean hasInlineData() {
        return this.commandLine.contains(" -d ");
    }

    public String getInlineData() {
        if (!hasInlineData())
            return "";
        String inlineData = this.commandLine.replaceAll(".*-d", "");
        inlineData = inlineData.replaceAll("(http|https).*", "");
        inlineData = inlineData.replaceAll(HEADER_FLAG_REGEX, "");
        inlineData = inlineData.replaceAll(VERBOSE_FLAG_REGEX, "");
        inlineData = inlineData.trim();
        return inlineData;
    }

    public boolean hasFileData() {
        return this.commandLine.contains(" -f ");
    }

    /**
     * POST request; get the data from file to output
     *
     * @return Name of the file for output
     */
    public String getFileDataName() {
        if (!hasFileData())
            return "";
        String inlineData = this.commandLine.replaceAll(".*-f", "");
        inlineData = inlineData.replaceAll("(http|https).*", "");
        inlineData = inlineData.replaceAll(HEADER_FLAG_REGEX, "");
        inlineData = inlineData.replaceAll(VERBOSE_FLAG_REGEX, "");
        inlineData = inlineData.replaceAll("[\"|']", "");
        inlineData = inlineData.trim();
        return inlineData;
    }

    public boolean hasFileOutput() {
        return this.commandLine.contains(" -o ");
    }

    /**
     * POST/GET: Get the name of the file to redirect output
     *
     * @return
     */
    public String getFileNameOutput() {
        return this.commandLine.replaceAll(".*-o\\s", "");
    }

    public String[] getHeaders() {
        String headers = this.commandLine.replaceAll("(get|post)?", "");
        headers = headers.replaceAll("(-v\\s)?", "");
        headers = headers.replaceAll("((-f|-d)\\s)" + STRING_VALUE, "");
        headers = headers.replaceAll(HTTP_REGEX + ".*", "");
        headers = headers.replaceAll("-h", "").trim();
        headers = headers.replaceAll("\\s+(?=(?:[^\'\"]*[\'\"][^\'\"]*[\'\"])*[^\'\"]*$)", ",");
        headers = headers.replaceAll("[\"']", "");

        if (headers.isEmpty())
            return new String[]{};

        return headers.split(",");

    }

    public String getURL() {
        if (isLocalhost())
            return LOCALHOSTNAME;
        else
            return this.shortenedURL;
    }

    public String getDirectory() {
        String directory;
        if (isLocalhost()) {
            directory = this.URL.replaceAll(".*/(" + LOCALHOSTIP + "|" + LOCALHOSTNAME + "):(\\d{4}|\\d{5})", "");
            return directory;
        } else {
            directory = this.URL.replaceAll(".*(com|ca|org|edu|io|info)", "");
            directory = directory.replaceAll("\\s.*", "");
        }
        return directory;
    }

    public boolean isPost() {
        return this.commandLine.matches(POST_OVERALL_REGEX);
    }

    public boolean isGet() {
        return this.commandLine.matches(GET_OVERALL_REGEX);
    }

}
