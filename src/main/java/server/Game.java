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

public class  Game extends Application {
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
      movePlayerY(-5);
    }
    if (isPressed(KeyCode.DOWN)) {
      movePlayerY(5);
    }
    if (isPressed(KeyCode.LEFT)) {
      movePlayerX(-5);
    }
    if (isPressed(KeyCode.RIGHT)) {
      movePlayerX(5);
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
            if (player.rectangle.getTranslateX() + 40 == platform.getTranslateX()) {
              return;
            }
          } else {
            if (player.rectangle.getTranslateX() == platform.getTranslateX() + 40) {
              return;
            }
          }
        }
      }
      player.rectangle.setTranslateX(player.rectangle.getTranslateX() + (movingRight ? 1 : -1));
    }
  }

  public void movePlayerY(int value) {
    boolean movingDown = value > 0;

    for (int i = 0; i < Math.abs(value); i++) {
      for (Node platform : platforms) {
        if (player.rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
          if (movingDown) {
            if (player.rectangle.getTranslateY() + 40 == platform.getTranslateY()) {
              return;
            }
          } else {
            if (player.rectangle.getTranslateY() == platform.getTranslateY() + 40) {
              return;
            }
          }
        }
      }
      player.rectangle.setTranslateY(player.rectangle.getTranslateY() + (movingDown ? 1 : -1));
    }
  }

  private Node createEntity(int x, int y, int w, int h, Color color) {
    Rectangle entity = new Rectangle(w, h);
    entity.setTranslateX(x);
    entity.setTranslateY(y);
    entity.setFill(color);
    gameRoot.getChildren().add(entity);
    return entity;
  }

  public void initializeContent() {

    levelWidth = LevelData.Level1[0].length() * 50;

    for (int i=0; i<LevelData.Level1.length; i++) {
      String line = LevelData.Level1[i];
      for (int j = 0; j < line.length(); j++) {
        switch (line.charAt(j)) {
          case '0':
            break;
          case '1':
            Node platform = createEntity(j * 50, i * 50, 50, 50, Color.BLUE);
            platforms.add(platform);
            break;
        }
      }
    }

    player = new Cube(new Vector2D(100, 1000),  new Vector2D(0,0), new Vector2D(50,50));
    appRoot.getChildren().add(player.rectangle);

    player.rectangle.translateXProperty().addListener((obs, old, newValue) -> {   // Listens for changes in the player's x position and moves the terrain accordingly
      int offset = newValue.intValue();

      if (offset > 400 && offset < levelWidth - 400) {
        gameRoot.setLayoutX(-(offset - 400));
      }
    });


  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Rectangle bg = new Rectangle(800, 600);
    appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
    initializeContent();
    Scene scene = new Scene(appRoot);
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


  }

}
