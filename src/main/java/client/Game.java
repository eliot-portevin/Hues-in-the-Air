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
  private ArrayList<Node> stars = new ArrayList<>(); // Used to store collectable stars
  private Pane appRoot = new Pane();
  private Pane gameRoot = new Pane();
  private Pane uiRoot = new Pane();
  private Cube player;
  private int levelWidth;
  private int gridSize = 50;
  private boolean jumped;
  private final Vector2D g = new Vector2D(0, 0.01);

  private AnimationTimer timer;


  public void update(){

    if (isPressed(KeyCode.UP)) {
      movePlayerY(-2);
    }
    if (isPressed(KeyCode.DOWN)) {
      movePlayerY(2);
    }
    if (isPressed(KeyCode.LEFT)) {
      movePlayerX(-2);
    }
    if (isPressed(KeyCode.RIGHT)) {
      movePlayerX(2);
    }
    if (isPressed(KeyCode.SPACE)) {
      timer.stop();
      this.jump();
      timer.start();
    }
  }

  private boolean isPressed(KeyCode keyCode) {
    return keys.getOrDefault(keyCode, false);
  }

  public void movePlayerX(int value) {
    boolean movingRight = value > 0;

    for (int i = 0; i < Math.abs(value); i++) {
      for (Node platform : platforms) {
        if (player.rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
          if (movingRight) {
            if (player.rectangle.getTranslateX() + player.size.getX() == platform.getTranslateX()) {
              return;
            }
          } else {
            if (player.rectangle.getTranslateX() == platform.getTranslateX() + gridSize) {
              return;
            }
          }
        }
      }
      player.move1X(movingRight);
    }
  }

  public void movePlayerY(int value) {
    boolean movingDown = value > 0;

    for (int i = 0; i < Math.abs(value); i++) {
      for (Node platform : platforms) {
        if (player.rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
          if (movingDown) {
            if (player.rectangle.getTranslateY() + player.size.getY() == platform.getTranslateY()) {
              return;
            }
          } else {
            if (player.rectangle.getTranslateY() == platform.getTranslateY() + gridSize) {
              return;
            }
          }
        }
      }
        player.move1Y(movingDown);
    }
  }

  public void jump() {
    player.velocity.setY(-1);
    Vector2D startPosition = new Vector2D(player.position.getX(), player.position.getY());

    AnimationTimer jumpTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        player.changePosition();
        player.velocity.setY(player.velocity.getY() + g.getY());
        if(player.position.equals(startPosition)) {
          this.stop();
        }
      }
    };
    jumpTimer.start();
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
    Rectangle bg = new Rectangle(levelWidth, 600); // Creates the background
    bg.setFill(Colours.BLACK.getHex()); // Sets the background colour

    for (int i=0; i<LevelData.Level1.length; i++) { // Creates the platforms
      String line = LevelData.Level1[i];
      for (int j = 0; j < line.length(); j++) {
        switch (line.charAt(j)) {
          case '0':
            break;
          case '1':
            Node platform = createEntity(j * gridSize, i * gridSize, gridSize, gridSize, Colours.BLUE1.getHex());
            platforms.add(platform);
            break;
        }
      }
    }

    player = new Cube(gameRoot, new Vector2D(100, 100), new Vector2D(0,0), new Vector2D(20,20));  // creates the player

    player.rectangle.translateXProperty().addListener((obs, old, newValue) -> {   // Listens for changes in the player's x position and moves the terrain accordingly
      int offset = newValue.intValue();

      if (offset > 400 && offset < levelWidth - 400) {
        gameRoot.setLayoutX(-(offset - 400));
      }
    });

    appRoot.getChildren().addAll(bg, gameRoot, uiRoot);

  }

  /**
   * Initializes the content, sets the scene and starts the game with the animation timer
   * @param primaryStage the primary stage for this application, onto which
   * the application scene can be set.
   * Applications may create other stages, if needed, but they will not be
   * primary stages.
   * @throws Exception
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

    // Start the game loop called 60 times per second
    this.timer = new AnimationTimer() {
      @Override
      public void handle(long now) { // Called every frame
        update();
      }
    };
    this.timer.start();
  }
  /**
   * Launches the application
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

}
