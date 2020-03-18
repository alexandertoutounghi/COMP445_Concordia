To execute predefined test cases for running the server call the './run' bash file. It will iterate between different calls to the  'httpfs' command to check for validity
Please note enact  command 'chmod u+x' for both './run' and './httpfs' before attempting otherwise you cannot execute
 
httpfs: Bash script to run the server, simply provide command line arguments below:

httpfs is a simple file server usage: httpfs [-v] [-p PORT] [-d PATH-TO-DIR]
-v Prints debugging messages.
-p Specifies the port number that the server will listen and serve at-- Range:[1024,65535], Default is 8080.\n" +
-d Specifies the directory that the server will use to read/write requested files. 
Default is the current directory when launching the application.";

Simply call ./httpfs [Commands] to start the server

run: runs predefined httpfs tests cases stored inside ServerTestCases.txt 

ServerTestCases: Please copy paste one of the commands from the list of 
