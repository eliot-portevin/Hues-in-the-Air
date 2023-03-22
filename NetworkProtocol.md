# Network Protocol

Our network protocol is split up into a ServerProtocol and a ClientProtocol. <br>
The packages sent are built up the same on both ends: <br>
COMMAND + SEPARATOR + ARGUMENT_1 + SEPARATOR + ARGUMENT... + SEPARATOR + ARGUMENT_N


## ServerProtocol

### SEPARATOR <br>
returns "<&!>" <br>
Used to separate arguments in the packages sent from client to server or vice versa <br>
Example: ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient <br>
Command sent form server to client. Here the separator is used to separate the argument NO_USER_FOUND from the recipient (the specific user not found)

### NO_USERNAME_SET
returns "NO_USERNAME_SET" <br>
Used to request username from client <br>
Example: ServerProtocol.NO_USERNAME_SET.toString() <br>
This string would be sent to the client to receive the username of the clients system.

### USERNAME_SET_TO
Inform client which username he has now <br>
Example: ServerProtocol.USERNAME_SET_TO.toString() + ServerProtocol.SEPARATOR + this.username;
Command sent form server to client. This string is going to be sent to the client to tell the client the username is now set to this.username

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
Command sent form server to client. This string is sent by server to all clients to send message from client to all clients connected to the server

### SEND_MESSAGE_LOBBY
A message is being sent to all clients in the lobby <br>
Example: ServerProtocol.SEND_MESSAGE_LOBBY + ServerProtocol.Separator + username + ServerProtocol.Separator + message <br>
Command sent form server to client. This string is sent by server to all clients to send message from client to all clients connected to the lobby

### SEND_CLIENT_LIST
List of all clients is being sent to a client <br>
Example: ServerProtocol.SEND_CLIENT_LIST.toString() + ServerProtocol.SEPARATOR + clients.stream().map(ClientHandler::getUsername).collect(Collectors.joining(" ")); <br>
Command sent form server to client. This string is sent from server to client to inform client who is connected to the server

### PONG
Signal regularly sent from server to client to confirm connection
Example: ServerProtocol.PONG.toString(); <br>
Command sent form server to client.
## ClientProtocol

### COMMAND_SYMBOL
Symbol inputted by the client in the console to indicate that the following input is a command <br>
returns "!" <br>
Example: if (command.startsWith(commandSymbol)) {send command to server} <br>
Here the client detects if a user input is meant as a command and if yes sends it to the server

### SET_USERNAME
Set client username <br>
Example: ClientProtocol.SET_USERNAME.toString() + ServerProtocol.SEPARATOR + username <br>
Sends command to server which tells server to update the username

### SEND_MESSAGE_LOBBY
Sends message to whole lobby <br>
Example: ClientProtocol.SEND_MESSAGE_LOBBY.toString() + ServerProtocol.SEPARATOR + message <br>
Command sent from client to server. Server then sends message to all clients in lobby.

### WHISPER
Sends message to specific client <br>
Example: ClientProtocol.SEND_MESSAGE_CLIENT + ClientProtocol.SEPARATOR + receiver.username + ClientProtocol.SEPARATORm + message <br>
Command sent from client to server. Server then sends message only to specific client.

### BROADCAST
Send a chat message to the whole server <br>
Example: ClientProtocol.BROADCAST + ClientProtocol.Separator + username + ClientProtocol.Separator + message <br>
Command sent from client to server. Server then sends message to all clients connected

### LOGOUT
Used when client is exiting the program <br>
Example: ClientProtocol.LOGOUT.toString() <br>
Command sent from client to server. Server get information, that client is disconnecting --> Logout protocol on serverside

### JOIN_LOBBY
Client wants to join a lobby <br>
Example: ClientProtocol.JOIN_LOBBY + ClientProtocol.Separator + lobbyName + ClientProtocol.Separator + password <br>
Command sent from client to server. Client requests to join specific lobby

### CREATE_LOBBY
Client wants to create a lobby
Example: ClientProtocol.CREATE_LOBBY + ClientProtocol.Separator + lobbyName + ClientProtocol.Separator + password <br>
Command sent from client to server. Client requests to create lobby on server.

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

### PING
Signal regularly sent from client to server to confirm connection<br>
Example: ClientProtocol.PING <br>
Command sent form client to server. Client sends this command regularly to server to detect connection issues.