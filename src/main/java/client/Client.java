package client;

import server.ServerProtocol;

import java.net.Socket;
import java.io.IOException;
import static shared.Encryption.decrypt;
import static shared.Encryption.encrypt;

public class Client {

  // Status of client
  boolean clientConnected = true;
  int noAnswerCounter = 0;

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

    String serverIp = args[0].split(":")[0];

    this.inputSocket = new ServerIn(socket, this);
    this.outputSocket = new ServerOut(socket, this);

    this.inputThread = new Thread(this.inputSocket);
    this.outputThread = new Thread(this.outputSocket);
    this.pingSender = new Thread(new PingSender(this));

    inputThread.start();
    outputThread.start();
    this.pingSender.run();

    System.out.println("[CLIENT] Connection to server established");
  }

  protected void ping() {
    String command = ClientProtocol.PING.toString();
    this.outputSocket.sendToServer(command);
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
      this.clientConnected = true;
    }
  }

  public void reconnect() {
    this.connectToServer();
  }

  protected void setUsername(String username) {
    String command = ClientProtocol.SET_USERNAME.toString() + ServerProtocol.SEPARATOR + username;
    this.username = username;
    this.outputSocket.sendToServer(command);
  }

  protected void sendMessageServer(String message) {
    String command = ServerProtocol.BROADCAST.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
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

  protected void sendMessageLobby(String message) {
    String command =
        ClientProtocol.SEND_MESSAGE_LOBBY.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

  protected void logout() {
    // Communicate with server that client is logging out
    // TODO: solve SocketException when logging out
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

  protected void createLobby(String name, String password) {
    String command =
        ClientProtocol.CREATE_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
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

  protected void whoami() {
    System.out.println(this.username);
  }
}
