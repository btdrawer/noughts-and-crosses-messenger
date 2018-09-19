# noughtsandcrosses
A simple server-based Noughts and Crosses game.

Required technologies:
- Java
- JavaFX (for the client)
- MySQL (for the server)
## Launching the game
I have provided only the source code. It can be compiled by navigating to the root of the directory, and from a terminal writing:<br/>
javac *<br/><br/>
Then, to launch the server, type:<br/>
 cd server<br/>
 java Main<br/><br/>
 Or, to launch the client:
 cd client<br/>
 java Main
## Setting up the server
When launching server.Main, the command-line will allow you to input:
- The maximum number of clients the server can accommodate;
- The IP address and port number the server will listen on;
- The URL, username, and password of the MySQL database you would like to use.
