package client;

import game.Colours;
import game.Level;
import game.LevelData;
import game.Vector2D;
import java.util.HashMap;
import java.util.Timer;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** The game class which the client uses to handle the game logic. */
public class Game {
  /** The keys that are pressed. */
  public HashMap<KeyCode, Boolean> keys = new HashMap<>();

  private Pane appRoot = new Pane();
  private Pane gameRoot;
  private int levelWidth;
  private int levelHeight;
  private int gridSize = 50;
  private double cameraMarginWidth;
  private double cameraMarginHeight;
  /** Whether the cube is jumping or not. */
  public boolean jumped;

  private int cubeSize = 30;
  private AnimationTimer timer;

  /** Whether a jump request has been sent to the server or not. */
  public boolean jumpRequestSent = false;

  private final Client client;
  private final Timer pauseTimer = new Timer();

  private boolean running = true;
  /** Whether the game has started or not. */
  public boolean gameStarted = false;

  /** The level that is currently being played. */
  public Level level;
  private Cube player;

  /**
   * Creates a new game.
   * @param client the client that is playing the game
   */
  public Game(Client client) {
    this.client = client;
  }

  /** Called every frame and handles the game logic.
   * @param deltaF the time between the last frame and the current frame
   * */
  public void update(double deltaF) {
    // Possibility to add a pause method
    this.gameUpdate(deltaF);
  }

  /** The update method that is called if the game is not paused. Handles the game logic. */
  private void gameUpdate(double deltaF) {
    this.analyseKeys(deltaF);
  }

  /**
   * Called every frame. If the key ESCAPE is pressed, the game is paused. Otherwise, the game logic
   * is handled.
   * @param deltaF the time between the last frame and the current frame
   */
  private void analyseKeys(double deltaF) {
    // Possibility to add a pause method
    if (isPressed(KeyCode.SPACE)) {
      if (!jumpRequestSent) {
        client.sendGameCommand(ClientProtocol.SPACE_BAR_PRESSED.toString());
        jumpRequestSent = true;
      }
    }
  }

  /** The update method that is called if the game is paused. */
  private void pauseUpdate(double deltaF) {
    analyseKeys(deltaF);
  }
  /**
   * update the position of the player
   *
   * @param positionX - x position
   * @param positionY - y position
   */
  protected void updatePosition(String positionX, String positionY) {
    player.setPositionTo(Double.parseDouble(positionX), Double.parseDouble(positionY));
  }

  /** Returns whether a key has been pressed by the user or not. */
  private boolean isPressed(KeyCode keyCode) {
    return keys.getOrDefault(keyCode, false);
  }

  /**
   * Initializes the content of the game Loads the level data and creates the platforms Creates the
   * player Creates the stars Will create the coin to finish the game.
   * @param backgroundPane the pane that the game is displayed on
   */
  public void initializeContent(Pane backgroundPane) {
    this.appRoot = backgroundPane;
    gameRoot = new Pane();
    levelWidth = LevelData.Level1[0].length() * gridSize;
    levelHeight = LevelData.Level1.length * gridSize;
    cameraMarginWidth = cubeSize*10;
    cameraMarginHeight = cubeSize*5;

    Rectangle bg =
        new Rectangle(
            this.gameRoot.getWidth(), this.gameRoot.getHeight()); // Creates the background
    bg.setFill(Colours.BLACK.getHex()); // Sets the background colour
    appRoot.getChildren().addAll(bg, gameRoot); // Adds the background and gameRoot to the appRoot
  }

  /** The client has received the level path from the server and can now load the level. */
  public void loadLevel(String levelPath) {
    this.level = new Level(levelPath, 50, gameRoot);
    this.client.requestCriticalBlocks();
    Vector2D playerSpawn =
        new Vector2D(
            level.playerSpawnIdx[0] * level.blockWidth, level.playerSpawnIdx[1] * level.blockWidth);
    load_player(playerSpawn);
  }

  /** Loads the player */
  private void load_player(Vector2D position) {
    player = new Cube(gameRoot, position, new Vector2D(cubeSize, cubeSize)); // creates the player

    player
        .rectangle
        .translateXProperty()
        .addListener(
            (obs,
                old,
                newValue) -> { // Listens for changes in the player's x position and moves the
                               // terrain accordingly
              int offsetX = newValue.intValue();

              if (offsetX > cameraMarginWidth && offsetX < levelWidth - cameraMarginWidth) {
                gameRoot.setLayoutX(-(offsetX - cameraMarginWidth));
              }
            });

    player
        .rectangle
        .translateYProperty()
        .addListener(
            (obs,
                old,
                newValue) -> { // Listens for changes in the player's Y position and moves the
                               // terrain accordingly
              int offsetY = newValue.intValue();

              if (offsetY > cameraMarginHeight && offsetY < levelHeight - cameraMarginHeight) {
                gameRoot.setLayoutX(-(offsetY - cameraMarginHeight));
              }
            });

    player.blockSize = gridSize; // Sets the grid size for the player
  }

  /**
   * Launches the application.
   *
   * @param pane the pane to launch the application in
   */
  public void run(Pane pane) {
    this.initializeContent(pane);

    this.timer =
        new AnimationTimer() {
          double timePerFrame = 1e9 / 60; // 60 FPS
          double deltaF = 0;
          long previousTime = 0;
          int frames = 0;
          long lastCheck = System.currentTimeMillis();

          @Override
          public void handle(long now) { // Called every frame
            deltaF += (now - previousTime) / timePerFrame;
            previousTime = now;
            if (deltaF >= 1) {
              while (deltaF >= 1) {
                update(deltaF);
                frames++;
                deltaF--;
              }
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
              frames = 0;
              lastCheck = System.currentTimeMillis();
            }
          }
        };
    this.timer.start();
  }

  /**
   * Set the colour of a block and its neighbours to a given colour.
   *
   * @param x the x index in the grid
   * @param y the y index in the grid
   * @param colour the colour to set the block to
   */
  public void setBlockColour(int x, int y, Color colour) {
    this.level.setNeighbourColours(x, y, colour);
  }
}
