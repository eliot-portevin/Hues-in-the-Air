package client;

import client.controllers.GameController;
import client.controllers.LobbyController;
import client.controllers.LoginController;
import client.controllers.MenuController;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.Server;
import server.ServerProtocol;

/**
 * The client class. This class creates a window and handles the connection to the server. It also
 * handles the GUI and the controllers.
 */
public class Client extends Application {

  // Status of client
  boolean connectedToServer = false;
  boolean serverHasPonged = true;
  int noAnswerCounter = 0;
  int receivedNullCounter = 0;
  boolean shuttingDown = false;
  /** The instance of the client. Set upon startup. */
  public static Client instance;

  boolean loginScreen = true;

  boolean menuScreen = false;
  boolean lobbyScreen = false;
  boolean gameScreen = false;
  boolean isInLobby = false;

  String lobbyName = "";

  // Server info
  private static int SERVER_PORT;
  private static String SERVER_IP;
  private Socket socket;

  // Input and output streams
  private ServerIn inputSocket;
  private ServerOut outputSocket;

  // Controllers
  /** The login controller */
  private LoginController loginController;
  /** The menu controller */
  private MenuController menuController;
  /** The lobby controller */
  private LobbyController lobbyController;
  /** The game controller */
  public GameController gameController;

  /** The username of the client */
  protected String username = null;
  /** The colour of the client */
  private Color colour = null;

  // GUI
  // Window
  private GridPane root = new GridPane();
  private Stage stage;

  // Sound
  private MediaPlayer clickPlayer;
  private MediaPlayer menuMusicPlayer;
  private final String[] musicPaths = {
    "bedtime_stories.mp3",
    "ghiblis_waltz.mp3",
    "paradise.mp3",
    "silver_seven_step.mp3",
    "weightless.mp3"
  };
  private MediaPlayer gameMusicPlayer;
  int currentMusicIdx = -1;
  boolean menuMusicPlaying = false;

  /** The logger for the client */
  public Logger LOGGER;

  /**
   * The italics font used in the application. It cannot be loaded in css files, so it is loaded
   * here
   */
  public static final javafx.scene.text.Font bebasItalics =
      javafx.scene.text.Font.loadFont(
          Objects.requireNonNull(Client.class.getResource("/fonts/Bebas_Neue_Italics.otf"))
              .toExternalForm(),
          20);

