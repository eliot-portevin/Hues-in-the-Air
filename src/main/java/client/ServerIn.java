package client;

import server.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

import static shared.Encryption.decrypt;

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
        } else {
          System.out.println("[CLIENT] ServerIn: command is null");
        }
      }
      this.serverSocket.close();
      this.in.close();
    } catch (IOException e) {
      System.err.println("[ServerIn]: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private String[] receiveFromServer() throws IOException {
    return decrypt(this.in.readLine()).split(ServerProtocol.SEPARATOR.toString());
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
          case PING -> this.resetClientStatus();
          case USERNAME_SET_TO -> this.client.username = command[1];
          case SEND_CLIENT_LIST -> this.client.printClientList(command[1].split(" "));
        }
      }
    } catch (IllegalArgumentException e) {
      System.out.println("ServerIn: Unknown protocol: " + command[0]);
    }
  }

  private void resetClientStatus() {
    this.client.connectedToServer = true;
    this.client.noAnswerCounter = 0;
  }

  private void receiveMessage(String[] command, String privacy) {
    if (command[0].equals(this.client.username)) {
      System.out.printf("%s [%s]: %s\n> ", privacy, "You", command[1]);
    } else {
      System.out.printf("%s [%s]: %s\n> ", privacy, command[0], command[1]);
    }
  }
}
