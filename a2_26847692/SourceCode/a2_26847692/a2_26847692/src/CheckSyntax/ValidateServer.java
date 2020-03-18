package CheckSyntax;

import IO.FileIO;
import Server.Exceptions.ServerException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ValidateServer {
    private static final String PORT = "(102[4-9]|10[3-9][0-9]|1[1-9][0-9][0-9]|[2-9][0-9][0-9][0-5]|[1-5][0-9][0-9][0-9][0-9]|" +
            "6553[0-5]|655[0-2][0-9]|65[0-4][0-9][0-9]|6[0-4][0-9][0-9][0-9]){1}";
    private static final String SPACE_REGEX = "\\s";
    private static final String HELP = "help.*";
    private static final String STRING_VALUE = "('[^']*'|\"[^\"]*\"|[^\\s]*)";
    private static final String VERBOSE_FLAG_REGEX = "(\\s?(-v))?";
    private static final String PORT_REGEX = "(\\s?(-p)\\s("+PORT+"))?";
    private static final String PATH_TO_DIR_REGEX="(\\s?(-d)\\s("+STRING_VALUE+"))?";
    private static final String REGEX_1 = VERBOSE_FLAG_REGEX+PORT_REGEX+PATH_TO_DIR_REGEX;
    private static final String REGEX_2 = VERBOSE_FLAG_REGEX+PATH_TO_DIR_REGEX +PORT_REGEX;
    private static final String REGEX_3 = PATH_TO_DIR_REGEX+ VERBOSE_FLAG_REGEX +PORT_REGEX;
    private static final String REGEX_4 = PATH_TO_DIR_REGEX+PORT_REGEX+VERBOSE_FLAG_REGEX;
    private static final String REGEX_5 = PORT_REGEX+PATH_TO_DIR_REGEX+VERBOSE_FLAG_REGEX;
    private static final String REGEX_6 = PORT_REGEX + VERBOSE_FLAG_REGEX+PATH_TO_DIR_REGEX;
    private static final String OVERALL_REGEX = "("+REGEX_1 +"|"+ REGEX_2 +"|"+REGEX_3+"|"+REGEX_4+REGEX_5+REGEX_6+")";
    
    private String commandLine;
    private String directory;
    private int port;
    private static int DEFAULT_PORT=8080;
    private static final String DEFAULT_PATH = "src/ServerFiles";
//    private static final Path PATH = Paths.get(DEFAULT_PATH);
//    Path path = new Paths.get("")

    public ValidateServer(String[] args) throws ServerException{
        ConvertToString(args);
        CheckIfValid();
    }

    void ConvertToString(String[] args) {
        StringBuffer sb = new StringBuffer();
        for (String val : args)
            sb.append(val).append(" ");
        this.commandLine = sb.toString().trim();
    }

    void CheckIfValid() throws ServerException {
        if (commandLine.matches(HELP))
            throw new ServerException();
        else if (commandLine.matches(OVERALL_REGEX)) {
//            setVariables();
//            if (!isDirectoryValid())
//                throw new ServerException("Invalid Directory, Only directories allowed are in ServerFiles Folder");
        }
        else
            throw new ServerException("Invalid Command Line Operation");

    }
    public boolean hasVerbose() {
        return this.commandLine.contains("-v");
    }

    public boolean hasDirectory() {
        return this.commandLine.contains("-d ");
    }
    public String setDirectory() {
        String s = this.commandLine.replaceAll(".*\\s?-d\\s","");
        s = s.replaceAll("\\s?-p\\s" + PORT,"");
        s = s.replaceAll("-v","").trim();
        return s;
    }



    /**
     * Security to make sure that only files within the 'ServerFiles' Directtory are allowed to be accessed
     * @return true or false to determine if the directory is valid
     */
    public boolean isDirectoryValid() {
        Path defaultPath = Paths.get(DEFAULT_PATH);
        Path requested = Paths.get(setDirectory());
        System.out.println(requested.toAbsolutePath());
        System.out.println(defaultPath.getParent());
        return requested.toAbsolutePath().startsWith(defaultPath.toAbsolutePath());
    }
    public int setPort() throws ServerException {
            String s = this.commandLine.replaceAll("[^\\d]", "");
            if (s.isEmpty())
                throw new ServerException("No Port Provided!");
            return Integer.parseInt(s);
    }

    public boolean hasPort() {return commandLine.contains("-p ");}

    /**
     * Checks if the file actually exists inside the directory
     *
     * @return true or false if the file exists
     */
    public boolean fileExists() {
        return FileIO.fileExists(setDirectory());
    }

}
