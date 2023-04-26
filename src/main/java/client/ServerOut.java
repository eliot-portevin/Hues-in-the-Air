package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import server.ServerProtocol;

/** Sends commands from client to server. */
public class ServerOut implements Runnable {

  private final Socket serverSocket;
  private final PrintWriter out;
  private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
  private final Client client;

  /** Used for the while loop in the run method */
  protected Boolean running = true;

  /**
   * Creates an instance of ServerOut.
   *
   * @param serverSocket The socket to the server
   * @param client The client which has created this instance
   *
   * @throws IOException If an I/O error occurs when creating the output stream, the socket is closed.
   */
  public ServerOut(Socket serverSocket, Client client) throws IOException {
    this.serverSocket = serverSocket;
    this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
    this.client = client;
  }

  /** From the Runnable interface. Runs the ServerOut thread to send commands to the server */
  @Override
  public void run() {
    try {
      while (this.running) {
        String command = this.keyboard.readLine();

        if (command != null) {
          if (command.equals("exit")) {
            this.client.exit();
          }
        }
      }
      try {
        this.serverSocket.close();
        this.out.close();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (IOException e) {
      System.err.println("[ServerOut]: " + e.getMessage());
      e.printStackTrace();
    }
    // Close the socket and the input stream
    try {
      this.serverSocket.close();
      this.keyboard.close();
    } catch (IOException e) {
      System.err.println(
          "[CLIENT] Failed to close serverSocket and input stream: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Sends a message to the server. Called by {@link #run()}
   *
   * @param message The message to send to the server
   */
  protected void sendToServer(String message) {
    if (this.validateMessage(message)) {
      this.out.println(message);
    }
  }

  /**
   * Validates the message to be sent to the server. Called by {@link #sendToServer(String)}
   *
   * @param message The message to validate
   * @return True if the message is valid, false otherwise
   */
  private Boolean validateMessage(String message) {
    if (message == null) {
      System.out.println("[SERVER_OUT] Message is null");
      return false;
    } else {
      String[] command = message.split(ServerProtocol.SEPARATOR.toString());
      if (command.length > ClientProtocol.valueOf(command[0]).getNumArgs() + 1) {
        System.out.print(
            "[SERVER_OUT] Tried to send too many arguments: "
                + Arrays.toString(command)
                + "\n> Would you just have happened to have "
                + ServerProtocol.SEPARATOR.toString()
                + " in your message?"
                + "\n> If you are trying to test this program's security, feel free to go away.\n> ");
        return false;
      }
    }
    return true;
  }
}
