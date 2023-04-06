package server;
import java.util.ArrayList;

import gui.Colours;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;

import java.awt.*;
import java.util.HashMap;

public class Game extends Application {
  private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
  private ArrayList<Node> platforms = new ArrayList<Node>(); // Used to store platforms
  private ArrayList<Node> stars = new ArrayList<Node>(); // Used to store collectable stars
  private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();
    private Cube player;
    private int levelWidth;

  public static void main(String[] args) {
    Application.launch(args);
  }

  public void update(){
    if (isPressed(KeyCode.UP)) {
      player.rectangle.setTranslateY(player.rectangle.getTranslateY()- 5);
    }
    if (isPressed(KeyCode.DOWN)) {
      player.rectangle.setTranslateY(player.rectangle.getTranslateY() + 5);
    }
    if (isPressed(KeyCode.LEFT)) {
      player.rectangle.setTranslateX(player.rectangle.getTranslateX() - 5);
    }
    if (isPressed(KeyCode.RIGHT)) {
      player.rectangle.setTranslateX(player.rectangle.getTranslateX() + 5);
    }
  }


  private boolean isPressed(KeyCode keyCode) {
    return keys.getOrDefault(keyCode, false);
  }

  public void initializeContent() {
    Rectangle bg = new Rectangle(800, 600);
    levelWidth = LevelData.Level1[0].length() * 50;
    // Create a rectangle with 100px width and 50px height at position (0, 0)
    Cube cube = new Cube(gameRoot, new Vector2D(350,175),  new Vector2D(0,0), new Vector2D(100,100));
    // Set the fill color of the rectangle
    cube.rectangle.setFill(Color.RED);
    // Add the rectangle to the root
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    initializeContent();
    Scene scene = new Scene(gameRoot);
    scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
    scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));

    primaryStage.setTitle("Game");
    primaryStage.setScene(scene);
    primaryStage.show();

    AnimationTimer timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        update();
      }
    };
    timer.start();


    /**
    Cube cube = new Cube(gameRoot, new Vector2D(350,175),  new Vector2D(0,0), new Vector2D(100,100));

    Button btup = new Button("UP");
    btup.setLayoutX(350);
    btup.setLayoutY(325);

    gameRoot.getChildren().add(btup);

    btup.setOnAction(e -> cube.rectangle.setY(cube.rectangle.getY()-10));
      */


  }

}
