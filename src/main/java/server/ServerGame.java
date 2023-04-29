package server;

import game.Block;
import game.GameConstants;
import game.Level;
import game.Vector2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/** The class which handles the logic of the game for the server. */
public class ServerGame implements Runnable {
  // Pane that contains the game
  private Pane gameRoot;

  // Used to store the grid of blocks, null if no block is present
  private Level level;
  private final int[] difficultyProbabilities = {50, 35, 15};

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

  private int lives = GameConstants.DEFAULT_LIVES.getValue();

  // Lobby
  private final ArrayList<ClientHandler> clients;
  private final Lobby lobby;
  private final String gameId;
  private int levelsCompleted = 0;

  /** The instance of the game */
  public static ServerGame instance;

  protected Boolean running = true;
  private Boolean coinCollision = false;

  /**
   * Creates a new game
   *
   * @param clientsAndColours The clients and their respective colours
   * @param gameId The number of the game
   */
  public ServerGame(HashMap<ClientHandler, Color> clientsAndColours, String gameId, Lobby lobby) {
    this.clientColours = clientsAndColours;
    this.clients = new ArrayList<>(clientColours.keySet());
    this.gameId = gameId;

    this.lobby = lobby;
    instance = this;
  }

  /** Sends a position update to all clients, so they can move the cube to its current position. */
  protected void cubePositionUpdate() {
    for (ClientHandler client : clients) {
      client.positionUpdate(
          ServerProtocol.POSITION_UPDATE
              + ServerProtocol.SEPARATOR.toString()
              + player.getPosition().getX()
              + ServerProtocol.SEPARATOR
              + player.getPosition().getY()
              + ServerProtocol.SEPARATOR
              + player.getVelocity().getX()
              + ServerProtocol.SEPARATOR
              + player.getVelocity().getY()
              + ServerProtocol.SEPARATOR
              + player.accelerationAngle);
    }
  }

  /**
   * Informs all clients of how many lives they have left and how many levels they have completed.
   * That way, they can update their UI accordingly.
   */
  private void gameStatusUpdate() {
    for (ClientHandler client : clients) {
      client.gameStatusUpdate(
          ServerProtocol.GAME_STATUS_UPDATE
              + ServerProtocol.SEPARATOR.toString()
              + this.lives
              + ServerProtocol.SEPARATOR
              + this.levelsCompleted);
    }
  }

  /**
   * The cube has just jumped. Inform the client of the coordinates of the rotation point and update
   * their movement.
   */
  protected void jumpUpdate() {
    for (ClientHandler client : clients) {
      client.jumpUpdate(
          ServerProtocol.JUMP_UPDATE
              + ServerProtocol.SEPARATOR.toString()
              + player.rotationPoint.getX()
              + ServerProtocol.SEPARATOR
              + player.rotationPoint.getY());
    }
    cubePositionUpdate();
  }

  /**
   * Called every frame and handles the game logic
   *
   * @param dt The time since the last frame
   */
  public void update(double dt) {
    // Potentially add pause update if wished
    this.gameUpdate(dt);
  }

  /** The update method that is called if the game is not paused. Handles the game logic. */
  private void gameUpdate(double dt) {
    Block[] neighbourBlocks =
        this.level.getNeighbourBlocks(player.getPosition().getX(), player.getPosition().getY());
    player.move(neighbourBlocks, dt);
  }

  /**
   * Initializes the content of the game Loads the level data and creates the platforms Creates the
   * player Creates the stars Will create the coin to finish the game
   */
  public void initialiseContent() {
    // Create empty pane to which the game will virtually be added
    gameRoot = new Pane();

    this.load_level();
  }

  /** Loads the level */
  private void load_level() {
    // Inform clients of the game status
    this.gameStatusUpdate();

    // Get a random level path
    String levelPath = this.getRandomLevelPath();
    this.sendLevelPath(levelPath);

    // Load the level
    this.level = new Level(levelPath, 50, gameRoot);
    this.level.setBlockColours(new ArrayList<>(clientColours.values()));
    this.sendCriticalBlocks();

    // Spawn player
    Vector2D playerSpawn =
        new Vector2D(
            level.playerSpawnIdx[0] * level.blockWidth, level.playerSpawnIdx[1] * level.blockWidth);
    load_player(playerSpawn);
  }

  /** Loads the player. */
  private void load_player(Vector2D position) {
    player = new ServerCube(gameRoot, position); // creates the player
    player.start_position = position.copy();
    player.resetMovement();

    this.cubeMoving = false;
  }

  /** Runnable run method. This method is called when the thread is started. */
  @Override
  public void run() {
    this.initialiseContent();

    long previousTime = System.nanoTime();
    long clientUpdateTime = System.nanoTime();
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
      }

      if ((now - clientUpdateTime) * 1e-9 > (double) 1 / 10) {
        clientUpdateTime = System.nanoTime();
        cubePositionUpdate();
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
   * @return The game instance.
   */
  public static ServerGame getInstance() {
    return instance;
  }

  /**
   * The cube has entered in contact with a white block. Its position, velocity and acceleration are
   * reset. A life is deducted from the players and the game is ended if that was their last life.
   */
  public void die() {
    this.player.resetMovement();
    this.cubeMoving = false;
    this.lives--;

    if (this.lives <= 0) {
      this.endGame();
    }

    this.gameStatusUpdate();
  }

  /**
   * Informs all clients of the positions and colours of the critical blocks in the level. Called at
   * the beginning of the game.
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
   * Sends the path of the current level to all clients.
   *
   * @param levelPath The path of the level.
   */
  private void sendLevelPath(String levelPath) {
    for (ClientHandler client : clients) {
      client.sendLevelPath(levelPath);
    }
  }

  /**
   * Closes the game loop and informs the server that the game has ended. The server then proceeds
   * to remove the game from its list of games and to inform the clients that the game has ended.
   */
  protected void endGame() {
    this.lobby.endGame();
  }

  /**
   * @return The ClientHandlers of the players in the game.
   */
  protected ArrayList<ClientHandler> getPlayers() {
    return this.clients;
  }

  /**
   * Gets a random level path from the level's folder.
   *
   * @return The path to the level.
   */
  private String getRandomLevelPath() {
    boolean found = false;
    String path = null;
    String difficulty;

    while (!found) {
      int random = (int) (Math.random() * 100);

      if (random < difficultyProbabilities[0]) {
        difficulty = "easy/";
      } else if (random < difficultyProbabilities[1]) {
        difficulty = "medium/";
      } else {
        difficulty = "hard/";
      }

      File dir =
          new File(
              Objects.requireNonNull(getClass().getResource("/levels/" + difficulty)).getPath());
      File[] files = dir.listFiles((dir1, name) -> name.endsWith(".csv"));

      if (files != null) {
        if (files.length > 0) {
          int randomFile = (int) (Math.random() * files.length);
          path = files[randomFile].getPath();
          found = true;
        }
      }
    }
    // Use this to test a specific level
    return getClass().getResource("/levels/easy/level_05.csv").getPath();
    // return path;
  }

  /**
   * The cube has collided with a coin in the level. A new level is loaded and the number of levels
   * completed is incremented.
   */
  public void nextLevel() {
    if (!coinCollision) {
      coinCollision = true;

      this.load_level();
      this.levelsCompleted++;
      Server.getInstance().updateGameLevelsCompleted(this);

      this.gameStatusUpdate();
    }
  }

  /**
   * Returns the number of levels completed by the players in this game.
   *
   * @return The number of levels completed.
   */
  public int getLevelsCompleted() {
    return this.levelsCompleted;
  }
}
