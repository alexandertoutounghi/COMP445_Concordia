Run the run bash file to run a batch of predefined test cases. Make sure
to 'chmod u+x' before doing so to ensure that it allows you to run it

There are two Jar files to 'httpfs.jar' and 'httpc.jar' run server and client respectively.
They are ran with bash scripts 'httpfs' and 'httpsc' to run server client. All server functionality
is inside the 'ServerDir' directory and all client functionality is inside the 'ClientDir' Directory.
For clarification, all client requests where ran with httpc from first assigment

In the Home Directory, there is a file called 'TestCases' that provides test cases for both the server and client users.
You can invoke the './Run' script to run the testing,  however to run it, you need to have 'gnome-terminal'
installed. Invoke "sudo apt-get install gnome-terminal'.There are two log files "ClientTestResult.txt", and "ServerTestResult.txt" that print out client and server logs respectively and can be see after
the ./Run is called it appends to these files. otherwises the terminal can alsos display the results. 

Otherwise you will need to open two seperate terminal windows and cd ./ServerDir/ and run './httpfs <command>""  for the file server
and in the second window run ./ClientDir and rattle off client requests to the server using './httpsc <command>'

There are a copy of mock tests 'TestCases_Manually' copy paste to create server and run client in sepeare windows, can be done
outside the "ClientDir"/"ServerDir" Home Dir.

All Source Code is inside the 'Source Code' directory, for this assignment and the previous ones