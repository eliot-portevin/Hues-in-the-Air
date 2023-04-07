package client;

import server.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;


/** Handles the input from the server */
public class ServerIn implements Runnable {

  private final Socket serverSocket;
  private final BufferedReader in;
  private final Client client;

  protected Boolean running = true;

  /** Creates an instance of ServerConnection */
  public ServerIn(Socket serverSocket, Client client) throws IOException {
    this.serverSocket = serverSocket;
    this.in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
    this.client = client;
  }

  /** From the Runnable interface. Runs the ServerIn thread to receive commands from the server */
  @Override
  public void run() {
    try {
      while (this.running) {
        String[] command = this.receiveFromServer();
        if (command != null) {
          this.protocolSwitch(command);
        }
      }
      this.serverSocket.close();
      this.in.close();
    } catch (IOException e) {
      System.err.println("[ServerIn]: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Receives a command from the server via the socket and returns it as a string array. If the
   * received string is null, the method returns null. This however only happens when the connection
   * to the server is lost. Called by {@link #run()}.
   *
   * @throws IOException
   */
  private String[] receiveFromServer() throws IOException {
    String input = this.in.readLine();
    if (input != null) {
      return input.split(ServerProtocol.SEPARATOR.toString());
    } else {
      this.client.receivedNullCounter++;
      // Received null from server
      return null;
    }
  }

  /**
   * Client receives a command from the server and runs the appropriate method.
   *
   * <p>The command is split into an array of strings. The first string is the protocol and the rest
   * of the strings are the arguments. See {@link ServerProtocol} for possible protocols.
   */
  private void protocolSwitch(String[] command) {
    try {
      ServerProtocol protocol = ServerProtocol.valueOf(command[0]);

      if (protocol.getNumArgs() == command.length - 1) {
        switch (protocol) {
          case NO_USERNAME_SET -> this.client.setUsername(this.client.username);
          case BROADCAST -> this.receiveMessage(
              Arrays.copyOfRange(command, 1, command.length), "Public");
          case WHISPER -> this.receiveMessage(
              Arrays.copyOfRange(command, 1, command.length), "Private");
          case SEND_MESSAGE_LOBBY -> this.receiveMessage(
              Arrays.copyOfRange(command, 1, command.length), "Lobby");
          case SERVER_PING -> this.client.pong();
          case SERVER_PONG -> this.resetClientStatus();
          case USERNAME_SET_TO -> this.client.username = command[1];
          case SEND_CLIENT_LIST -> this.client.printClientList(command[1].split(" "));
          case LOBBY_EXITED -> this.client.lobbyExited(command[1]);
          case UPDATE_LOBBY_INFO -> this.client.updateLobbyInfo(command[1]);
        }
      }
    } catch (IllegalArgumentException e) {
      System.out.println("ServerIn: Unknown protocol: " + command[0]);
    }
  }

  /**
   * Resets the client status to "connected to server" and "noAnswerCounter" to 0. Called by {@link
   * #protocolSwitch(String[])} upon receiving a PONG from the server.
   */
  private void resetClientStatus() {
    this.client.connectedToServer = true;
    this.client.noAnswerCounter = 0;
  }

  /**
   * Prints a message received from another client to the console by formatting it. Called by {@link
   * #protocolSwitch(String[])}.
   *
   * @param command
   * @param privacy
   */
  private void receiveMessage(String[] command, String privacy) {
    if (command[0].equals(this.client.username)) {
      System.out.printf("%s [%s]: %s\n> ", privacy, "You", command[1]);
    } else {
      System.out.printf("%s [%s]: %s\n> ", privacy, command[0], command[1]);
    }
  }
}
