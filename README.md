# noughtsandcrosses
A simple server-based Noughts and Crosses game and 1:1 messaging client.

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
 Or, to launch the client:<br/>
 cd client<br/>
 java Main
## Setting up the server
When launching server.Main, the command-line will allow you to input:
- The maximum number of clients the server can accommodate;
- The IP address and port number the server will listen on;
- The URL, username, and password of the MySQL database you would like to use.
The terminal does not hide your database password; this is because the standard way of doing this in Java creates a bug in Eclipse which is the IDE I am currently using, and since I am only using a local database it is not a big deal for me. I will fix this in the future.
## Known bugs
- Leaderboard currently does not display.
- Users are able to log in to the same account from multiple clients simultaneously.
## Desired improvements
- Display notifications when a user receives a message;
- Colourise people's status;
- Display 'online/busy' status in the list of online users.
