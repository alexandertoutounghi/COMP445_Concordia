package ca.concordia.Network.IO;

import ca.concordia.Network.InvalidHttpRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class FileIO {
    protected String filePath;
    private String outputToFile = ".*-o\\s.*";
    private String verboseFlagRegex = ".*-v\\p{Blank}.*";

    public FileIO() {
        filePath = "";
    }

    public FileIO(String requestFile) {
//        setFilePath(requestFile);
        this.filePath = requestFile;
    }

    public void writeToFile(String message) throws InvalidHttpRequest {
        try {
            //if there is no output to a file
            if (filePath.isEmpty()) {
                return;
                //yes output to a file
            }
            else {
//                System.out.println(filePath);
                //throws invalid path exception
                Path path = Paths.get(this.filePath);
                //IOException is thrown
                BufferedWriter out = new BufferedWriter(new FileWriter(path.getFileName().toString()));
                out.write(message);
                out.flush();
                out.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error redirecting output to file");
            throw new InvalidHttpRequest();
        }
    }

    public static String readFromFile(String fileName) throws InvalidHttpRequest {
        try {
//            Path path = Paths.get("src/"+fileName);
            Path path = Paths.get(fileName);
            File file = new File(path.toString());
//            FileReader in =  new FileReader(path.getFileName().toString());
//            BufferedReader read= new BufferedReader(in);
//            String line = "";
            StringBuilder data = new StringBuilder();
            data.append(Files.readString(path));

            return data.toString().replaceAll("\r","");

//            while ((line =read.readLine()) != null)
//                data.append(line);
//            return data.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error! File " + fileName +" Not Found or File IO exception!");
            throw new InvalidHttpRequest();
        }
    }

    private void setFilePath(String request) {
        //if the request sends output to a file
        if (request.matches(outputToFile))
            this.filePath = request.replaceAll(".*-o\\s", "");
        else
            this.filePath = "";
    }
}
