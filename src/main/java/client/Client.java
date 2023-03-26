package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import server.ServerProtocol;

public class Client {

  // Status of client
  boolean connectedToServer = true;
  int noAnswerCounter = 0;
  int receivedNullCounter = 0;
  boolean shuttingDown = false;

  // Server info
  private static int SERVER_PORT;
  private static String SERVER_IP;
  private Socket socket;

  // Input and output streams
  private ServerIn inputSocket;
  private ServerOut outputSocket;
  private Thread inputThread;
  private Thread outputThread;
  private Thread pingSender;

  // Username
  protected String username = System.getProperty("user.name");

  /**
   * Run method from Runnable. Called from {@link ClientMain}. Creates input and output threads
   * (server communication) and starts them.
   */
  public void run(String[] args) throws IOException {
    start(args);
    this.connectToServer();

    this.inputSocket = new ServerIn(socket, this);
    this.outputSocket = new ServerOut(socket, this);

    this.inputThread = new Thread(this.inputSocket);
    this.outputThread = new Thread(this.outputSocket);
    this.pingSender = new Thread(new ClientPingSender(this));

    inputThread.start();
    outputThread.start();
    this.pingSender.start();

    System.out.println("[CLIENT] Connection to server established");
  }

  /** Pings the server. Called from {@link ClientPingSender} */
  protected void ping() {
    if (!shuttingDown) {
      String command = ClientProtocol.CLIENT_PING.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  /** Pongs the server when ping from server was received. Called from {@link ServerIn} */
  protected void pong() {
    if (!shuttingDown) {
      String command = ClientProtocol.CLIENT_PONG.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  /**
   * Sets the SERVER_IP and SERVER_PORT for the class upon startup. Called from {@link Client#run(
   * String[])}
   */
  private void start(String[] args) {
    System.out.println("Starting client...");
    String[] serverInfo = args[0].split(":");
    if (serverInfo.length != 2) {
      System.err.println(
          "Start the client with the following format: java Client <serverIP>:<serverPort>");
    }

    // Set server IP
    if (serverInfo[0].equals("localhost")) this.SERVER_IP = "127.0.0.1";
    else this.SERVER_IP = serverInfo[0];

    // Set server port
    try {
      this.SERVER_PORT = Integer.parseInt(serverInfo[1]);
    } catch (NumberFormatException e) {
      System.err.println(
          "Start the client with the following format: java Client <serverIP>:<serverPort>");
    }
  }

  /**
   * Tries to connect to the server socket according to the SERVER_IP and SERVER_PORT. Called from
   * {@link Client#run(String[])}. If connection is refused, retries every 2 seconds.
   */
  private void connectToServer() {
    this.socket = new Socket();
    // Try to connect to the server, if connection is refused, retry every 2 seconds
    while (!this.socket.isConnected()) {
      try {
        this.socket = new Socket(SERVER_IP, SERVER_PORT);
      } catch (IOException e) {
        System.err.println("[CLIENT] Connection timed out. Retrying...");
      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    if (this.socket.isConnected()) {
      this.connectedToServer = true;
    }
  }

  /**
   * Client wants to change their username. Sends a command to the server ({@link
   * server.ClientHandler}) containing the new username. Called upon startup when server requests
   * username and from {@link ServerOut}.
   *
   * @param username
   */
  protected void setUsername(String username) {
    String command = ClientProtocol.SET_USERNAME.toString() + ServerProtocol.SEPARATOR + username;
    this.username = username;
    this.outputSocket.sendToServer(command);
  }

  /**
   * Client wants to change their username. Reads the new username from the console and calls {@link
   * Client#setUsername(String)}.
   */
  protected void setUsername() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter username: \n> ");
      String username = reader.readLine();
      this.setUsername(username);
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to read username: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Client wants to broadcast a message to all clients on server. This method is called if the
   * client has inputted the message directly in the command line (see {@link ServerOut}). Sends a
   * command to the server ({@link server.ClientHandler}) containing the message. Called from {@link
   * ServerOut}.
   *
   * @param message
   */
  protected void sendMessageServer(String message) {
    String command = ServerProtocol.BROADCAST.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

  /**
   * See {@link Client#sendMessageServer(String)}. This method is called if the client has not
   * inputted the message directly in the command line. Requests the message from the console and
   * calls {@link Client#sendMessageServer(String)} with the message as a parameter.
   */
  protected void sendMessageServer() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter message: \n> ");
      String message = reader.readLine();
      this.sendMessageServer(message);
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to read message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * This client wants to send a private message to another client.
   *
   * <p>Protocol format: WHISPER<SEPARATOR>recipient.username<SEPARATOR>message
   */
  protected void sendMessageClient(String recipient, String message) {
    String command =
        ClientProtocol.WHISPER.toString()
            + ServerProtocol.SEPARATOR
            + recipient
            + ServerProtocol.SEPARATOR
            + message;
    this.outputSocket.sendToServer(command);
  }

  /**
   * See {@link Client#sendMessageClient(String, String)}. This method is called if the client has
   * not inputted the message directly in the command line. Requests the message from the console
   * and calls {@link Client#sendMessageClient(String, String)} with the message as a parameter.
   */
  protected void sendMessageClient() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter recipient name: \n> ");
      String recipient = reader.readLine();
      System.out.print("Enter message: \n> ");
      String message = reader.readLine();
      this.sendMessageClient(recipient, message);
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to read message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * This client wants to send a message to all clients in the lobby.
   *
   * <p>Protocol format: SEND_MESSAGE_LOBBY<SEPARATOR>message
   */
  protected void sendMessageLobby(String message) {
    String command =
        ClientProtocol.SEND_MESSAGE_LOBBY.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

  /**
   * See {@link Client#sendMessageLobby(String)}. This method is called if the client has not
   * inputted the message directly in the command line. Requests the message from the console and
   * calls {@link Client#sendMessageLobby(String)} with the message as a parameter.
   */
  protected void sendMessageLobby() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter message: \n> ");
      String message = reader.readLine();
      this.sendMessageLobby(message);
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to read message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * When client exits the program, this method is called. Communicates with server that client is
   * logging out and closes the socket.
   */
  protected void logout() {
    // Communicate with server that client is logging out
    String command = ClientProtocol.LOGOUT.toString();
    this.outputSocket.sendToServer(command);

    // Close the socket and stop the threads
    this.inputSocket.running = false;
    this.outputSocket.running = false;
    try {
      this.socket.close();
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to close socket: " + e.getMessage());
      e.printStackTrace();
    }
    System.exit(0);
  }

  /**
   * Client wants to create a lobby. Sends a command to the server ({@link server.ClientHandler})
   * containing the lobby name and password. Called from {@link ServerOut}.
   *
   * @param name
   * @param password
   */
  protected void createLobby(String name, String password) {
    String command =
        ClientProtocol.CREATE_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
  }

  /**
   * See {@link Client#createLobby(String, String)}. This method is called if the client has not
   * inputted the lobby name and password directly in the command line. Requests these parameters
   * from the console and calls {@link Client#createLobby(String, String)}.
   */
  protected void createLobby() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter lobby name: \n> ");
      String name = reader.readLine();
      System.out.print("Enter lobby password: \n> ");
      String password = reader.readLine();
      this.createLobby(name, password);
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to read lobby name: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Client wants to join an existing lobby. Sends a command to the server ({@link
   * server.ClientHandler}) containing the lobby name and password. Called from {@link ServerOut}.
   *
   * @param name
   * @param password
   */
  protected void joinLobby(String name, String password) {
    String command =
        ClientProtocol.JOIN_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
  }

  /**
   * See {@link Client#joinLobby(String, String)}. This method is called if the client has not
   * inputted the lobby name and password directly in the command line. Requests these parameters
   * from the console and calls {@link Client#joinLobby(String, String)}.
   */
  protected void joinLobby() {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Enter lobby name: \n> ");
      String name = reader.readLine();
      System.out.print("Enter lobby password: \n> ");
      String password = reader.readLine();
      this.joinLobby(name, password);
    } catch (IOException e) {
      System.err.println("[CLIENT] Failed to read lobby name: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /** Client wants to know their username. Called from {@link ServerOut}. */
  protected void whoami() {
    System.out.println(this.username);
  }

  /** Client wants to know the list of all clients in the lobby. Called from {@link ServerOut}. */
  protected void listClientsLobby() {
    String command = ClientProtocol.LIST_LOBBY.toString();
    this.outputSocket.sendToServer(command);
  }

  /** Client wants to know the list of all clients in the server. Called from {@link ServerOut}. */
  protected void listClientsServer() {
    String command = ClientProtocol.LIST_SERVER.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * Client has called {@link Client#listClientsLobby()} or {@link Client#listClientsServer()}. Upon
   * receiving the requested list of clients, this list is printed to the console.
   *
   * @param clients
   */
  public void printClientList(String[] clients) {
    System.out.println();
    for (String client : clients) {
      System.out.println(" - " + client);
    }
    System.out.print("> ");
  }
}
