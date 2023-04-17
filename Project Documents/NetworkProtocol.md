# Network Protocol

Our network protocol is split up into a ServerProtocol and a ClientProtocol. <br>
The packages sent are built up the same way on both ends: <br>
COMMAND + SEPARATOR + ARGUMENT_1 + SEPARATOR + ARGUMENT... + SEPARATOR + ARGUMENT_N


## ServerProtocol

### SEPARATOR <br>
returns "<&!>" <br>
Used to separate arguments in the packages sent from client to server or vice versa <br>
Example: LOBBY_EXITED<&!>lobbyName <br>
Command sent form server to client. Here the separator is used to separate the argument LOBBY_EXITED from the argument recipient (the lobby name)

### LOBBY_INFO_SEPARATOR
returns "<&?>" <br>
Used to separate lobby information when a list of clients is requested <br>
Example: UPDATE_LOBBY_LIST<&!>username1<&?>username2<&?>username3 <br>
Command sent form server to client. Here the separator is used to separate the arguments (username1, username2, username3) from each other.

### USERNAME_SET_TO
Inform client that their username has been changed. <br>
Example: USERNAME_SET_TO<&!>username <br>
Command sent form server to client to tell the client that their username is now successfully set to this.username <br>

### NO_USER_FOUND
No user with that username was found <br>
Example: NO_USER_FOUND<&!>username <br>
Command sent form server to client. This string is going to be sent to server to tell client that user was not found

### SEND_PRIVATE_MESSAGE
Used to communicate from client to client <br>
Example: SEND_PRIVATE_MESSAGE<&!>recipient<&!>message <br>
Command sent form server to client. This string is sent from server client to send message to specific client

### SEND_PUBLIC_MESSAGE
A message is being sent to the whole server <br>
Example: SEND_PUBLIC_MESSAGE<&!>sender<&!>message <br>
Command sent form server all clients, to relay the message of one client to all clients connected to the server

### SEND_LOBBY_MESSAGE
A message is being sent to all clients in the lobby <br>
Example: SEND_LOBBY_MESSAGE<&!>username<&!>message <br>
Command sent form server all clients in the lobby, to send message to all clients connected to the lobby

### LOBBY_JOINED
A client has successfully joined a lobby <br>
Example: LOBBY_JOINED<&!>lobbyName <br>
Command sent form server to client. This string is sent to client to inform them that they have successfully joined a lobby.

### LOBBY_EXITED
Sent to a client to inform them that they have successfully exited a lobby. Enables them to print
a message to the console.
Example: LOBBY_EXITED<&!>lobbyName <br>
Command sent form server to client. This string is sent to client to inform them that they have successfully exited a lobby.

### UPDATE_FULL_LIST
Sends a list of all lobbies and their clients to the client. <br>
Example: UPDATE_FULL_LIST<&!>lobby1 username1 username2 username3<&?> lobby2 username4 username5<br>
Command sent form server to client. This string is sent to client for them to have access to a full list of all lobbies and their clients.

### UPDATE_CLIENT_LIST
Sends a list of all clients in the server to the client. <br>
Example: UPDATE_CLIENT_LIST<&!>username1<&?>username2<&?>username3 <br>
Command sent form server to client. This string is sent to client for them to have access to a list of all clients connected to the server.

### UPDATE_LOBBY_LIST
Sends a list of all clients in the lobby to the client. <br>
Example: UPDATE_LOBBY_LIST<&!>username1 true #f57dc6<&?>username2 false #ffffff<&?>username3 true #b35h6e<br>
Command sent form server to client. This string is sent to client for them to have access to a list of all clients connected to the lobby.

### UPDATE_GAME_LIST
Sends a list of all games that have been played or are currently being played to the client. Contains whether the games have been ended or not.<br>
Example: UPDATE_GAME_LIST<&!>game1 true<&?>game2 false<&?>game3 false<br>
Command sent form server to client. This string is sent to client for them to have access to a list of all games that have been played or are currently being played.

### TOGGLE_READY_STATUS
Informs the client that their ready status has successfully been changed. <br>
Example: TOGGLE_READY_STATUS<&!>true <br>
Command sent form server to client. This string is sent to client to inform them that their ready status has successfully been changed.

### START_GAME
Informs the client that the game has started. <br>
Example: START_GAME <br>
Command sent form server to clients in lobby. This string is sent to client to inform them that the game has started.

### SERVER_PING
Signal regularly sent from server to client to confirm connection<br>
Example: SERVER_PING <br>
Command sent form client to server. Server checks if client is still connected by sending this command to client.

