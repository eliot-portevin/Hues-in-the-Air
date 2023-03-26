package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import server.Lobby;
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
    System.out.print("> ");
  }

  protected void ping() {
    if (!shuttingDown) {
      String command = ClientProtocol.CLIENT_PING.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  protected void pong() {
    if (!shuttingDown) {
      String command = ClientProtocol.CLIENT_PONG.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  public static void start(String[] args) {
    System.out.println("Starting client...");
    String[] serverInfo = args[0].split(":");
    if (serverInfo.length != 2) {
      System.err.println(
          "Start the client with the following format: java Client <serverIP>:<serverPort>");
    }

    // Set server IP
    if (serverInfo[0].equals("localhost")) SERVER_IP = "127.0.0.1";
    else SERVER_IP = serverInfo[0];

    // Set server port
    try {
      SERVER_PORT = Integer.parseInt(serverInfo[1]);
    } catch (NumberFormatException e) {
      System.err.println(
          "Start the client with the following format: java Client <serverIP>:<serverPort>");
    }
  }

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

  protected void setUsername(String username) {
    String command = ClientProtocol.SET_USERNAME.toString() + ServerProtocol.SEPARATOR + username;
    this.username = username;
    this.outputSocket.sendToServer(command);
  }

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

  protected void sendMessageServer(String message) {
    String command = ServerProtocol.BROADCAST.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

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
   * <p>Protocol format: SEND_MESSAGE_CLIENT<SEPARATOR>recipient.username<SEPARATOR>message
   */
  protected void sendMessageClient(String recipient, String message) {
    String command =
        ServerProtocol.WHISPER.toString()
            + ServerProtocol.SEPARATOR
            + recipient
            + ServerProtocol.SEPARATOR
            + message;
    this.outputSocket.sendToServer(command);
  }

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

  protected void sendMessageLobby(String message) {
    String command =
        ClientProtocol.SEND_MESSAGE_LOBBY.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

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

  protected void exit() {
    // Communicate with server that client is logging out
    // TODO: solve SocketException when logging out
    String command = ClientProtocol.EXIT.toString();
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
  protected void exitLobby() {
    String command = ClientProtocol.EXIT_LOBBY.toString();
    this.outputSocket.sendToServer(command);
    System.out.println("exit");
  }

  protected void createLobby(String name, String password) {
    String command =
        ClientProtocol.CREATE_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
  }

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

  protected void joinLobby(String name, String password) {
    String command =
        ClientProtocol.JOIN_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
  }

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

  protected void whoami() {
    System.out.println(this.username);
  }

  protected void listClientsLobby() {
    String command = ClientProtocol.LIST_LOBBY.toString();
    this.outputSocket.sendToServer(command);
  }

  protected void listClientsServer() {
    String command = ClientProtocol.LIST_SERVER.toString();
    this.outputSocket.sendToServer(command);
  }

  public void printClientList(String[] clients) {
    System.out.println("###############");
    for (String client : clients) {
      System.out.println("> " + client);
    }
    System.out.println("> ###############");
    System.out.print("> ");
  }
  public void lobbyExited(String lobbyName) {
    System.out.print("> Exiting lobby " + lobbyName + "\n> ");
  }
}
