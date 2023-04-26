package server;

import game.Block;
import game.Level;
import game.Vector2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/** The class which handles the logic of the game for the server. */
public class ServerGame implements Runnable {
  // Pane that contains the game
  private Pane gameRoot;

  /** The keys which are currently being pressed by players */
  public HashMap<KeyCode, Boolean> keys = new HashMap<>();

  private Block[][] grid; // Used to store the grid of blocks, null if no block is present
  private Level level;

  /** The colours which can be given to players */
  public static ArrayList<Color> blockColours =
      new ArrayList<>(
          Arrays.asList(
              Color.valueOf("#f57dc6"),
              Color.valueOf("#b3d5f2"),
              Color.valueOf("#9ae6ae"),
              Color.valueOf("#fccf78")));

  private final HashMap<ClientHandler, Color> clientColours;

  // In-Game variables
  private ServerCube player;
  /** Whether the cube is currently moving */
  public boolean cubeMoving = false;
  private final int gridSize = 50;
  private final int cubeSize = 30;
  private boolean jumped;
  private AnimationTimer timer;

  private Boolean running = true;
  private final ArrayList<ClientHandler> clients;

  private final String gameId;
  /** The instance of the game */
  public static ServerGame instance;

  /** Creates a new game
   *
   * @param clientColours The clients and their respective colours
   * @param clients The clients
   * @param gameId The number of the game
   */
  public ServerGame(
      HashMap<ClientHandler, Color> clientColours,
      ArrayList<ClientHandler> clients,
      String gameId) {
    this.clientColours = clientColours;
    this.clients = clients;
    this.gameId = gameId;
    initialiseContent();

    instance = this;
  }

  /** Updates position on all clients */
  protected void updateAllClientPositions() {
    for (ClientHandler client : clients) {
      client.positionUpdate(
          ServerProtocol.POSITION_UPDATE
              + ServerProtocol.SEPARATOR.toString()
              + player.position.getX()
              + ServerProtocol.SEPARATOR
              + player.position.getY());
    }
  }

  /** Called every frame and handles the game logic
   * @param dt The time since the last frame
   * */
  public void update(double dt) {
    // Potentially add pause update if wished
    this.gameUpdate(dt);
  }

  /** The update method that is called if the game is not paused. Handles the game logic. */
  private void gameUpdate(double dt) {
    Block[] neighbourBlocks =
        this.level.getNeighbourBlocks(player.position.getX(), player.position.getY());
    player.move(neighbourBlocks, dt);
  }

  /**
   * Initializes the content of the game Loads the level data and creates the platforms Creates the
   * player Creates the stars Will create the coin to finish the game
   */
  public void initialiseContent() {
    // Create a level and add it to an empty pane
    gameRoot = new Pane();
    this.level = new Level("easy", 50, gameRoot);
    this.level.setNeighbourColours(new ArrayList<>(clientColours.values()));

    // Spawn player
    Vector2D playerSpawn =
        new Vector2D(
            level.playerSpawnIdx[0] * level.blockWidth, level.playerSpawnIdx[1] * level.blockWidth);
    load_player(playerSpawn);
  }

  /** Loads the player */
  private void load_player(Vector2D position) {
    player = new ServerCube(gameRoot, position, cubeSize, gridSize); // creates the player
    player.start_position = position.clone();
  }

  /** Starts the game loop */
  public void startGameLoop() {
    this.running = true;
  }

  /** Runnable run method. This method is called when the thread is started. */
  @Override
  public void run() {
    this.initialiseContent();

    long previousTime = System.nanoTime();
    long now;
    int FPS = 120;
    double dt;

    while (this.running) {
      now = System.nanoTime();
      dt = (now - previousTime) * 1e-9; // Time since last frame in seconds

      if (dt > (double) 1 / FPS) {
        dt = dt > 1 ? 1 : dt; // Limit skipped frames

        previousTime = now;

        update(dt);
        updateAllClientPositions();
      }
    }
    
    this.endGame();
  }

  /**
   * A client has pressed the space bar. If the cube isn't moving yet, its speed is initialised.
   * Otherwise, a jump request is handled.
   *
   * @param client - The client that pressed the space bar.
   */
  public void spaceBarPressed(ClientHandler client) {
    if (!cubeMoving) {
      this.player.initialiseSpeed();
      this.cubeMoving = true;
    } else {
      this.player.jump(clientColours.get(client));
    }
  }

  /**
   * @return The name of the game instance concerned.
   */
  protected String getGameId() {
    return gameId;
  }

  /**
   * A client has left the server. The game is closed.
   *
   * @param client - The client that left the server.
   */
  protected void removeClient(ClientHandler client) {
    this.running = false;
  }

  /**
   * @return The game instance.
   */
  public static ServerGame getInstance() {
    return instance;
  }

  /** The cube has touched a white block. The block position is reset, etc. */
  public void resetLevel() {
    this.cubeMoving = false;
  }

  /**
   * Informs all clients of the positions and colours of the critical blocks in the level. Called at the beginning
   * of the game.
   */
  public void sendCriticalBlocks() {
    ArrayList<Block> blocks = level.getCriticalBlocks();

    StringBuilder command = new StringBuilder(ServerProtocol.SEND_CRITICAL_BLOCKS.toString());
    command.append(ServerProtocol.SEPARATOR);

    for (Block block : blocks) {
      command
          .append(ServerProtocol.SUBSEPARATOR)
          .append(block.getIndex()[0])
          .append(ServerProtocol.SUBSUBSEPARATOR)
          .append(block.getIndex()[1])
          .append(ServerProtocol.SUBSUBSEPARATOR)
          .append(block.getColour().toString());
    }

    for (ClientHandler client : clients) {
      client.sendCriticalBlocks(command.toString());
    }
  }

  /**
   * Closes the game loop and informs the server that the game has ended. The server then proceeds to
   * remove the game from its list of games and to inform the clients that the game has ended.
   */
  protected void endGame() {
    this.running = false;
    Server.getInstance().endGame(this);
  }

  /**
   * @return The ClientHandlers of the players in the game.
   */
  protected ArrayList<ClientHandler> getPlayers() {
    return this.clients;
  }
}
