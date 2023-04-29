package client;

import game.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** The game class which the client uses to handle the game logic. */
public class ClientGame {
  /** The keys that are pressed. */
  public HashMap<KeyCode, Boolean> keys = new HashMap<>();

  private Pane appRoot = new Pane();
  private Pane gameRoot;
  private int gridSize = 50;
  private double cameraMarginWidth;
  private double cameraMarginHeight;
  private Vector2D playerScreenPosition;

  private int cubeSize = 30;
  private AnimationTimer timer;

  /** Whether a jump request has been sent to the server or not. */
  public boolean jumpRequestSent = false;

  private final Client client;

  private boolean running = true;

  /** The level that is currently being played. */
  public Level level;

  private ClientCube player;

  /**
   * Creates a new game.
   *
   * @param client the client that is playing the game
   */
  public ClientGame(Client client, Pane backgroundPane) {
    this.client = client;
    this.appRoot = backgroundPane;
  }

  /**
   * Called every frame and handles the game logic.
   *
   * @param dt the time between the last frame and the current frame
   */
  public void update(double dt) {
    // Possibility to add a pause method
    this.gameUpdate(dt);
  }

  /** The update method that is called if the game is not paused. Handles the game logic. */
  private void gameUpdate(double dt) {
    this.analyseKeys(dt);

    if (player != null) {
      Block[] neighbourBlocks =
          this.level.getNeighbourBlocks(player.getPosition().getX(), player.getPosition().getY());
      player.move(neighbourBlocks, dt);
    }
  }

  /**
   * Called every frame. If the key ESCAPE is pressed, the game is paused. Otherwise, the game logic
   * is handled.
   *
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
  /**
   * update the position of the player
   *
   * @param positionX - x position
   * @param positionY - y position
   */
  protected void updatePosition(String positionX, String positionY, String velocityX, String velocityY, String accelerationAngle) {
    player.setPositionTo(Double.parseDouble(positionX), Double.parseDouble(positionY));
    player.setVelocityTo(Double.parseDouble(velocityX), Double.parseDouble(velocityY));
    player.onlySetAccelerationAngle(Integer.parseInt(accelerationAngle));
  }

  /** Returns whether a key has been pressed by the user or not. */
  private boolean isPressed(KeyCode keyCode) {
    return keys.getOrDefault(keyCode, false);
  }

  /**
   * Initializes the content of the game Loads the level data and creates the platforms Creates the
   * player Creates the stars Will create the coin to finish the game.
   */
  public void initialiseContent() {
    gameRoot = new Pane();

    // The camera is always centered on the player (middle of the screen)
    playerScreenPosition =
        new Vector2D(
            this.appRoot.getWidth() / 2,
            this.appRoot.getHeight() / 2); // Sets the player's position
    this.appRoot
        .widthProperty()
        .addListener(
            (obs, old, newValues) -> playerScreenPosition.setX(newValues.doubleValue() / 2));
    this.appRoot
        .heightProperty()
        .addListener(
            (obs, old, newValues) -> playerScreenPosition.setY(newValues.doubleValue() / 2));
    cameraMarginWidth = cubeSize * 10;
    cameraMarginHeight = cubeSize * 5;

    Rectangle bg =
        new Rectangle(
            this.gameRoot.getWidth(), this.gameRoot.getHeight()); // Creates the background
    bg.setFill(Colours.BLACK.getHex()); // Sets the background colour
    appRoot.getChildren().addAll(bg, gameRoot); // Adds the background and gameRoot to the appRoot
  }

  /** The client has received the level path from the server and can now load the level. */
  public void loadLevel(String levelPath) {
    this.gameRoot.getChildren().clear();

    this.level = new Level(levelPath, 50, gameRoot);
    this.client.requestCriticalBlocks();

    this.loadCoin();

    Vector2D playerSpawn =
        new Vector2D(
            level.playerSpawnIdx[0] * level.blockWidth, level.playerSpawnIdx[1] * level.blockWidth);
    load_player(playerSpawn);
  }

  /** Loads the player */
  private void load_player(Vector2D spawnPosition) {
    player = new ClientCube(gameRoot, spawnPosition); // creates the player

    player
        .rectangle
        .translateXProperty()
        .addListener(
            (obs,
                old,
                newValue) -> { // Listens for changes in the player's x position and moves the
              // camera
              updateCameraPosition();
            });

    player
        .rectangle
        .translateYProperty()
        .addListener(
            (obs,
                old,
                newValue) -> { // Listens for changes in the player's Y position and moves the
              // camera
              updateCameraPosition();
            });

    player.blockSize = gridSize; // Sets the grid size for the player
  }

  /**
   * Gets the coin position from the level and adds it to the game root. If no coin is found, the
   * coin is placed outside the level (top left corner).
   */
  private void loadCoin() {
    Image coinImage =
        new Image(
            Objects.requireNonNull(getClass().getResource("/images/coin.png")).toExternalForm());
    ImageView coin = new ImageView(coinImage);
    coin.setFitHeight(GameConstants.BLOCK_SIZE.getValue());
    coin.setFitWidth(GameConstants.BLOCK_SIZE.getValue());
    coin.setX(level.coinIdx[0] * level.blockWidth);
    coin.setY(level.coinIdx[1] * level.blockWidth);
    this.gameRoot.getChildren().add(coin);
  }

  /**
   * Launches the application.
   */
  public void run() {
    this.initialiseContent();

    this.timer =
        new AnimationTimer() {
          long previousTime = System.nanoTime();
          final int FPS = 120;
          double dt;

          @Override
          public void handle(long now) { // Called every frame
            dt = (now - previousTime) * 1e-9; // Time since last frame in seconds

            if (dt > (double) 1/FPS) {
              previousTime = now;

              update(dt);
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

  /** The player has moved. Get the position at which the level should be drawn. */
  private void updateCameraPosition() {
    Vector2D offset =
        new Vector2D(player.rectangle.getTranslateX(), player.rectangle.getTranslateY());
    offset.multiplyInPlace(-1);
    offset.addInPlace(playerScreenPosition);
    gameRoot.setLayoutX(offset.getX());
    gameRoot.setLayoutY(offset.getY());
  }

  /**
   * The cube has successfully jumped. The client has now just been informed of the coordinates of the point
   * around which the cube should rotate.
   *
   * @param rotationPointX the x coordinate of the rotation point
   * @param rotationPointY the y coordinate of the rotation point
   */
  public void updateJump(String rotationPointX, String rotationPointY) {
    player.rotationPoint = new Vector2D(Double.parseDouble(rotationPointX), Double.parseDouble(rotationPointY));
    player.jumping = true;
    player.canRotate = true;
  }
}