### SERVER_PONG
Signal sent to client upon receiving a PING from the client <br>
Example: SERVER_PONG; <br>
Command sent form server to client. Server has received a PING from client and sends this command to client to confirm connection.

### TOGGLE_PAUSE
Signal sent to client upon receiving a pause request.
Example: TOGGLE_PAUSE <br>
Command sent form server to client. Server has received a pause request from client and sends this command to all clients to pause the game.

###  START_GAME_LOOP
Signal sent to client upon receiving a start game request.
Example: START_GAME_LOOP <br>
Command sent form server to client. Server has received a start game request from client and sends this command to all clients to start the game.

### POSITION_UPDATE
Updates the position of the cube for the client. <br>
Example: POSITION_UPDATE<&!>x_pos<&!>y_pos <br>
Command sent form server to client. Server sends this command to all clients to update the position of the cube.

## ClientProtocol
### COMMAND_SYMBOL
Symbol inputted by the client to indicate that the following input is a command <br>
returns "!" <br>
Example: if (command.startsWith(commandSymbol)) {send command to server} <br>
Here the client detects if a user input is meant as a command and if yes sends it to the server

### SET_USERNAME
Set client username <br>
Example: SET_USERNAME<&!>username <br>
Sends command to server which asks server to update the username. The server then has the right
to modify that request if the username is already taken.

### SEND_LOBBY_MESSAGE
Sends message to the whole lobby <br>
Example: SEND_LOBBY_MESSAGE<&!>message <br>
Command sent from client to server. Server then sends message to all clients in lobby.

### SEND_PRIVATE_MESSAGE
Sends message to specific client <br>
Example: SEND_PRIVATE_MESSAGE<&!>recipient<&!>message <br>
Command sent from client to server. Server then sends message only to specific client.

### SEND_PUBLIC_MESSAGE
Send a chat message to the whole server <br>
Example: SEND_PUBLIC_MESSAGE<&!>message <br>
Command sent from client to server. Server then sends message to all clients connected

### EXIT
Used when client is exiting the program <br>
Example: EXIT <br>
Command sent from client to server. Server get information, that client is disconnecting --> Logout protocol on serverside

### JOIN_LOBBY
Client wants to join a lobby <br>
Example: JOIN_LOBBY<&!>lobbyName<&!>lobbyPassword <br>
Command sent from client to server. Client requests to join specific lobby.

### CREATE_LOBBY
Client wants to create a lobby
Example: CREATE_LOBBY<&!>lobbyName<&!>lobbyPassword <br>
Command sent from client to server. Client requests to create lobby on server.

### EXIT_LOBBY
Client wants to exit a lobby. If they are not in a lobby, the server will ignore their request. <br>
Example: EXIT_LOBBY <br>

### GET_CLIENTS_SERVER
Client wants a list of all clients connected to the server <br>
Example: GET_CLIENTS_SERVER <br>
Command sent from client to server. Client requests names of all clients connected to the server

### GET_CLIENTS_LOBBY
Client wants a list of all clients connected to the lobby <br>
Example: GET_CLIENTS_LOBBY <br>
Command sent from client to server. Client requests names of all clients connected to the lobby

### TOGGLE_READY_STATUS
Client wants to toggle their ready status <br>
Example: TOGGLE_READY_STATUS<&!>clientIsReady <br> 
Command sent from client to server. Client wants to toggle their ready status

### CLIENT_PING
Signal regularly sent from client to server to confirm connection<br>
Example: PING <br>
Command sent form client to server. Client sends this command regularly to server to detect connection issues.

### CLIENT_PONG
Signal sent to server upon receiving a PING from the server <br>
Example: PONG <br>

### REQUEST_JUMP
Signal sent to server to tell server that client wants to jump <br>
Example: REQUEST_JUMP <br>
Command sent form client to server. Client sends this command to server to tell server that client wants to jump,
server then checks whether the client is allowed to jump and either makes all clients jump or does nothing.

### REQUEST_PAUSE
Signal sent to server to tell server that client wants to pause the game <br>
Example: REQUEST_PAUSE <br>
Command sent form client to server. Client sends this command to server to tell server that client wants to pause the game,
server then pauses the game and sends TOGGLE_PAUSE to all clients.

### START_GAME_LOOP
Signal sent to server to tell server that client wants to start the game <br>
Example: START_GAME_LOOP <br>
Command sent form client to server. Client sends this command to server to tell server that client wants to start the game,
server then starts the game and sends START_GAME to all clients.