  /**
   * Starts the application by creating a scene and setting the stage properties. Then proceeds to
   * set the scene as the login screen.
   */
  @Override
  public void start(Stage primaryStage) {
    // Set instance, required for other classes to access the client (for example the controllers)
    instance = this;
    LOGGER = LogManager.getLogger(Client.class);

    LOGGER.info("Starting the program.");

    // Set sound
    Media clickSound =
        new Media(Objects.requireNonNull(getClass().getResource("/sounds/click.wav")).toString());
    this.clickPlayer = new MediaPlayer(clickSound);
    Media menuMusic =
        new Media(
            Objects.requireNonNull(getClass().getResource("/sounds/menu_music.mp3")).toString());
    this.menuMusicPlayer = new MediaPlayer(menuMusic);

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
    this.stage.setMinWidth(960);
    this.stage.setMinHeight(540);

    // Set stage properties
    this.stage.setOnCloseRequest(
        e -> {
          LOGGER.info("User has clicked the close button.");
          e.consume();
          this.handleEscape();
        });

    this.stage.setResizable(true);
    this.stage.show();

    this.loadSplashScreen(args);
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
      LOGGER.info("User has confirmed exit.");
      this.exit();
    }
  }

  /**
   * Plays the intro video before launching the login screen.
   */
  private void loadSplashScreen(String[] args) {
    this.LOGGER.info("Loading splash screen.");

    // Load intro video
    MediaPlayer splashPlayer =
        new MediaPlayer(
            new Media(
                Objects.requireNonNull(getClass().getResource("/images/intro.mp4")).toString()));
    MediaView mediaView = new MediaView(splashPlayer);

    // Create a new pane and set it as the root
    GridPane pane = new GridPane();
    pane.getChildren().add(mediaView);
    this.stage.getScene().setRoot(pane);
    this.stage.setFullScreen(true);
    this.stage.getScene().setFill(Color.BLACK);

    // Set media view properties
    mediaView.setPreserveRatio(true);
    mediaView.setFitWidth(this.stage.getWidth());
    if (mediaView.getFitHeight() > this.stage.getHeight()) {
      mediaView.setFitHeight(this.stage.getHeight());
    }

    splashPlayer.play();

    LOGGER.info("Splash screen playing.");

    // Launch the login screen after the video has finished playing
    splashPlayer.setOnEndOfMedia(
        () -> {
          try {
            this.loadLoginScreen(args);
          } catch (IOException e) {
            LOGGER.error("Could not load login screen. Closing the program.");
            e.printStackTrace();
            System.exit(1);
          }
        });
  }

  /**
   * Loads the login screen from fxml file. Called upon start of the application.
   *
   * @throws IOException if the fxml file could not be loaded (method FXMLLoader.load()).
   */
  private void loadLoginScreen(String[] args) throws IOException {
    this.LOGGER.info("Loading login screen.");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/login/LoginPage.fxml"));
    this.root = loader.load();
    this.stage.getScene().setRoot(this.root);

    // Set controller
    this.loginController = loader.getController();
    this.loginController.fillFields(args);

    // Set the scene
    this.stage.getScene().setRoot(this.root);
    LOGGER.info("Login screen loaded.");
  }

  /**
   * Loads the menu screen from fxml file. Called when the user has successfully logged in.
   *
   * @throws IOException if the fxml file could not be loaded (method FXMLLoader.load()).
   */
  private void loadMenuScreen() throws IOException {
    this.LOGGER.info("Loading menu screen.");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/menu/MenuPage.fxml"));
    this.root = loader.load();
    this.stage.getScene().setRoot(this.root);

    // Set controller
    this.menuController = loader.getController();

    // Request the lobby list, the client list and the game list
    this.requestMenuLists();

    // Play menu music
    this.playMenuMusic();

    this.loginScreen = false;
    this.menuScreen = true;
    this.lobbyScreen = false;
    this.connectedToServer = true;
    LOGGER.info("Menu screen loaded.");
  }

  /**
   * Loads the lobbyScreen from fxml file
   *
   * @throws IOException if the fxml file could not be loaded (method FXMLLoader.load())
   */
  void loadLobbyScreen() throws IOException {
    this.LOGGER.info("Loading lobby screen.");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/lobby/Lobby.fxml"));
    this.root = loader.load();
    this.stage.getScene().setRoot(this.root);

    // Set controller
    this.lobbyController = loader.getController();

    // Reset the toggle status in case the user was in a game before
    this.sendToggleReady(false);

    // Music
    this.playMenuMusic();

    this.menuScreen = false;
    this.isInLobby = true;
    this.lobbyScreen = true;
    this.gameScreen = false;
    LOGGER.info("Lobby screen loaded.");
  }

  /**
   * Loads the game screen from the fxml file.
   *
   * @throws IOException if the fxml file could not be found
   */
  public void loadGameScreen() throws IOException {
    this.LOGGER.info("Loading game screen.");
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/game/Game.fxml"));
    this.root = loader.load();
    this.stage.getScene().setRoot(this.root);

    // Set controller
    this.gameController = loader.getController();
    this.gameController.setClient(this);
    this.gameController.startGame();

    this.lobbyScreen = false;
    this.gameScreen = true;
    LOGGER.info("Game screen loaded.");

    // Music
    this.playGameMusic();
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
      LOGGER.info(
          "Attempting to connect to server with IP: " + serverIP + " and port: " + serverPort);
      SERVER_IP = serverIP;
      SERVER_PORT = Integer.parseInt(serverPort);
      if (username.isEmpty()) {
        username = System.getProperty("user.name");
      } else if (username.length() > Server.MAX_NAME_LENGTH) {
        loginController.alertManager.displayAlert(
            "Username cannot be longer than " + Server.MAX_NAME_LENGTH + " characters.", true);
        return;
      } else if (username.equalsIgnoreCase("you")) {
        loginController.alertManager.displayAlert("Username cannot be 'you'.", true);
        return;
      }

      // Create sockets
      this.socket = new Socket(SERVER_IP, SERVER_PORT);

      if (this.socket.isConnected()) {
        this.inputSocket = new ServerIn(socket, this);
        this.outputSocket = new ServerOut(socket, this);

        // Create threads for sockets
        Thread inputThread = new Thread(this.inputSocket);
        Thread outputThread = new Thread(this.outputSocket);
        Thread pingSender = new Thread(new ClientPingSender(this));

        // Start threads
        inputThread.start();
        outputThread.start();
        pingSender.start();

        LOGGER.info("Connected to server.");
        this.serverHasPonged = true;

        this.setUsername(username);

        // Load menu screen
        this.loadMenuScreen();
      }
    } catch (IOException | NumberFormatException e) {
      this.loginController.alertManager.displayAlert("Could not connect to server.", true);
      LOGGER.error("Could not connect to server. " + e.getMessage());
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
  /**
   * Starts the client and sets the IP and the port
   *
   * @param args the IP and the port of the server
   */
  public static void start(String[] args) {
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

  /**
   * Sets the username of the client. Inform when the username is already set to that one
   *
   * @param username the username to set
   */
  public void setUsername(String username) {
    if (this.menuScreen) {
      if (username.equals(this.username)) {
        this.menuController.alertManager.displayAlert(
            "Username already set to " + username + ".", true);
        return;
      } else if (username.length() > Server.MAX_NAME_LENGTH) {
        this.menuController.alertManager.displayAlert(
            "Username cannot be longer than " + Server.MAX_NAME_LENGTH + " characters.", true);
        return;
      } else if (username.equalsIgnoreCase("you")) {
        this.menuController.alertManager.displayAlert("\"You\" isn't much of a name is it?", true);
        return;
      }
    }
    String command =
        ClientProtocol.SET_USERNAME.toString()
            + ServerProtocol.SEPARATOR
            + username.replace(" ", "_");
    this.outputSocket.sendToServer(command);
    LOGGER.info("Requested username change from " + this.username + " to " + username + ".");
  }

  /**
   * This client wants to send a public message to all clients (broadcast).
   *
   * @param message the message to send
   */
  public void sendPublicMessage(String message) {
    try {
      String command =
          ServerProtocol.SEND_PUBLIC_MESSAGE.toString() + ServerProtocol.SEPARATOR + message;
      this.outputSocket.sendToServer(command);
    } catch (Exception e) {
      // The message was empty
      this.menuController.alertManager.displayAlert(message + "\nis an invalid message.", true);
    }
  }

  /**
   * This client wants to send a private message to another client (whisper chat).
   *
   * @param message the message to send
   */
  public void sendPrivateMessage(String message) {
    String[] split = message.split(" ", 2);
    if (split.length <= 1) {
      if (this.isInLobby) {
        this.lobbyController.alertManager.displayAlert("Tried sending empty message.", true);
      } else if (this.menuScreen) {
        this.menuController.alertManager.displayAlert("Tried sending empty message.", true);
      }
      return;
    }
    String recipient = split[0].substring(1);
    String messageContent = split[1];

    if (recipient.equals(this.username)) {
      if (this.isInLobby) {
        this.lobbyController.alertManager.displayAlert(
            "You cannot send messages to yourself.", true);
      } else if (this.menuScreen) {
        this.menuController.alertManager.displayAlert(
            "You cannot send messages to yourself.", true);
      }
      return;
    }

    String command =
        ServerProtocol.SEND_PRIVATE_MESSAGE.toString()
            + ServerProtocol.SEPARATOR
            + recipient
            + ServerProtocol.SEPARATOR
            + messageContent;
    this.outputSocket.sendToServer(command);
  }

  /**
   * This client wants to send a message to the other clients in the lobby.
   *
   * <p>Protocol format: SEND_MESSAGE_LOBBY&#60;SEPARATOR&#62;message
   *
   * @param message the message to send
   */
  public void sendLobbyMessage(String message) {
    String command =
        ClientProtocol.SEND_LOBBY_MESSAGE.toString() + ServerProtocol.SEPARATOR + message;
    this.outputSocket.sendToServer(command);
  }

  /**
   * Displays an alert on the screen when a user is not found
   *
   * @param username The username that was not found
   */
  public void noUserFound(String username) {
    if (this.menuScreen) {
      this.menuController.alertManager.displayAlert("User " + username + " not found.", true);
    } else if (this.lobbyScreen) {
      this.lobbyController.alertManager.displayAlert("User " + username + " not found.", true);
    } else if (this.gameScreen) {
      this.gameController.alertManager.displayAlert("User " + username + " not found.", true);
    }
  }

  /** Notifies server that the client is logging out, closes the socket and stops the threads */
  protected void exit() {
    LOGGER.info("Closing the program.");
    if (this.connectedToServer) {
      try {
        // Communicate with server that client is logging out
        String command = ClientProtocol.EXIT.toString();
        this.outputSocket.sendToServer(command);
      } catch (NullPointerException e) {
        LOGGER.error("Socket is already closed" + e.getMessage());
      }

      // Close the socket and stop the threads
      try {
        this.socket.close();
      } catch (IOException e) {
        LOGGER.error("Could not close the socket.");
      }

      this.inputSocket.running = false;
      this.outputSocket.running = false;
    }

    try {
      this.stage.close();
      Platform.exit();
    } catch (Exception e) {
      LOGGER.error("Stage is already closed");
    }

    System.exit(0);
  }

  /** Notifies the server that the client wants to exit the lobby */
  public void exitLobby() {
    String command = ClientProtocol.EXIT_LOBBY.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * The client creates a new lobby
   *
   * @param name The name of the lobby
   * @param password The password required to enter the lobby
   */
  public void createLobby(String name, String password) {
    if (!this.isInLobby) {
      if (name.equals("") || password.equals("")) {
        return;
      }
      if (name.length() > Server.MAX_NAME_LENGTH) {
        if (this.menuScreen) {
          this.menuController.alertManager.displayAlert(
              "Lobby name cannot be longer than " + Server.MAX_NAME_LENGTH + " characters.", true);
          return;
        }
      }
      LOGGER.info("Requesting creation of lobby with name " + name + ".");
      String command =
          ClientProtocol.CREATE_LOBBY.toString()
              + ServerProtocol.SEPARATOR
              + name.replaceAll(" ", "_")
              + ServerProtocol.SEPARATOR
              + password;
      this.outputSocket.sendToServer(command);
    }
  }

  /**
   * Notifies the server that the client wants to join a lobby
   *
   * @param name The name of the lobby which the client wants to join
   * @param password The password of the lobby which the client wants to join
   */
  public void joinLobby(String name, String password) {
    if (!this.isInLobby) {
      if (name.equals("") || password.equals("")) {
        System.out.println("Invalid lobby name or password");
        return;
      }
      LOGGER.info("Requesting to join lobby with name " + name + ".");
      String command =
          ClientProtocol.JOIN_LOBBY.toString()
              + ServerProtocol.SEPARATOR
              + name
              + ServerProtocol.SEPARATOR
              + password;
      this.outputSocket.sendToServer(command);
    }
  }

  /**
   * If the client is in a lobby, their list of clients in the lobby is updated.
   *
   * @param clientList The list of clients in the lobby in the format from the server command
   */
  protected void updateLobbyList(String clientList) {
    if (this.isInLobby) {
      this.lobbyController.updateLobbyList(
          clientList.split(ServerProtocol.SUBSEPARATOR.toString()));
    }
  }

  /**
   * The client has received a list of all games that are currently running or have been completed.
   * These are passed on to the games tab controller in the menu.
   *
   * @param gameList The list of games in the format from the server command
   */
  protected void updateGameList(String gameList) {
    if (this.menuScreen) {
      String[] games = gameList.split(ServerProtocol.SUBSEPARATOR.toString());
      this.menuController.setGameList(games);
    }
  }

  /** The client has entered the menu screen and wants to update all the lists available to them. */
  protected void requestMenuLists() {
    String command = ClientProtocol.GET_FULL_MENU_LISTS.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * The client has loaded the level successfully and wants to know the critical blocks in order to
   * colour the level.
   */
  protected void requestCriticalBlocks() {
    String command = ClientProtocol.REQUEST_CRITICAL_BLOCKS.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * The client has loaded the lobby screen successfully and would like to know which clients are in
   * their lobby.
   */
  public void requestLobbyList() {
    String command = ClientProtocol.GET_FULL_LOBBY_LIST.toString();
    this.outputSocket.sendToServer(command);
  }

  /**
   * Inform the server when the toggle.isReady is true
   *
   * @param isReady whether the client is ready or not
   */
  public void sendToggleReady(Boolean isReady) {
    LOGGER.info("Toggling ready status to " + isReady + ".");
    String command =
        ClientProtocol.TOGGLE_READY_STATUS.toString() + ServerProtocol.SEPARATOR + isReady;
    this.outputSocket.sendToServer(command);
  }

  /**
   * This method set the toggle to ready when it's called
   *
   * @param isReady whether the client is ready or not
   */
  public void setToggleReady(boolean isReady) {
    this.lobbyController.setToggleReady(String.valueOf(isReady));
  }

  /**
   * Prints a confirmation to the console that the client has exited the lobby <code>lobbyName
   * </code>. Proceeds to load the menu screen.
   *
   * @param lobbyName The name of the lobby that was exited
   */
  public void lobbyExited(String lobbyName) {
    this.isInLobby = false;
    this.lobbyController = null;
    try {
      this.loadMenuScreen();
      this.menuController.alertManager.displayAlert("Exited lobby " + lobbyName + ".", false);
      LOGGER.info("Exited lobby " + lobbyName + ".");
    } catch (IOException e) {
      LOGGER.fatal("Failed to load menu screen. Shutting down.");
      this.exit();
    }
  }

  /**
   * Updates the list of lobbies and their respective clients in the gui. The list of lobbies is
   * given in the following format: <code>
   * lobbyName1 client1 client2 client3&#60;&#38;&#63;&#62;lobbyName2</code>
   *
   * @param command The command containing the list of lobbies and their respective clients
   */
  public void updateLobbyInfo(String command) {
    if (this.menuScreen) {
      ArrayList<ArrayList<String>> lobbyInfos = new ArrayList<>();
      for (String lobbyInfo : command.split(ServerProtocol.SUBSEPARATOR.toString())) {
        // Split the lobby info into the lobby name and the clients
        String[] split = lobbyInfo.split(" ");
        ArrayList<String> lobbyInfoList = new ArrayList<>(Arrays.asList(split));

        lobbyInfos.add(lobbyInfoList);
      }
      String[][] asArray =
          lobbyInfos.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);
      this.menuController.setLobbyList(asArray);
    }
  }

  /**
   * Updates the clientList in the server
   *
   * @param command The command received from the server containing the list of clients
   */
  public void updateClientInfo(String command) {
    if (this.menuScreen) {
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

  /**
   * The client has received confirmation from the server that they have entered a lobby. Proceeds
   * to load the lobby screen.
   *
   * @param lobbyName The name of the lobby that was entered
   */
  public void enterLobby(String lobbyName) {
    LOGGER.info("Entered lobby " + lobbyName + ".");
    this.lobbyName = lobbyName;
    try {
      this.loadLobbyScreen();
    } catch (IOException e) {
      LOGGER.fatal("Failed to load lobby screen. Shutting down.");
      this.exit();
    }
  }

  /**
   * Called when the client has received a message from the server. Appends the message to the chat.
   *
   * @param message The message that was received
   * @param sender The sender of the message
   * @param privacy Whether the message was a broadcast or a private message
   */
  protected void receiveMessage(String message, String sender, String privacy) {
    if (sender.equals(this.username)) sender = "You";

    if (menuScreen) {
      this.menuController.receiveMessage(message, sender, privacy);
    } else if (lobbyScreen) {
      this.lobbyController.receiveMessage(message, sender, privacy);
    } else if (gameScreen) {
      this.gameController.receiveMessage(message, sender, privacy);
    }
  }

  /**
   * Sends the game commands to the server
   *
   * @param command The command to be sent to the server
   */
  public void sendGameCommand(String command) {
    this.outputSocket.sendToServer(command);
  }

  /**
   * Sets the volume of the music according to the slider in the settings menu
   *
   * @param volume The volume of the music
   */
  public void setMusicVolume(double volume) {
    this.menuMusicPlayer.setVolume(volume);
  }

  /**
   * Sets the volume of the sound effects according to the slider in the settings menu
   *
   * @param volume The volume of the sound effects
   */
  public void setSfxVolume(double volume) {
    this.clickPlayer.setVolume(volume);
  }

  /**
   * Sets the username with the String gotten
   *
   * @param username The username to be set
   */
  public void usernameSetTo(String username) {
    if (this.username != null) {
      if (this.menuScreen) {
        this.menuController.alertManager.displayAlert("Username set to " + username + ".", false);
        this.menuController.settingsTabController.setUsernameField();
      }
    }
    LOGGER.info(
        "Received confirmation of username change from " + this.username + " to " + username + ".");
    this.username = username;
    this.menuController.settingsTabController.setUsernameField();
  }

  /**
   * Loads a level in the game screen
   *
   * @param levelPath The path of the level to be loaded from the resources
   */
  public void loadLevel(String levelPath) {
    if (this.gameController != null) {
      this.gameController.loadLevel(levelPath);
    }
  }

  /**
   * Returns the username as a String
   *
   * @return The username of the client
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Returns the lobbyName as a String
   *
   * @return The name of the lobby
   */
  public String getLobbyName() {
    return this.lobbyName;
  }

  /**
   * Informs the client that their colour (in lobby) has been set. Called from LobbyController.
   *
   * @param colour The colour that the client has been set to
   */
  public void setColour(Color colour) {
    this.colour = colour;
  }

  /**
   * Returns the colour which has been assigned to the client from the server (in lobby).
   *
   * @return The colour of the client
   */
  public Color getColour() {
    return this.colour;
  }

  /**
   * Returns a music from the list of game music files which is different from the previous one. If
   * the method fails to find a different music after 20 tries, it returns null.
   *
   * @return A random music
   */
  private Media getRandomMusic() {
    Random rand = new Random();

    for (int i = 0; i < 20; i++) {
      int index = rand.nextInt(this.musicPaths.length);
      if (index != currentMusicIdx) {
        currentMusicIdx = index;
        return new Media(
            Objects.requireNonNull(getClass().getResource("/sounds/" + musicPaths[index]))
                .toString());
      }
    }

    return null;
  }

  /**
   * Gets a random music, plays it and sets the onEndOfMedia to play another random music. Used
   * while in game.
   */
  private void playGameMusic() {
    if (this.menuMusicPlayer != null) this.menuMusicPlayer.stop();
    this.menuMusicPlaying = false;

    Media music = this.getRandomMusic();

    if (music != null) {
      this.gameMusicPlayer = new MediaPlayer(music);
      this.gameMusicPlayer.setCycleCount(1);
      this.gameMusicPlayer.play();
      this.gameMusicPlayer.seek(this.gameMusicPlayer.getStartTime());
      this.gameMusicPlayer.setVolume(menuMusicPlayer.getVolume());

      // Change the music when the song is finished
      this.gameMusicPlayer.setOnEndOfMedia(this::playGameMusic);
    }
  }

  /**
   * Stops the game music. If the menu music isn't playing already, plays it from the start. Used
   * when loading the menu- and lobby screens.
   */
  private void playMenuMusic() {
    if (this.gameMusicPlayer != null) this.gameMusicPlayer.stop();

    if (!this.menuMusicPlaying) {
      this.menuMusicPlaying = true;

      this.menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      this.menuMusicPlayer.play();
      this.menuMusicPlayer.seek(this.menuMusicPlayer.getStartTime());
    }
  }

  /**
   * The client has inputted "!skip" in the chat whilst in game. Sends a skip command to the server,
   * requesting that the level be skipped.
   */
  public void skip() {
    if (gameScreen) {
      String command = ClientProtocol.SKIP_LEVEL.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  /**
   * The client has inputted "!immortal" in the chat whilst in game. Sends an immortal command to
   * the server, requesting that the client be made immortal.
   */
  public void setImmortal() {
    if (gameScreen) {
      String command = ClientProtocol.SET_IMMORTAL.toString();
      this.outputSocket.sendToServer(command);
    }
  }

  /**
   * The client has inputted "!mortal" in the chat whilst in game. Sends a mortal command to the
   * server, requesting that the client be made mortal (again).
   */
  public void setMortal() {
    if (gameScreen) {
      String command = ClientProtocol.SET_MORTAL.toString();
      this.outputSocket.sendToServer(command);
    }
  }
}
