package IO;

import Server.Exceptions.Status403;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileIO {
    public static boolean createdNewFile = false;
    private String path;

    public FileIO(String path) {
        this.path = path;
    }

    /**
     * Checks if the file/directory exists in the current server directory
     * @param path Provided Directory
     * @return True or False if the file/directory exists
     */
    public static boolean fileExists(String path) {
        Path urlPath = Paths.get(path);
        File file = new File(urlPath.toString());
        return file.exists();
    }

    /**
     * Checks if the file/directory is write allowed in the current server directory.
     * @param path Provided Directory
     * @return True or False if the file/directory is write accessible.
     */
    public static boolean isWriteable(String path) {
        Path urlPath = Paths.get(path);
        File file = new File(urlPath.toString());
        System.out.println(file.canWrite());
        return file.canWrite();
    }

    /**
     * Checks if the file/directory is readable in the current server directory
     * @param path Provided directory
     * @return True or False if the file/directory is read accessible
     */
    public static boolean isReadable(String path) {
        Path urlPath = Paths.get(path);
        File file = new File(urlPath.toString());
        return file.canRead();
    }


    /**
     * Utility method for printAllFiles method to generate tabs to
     * visually show files with directories
     * @param count a simply count to denote how many tabs to create
     * @return String containing the tabs
     */
    private static String generateTabs(int count) {
        StringBuilder s = new StringBuilder();
        while (count != 0) {

            s.append("\t");
            count--;
        }

        return s.toString();
    }

    /**
     *Method prints all the files/directories within the specified directory.
     * @param directories the parent directory for which the files/directories are within
     * @param count count value for the number of tabs to print
     * @return The string value for the printed directories
     */
    public static StringBuilder printAllFiles(File directories, int count) {
        try {
            //list all the files
            File[] files = directories.listFiles();
            StringBuilder s = new StringBuilder();
            //get the parent directory name
            s.append(directories.getName() + "\n");
            ++count;
            if (directories.listFiles().length == 0)
                s.append(generateTabs(count) + "Empty\n");
            for (File file : files) {
                if (file.isDirectory())
                    s.append(generateTabs(count) + printAllFiles(file, count));
                else
                    s.append(generateTabs(count) + file.getName() + "\n");
            }
            return s;
        } catch (Exception e) {
        }
        return new StringBuilder();
    }

    /**
     *Creates a file, if it does not exist, used for POST request to create a new file
     * @param path Provided diractory and filename to create file
     * @throws Status403 Cannot create a new file in the directory
     */
    public static void createFile(String path) throws Status403 {
        try {
            Path urlPath = Paths.get(path);
            File file = new File(urlPath.getFileName().toString());
            file.createNewFile();
            createdNewFile = true;
        } catch (Exception e) {
            throw new Status403();
        }

    }

    /**
     *Method to write a message to files in the ServerFiles Directory
     * Used for POST requests
     * @param message String to write to the file
     * @throws Status403 If we cannot open the file for writing
     */
    public void writeToFile(String message) throws Status403 {
        try {
            //throws invalid path exception
            Path path = Paths.get(this.path);
            BufferedWriter out = new BufferedWriter(new FileWriter(path.toString()));
            out.write(message);
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new Status403();
        }
    }

    /**
     * Method ot read the contents of the file in the ServerFiles Directory
     * Used for GET requests
     * @param fileName File in the directory to read from
     * @return Contents of the file
     * @throws Status403 If we cannot open the file for reading
     */
    public static String readFromFile(String fileName) throws Status403 {
        try {
            Path path = Paths.get(fileName);
            File file = new File(path.toString());
            StringBuilder data = new StringBuilder();
            data.append(Files.readString(path));
            return data.toString();
        } catch (Exception e) {
            throw new Status403();
        }
    }

}
