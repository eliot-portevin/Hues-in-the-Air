# Network Protocol

Our network protocol is split up into a ServerProtocol and a ClientProtocol. <br>
The packages sent are built up the same way on both ends: <br>
COMMAND + SEPARATOR + ARGUMENT_1 + SEPARATOR + ARGUMENT... + SEPARATOR + ARGUMENT_N


## ServerProtocol

### SEPARATOR <br>
returns "<&!>" <br>
Used to separate arguments in the packages sent from client to server or vice versa <br>
Example: ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient <br>
Command sent form server to client. Here the separator is used to separate the argument NO_USER_FOUND from the argument recipient (the specific user not found)

### NO_USERNAME_SET
Used to request username from client when it hasn't been set yet (called upon startup of the game).<br>
Example: ServerProtocol.NO_USERNAME_SET.toString() <br>
Command sent from server to client, to receive the username of the client's system.

### USERNAME_SET_TO
Inform client that their username has been changed. <br>
Example: ServerProtocol.USERNAME_SET_TO.toString() + ServerProtocol.SEPARATOR + this.username; <br>
Command sent form server to client, to tell the client, its username is now set to this.username <br>

### NO_USER_FOUND
No user with that username was found <br>
Example: ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient <br>
Command sent form server to client. This string is going to be sent to server to tell client that user was not found

### WHISPER
Used to communicate from client to client <br>
Example: ServerProtocol.WHISPER + ServerProtocol.SEPARATOR + sender.username + ServerProtocol.SEPARATOR + message <br>
Command sent form server to client. This string is sent from server client to send message to specific client

### BROADCAST
A message is being sent to the whole server <br>
Example: ServerProtocol.SEND_MESSAGE_SERVER + ServerProtocol.Separator + username + ServerProtocol.Separator + message <br>
Command sent form server all clients, to relay the message of one client to all clients connected to the server

### SEND_MESSAGE_LOBBY
A message is being sent to all clients in the lobby <br>
Example: ServerProtocol.SEND_MESSAGE_LOBBY + ServerProtocol.Separator + username + ServerProtocol.Separator + message <br>
Command sent form server all clients in the lobby, to send message to all clients connected to the lobby

### SEND_CLIENT_LIST
List of all clients is being sent to a client upon their request.<br>
Example: ServerProtocol.SEND_CLIENT_LIST.toString() + ServerProtocol.SEPARATOR + clients.stream().map(ClientHandler::getUsername).collect(Collectors.joining(" ")); <br>
Command sent form server to client, to inform client of who is connected to the server

### PONG
Signal regularly sent from server to client to confirm connection
Example: ServerProtocol.PONG.toString(); <br>

### LOBBY_EXITED
Sent to a client to inform them that they have successfully exited a lobby. Enables them to print
a message to the console.
Example: ServerProtocol.LOBBY_EXITED.toString() + ServerProtocol.SEPARATOR + lobbyName <br>

## ClientProtocol

### COMMAND_SYMBOL
Symbol inputted by the client in the console to indicate that the following input is a command <br>
returns "!" <br>
Example: if (command.startsWith(commandSymbol)) {send command to server} <br>
Here the client detects if a user input is meant as a command and if yes sends it to the server

### SET_USERNAME
Set client username <br>
Example: ClientProtocol.SET_USERNAME.toString() + ServerProtocol.SEPARATOR + username <br>
Sends command to server which asks server to update the username. The server then has the right
to modify that request if the username is already taken.

### SEND_MESSAGE_LOBBY
Sends message to the whole lobby <br>
Example: ClientProtocol.SEND_MESSAGE_LOBBY.toString() + ServerProtocol.SEPARATOR + message <br>
Command sent from client to server. Server then sends message to all clients in lobby.

### WHISPER
Sends message to specific client <br>
Example: ClientProtocol.SEND_MESSAGE_CLIENT + ClientProtocol.SEPARATOR + receiver.username + ClientProtocol.SEPARATOR + message <br>
Command sent from client to server. Server then sends message only to specific client.

### BROADCAST
Send a chat message to the whole server <br>
Example: ClientProtocol.BROADCAST + ClientProtocol.Separator + username + ClientProtocol.Separator + message <br>
Command sent from client to server. Server then sends message to all clients connected

### EXIT
Used when client is exiting the program <br>
Example: ClientProtocol.EXIT.toString() <br>
Command sent from client to server. Server get information, that client is disconnecting --> Logout protocol on serverside

### JOIN_LOBBY
Client wants to join a lobby <br>
Example: ClientProtocol.JOIN_LOBBY + ClientProtocol.Separator + lobbyName + ClientProtocol.Separator + password <br>
Command sent from client to server. Client requests to join specific lobby

### CREATE_LOBBY
Client wants to create a lobby
Example: ClientProtocol.CREATE_LOBBY + ClientProtocol.Separator + lobbyName + ClientProtocol.Separator + password <br>
Command sent from client to server. Client requests to create lobby on server.

### EXIT_LOBBY
Client wants to exit a lobby. If they are not in a lobby, the server will ignore their request. <br>
Example: ClientProtocol.EXIT_LOBBY <br>

### WHOAMI
Client requests its name from server
Example: ClientProtocol.WHOAMI 
Command sent from client to server. Client requests its name from server

### LIST_SERVER
Client wants to know the name of the other players inside the server <br>
Example: ClientProtocol.LIST_SERVER <br>
Command sent from client to server. Client requests all client names on the server

### LIST_LOBBY
Client wants to know the name of the other players inside its lobby <br>
Example: ClientProtocol.LIST_SERVER <br>
Command sent from client to server. Client requests names of all clients in its lobby

### CLIENT_PING
Signal regularly sent from client to server to confirm connection<br>
Example: ClientProtocol.PING <br>
Command sent form client to server. Client sends this command regularly to server to detect connection issues.

### CLIENT_PONG
Signal sent to server upon receiving a PING from the server <br>
Example: ClientProtocol.PONG <br>