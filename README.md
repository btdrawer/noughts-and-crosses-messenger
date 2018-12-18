# noughtsandcrosses
A simple server-based Noughts and Crosses game and 1:1 messaging client.

Required technologies:
- Java
- JavaFX (for the client)
- MySQL (for the server)
## Launching the game
Since I have not yet found a way to create a standalone, executable JavaFX application, I would recommend cloning this repository into an IDE of choice (I would suggest Eclipse since that is what I use).<br/><br/>
Once cloned, for the client, add the JavaFX library. There are instructions on how to do that [here](https://openjfx.io/openjfx-docs/) (note this is a modular application, so in the instructions for your IDE make sure to look at the instructions for Modular applications).<br/><br/>
For the server, meanwhile, add the MySQL JDBC driver Connector/J, which can be downloaded [here](https://dev.mysql.com/downloads/connector/j/). I personally use the platform-independent driver, which can be added to an IDE as a JAR.
## Setting up the server
When launching server.Main, the command-line will allow you to input:
- The maximum number of clients the server can accommodate;
- The IP address and port number the server will listen on;
- The URL, username, and password of the MySQL database you would like to use.
The terminal does not hide your database password; this is because the standard way of doing this in Java creates a bug in Eclipse which is the IDE I am currently using, and since I am only using a local database it is not a big deal for me. I will fix this in the future.
## Known bugs
- Leaderboard currently does not display.
## Desired improvements
- Display notifications when a user receives a message;
- Colourise people's status;
- Display 'online/busy' status in the list of online users.
