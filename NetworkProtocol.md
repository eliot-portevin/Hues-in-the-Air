# Network Protocol

Our network protocol is split up into a ServerProtocol and a ClientProtocol. <br>
The packages sent are built up the same on both ends: <br>
COMMAND + SEPARATOR + ARGUMENT_1 + ARGUMENT... + ARGUMENT_N


## ServerProtocol

### SEPARATOR <br>
returns "<&!>" <br>
Is used to separate arguments in the packages sent from client to server or vice versa <br>
Example: ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient <br>
Here the separator is used to separate the argument NO_USER_FOUND from the recipient (the specific user not found)

### NO_USERNAME_SET
returns "NO_USERNAME_SET" <br>
Is used to request username from client <br>
Example: ServerProtocol.NO_USERNAME_SET.toString() <br>
This string would be sent to the client to receive the username of the clients system.

### USERNAME_SET_TO
Inform client which username he has now <br>
Example: ServerProtocol.USERNAME_SET_TO.toString() + ServerProtocol.SEPARATOR + this.username;
This string is going to be sent to the client to tell the client the username is now set to this.username

### NO_USER_FOUND
No user with that username was found <br>
Example: ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient <br>
This string is going to be sent to server to tell client that user was not found


