package client;

import game.Level;
import game.Colours;

import java.util.HashMap;
import java.util.Timer;

import game.LevelData;
import game.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class  Game {
  public HashMap<KeyCode, Boolean> keys = new HashMap<>();

  private Pane appRoot = new Pane();
  private Pane gameRoot;
  private int levelWidth;
  private int levelHeight;
  private int gridSize = 50;
  public boolean jumped;
  private int cubesize = 30;
  private AnimationTimer timer;

  private boolean pauseRequestSent = false;
  public boolean jumpRequestSent = false;
  private Client client;
  private Timer pauseTimer = new Timer();

  private boolean running = true;
  public boolean pause = false;
  public boolean gameStarted = false;

  public Level level;
  private Cube player;

  public Game(Client client) {
    this.client = client;
  }

  /**
   * Called every frame and handles the game logic
   */
  public void update(double deltaF){
    if (!pause) {
      this.gameUpdate(deltaF);
    }
    else {
      this.pauseUpdate(deltaF);
    }
  }

  /**
   * The update method that is called if the game is not paused. Handles the game logic.
   */
  private void gameUpdate(double deltaF) {
    this.analyseKeys(deltaF);
  }
  /** sends a request to the server to Toggle the pause
   */
  private void setPauseRequestSent() {
    this.pauseRequestSent = false;
  }

  /**
   * Called every frame. If the key ESCAPE is pressed, the game is paused. Otherwise, the game logic is handled.
   */
  private void analyseKeys(double deltaF) {
    if (!this.pause) {
      if (isPressed(KeyCode.SPACE)) {
        if (!jumpRequestSent) {
          client.sendGameCommand(ClientProtocol.SPACE_BAR_PRESSED.toString());
          jumpRequestSent = true;
        }
      }
    }
  }

  /**
   * The update method that is called if the game is paused.
   */
  private void pauseUpdate(double deltaF) {
    analyseKeys(deltaF);
  }
  /** update the position of the player
   * @param positionX - x position
   * @param positionY - y position
   */
  protected void updatePosition(String positionX, String positionY) {
    player.setPositionTo(Double.parseDouble(positionX), Double.parseDouble(positionY));
  }



  /**
   * Sets whether the game is paused or not.
   */
  public void setPause() {
    this.pause = !this.pause;
  }

  /**
   * Returns whether a key has been pressed by the user or not.
   */
  private boolean isPressed(KeyCode keyCode) {
    return keys.getOrDefault(keyCode, false);
  }

  /**
   * Initializes the content of the game
   * Loads the level data and creates the platforms
   * Creates the player
   * Creates the stars
   * Will create the coin to finish the game
   */
  public void initializeContent(Pane backgroundPane) {
    this.appRoot = backgroundPane;
    gameRoot = new Pane();
    levelWidth = LevelData.Level1[0].length() * gridSize;
    levelHeight = LevelData.Level1.length * gridSize;

    Rectangle bg = new Rectangle(this.gameRoot.getWidth(), this.gameRoot.getHeight()); // Creates the background
    bg.setFill(Colours.BLACK.getHex()); // Sets the background colour
    appRoot.getChildren().addAll(bg, gameRoot); // Adds the background and gameRoot to the appRoot

    // Load level
    this.level = new Level("easy", 50, gameRoot);
    this.client.requestCriticalBlocks();
    Vector2D playerSpawn =
        new Vector2D(
            level.playerSpawnIdx[0] * level.blockWidth, level.playerSpawnIdx[1] * level.blockWidth);
    load_player(playerSpawn);
  }

  /**
   * Loads the player
   */
  private void load_player(Vector2D position){
    player = new Cube(gameRoot, position, new Vector2D(cubesize,cubesize));  // creates the player

    player.rectangle.translateXProperty().addListener((obs, old, newValue) -> {   // Listens for changes in the player's x position and moves the terrain accordingly
      int offset = newValue.intValue();

      if (offset > 400 && offset < levelWidth - 400) {
        gameRoot.setLayoutX(-(offset - 400));
      }
    });

    player.rectangle.translateYProperty().addListener((obs, old, newValue) -> {   // Listens for changes in the player's Y position and moves the terrain accordingly
      int offset = newValue.intValue();

      if (offset > 400 && offset < levelHeight - 400) {
        gameRoot.setLayoutX(-(offset - 400));
      }
    });

    player.gridSize = gridSize; // Sets the grid size for the player
  }

  /**
   * Launches the application
   */
  public void run(Pane pane) {
    this.initializeContent(pane);

    this.timer = new AnimationTimer() {
      double timePerFrame = 1e9 / 60; // 60 FPS
      double deltaF = 0;
      long previousTime = 0;
      int frames = 0;
      long lastCheck = System.currentTimeMillis();
      @Override
      public void handle(long now) { // Called every frame
        deltaF += (now - previousTime)/timePerFrame;
        previousTime = now;
        if(deltaF >= 1) {
          while(deltaF >= 1) {
            update(deltaF);
            frames++;
            deltaF--;
          }
        }

        if(System.currentTimeMillis() - lastCheck >= 1000) {
          frames = 0;
          lastCheck = System.currentTimeMillis();
        }
      }
    };
    this.timer.start();
  }

  /**
   * Set the colour of a block and its neighbours to a given colour.
   * @param x the x index in the grid
   * @param y the y index in the grid
   * @param colour the colour to set the block to
   */
  public void setBlockColour(int x, int y, Color colour) {
    this.level.setNeighbourColours(x, y, colour);
  }
}
