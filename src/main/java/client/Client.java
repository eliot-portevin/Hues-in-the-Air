package client;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import client.controllers.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.ServerProtocol;
import client.controllers.LoginController;

public class Client extends Application {

  // Status of client
  boolean connectedToServer = true;
  int noAnswerCounter = 0;
  int receivedNullCounter = 0;
  boolean shuttingDown = false;
  public static Client instance;

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

  // Controllers
  private LoginController loginController;
  private MenuController menuController;

  // Username
  protected String username = System.getProperty("user.name");

  // GUI
  // Window
  private GridPane root = new GridPane();
  private Stage stage;

  // Sound
  private MediaPlayer clickPlayer;

  // Logger
  private Logger logger;

  /**
   * Starts the application by creating a scene and setting the stage properties. Then proceeds to
   * set the scene as the login screen.
   */
  @Override
  public void start(Stage primaryStage) {
    // Set instance, required for other classes to access the client (for example the controllers)
    instance = this;
    logger = LogManager.getLogger(Client.class);

    // Set sound
    Media clickSound =
        new Media(Objects.requireNonNull(getClass().getResource("/sounds/click.wav")).toString());
    this.clickPlayer = new MediaPlayer(clickSound);

    // Get server info from command line arguments
    String[] args = getParameters().getRaw().toArray(new String[3]);

    // Create a black scene depending on the screen resolution
    Scene scene = initScene();
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource("/layout/FontStyle.css"))
                .toExternalForm());

    // Set stage scene
    this.stage = primaryStage;
    this.stage.setTitle("Hues in the Air");
    this.stage.setScene(scene);

    try {
      this.loadLoginScreen(args);
      // this.loadMenuScreen();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Could not load login screen. Closing the program.");
      System.exit(1);
    }

    // Set stage properties
    this.stage.setOnCloseRequest(
        e -> {
          e.consume();
          this.handleEscape();
        });

    // this.stage.setFullScreen(true);
    this.stage.setResizable(true);
    this.stage.show();
  }

  /**
   * Creates a blank scene depending on the screen resolution. The scene has a width of 16:9 and is
   * scaled to fit the screen.
   *
   * @return scene
   */
  private Scene initScene() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth();
    double height = screenSize.getHeight();

    double HEIGHT = width / 16 * 9;
    // Screen resolution
    double WIDTH = width;

    double scalingFactor = width / 1920;
    if (scalingFactor > 1) {
      HEIGHT = height;
      WIDTH = height / 9 * 16;
    }

    return new Scene(this.root, WIDTH, HEIGHT);
  }

  /**
   * The user has pressed the escape key or clicked the close button. A dialog is shown to confirm
   * the user's intention to exit the game.
   */
  private void handleEscape() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Hues in the Air");

    // Set dialog pane style
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add("/layout/Dialog.css");

    // Add logo
    ImageView logo = new ImageView(new Image("images/logo.jpg"));
    logo.setFitHeight(50);
    logo.setFitWidth(50);
    alert.setGraphic(logo);

    alert.setHeaderText("Are you sure you want to exit the game?");
    alert.initOwner(this.stage);
    alert.initStyle(StageStyle.UNIFIED);
    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.orElse(null) == ButtonType.YES) {
      this.exit();
    }
  }

  /**
   * Loads the login screen from fxml file. Called upon start of the application.
   *
   * @throws IOException if the fxml file could not be loaded (method FXMLLoader.load()).
   */
  private void loadLoginScreen(String[] args) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/LoginPage.fxml"));
    this.root = loader.load();

    // Set controller
    this.loginController = loader.getController();
    this.loginController.fillFields(args);

    // Set the scene
    this.stage.getScene().setRoot(this.root);
  }

  /**
   * Loads the menu screen from fxml file. Called when the user has successfully logged in.
   *
   * @throws IOException if the fxml file could not be loaded (method FXMLLoader.load()).
   */
  private void loadMenuScreen() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/MenuPage.fxml"));
    this.root = loader.load();

    // Set controller
    this.menuController = loader.getController();

    // Set the scene
    this.stage.getScene().setRoot(this.root);

    // Request list of clients and lobbies from server
    this.requestServerInfo();
  }

  private void requestServerInfo() {
    String command = ClientProtocol.REQUEST_SERVER_STATUS.toString();
    this.outputSocket.sendToServer(command);
  }

  /** Plays a clicking sound. Called when the user hovers over a button. */
  public void clickSound() {
    this.clickPlayer.play();
    this.clickPlayer.seek(this.clickPlayer.getStartTime());
  }

  /**
   * Connects to the server. Called from the login controller when the user has click the connect
   * button. The method creates the sockets and threads for the input and output streams before
   * starting the said threads.
   *
   * @param username the username of the user
   * @param serverIP the IP address of the server
   * @param serverPort the port used by the server
   */
  public void connect(String username, String serverIP, String serverPort) {
    try {
      SERVER_IP = serverIP;
      SERVER_PORT = Integer.parseInt(serverPort);
      if (!username.isEmpty()) {
        this.username = username;
      }

      // Create sockets
      this.socket = new Socket(SERVER_IP, SERVER_PORT);

      if (this.socket.isConnected()) {
        this.inputSocket = new ServerIn(socket, this);
        this.outputSocket = new ServerOut(socket, this);

        // Create threads for sockets
        this.inputThread = new Thread(this.inputSocket);
        this.outputThread = new Thread(this.outputSocket);
        this.pingSender = new Thread(new ClientPingSender(this));

        // Start threads
        this.inputThread.start();
        this.outputThread.start();
        this.pingSender.start();

        // Load menu screen
        this.loadMenuScreen();

        System.out.println("Connected to server.");
        this.connectedToServer = true;
      }
    } catch (IOException | NumberFormatException e) {
      this.loginController.displayErrorMessage();
    }
  }

  /** Sends a CLIENT_PING message to the server */
  protected void ping() {
    if (!shuttingDown) {
      String command = ClientProtocol.CLIENT_PING.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  /** Sends a CLIENT_PONG message to the server (meant as a response to the SERVER_PING message) */
  protected void pong() {
    if (!shuttingDown) {
      String command = ClientProtocol.CLIENT_PONG.toString();
      this.outputSocket.sendToServer(command);
    }
  }
  /** Starts the client */
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

  /** sets the username of the client */
  protected void setUsername(String username) {
    String command = ClientProtocol.SET_USERNAME.toString() + ServerProtocol.SEPARATOR + username;
    this.username = username;
    this.outputSocket.sendToServer(command);
  }
  /** Handles interaction with client when the client wants to set a username */
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
   * This client wants to send a public message to all clients (broadcast)
   *
   * <p>Protocol format: BROADCAST&#60SEPARATOR&#62message
   */
  protected void sendMessageServer(String message) {
    String command = ServerProtocol.BROADCAST.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

  /**
   * Handles interaction with client in the console when the client wants to send a public message
   * to all other clients (calls <code>sendMessageServer(String message)</code>)
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
   * This client wants to send a private message to another client (whisper chat).
   *
   * <p>Protocol format:
   * SEND_MESSAGE_CLIENT&#60SEPARATOR&#62recipient.username&#60SEPARATOR&#62message
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

  /**
   * Handles interaction with client in the console when the client wants to send a private message
   * to another client (calls <code>sendMessageClient(String recipient, String message)</code>)
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
   * This client wants to send a message to the other clients in the lobby.
   *
   * <p>Protocol format: SEND_MESSAGE_LOBBY&#60SEPARATOR&#62message
   */
  protected void sendMessageLobby(String message) {
    String command =
        ClientProtocol.SEND_MESSAGE_LOBBY.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

  /**
   * Handles interaction with client in the console when the client wants to send a message to the
   * other clients in the lobby (calls <code>sendMessageLobby(String message)</code>)
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

  /** Notifies server that the client is logging out, closes the socket and stops the threads */
  protected void exit() {
    // Communicate with server that client is logging out
    // TODO: solve SocketException when logging out
    String command = ClientProtocol.EXIT.toString();
    try {
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
    } catch (NullPointerException e) {
      System.out.println("Socket is already closed");
    }
    this.stage.close();
    System.exit(0);
  }

  /** Notifies the server that the client wants to exit the lobby */
  protected void exitLobby() {
    String command = ClientProtocol.EXIT_LOBBY.toString();
    this.outputSocket.sendToServer(command);
    System.out.println("exit");
  }

  /**
   * The client creates a new lobby
   *
   * @param name The name of the lobby
   * @param password The password required to enter the lobby
   */
  public void createLobby(String name, String password) {
    if (name.equals("") || password.equals("")) {
      System.out.println("Invalid lobby name or password");
      return;
    }
    String command =
        ClientProtocol.CREATE_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name.replaceAll(" ", "_")
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
  }

  /**
   * Handles interaction with client in the console when the client wants to create a new lobby
   * (calls <code>createLobby(String name, String password)</code>)
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
   * Notifies the server that the client wants to join a lobby
   *
   * @param name The name of the lobby which the client wants to join
   * @param password The password of the lobby which the client wants to join
   */
  public void joinLobby(String name, String password) {
    if (name.equals("") || password.equals("")) {
      System.out.println("Invalid lobby name or password");
      return;
    }
    String command =
        ClientProtocol.JOIN_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + name
            + ServerProtocol.SEPARATOR
            + password;
    this.outputSocket.sendToServer(command);
  }

  /**
   * Handles interaction with client in the console when the client wants to join a lobby (calls
   * <code>joinLobby(String name, String password)</code>)
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

  /** Prints the username of the client to the console */
  protected void whoami() {
    System.out.println(this.username);
    System.out.print("> ");
  }

  /** Sends a request to the server asking for the list of clients in the lobby */
  protected void listClientsLobby() {
    String command = ClientProtocol.LIST_LOBBY.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * Sends a request to the server asking for the list of total clients connected with the server
   */
  protected void listClientsServer() {
    String command = ClientProtocol.LIST_SERVER.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * Prints the list of clients that are passed in to the console
   *
   * @param clients A String array containing the usernames of clients
   */
  public void printClientList(String[] clients) {
    System.out.println("###############");
    for (String client : clients) {
      System.out.println("> " + client);
    }
    System.out.println("> ###############");
    System.out.print("> ");
  }

  /**
   * Prints a confirmation to the console that the client has exited the lobby <code>lobbyName
   * </code>
   *
   * @param lobbyName The name of the lobby that was exited
   */
  public void lobbyExited(String lobbyName) {
    System.out.print("> Exiting lobby " + lobbyName + "\n> ");
  }

  /**
   * Updates the list of lobbies and their respective clients in the gui. The list of lobbies is
   * given in the following format: <code>lobbyName1 client1 client2 client3<&?>lobbyName2</code>
   *
   * @param command
   */
  public void updateLobbyInfo(String command) {
    ArrayList<ArrayList<String>> lobbyInfos = new ArrayList<>();
    for (String lobbyInfo : command.split(ServerProtocol.LOBBY_INFO_SEPARATOR.toString())) {
      // Split the lobby info into the lobby name and the clients
      String[] split = lobbyInfo.split(" ");
      ArrayList<String> lobbyInfoList = new ArrayList<>(Arrays.asList(split));

      lobbyInfos.add(lobbyInfoList);
    }
    String[][] asArray =
        lobbyInfos.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);
    this.menuController.setLobbyList(asArray);
  }

  public void updateClientInfo(String command) {
    if (this.menuController != null) {
      String[] clients = command.split(" ");
      this.menuController.setUsersList(clients);
    }
  }

  /**
   * Returns an instance of the client. Called from controllers to access various methods or
   * variables of the client.
   *
   * @return The instance of the client
   */
  public static Client getInstance() {
    return instance;
  }
}
