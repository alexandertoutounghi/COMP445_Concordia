This is an implementation of a File Transfer Server;

CheckSyntax Directory: 
This directory is responsible for checking validity of command line arguments when 
invoking the 'httpfs' command, as well as the client request messages when communicating 
with the server

----ValidateClient: Responsible for parsing the client request messages for validity,
and extracting the important information to generate appropriate response by server

----ValidateServer: Responsible for Parsing through the commandline args when invoking
'httpfs' to check for valid ports,working directories,as well as set verbosity

IO Directory: 
This directory contains files responsible for IO for filesystems and as well for network
communication via sockets. 

-----FileIO: Responsible for reading/writing/creating files, checking if files exist
and are read/write accessible

-----NetworkIO: Responsible for receiving/sending requests/replies from and to the client
respectively

Server Directory: 
This directory is responsible for managing the main server functionality of server setup,
and communication. It utilizes the aforementioned classes to subdivide its functionality.
 However it acts as a main driver to simulate the actual server. 
 
---Exceptions Directory:Responsible for handling errors with regards to invalid commandline
input and client requests
    
    -----Server Exeception: Enacted when there is an overall error on the server side, usually
    involving incorrect commandline argument input when invoking 'httfps'
    
    -----Status 400: Called when the server receives client http request, but doesnt
    understand that request (invalid request)
    
    -----Status 403: Called when the client requests a file for reading/writing (POST/GET)
    that is not allowed due to set permissions.
    
    -----Status 404: Called when the client requests a file for read/write however cannot 
    be located/
ServerFiles Directory:
Directory where all the files are contained, for reading and writing 


httpfs:  The main method class for running this library.


 