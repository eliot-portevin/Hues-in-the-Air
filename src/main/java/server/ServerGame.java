package server;

import client.LevelData;
import client.Vector2D;
import game.Block;
import game.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ServerGame implements Runnable {
  // Pane that contains the game
  private Pane gameRoot;

  // Keys pressed by the players
  public HashMap<KeyCode, Boolean> keys = new HashMap<>();
  
  private Block[][] grid; // Used to store the grid of blocks, null if no block is present
  private Level level;

  // Clients
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
  public boolean gameStarted = false;
  private int gridSize = 50;
  private int cubeSize = 49;
  private boolean jumped;
  private AnimationTimer timer;
  public boolean pause = false;
  private boolean allClientsReady = false;
  private String[] levelData = LevelData.Level1;

  private Boolean running = true;
  private final ArrayList<ClientHandler> clients;

  private final String gameId;
  public static ServerGame instance;

  public ServerGame(
      HashMap<ClientHandler, Color> clientColours,
      ArrayList<ClientHandler> clients,
      String gameId) {
    this.clientColours = clientColours;
    this.clients = clients;
    this.gameId = gameId;
    initializeContent();

    instance = this;
  }

  /** Handles the jumprequest from the client */
  protected boolean handleJumpRequest(ClientHandler client) {
    if (client.canJump) {
      player.jump();
      return true;
    }
    return false;
  }
  /** Updates position on all clients */
  protected void updateAllClientPositions() {
    for (ClientHandler client : clients) {
      client.positionUpdate(
          ServerProtocol.POSITION_UPDATE.toString()
              + ServerProtocol.SEPARATOR.toString()
              + player.position.getX()
              + ServerProtocol.SEPARATOR.toString()
              + player.position.getY());
    }
  }
  /** Toggles pause state */
  protected void setPause() {
    pause = !pause;
  }
  /** Called every frame and handles the game logic */
  public void update(double dt) {
    if (!pause) {
      this.gameUpdate(dt);
    } else {
      this.pauseUpdate(dt);
    }
  }

  /** The update method that is called if the game is not paused. Handles the game logic. */
  private void gameUpdate(double dt) {
    Block[] neighbourBlocks =
        this.level.getNeighbourBlocks(player.position.getX(), player.position.getY());
    player.move(neighbourBlocks, dt);
  }

  /** The update method that is called if the game is paused. */
  private void pauseUpdate(double deltaF) {
    // Todo: Add pause menu and pause logic
  }

  /** Sets whether the game is paused or not. */
  public void setPause(boolean pause) {
    this.pause = pause;
  }

  /**
   * Initializes the content of the game Loads the level data and creates the platforms Creates the
   * player Creates the stars Will create the coin to finish the game
   */
  public void initializeContent() {
    gameRoot = new Pane();
    this.level = new Level("easy", 50, gameRoot);

    Vector2D playerSpawn =
        new Vector2D(
            level.playerSpawnIdx[0] * level.blockWidth, level.playerSpawnIdx[1] * level.blockWidth);
    load_player(playerSpawn);
  }

  /** Loads the player */
  private void load_player(Vector2D position) {
    player = new ServerCube(gameRoot, position, new Vector2D(cubeSize, cubeSize)); // creates the player
    player.start_position = position;
    player.gridSize = gridSize; // Sets the grid size for the player
  }

  /** Starts the game loop */
  public void startGameLoop() {
    this.running = true;
    this.pause = false;
  }

  /** Runnable run method. This method is called when the thread is started. */
  @Override
  public void run() {
    this.initializeContent();

    long previousTime = System.nanoTime();
    long now = System.nanoTime();
    int FPS = 60;
    double dt = 0;

    while (this.running) {
      now = System.nanoTime();
      dt = (now - previousTime) * 1e-9; // Time since last frame in seconds

      if (!pause) {
        if (dt > (double) 1 / FPS) {
          dt = dt > 1 ? 1 : dt; // Limit skipped frames

          previousTime = now;

          update(dt);
          updateAllClientPositions();
        }

      } else {
        pauseUpdate(dt);
      }
    }
    Server.getInstance().endGame(this);
  }

  /**
   * A client has pressed the space bar. If the cube isn't moving yet, its speed is initialised.
   * Otherwise, a jump request is handled.
   *
   * @param client - The client that pressed the space bar.
   */
  public void spaceBarPressed(ClientHandler client) {
    if (!gameStarted) {
      this.player.initialiseSpeed();
      this.gameStarted = true;
    }
    else {
      this.player.jump();
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

  /**
   * The cube has touched a white block. The block position is reset, etc.
   */
  public void resetLevel() {
    this.gameStarted = false;
  }
}
