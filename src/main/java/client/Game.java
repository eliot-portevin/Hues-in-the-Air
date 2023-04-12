package client;

import gui.Colours;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class  Game extends Application {
  private HashMap<KeyCode, Boolean> keys = new HashMap<>();
  private ArrayList<Node> platforms = new ArrayList<>(); // Used to store platforms
  private ArrayList<Node> death_platforms = new ArrayList<>();
  private ArrayList<Node> stars = new ArrayList<>(); // Used to store collectable stars
  private Pane appRoot = new Pane();
  private Pane gameRoot;
  private Pane uiRoot = new Pane();
  private Cube player;
  private int levelWidth;
  private int levelHeight;
  private int gridSize = 50;
  private boolean jumped;
  private AnimationTimer timer;

  public Game () {
    this.gameRoot = Client.getInstance().gameController.getPane();
  }

  /**
   * Called every frame and handles the game logic
   */
  public void update(){
    player.move(player.velocity);
    if (isPressed(KeyCode.UP)) {
      player.move(new Vector2D(0,-2));
    }
    if (isPressed(KeyCode.DOWN)) {
      player.move(new Vector2D(0,2));
    }
    if (isPressed(KeyCode.LEFT)) {
      player.move(new Vector2D(-2,0));
    }
    if (isPressed(KeyCode.RIGHT)) {
      player.move(new Vector2D(2,0));
    }
    if (isPressed(KeyCode.SPACE)) {
      player.jump();
    }

    player.checkForWhiteBlockHit();
  }

  private boolean isPressed(KeyCode keyCode) {
    return keys.getOrDefault(keyCode, false);
  }


  /**
   * Creates a new rectangle entity
   * @param x - x position
   * @param y - y position
   * @param w - width
   * @param h - height
   * @param color - colour
   * @return - returns the rectangle entity
   */
  private Node createEntity(int x, int y, int w, int h, Color color) {
    Rectangle entity = new Rectangle(w, h);
    entity.setTranslateX(x);
    entity.setTranslateY(y);
    entity.setFill(color);
    gameRoot.getChildren().add(entity);
    return entity;
  }

  /**
   * Initializes the content of the game
   * Loads the level data and creates the platforms
   * Creates the player
   * Creates the stars
   * Will create the coin to finish the game
   */
  public void initializeContent() {
    levelWidth = LevelData.Level1[0].length() * gridSize;
    levelHeight = LevelData.Level1.length * gridSize;
    Rectangle bg = new Rectangle(1600, 900); // Creates the background
    bg.setFill(Colours.BLACK.getHex()); // Sets the background colour

    load_platforms(); // Loads the platforms

    //load_player(); // Loads the player


    appRoot.getChildren().addAll(bg, gameRoot, uiRoot);

  }
  /**
   * Loads the platforms from the level data
   */

  private void load_platforms() {
    for (int i=0; i<LevelData.Level1.length; i++) { // Creates the platforms
      String line = LevelData.Level1[i];
      for (int j = 0; j < line.length(); j++) {
        switch (line.charAt(j)) {
          case '0':
            break;
          case '1':
            Node platform1 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.WHITE.getHex());
            platforms.add(platform1);
            death_platforms.add(platform1);
            break;
          case '2':
            Node platform2 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.PINK.getHex());
            platforms.add(platform2);
            break;
          case '3':
            Node platform3 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.BLUE1.getHex());
            platforms.add(platform3);
            break;
          case '4':
            Node platform4 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.GREEN.getHex());
            platforms.add(platform4);
            break;
          case '5':
            Node platform5 = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.YELLOW.getHex());
            platforms.add(platform5);
            break;
          case '7':
            load_player(new Vector2D(j * gridSize, i * gridSize));

        }
      }
    }
  }
  /**
   * Loads the player
   */
  private void load_player(Vector2D position){
    player = new Cube(gameRoot, position, new Vector2D(20,20));  // creates the player

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
    player.platforms = platforms; // Sets the platforms for the player
    player.death_platforms = death_platforms;
    player.gridSize = gridSize; // Sets the grid size for the player
  }
  /**
   * Initializes the content, sets the scene and starts the game with the animation timer
   * @param primaryStage the primary stage for this application, onto which
   * the application scene can be set.
   * Applications may create other stages, if needed, but they will not be
   * primary stages.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    initializeContent();
    Scene scene = new Scene(appRoot);   // Creates the scene
    scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
    scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

    primaryStage.setTitle("Game"); // Sets the title of the window
    primaryStage.setScene(scene); // Sets the scene
    primaryStage.show(); // Shows the window
    //final long[] delta = {0};
    //final long[] lastFrameTime = {0};

    // Start the game loop called 60 times per second
    this.timer = new AnimationTimer() {
      //long delta;
      //long lastFrameTime;
      @Override
      public void handle(long now) { // Called every frame
        //delta[0] = now - lastFrameTime[0];
        //lastFrameTime[0] = now;
        //System.out.println(getFrameRateHertz(delta[0]));
        update();
      }
    };
    this.timer.start();
  }
/**
   * Gets the frame rate in hertz
   * @param deltaTimeNano - the time between frames in nanoseconds
   * @return - the frame rate in hertz
   */
  public double getFrameRateHertz(long deltaTimeNano) {
    double frameRate = 1d / deltaTimeNano;
    return frameRate * 1e9;
  }
  /**
   * Launches the application
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

}
